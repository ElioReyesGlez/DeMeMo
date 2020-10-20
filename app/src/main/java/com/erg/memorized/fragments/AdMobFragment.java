package com.erg.memorized.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.erg.memorized.R;
import com.erg.memorized.helpers.BillingHelper;
import com.erg.memorized.helpers.MessagesHelper;
import com.erg.memorized.model.ItemUser;
import com.erg.memorized.util.SuperUtil;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

public class AdMobFragment extends Fragment implements View.OnClickListener {

    public static final String TAG = "AdMobFragment";

    private CountDownTimer countDownTimer;
    private boolean isOnTick = false;

    private Button btnGetPremium;
    private InterstitialAd mInterstitialAd;
    private TextView tvCountdown;
    private LinearLayout llCountdownContainer;
    private ItemUser currentUser;
    private BillingHelper billingHelper;
    private boolean jumpFlag;

    private long timerMilliseconds =  7000;


    public AdMobFragment(ItemUser currentUser, boolean jumpFlag) {
        this.currentUser = currentUser;
        this.jumpFlag = jumpFlag;
    }

    public static AdMobFragment newInstance(ItemUser user , boolean jumpFlag) {
        return new AdMobFragment(user, jumpFlag);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ad_mob, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnGetPremium = view.findViewById(R.id.btn_get_premium);
        tvCountdown = view.findViewById(R.id.tv_countdown);
        llCountdownContainer = view.findViewById(R.id.ll_countdown_container);

        btnGetPremium.setOnClickListener(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final Context appContext = getAppContext();
        if (appContext == null)
            return;



        mInterstitialAd = newInterstitialAd(appContext);
        loadInterstitial();

        billingHelper = new BillingHelper(requireActivity(), currentUser);
        billingHelper.init();
    }

    private Context getAppContext() {
        if (getActivity() == null || getActivity().getApplicationContext() == null)
            return null;
        return getActivity().getApplicationContext();
    }



    @Override
    public void onClick(View v) {
        SuperUtil.vibrate(requireContext());
        if (v.getId() == R.id.btn_get_premium) {
            billingHelper.loadAllSkusAndStartBillingFlow();
        }
    }

    private void startCountdown(Context appContext, long countDown) {
        final int countDownInterval = 1000;
        countDownTimer = new CountDownTimer(countDown, countDownInterval) {
            public void onTick(long millisUntilFinished) {
                timerMilliseconds = millisUntilFinished;
                long auxVar = millisUntilFinished / countDownInterval;
                tvCountdown.setText(String.valueOf((int) auxVar));
                isOnTick = true;
                Log.d(TAG, "onTick: isOnTick");
            }

            public void onFinish() {
                if (isVisible()) {
                    tvCountdown.setText("");
                    SuperUtil.hideView(null, llCountdownContainer);
                    isOnTick = false;
                    showInterstitial(appContext);
                    Log.d(TAG, "onFinish: countDownTimer Finished");
                }
            }
        };
        countDownTimer.start();
    }

    private void cancelTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            Log.d(TAG, "cancelTimer: countDownTimer Canceled");
        }
    }

    private InterstitialAd newInterstitialAd(final Context context) {

        InterstitialAd interstitialAd = new InterstitialAd(context);
        interstitialAd.setAdUnitId(getString(R.string.testing_interstitial_ad_unit_id));
        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                Log.d(TAG, "onAdLoaded: done!");
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                if (jumpFlag) {
                    loadLeaderBoardView();
                } else {
                    SuperUtil.removeViewByTag(requireActivity(), TAG, true);
                }
                cancelTimer();
                Log.d(TAG, "onAdFailedToLoad: ErrorCode : " + errorCode);
            }

            @Override
            public void onAdClosed() {
                if (jumpFlag) {
                    loadLeaderBoardView();
                } else {
                    SuperUtil.removeViewByTag(requireActivity(), TAG, true);

                }

                Log.d(TAG, "onAdClosed: Load: " + LeaderBoardFragment.TAG);
            }
        });
        return interstitialAd;
    }

    private void showInterstitial(Context context) {
        // Show the ad if it"s ready. Otherwise toast and reload the ad.
        if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
            Log.d(TAG, "showInterstitial: Showing Ad ready!");
        } else {
            Toast.makeText(context, R.string.ad_not_load, Toast.LENGTH_SHORT).show();
            SuperUtil.removeViewByTag(requireActivity(), TAG, true);
            Log.d(TAG, "showInterstitial: Ad dit not load, is not ready");
        }
    }

    private void loadInterstitial() {
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        Log.d(TAG, "loadInterstitial: Loading Ad...");
    }

    @Override
    public void onPause() {
        cancelTimer();
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!currentUser.isPremium()) {
            Context appContext = getAppContext();
            if (appContext == null) return;

            startCountdown(getAppContext(), timerMilliseconds);
        } else {
            if (jumpFlag) {
                loadLeaderBoardView();
            } else {
                SuperUtil.removeViewByTag(requireActivity(), TAG, true);
            }
        }
    }

    private void loadLeaderBoardView() {
        SuperUtil.removeViewByTag(requireActivity(), TAG, true);
        SuperUtil.loadView(requireActivity(),
                LeaderBoardFragment.newInstance(),
                LeaderBoardFragment.TAG, true);
        cancelTimer();
    }

    public void onBackPressed() {
        if (isVisible()) {
            MessagesHelper.showInfoMessage(
                    requireActivity(), getString(R.string.function_temporarily_disabled));
        }
    }
}