package com.erg.memorized.helpers;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
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
import android.widget.TextView;

import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.FragmentActivity;

import com.erg.memorized.R;
import com.erg.memorized.fragments.ScorerFragment;
import com.erg.memorized.model.ItemVerse;
import com.erg.memorized.util.Constants;
import com.erg.memorized.util.SuperUtil;
import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;

import static com.erg.memorized.util.Constants.GOOGLE_APP_DETAILS_URL;
import static com.erg.memorized.util.Constants.MARKET_APP_DETAILS_URL;

public class MessagesHelper {

    public static void showInfoMessage(Activity context, String msg) {
        if (!context.isFinishing()) {
            Snackbar snackBar = Snackbar.make(context.findViewById(R.id.placeSnackBar)
                    , msg, Snackbar.LENGTH_SHORT);
            snackBar.setBackgroundTint(context.getColor(R.color.colorPrimary));
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
                SuperUtil.vibrateMin(context);
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
            snackBar.setAction(R.string.ok, v -> {
                SuperUtil.vibrateMin(context);
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

    public static void showEvaluatorDialogInfoMessage(FragmentActivity context,
                                                      int image,
                                                      String msg,
                                                      String tag) {
        SharedPreferencesHelper spHelper = new SharedPreferencesHelper(context);
        final Dialog dialog = new Dialog(context, R.style.alert_dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        LayoutInflater inflater = context.getLayoutInflater();
        @SuppressLint("InflateParams") View dialogView = inflater.inflate(R.layout.dialog_evaluator_info_view,
                null, false);
        ImageView ivIcon = dialogView.findViewById(R.id.iv_ic_dialog);
        TextView tvMsg = dialogView.findViewById(R.id.text_dialog);
        SwitchCompat btnSwitch = dialogView.findViewById(R.id.switch_do_not_show_again);

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
        @SuppressLint("InflateParams") View dialogView = inflater.inflate(R.layout.dialog_time_finished_view_lottie,
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


    private static void showRatingDialog(Activity context, Animation anim) {

        final Dialog dialog = new Dialog(context, R.style.alert_dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        LayoutInflater inflater = context.getLayoutInflater();
        @SuppressLint("InflateParams") View dialogView = inflater.inflate(R.layout.dialog_rating_view,
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
    }

    public static void showRateDialog(Activity activity, Animation anim) {
        if (!activity.isFinishing()) {
            showRatingDialog(activity, anim);
        }
    }

    public static void showTestDialog(FragmentActivity context, ItemVerse verse) {
        final Dialog dialog = new Dialog(context, R.style.alert_dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        LayoutInflater inflater = context.getLayoutInflater();
        @SuppressLint("InflateParams") View dialogView = inflater.inflate(R.layout.dialog_do_test_view,
                null, false);

        HorizontalScrollView horizontalScrollView = dialogView
                .findViewById(R.id.horizontal_scroll_btn_container);

        dialog.setContentView(dialogView);
        Objects.requireNonNull(dialog.getWindow()).setLayout(
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

    public static void showLivingAlertDialog(FragmentActivity context) {
        Animation animScaleUp = AnimationUtils.loadAnimation(context, R.anim.less_scale_up);
        final Dialog dialog = new Dialog(context, R.style.alert_dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        LayoutInflater inflater = context.getLayoutInflater();
        @SuppressLint("InflateParams")
        View dialogView = inflater.inflate(R.layout.dialog_leaving_alert_view,
                null, false);
        dialog.setContentView(dialogView);

        /*onClick on dialog cancel button*/
        Button cancelBtn = dialog.findViewById(R.id.cancel_dialog_button);
        cancelBtn.setOnClickListener(v -> {
            SuperUtil.vibrate(context);

            if (dialog.isShowing())
                dialog.dismiss();
        });

        /*onClick on dialog leave button*/
        Button editBtn = dialog.findViewById(R.id.leave_dialog_button);
        editBtn.setOnClickListener(v -> {
            SuperUtil.vibrate(context);
            context.onBackPressed();

            if (dialog.isShowing())
                dialog.dismiss();
        });
        dialog.show();
        dialogView.startAnimation(animScaleUp);
    }
}
