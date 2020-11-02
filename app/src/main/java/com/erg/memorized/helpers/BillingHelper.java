package com.erg.memorized.helpers;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.erg.memorized.R;
import com.erg.memorized.model.ItemUser;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import static com.erg.memorized.util.Constants.LEADER_BOARD_FIRE_BASE_REFERENCE;
import static com.erg.memorized.util.Constants.PREMIUM_USER_FIRE_BASE_REFERENCE;
import static com.erg.memorized.util.Constants.USER_COLUMN_PREMIUM_STATUS;
import static com.erg.memorized.util.Constants.USER_FIRE_BASE_REFERENCE;

public class BillingHelper {

    private static final String TAG = "BillingHelper";

    private static final String SKU_PREMIUM = "premium_status";

    private Activity context;
    private BillingClient billingClient;

    private ItemUser currentUser;

    public BillingHelper(Activity context, ItemUser currentUser)
    {
        this.context = context;
        this.currentUser = currentUser;
    }

    public void init() {

        Log.d(TAG, " billingClient init: On");

        billingClient = BillingClient.newBuilder(context)
                .enablePendingPurchases()
                .setListener(purchaseUpdateListener)
                .build();

        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    Log.d(TAG, "onBillingSetupFinished: Success Billing Connection");
                } else {
                    Log.e(TAG, "onBillingSetupFinished: Fail Billing Connection Code: "
                    + billingResult.getDebugMessage());
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                Log.d(TAG, "onBillingServiceDisconnected: Disconnected");
            }
        });
    }

    public void loadAllSkusAndStartBillingFlow() {
        if (billingClient.isReady()) {
            Log.d(TAG, "loadAllSkus started: billingClient isReady");

            List<String> skuList = new ArrayList<>();
            skuList.add(SKU_PREMIUM);
            SkuDetailsParams params = SkuDetailsParams.newBuilder()
                    .setSkusList(skuList)
                    .setType(BillingClient.SkuType.INAPP)
                    .build();

            billingClient.querySkuDetailsAsync(params,
                    (billingResult, skuDetailsList) -> {
                        Log.d(TAG, "querySkuDetailsAsync: ResponseCode: " + billingResult.getResponseCode()
                                + " DebugMessage: " +  billingResult.getDebugMessage());

                        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                            Log.d(TAG, "billingResult: " + billingResult.getDebugMessage());
                            assert skuDetailsList != null;
                            Log.d(TAG, "skuDetailsList: " + skuDetailsList.toString());
                            for (SkuDetails skuDetails : skuDetailsList) {
                                if (skuDetails.getSku().equals(SKU_PREMIUM)) {
                                    startBillingFlow(skuDetails);
                                }
                            }
                        } else {
                            Log.e(TAG, "loadAllSkusAndStartBillingFlow: " +
                                    context.getString(R.string.cant_not_query_product));
                            if (!context.isFinishing()) {
                                MessagesHelper.showInfoMessageError(context,
                                        context.getString(R.string.faild_billing_connection));

                            }
                        }
                    });
        }
    }

    private void startBillingFlow(SkuDetails skuDetails) {
        if (skuDetails != null) {
            BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                    .setSkuDetails(skuDetails)
                    .build();

            int responseCode = billingClient
                    .launchBillingFlow(context, billingFlowParams)
                    .getResponseCode();

            Log.d(TAG, "startBillingFlow: responseCode: " + responseCode);

            if (responseCode == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {
                if (currentUser != null) {
                    if (!currentUser.isPremium()) {
                        currentUser.setPremium(true);
                        RealmHelper realmHelper = new RealmHelper(context);
                        realmHelper.addUserToDB(currentUser);

                        updateUserPremiumStatus();
                        updateUserPremiumStatusOnLeaderBoard();
                    }
                }

                if (!context.isFinishing())
                    MessagesHelper.showInfoMessage(context,
                        context.getString(R.string.already_premium));
            }
        }
    }

    private final PurchasesUpdatedListener purchaseUpdateListener = (billingResult, purchases) -> {
        Log.d(TAG, "onPurchasesUpdated: billingResult: " + billingResult.getDebugMessage());
        if (purchases != null) {
            for (Purchase purchase : purchases) {
                String purchaseSku = purchase.getSku();
                if (purchaseSku.equals(SKU_PREMIUM)) {
                    currentUser.setPremium(true);
                    RealmHelper realmHelper = new RealmHelper(context);
                    realmHelper.addUserToDB(currentUser);

                    updateUserPremiumStatus();
                    updateUserPremiumStatusOnLeaderBoard();

                    Toast.makeText(context,context.getString(R.string.you_are_premium_now),
                            Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onPurchasesUpdated: Purchase: " + purchases.toString());
                }
            }
        }
    };

    private void updateUserPremiumStatus() {
        DatabaseReference fReferenceUser = FirebaseDatabase.getInstance()
                .getReference(USER_FIRE_BASE_REFERENCE)
                .child(currentUser.getId())
                .child(USER_COLUMN_PREMIUM_STATUS);

        fReferenceUser.setValue(String.valueOf(currentUser.isPremium()))
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "User Premium Status Uploaded : Success : "
                                + currentUser.isPremium());
                    } else {

                        if (task.getException() instanceof FirebaseNetworkException) {
                            Toast.makeText(context,context.getString(R.string.network_error),
                                    Toast.LENGTH_LONG).show();
                            Log.d(TAG, "upload: " + task.getException().getMessage());
                        } else {
                            Toast.makeText(context,context.getString(R.string.failed_synchronizing),
                                    Toast.LENGTH_LONG).show();
                        }

                        Log.e(TAG, "Failed uploading User Premium Status Error: "
                                + task.getException());
                    }
                });
    }

    private void updateUserPremiumStatusOnLeaderBoard() {
        DatabaseReference fReferenceLeaderBoard = FirebaseDatabase.getInstance()
                .getReference(LEADER_BOARD_FIRE_BASE_REFERENCE)
                .child(currentUser.getId())
                .child(PREMIUM_USER_FIRE_BASE_REFERENCE);
        fReferenceLeaderBoard.setValue(String.valueOf(currentUser.isPremium()))
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Leader board upload Premium Status: Success:  "
                        + currentUser.isPremium());
                    } else {
                        if (task.getException() instanceof FirebaseNetworkException) {
                            Toast.makeText(context,context.getString(R.string.network_error),
                                    Toast.LENGTH_LONG).show();
                            Log.d(TAG, "upload: " + task.getException());
                        } else {
                            Toast.makeText(context,context.getString(R.string.failed_synchronizing),
                                    Toast.LENGTH_LONG).show();
                        }

                        Log.e(TAG, "Failed uploading on Leader board Premium Status Error: "
                                + task.getException());
                    }
                });
    }
}
