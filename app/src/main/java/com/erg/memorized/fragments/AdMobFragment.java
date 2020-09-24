package com.erg.memorized.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
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
import com.erg.memorized.model.ItemUser;
import com.erg.memorized.util.SuperUtil;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

public class AdMobFragment extends Fragment {

    public static final String TAG = "AdMobFragment";

    private CountDownTimer countDownTimer;
    private boolean isOnTick = false;

    private Button btnGetPremium;
    private InterstitialAd mInterstitialAd;
    private TextView tvCountdown;
    private LinearLayout llCountdownContainer;
    private ItemUser currentUser;
    private BillingHelper billingHelper;


    public AdMobFragment(ItemUser currentUser) {
        this.currentUser = currentUser;
    }

    public static AdMobFragment newInstance(ItemUser user) {
        return new AdMobFragment(user);
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
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final Context appContext = getAppContext();
        if (appContext == null)
            return;

        billingHelper = new BillingHelper(requireActivity(), currentUser);

        btnGetPremium.setOnClickListener(view -> {
            SuperUtil.vibrate(requireContext());
            billingHelper.init();
        });

        mInterstitialAd = newInterstitialAd(appContext);
        loadInterstitial();
    }

    private Context getAppContext() {
        if (getActivity() == null || getActivity().getApplicationContext() == null)
            return null;
        return getActivity().getApplicationContext();
    }

    private void startCountdown(Context appContext) {
        final int countDownInterval = 1000;
        final int countDown = 7000;
        countDownTimer = new CountDownTimer(countDown, countDownInterval) {
            public void onTick(long millisUntilFinished) {
                long auxVar = millisUntilFinished / countDownInterval;
                tvCountdown.setText(String.valueOf((int) auxVar));
                isOnTick = true;
            }

            public void onFinish() {
                if (isVisible()) {
                    tvCountdown.setText("");
                    SuperUtil.hideView(null, llCountdownContainer);
                    isOnTick = false;
                    showInterstitial(appContext);
                }
            }
        };
        countDownTimer.start();
    }

    private void cancelTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    private InterstitialAd newInterstitialAd(final Context context) {
        InterstitialAd interstitialAd = new InterstitialAd(context);
        interstitialAd.setAdUnitId(getString(R.string.testing_interstitial_ad_unit_id));
        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                SuperUtil.removeViewByTag(requireActivity(),TAG, false);
                SuperUtil.loadView(requireActivity(),
                    LeaderBoardFragment.newInstance(),
                    LeaderBoardFragment.TAG, true);
                cancelTimer();
            }

            @Override
            public void onAdClosed() {
                SuperUtil.removeViewByTag(requireActivity(),TAG, true);
                SuperUtil.loadView(requireActivity(),
                    LeaderBoardFragment.newInstance(),
                    LeaderBoardFragment.TAG, true);
                cancelTimer();
            }
        });
        return interstitialAd;
    }

    private void showInterstitial(Context context) {
        // Show the ad if it"s ready. Otherwise toast and reload the ad.
        if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            Toast.makeText(context, R.string.ad_not_load, Toast.LENGTH_SHORT).show();
            SuperUtil.removeViewByTag(requireActivity(), TAG, true);
        }
    }

    private void loadInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .setRequestAgent("android_studio:ad_template").build();
        mInterstitialAd.loadAd(adRequest);
    }

    @Override
    public void onPause() {
        super.onPause();
        cancelTimer();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!currentUser.isPremium()) {
            Context appContext = getAppContext();
            if (appContext == null) return;
            startCountdown(getAppContext());
        }
    }
}