package com.erg.memorized.helpers;

import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

import com.erg.memorized.R;
import com.erg.memorized.fragments.ScorerFragment;
import com.erg.memorized.model.ItemVerse;
import com.erg.memorized.util.Constants;
import com.erg.memorized.util.SuperUtil;
import com.erg.memorized.views.FixedViewPager;
import com.google.android.material.snackbar.Snackbar;

import java.util.Timer;
import java.util.TimerTask;

import static com.erg.memorized.util.Constants.APP_NAME;
import static com.erg.memorized.util.Constants.CHANNEL_NAME;
import static com.erg.memorized.util.Constants.GOOGLE_APP_DETAILS_URL;
import static com.erg.memorized.util.Constants.MARKET_APP_DETAILS_URL;
import static com.erg.memorized.util.Constants.NOTIFY_CHANNEL_ID;

public class MessagesHelper {

    public static void showInfoMessage(Activity context, String msg) {
        if (!context.isFinishing()) {
            Snackbar snackBar = Snackbar.make(context.findViewById(R.id.placeSnackBar)
                    , msg, Snackbar.LENGTH_SHORT);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                snackBar.setBackgroundTint(context.getColor(R.color.colorPrimary));
            } else {
                snackBar.setBackgroundTint(context.getResources().getColor(R.color.colorPrimary));
            }
            snackBar.show();
        }
    }

    public static void showInfoMessageFragment(View rootView, Activity context, String msg) {
        if (!context.isFinishing()) {
            Snackbar snackBar = Snackbar.make(rootView.findViewById(R.id.placeSnackBar)
                    , msg, Snackbar.LENGTH_SHORT);
            snackBar.setBackgroundTint(context.getColor(R.color.colorPrimary));
            snackBar.show();
        }
    }

    public static void showWarningMessageFragment(View rootView, Context context, String msg) {
        Snackbar snackBar = Snackbar.make(rootView.findViewById(R.id.placeSnackBar)
                , msg, Snackbar.LENGTH_SHORT);
        snackBar.setBackgroundTint(context.getColor(R.color.yellow_bg_color));
        snackBar.show();
    }

    public static void showInfoMessageError(Activity context, String msg) {
        if (!context.isFinishing()) {

            Snackbar snackBar = Snackbar.make(context.findViewById(R.id.placeSnackBar)
                    , msg, Snackbar.LENGTH_SHORT);
            snackBar.setBackgroundTint(context.getColor(R.color.red_btn_bg_color));
            snackBar.show();
        }
    }

    public static void showInfoMessageWarning(Activity context, String msg) {
        if (!context.isFinishing()) {
            Snackbar snackBar = Snackbar.make(context.findViewById(R.id.placeSnackBar)
                    , msg, Snackbar.LENGTH_SHORT);
            snackBar.setBackgroundTint(context.getColor(R.color.yellow_bg_color));
            snackBar.setTextColor(context.getColor(R.color.dark_gray_btn_bg_color));
            snackBar.show();
        }
    }

    public static void showPermissionAskInfoMessage(Activity context, String msg) {

        if (!context.isFinishing()) {
            Snackbar snackBar = Snackbar.make(context.findViewById(R.id.placeSnackBar)
                    , msg, Snackbar.LENGTH_SHORT);
            snackBar.setBackgroundTint(context.getColor(R.color.yellow_bg_color));
            snackBar.setTextColor(context.getColor(R.color.dark_gray_btn_bg_color));
            snackBar.setDuration(Snackbar.LENGTH_INDEFINITE);
            snackBar.setAction(context.getString(R.string.got_it), v -> {
                CalendarHelper.requestCalendarReadWritePermission(context);
                snackBar.dismiss();
            });
            snackBar.show();
        }
    }

    public static void showInfoMessageWarningOnDialog(Activity context, String msg,
                                                      View dialogView) {
        if (!context.isFinishing()) {
            Snackbar snackBar = Snackbar.make(dialogView, msg, Snackbar.LENGTH_SHORT);
            snackBar.setBackgroundTint(context.getColor(R.color.yellow_bg_color));
            snackBar.setTextColor(context.getColor(R.color.dark_gray_btn_bg_color));
            snackBar.show();
        }
    }

    public static void showInfoMessageErrorOnDialog(Activity context, String msg,
                                                    View dialogView) {
        if (!context.isFinishing()) {
            Snackbar snackBar = Snackbar.make(dialogView, msg, Snackbar.LENGTH_SHORT);
            snackBar.setBackgroundTint(context.getColor(R.color.red_btn_bg_color));
            snackBar.show();
        }
    }

    public static void showInfoMessageWithDismiss(View rootView, Activity context,
                                                  String msg, String key,
                                                  SharedPreferencesHelper sharedPref) {

        if (!context.isFinishing()) {
            Snackbar snackBar = Snackbar.make(rootView.findViewById(R.id.placeSnackBar),
                    msg, Snackbar.LENGTH_INDEFINITE);
            snackBar.setBackgroundTint(context.getColor(R.color.colorPrimary));
            snackBar.setAction(R.string.dismiss, v -> {
                if (snackBar.isShown()) {
                    snackBar.dismiss();
                    if (key != null) {
                        switch (key) {
                            case Constants.AUDIO_MSG_KEY:
                                sharedPref.setOnAudioMessageViewed(key);
                                break;
                            case Constants.STATUS_BAR_MSG_KEY:
                                sharedPref.setOnStatusBarMessageViewed(key);
                                break;
                        }
                    }
                }
            });
            snackBar.show();
        }
    }

    public static void createNotificationChanel(Context context) {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            String description = context.getString(R.string.channel_description) + APP_NAME;
            NotificationChannel channel = new NotificationChannel(NOTIFY_CHANNEL_ID, CHANNEL_NAME, importance);
            channel.setDescription(description);

            NotificationManager notificationManager =
                    context.getSystemService(NotificationManager.class);

            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);
        }
    }

    public static void showEvaluatorDialogInfoMessage(FragmentActivity context,
                                                      int image,
                                                      String msg,
                                                      String tag) {
        SharedPreferencesHelper spHelper = new SharedPreferencesHelper(context);
        final Dialog dialog = new Dialog(context, R.style.alert_dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        LayoutInflater inflater = context.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_evaluator_info_view,
                null, false);
        ImageView ivIcon = dialogView.findViewById(R.id.iv_ic_dialog);
        TextView tvMsg = dialogView.findViewById(R.id.text_dialog);
        Switch btnSwitch = dialogView.findViewById(R.id.switch_do_not_show_again);

        ivIcon.setImageResource(image);
        tvMsg.setText(msg);
        dialog.setContentView(dialogView);

        /*onClick on dialog cancel button*/
        Button cancelBtn = dialog.findViewById(R.id.btn_ok);
        cancelBtn.setOnClickListener(v -> {
            SuperUtil.vibrate(context);
            Log.d(tag, "showEvaluatorDialogInfoMessage: " +
                    tag + " Switch: " + btnSwitch.isChecked());
            if (btnSwitch.isChecked())
                spHelper.setEvaluatorInfoMessageStatus(tag, true);
            if (dialog.isShowing())
                dialog.dismiss();
        });

        dialog.show();
        Animation animScaleUp = AnimationUtils.loadAnimation(context, R.anim.scale_up);
        dialogView.startAnimation(animScaleUp);

    }

    public static Dialog showTimeFinishedDialog(FragmentActivity context,
                                                ScorerFragment scorerFragment) {
        final Dialog dialog = new Dialog(context, R.style.alert_dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        LayoutInflater inflater = context.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_time_finished_view_lottie,
                null, false);
        dialog.setContentView(dialogView);

        /*onClick on dialog ok button*/
        Button btnOK = dialogView.findViewById(R.id.btn_dialog);
        btnOK.setOnClickListener(v -> {
            SuperUtil.vibrate(context);
            scorerFragment.showScorer();
            if (dialog.isShowing())
                dialog.dismiss();
        });

        dialog.show();
        Animation animScaleUp = AnimationUtils.loadAnimation(context, R.anim.scale_up);
        dialogView.startAnimation(animScaleUp);
        return dialog;
    }

    private static Dialog showRatingDialog(Activity context, Animation anim) {

        final Dialog dialog = new Dialog(context, R.style.alert_dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        LayoutInflater inflater = context.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_rating_view,
                null, false);
        dialog.setContentView(dialogView);
        HorizontalScrollView horizontalScrollView = dialogView
                .findViewById(R.id.horizontal_scroll_btn_container);

        SharedPreferencesHelper spHelper = new SharedPreferencesHelper(context);

        /*onClick on later  button*/
        Button btnCancel = dialogView.findViewById(R.id.rate_dialog_remind_later);
        btnCancel.setOnClickListener(v -> {
            SuperUtil.vibrate(context);

            spHelper.resetRateDialogFlags();

            if (dialog.isShowing())
                dialog.dismiss();
        });

        /*onClick on negative button*/
        Button btnNegative = dialogView.findViewById(R.id.rate_dialog_no);
        btnNegative.setOnClickListener(v -> {
            SuperUtil.vibrate(context);

            spHelper.setAgreeToShowRateDialog(false);
            spHelper.resetRateDialogFlags();

            if (dialog.isShowing())
                dialog.dismiss();
        });

        /*onClick on dialog ok button*/
        Button btnOK = dialogView.findViewById(R.id.rate_dialog_ok);
        btnOK.setOnClickListener(v -> {
            SuperUtil.vibrate(context);
            spHelper.resetRateDialogFlags();

            try {
                context.startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse(MARKET_APP_DETAILS_URL + context.getPackageName())));
            } catch (android.content.ActivityNotFoundException e) {
                context.startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse(GOOGLE_APP_DETAILS_URL + context.getPackageName())));
            }

            spHelper.setAlreadyRatedFlag();

            if (dialog.isShowing())
                dialog.dismiss();
        });

        dialog.show();
        if (anim != null)
            dialogView.startAnimation(anim);

        new Handler().postDelayed(() -> {
            if (dialog.isShowing()) {
                horizontalScrollView.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
            }
        }, 1000);

        spHelper.setLastLaunchRateDialogDate();
        return dialog;
    }

    public static void showRateDialog(Activity activity, Animation anim) {
        if (!activity.isFinishing()) {
            showRatingDialog(activity, anim);
        }
    }

    public static void showPremiumDialog(Activity activity, BillingHelper billingHelper,
                                         boolean isAutoLauncher) {
        if (!activity.isFinishing()) {
            showGetPremiumDialogInfoMessage(activity, billingHelper, isAutoLauncher);
        }
    }

    private static void showGetPremiumDialogInfoMessage(Activity context,
                                                       BillingHelper billingHelper,
                                                        boolean isAutoLauncher) {
        SharedPreferencesHelper spHelper = new SharedPreferencesHelper(context);
        final Dialog dialog = new Dialog(context, R.style.alert_dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        LayoutInflater inflater = context.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_get_premium_view,
                null, false);

        HorizontalScrollView horizontalScrollView = dialogView
                .findViewById(R.id.horizontal_scroll_btn_container);

        dialog.setContentView(dialogView);
        dialog.getWindow().setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        /*onClick No button*/
        Button btnNo = dialogView.findViewById(R.id.premium_dialog_no);
        btnNo.setOnClickListener(v -> {
            SuperUtil.vibrate(context);
            spHelper.setAgreeToShowPremiumDialog(false);
            spHelper.resetPremiumDialogFlags();

            if (dialog.isShowing())
                dialog.dismiss();
        });

        /*onClick later ok button*/
        Button btnLater = dialogView.findViewById(R.id.premium_dialog_remind_later);
        if (isAutoLauncher) {
            SuperUtil.showView(null, btnLater);
            btnLater.setOnClickListener(v -> {
                SuperUtil.vibrate(context);
                spHelper.resetPremiumDialogFlags();

                if (dialog.isShowing())
                    dialog.dismiss();
            });
        } else {
            SuperUtil.hideView(null, btnLater);
        }

        /*onClick dialog ok button*/
        Button btnOk = dialogView.findViewById(R.id.premium_dialog_ok);
        btnOk.setOnClickListener(v -> {
            SuperUtil.vibrate(context);
            if (dialog.isShowing())
                dialog.dismiss();
            spHelper.resetPremiumDialogFlags();
            billingHelper.init();
        });

        dialog.show();
        Animation animScaleUp = AnimationUtils.loadAnimation(context, R.anim.less_scale_up);
        dialogView.startAnimation(animScaleUp);

        new Handler().postDelayed(() -> {
            if (dialog.isShowing()) {
                horizontalScrollView.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
            }
        }, 1000);

        spHelper.setLastPremiumRateDialogDate();

    }


    public static void showTestDialog(FragmentActivity context, ItemVerse verse) {
        final Dialog dialog = new Dialog(context, R.style.alert_dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        LayoutInflater inflater = context.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_do_test_view,
                null, false);

        HorizontalScrollView horizontalScrollView = dialogView
                .findViewById(R.id.horizontal_scroll_btn_container);

        dialog.setContentView(dialogView);
        dialog.getWindow().setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        /*onClick No button*/
        Button btnNo = dialogView.findViewById(R.id.do_test_dialog_no);
        btnNo.setOnClickListener(v -> {
            SuperUtil.vibrate(context);
            if (dialog.isShowing())
                dialog.dismiss();
        });

        /*onClick No button*/
        Button btnOk = dialogView.findViewById(R.id.do_test_dialog_ok);
        btnOk.setOnClickListener(v -> {
            SuperUtil.vibrate(context);
            SuperUtil.loadView(context, ScorerFragment.newInstance(verse),
                    ScorerFragment.TAG, true);
            if (dialog.isShowing())
                dialog.dismiss();
        });

        dialog.show();
        Animation animScaleUp = AnimationUtils.loadAnimation(context, R.anim.less_scale_up);
        dialogView.startAnimation(animScaleUp);

        new Handler().postDelayed(() -> {
            if (dialog.isShowing()) {
                horizontalScrollView.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
            }
        }, 1000);
    }

    private static void setUpAutoSwapViewPage(FixedViewPager fixedViewPager) {

        Timer timer;
        final long DELAY_MS = 7000;//delay in milliseconds before task is to be executed
        final long PERIOD_MS = 7000; // time in milliseconds between successive task executions.

        /*After setting the adapter use the timer */
        final Handler handler = new Handler();
        final Runnable Update = fixedViewPager::moveNext;

        timer = new Timer(); // This will create a new Thread
        timer.schedule(new TimerTask() { // task to be scheduled
            @Override
            public void run() {
                handler.post(Update);
            }
        }, DELAY_MS, PERIOD_MS);
    }

}
