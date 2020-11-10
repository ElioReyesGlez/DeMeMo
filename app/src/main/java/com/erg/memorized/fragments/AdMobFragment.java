package com.erg.memorized.fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
import com.erg.memorized.helpers.SharedPreferencesHelper;
import com.erg.memorized.helpers.TimeHelper;
import com.erg.memorized.model.ItemUser;
import com.erg.memorized.util.Constants;
import com.erg.memorized.util.SuperUtil;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.LoadAdError;

public class AdMobFragment extends Fragment implements View.OnClickListener {

    public static final String TAG = "AdMobFragment";

    private View rootView;
    private CountDownTimer countDownTimer;
    private InterstitialAd mInterstitialAd;
    private TextView tvCountdown;
    private LinearLayout llCountdownContainer;
    private final ItemUser currentUser;
    private BillingHelper billingHelper;
    private final boolean jumpFlag;
    private long timerMilliseconds = 7000;

    public AdMobFragment(ItemUser currentUser, boolean jumpFlag) {
        this.currentUser = currentUser;
        this.jumpFlag = jumpFlag;
    }

    public static AdMobFragment newInstance(ItemUser user, boolean jumpFlag) {
        return new AdMobFragment(user, jumpFlag);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: starts");
        rootView = inflater.inflate(R.layout.fragment_ad_mob, container, false);

        setUpView();
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mInterstitialAd = newInterstitialAd(requireActivity());
        loadInterstitial();

        billingHelper = new BillingHelper(requireActivity(), currentUser);
        billingHelper.init();
    }

/*    private Context getAppContext() {
        if (getActivity() == null || getActivity().getApplicationContext() == null)
            return null;
        return getActivity().getApplicationContext();
    }*/

    private void setUpView() {
        Button btnGetPremium = rootView.findViewById(R.id.btn_get_premium);
        tvCountdown = rootView.findViewById(R.id.tv_countdown);
        TextView tvMission = rootView.findViewById(R.id.tv_mission_description);
        llCountdownContainer = rootView.findViewById(R.id.ll_countdown_container);

        btnGetPremium.setOnClickListener(this);
        tvMission.setOnClickListener(this);

        Log.d(TAG, "setUpView: Done!");
    }


    @Override
    public void onClick(View v) {
        SuperUtil.vibrate(requireContext());
        switch (v.getId()) {
            case R.id.btn_get_premium:
                billingHelper.loadAllSkusAndStartBillingFlow();
                break;
            case R.id.tv_mission_description:
                linkToDeMeMoMission();
                break;
        }
    }

    private void linkToDeMeMoMission() {
        if (isVisible()) {
            cancelTimer();
            showMissionDialog();
        }
    }

    public void showMissionDialog() {
        final Dialog dialog = new Dialog(requireContext(), R.style.alert_dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        LayoutInflater inflater = getLayoutInflater();
        @SuppressLint("InflateParams")
        View dialogView = inflater.inflate(R.layout.dialog_mission_view,
                null, false);
        dialog.setContentView(dialogView);

        /*onClick on dialog ok button*/
        Button btnOK = dialogView.findViewById(R.id.ok);
        btnOK.setOnClickListener(v -> {
            SuperUtil.vibrate(requireContext());
            startCountdown(requireContext(), timerMilliseconds);
            if (dialog.isShowing())
                dialog.dismiss();
        });

        dialog.show();
        Animation animScaleUp = AnimationUtils.loadAnimation(requireContext(), R.anim.less_scale_up);
        dialogView.startAnimation(animScaleUp);
    }

    private void startCountdown(Context appContext, long countDown) {
        final int countDownInterval = 1000;
        countDownTimer = new CountDownTimer(countDown, countDownInterval) {
            public void onTick(long millisUntilFinished) {
                timerMilliseconds = millisUntilFinished;
                long auxVar = millisUntilFinished / countDownInterval;
                tvCountdown.setText(String.valueOf((int) auxVar));
                Log.d(TAG, "onTick: isOnTick");
            }

            public void onFinish() {
                if (isVisible()) {
                    tvCountdown.setText("");
                    SuperUtil.hideView(null, llCountdownContainer);
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
        interstitialAd.setAdUnitId(Constants.interstitial_ad_unit_id);
        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                Log.d(TAG, "onAdLoaded: done!");
            }

            @Override
            public void onAdFailedToLoad(LoadAdError loadAdError) {
                if (jumpFlag) {
                    loadLeaderBoardView();
                } else {
                    SuperUtil.removeViewByTag(requireActivity(), TAG, true);
                }
                cancelTimer();
                Log.d(TAG, "onAdFailedToLoad: ErrorCode : " + loadAdError.toString());
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
            startCountdown(requireContext(), timerMilliseconds);
        } else {
            if (jumpFlag) {
                loadLeaderBoardView();
            } else {
                SuperUtil.removeViewByTag(requireActivity(), TAG, true);
            }
        }

        setUpAppLanguage();
    }

    private void setUpAppLanguage() {
        SharedPreferencesHelper spHelper = new SharedPreferencesHelper(requireContext());
        String[] arrayLanguagesCodes = getResources().getStringArray(R.array.languages_codes);
        int langPos = spHelper.getLanguagePos();
        TimeHelper.setLocale(requireActivity(), arrayLanguagesCodes[langPos]);
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