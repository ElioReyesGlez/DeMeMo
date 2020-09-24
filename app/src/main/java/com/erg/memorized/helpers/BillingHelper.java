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

import java.util.ArrayList;
import java.util.List;

public class BillingHelper {

    private static final String TAG = "BillingHelper";

    private static final String SKU_PREMIUM = "premium_status";

    private Activity context;
    private BillingClient billingClient;

    private ItemUser currentUser;

    private boolean isAgreeToBy = false;

    public BillingHelper(Activity context, ItemUser currentUser)
    {
        this.context = context;
        this.currentUser = currentUser;
    }

    public void init() {

        billingClient = BillingClient.newBuilder(context)
                .enablePendingPurchases()
                .setListener(purchaseUpdateListener).build();

        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    Log.d(TAG, "onBillingSetupFinished: Success Billing Connection");
                    loadAllSkus();
                } else {
                    Log.d(TAG, "onBillingSetupFinished: Fail Billing Connection");
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                Log.d(TAG, "onBillingServiceDisconnected: Disconnected");
            }
        });
    }

    private void loadAllSkus() {
        if (billingClient.isReady()) {

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
                            Log.d(TAG, "skuDetailsList: " + skuDetailsList.toString());
                            for (SkuDetails skuDetails : skuDetailsList) {
                                if (skuDetails.getSku().equals(SKU_PREMIUM)) {
                                    startBillingFlow(skuDetails);
                                }
                            }
                        } else {
                            MessagesHelper.showInfoMessageError(context,
                                    "Error cant not query product");
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
                    }
                }

                if (!context.isFinishing())
                    MessagesHelper.showInfoMessageError(context,
                        context.getString(R.string.already_premium));
            }
        }
    }

    private PurchasesUpdatedListener purchaseUpdateListener = (billingResult, purchases) -> {
        Log.d(TAG, "onPurchasesUpdated: billingResult: " + billingResult.getDebugMessage());
        if (purchases != null) {
            for (Purchase purchase : purchases) {
                String purchaseSku = purchase.getSku();
                if (purchaseSku.equals(SKU_PREMIUM)) {
                    currentUser.setPremium(true);
                    RealmHelper realmHelper = new RealmHelper(context);
                    realmHelper.addUserToDB(currentUser);

                    Log.d(TAG, "onPurchasesUpdated: Purchase: " + purchases.toString());
                    Toast.makeText(context,context.getString(R.string.you_are_premium_now),
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
    };
}
