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
import com.erg.memorized.fragments.NewVerseFragment;
import com.erg.memorized.fragments.ScorerFragment;
import com.erg.memorized.model.ItemVerse;
import com.erg.memorized.util.Constants;
import com.erg.memorized.util.SuperUtil;
import com.google.android.material.snackbar.Snackbar;

import static com.erg.memorized.util.Constants.GOOGLE_APP_DETAILS_URL;
import static com.erg.memorized.util.Constants.MARKET_APP_DETAILS_URL;

public class MessagesHelper {

    public static void showInfoMessage(Activity context, String msg) {
        if (!context.isFinishing()) {
            Snackbar snackBar = Snackbar.make(context.findViewById(R.id.placeSnackBar)
                    , msg, Snackbar.LENGTH_SHORT);
            snackBar.setBackgroundTint(context.getColor(R.color.colorPrimary));
            if (!snackBar.isShown())
                snackBar.show();
        }
    }

    public static void showInfoMessageFragment(View rootView, Activity context, String msg) {
        if (!context.isFinishing()) {
            Snackbar snackBar = Snackbar.make(rootView.findViewById(R.id.placeSnackBar)
                    , msg, Snackbar.LENGTH_SHORT);
            snackBar.setBackgroundTint(context.getColor(R.color.colorPrimary));
            if (!snackBar.isShown())
                snackBar.show();
        }
    }

    public static void showInfoMessageError(Activity context, String msg) {
        if (!context.isFinishing()) {

            Snackbar snackBar = Snackbar.make(context.findViewById(R.id.placeSnackBar)
                    , msg, Snackbar.LENGTH_SHORT);
            snackBar.setBackgroundTint(context.getColor(R.color.red_btn_bg_color));
            if (!snackBar.isShown())
                snackBar.show();
        }
    }

    public static void showInfoMessageWarning(Activity context, String msg) {
        if (!context.isFinishing()) {
            Snackbar snackBar = Snackbar.make(context.findViewById(R.id.placeSnackBar)
                    , msg, Snackbar.LENGTH_SHORT);
            snackBar.setBackgroundTint(context.getColor(R.color.yellow_bg_color));
            snackBar.setTextColor(context.getColor(R.color.dark_gray_btn_bg_color));
            if (!snackBar.isShown())
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
            if (!snackBar.isShown())
                snackBar.show();
        }
    }

    public static void showInfoMessageWarningOnDialog(Activity context, String msg,
                                                      View dialogView) {
        if (!context.isFinishing()) {
            Snackbar snackBar = Snackbar.make(dialogView, msg, Snackbar.LENGTH_SHORT);
            snackBar.setBackgroundTint(context.getColor(R.color.yellow_bg_color));
            snackBar.setTextColor(context.getColor(R.color.dark_gray_btn_bg_color));
            if (!snackBar.isShown())
                snackBar.show();
        }
    }

    public static void showInfoMessageErrorOnDialog(Activity context, String msg,
                                                    View dialogView) {
        if (!context.isFinishing()) {
            Snackbar snackBar = Snackbar.make(dialogView, msg, Snackbar.LENGTH_SHORT);
            snackBar.setBackgroundTint(context.getColor(R.color.red_btn_bg_color));
            if (!snackBar.isShown())
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
            if (!snackBar.isShown())
                snackBar.show();
        }
    }

    public static void showEvaluatorDialogInfoMessage(FragmentActivity context, ViewGroup container,
                                                      int image,
                                                      String msg,
                                                      String tag) {
        SharedPreferencesHelper spHelper = new SharedPreferencesHelper(context);
        final Dialog dialog = new Dialog(context, R.style.alert_dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        LayoutInflater inflater = context.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_evaluator_info_view,
                container, false);
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

    public static Dialog showTimeFinishedDialog(FragmentActivity context, ViewGroup container,
                                                ScorerFragment scorerFragment) {
        final Dialog dialog = new Dialog(context, R.style.alert_dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        LayoutInflater inflater = context.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_time_finished_view_lottie,
                container, false);
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
        @SuppressLint("InflateParams")
        View dialogView = inflater.inflate(R.layout.dialog_rating_view,
                null, false);
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


        dialog.setContentView(dialogView);
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

    public static void showTestDialog(FragmentActivity context, ViewGroup container,
                                      Animation anim,
                                      ItemVerse verse) {
        final Dialog dialog = new Dialog(context, R.style.alert_dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        LayoutInflater inflater = context.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_do_test_view,
                container, false);

        HorizontalScrollView horizontalScrollView = dialogView
                .findViewById(R.id.horizontal_scroll_btn_container);

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


        dialog.setContentView(dialogView);
        dialog.show();
        dialogView.startAnimation(anim);

        new Handler().postDelayed(() -> {
            if (dialog.isShowing()) {
                horizontalScrollView.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
            }
        }, 1000);
    }

    public static void showLivingAlertDialog(FragmentActivity context, ViewGroup container,
                                             Animation anim) {
        final Dialog dialog = new Dialog(context, R.style.alert_dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        LayoutInflater inflater = context.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_leaving_alert_view,
                container, false);

        /*onClick on dialog cancel button*/
        Button cancelBtn = dialogView.findViewById(R.id.cancel_dialog_button);
        cancelBtn.setOnClickListener(v -> {
            SuperUtil.vibrate(context);

            if (dialog.isShowing())
                dialog.dismiss();
        });

        /*onClick on dialog leave button*/
        Button editBtn = dialogView.findViewById(R.id.leave_dialog_button);
        editBtn.setOnClickListener(v -> {
            SuperUtil.vibrate(context);
            context.onBackPressed();

            if (dialog.isShowing())
                dialog.dismiss();
        });

        dialog.setContentView(dialogView);
        dialog.show();
        dialogView.startAnimation(anim);
    }

    public static void showDialogtDialogSplit(Activity context, ViewGroup container,
                                              Animation anim) {
        Dialog dialog = new Dialog(context, R.style.alert_dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        LayoutInflater inflater = context.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_split_verse_info_lottie,
                container, false);

        SharedPreferencesHelper spHelper = new SharedPreferencesHelper(context);
        Button btn = dialogView.findViewById(R.id.btn_dialog);
        btn.setOnClickListener(v -> {
            SuperUtil.vibrate(context);
            spHelper.setDialogSplitInfoStatus(true);
            dialog.dismiss();
        });

        dialog.setContentView(dialogView);
        dialog.show();
        dialogView.startAnimation(anim);
    }

    public static void showDialogAskToDoTest(FragmentActivity context, ViewGroup container,
                                             Animation anim,
                                             ItemVerse verse) {
        Dialog dialog = new Dialog(context, R.style.alert_dialog);
        dialog.setCancelable(false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater inflater = context.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_ask_to_do_test_view,
                container, false);
        HorizontalScrollView horizontalScrollView = dialogView
                .findViewById(R.id.horizontal_scroll_btn_container);

        SharedPreferencesHelper spHelper = new SharedPreferencesHelper(context);
        Button btnCancel = dialogView.findViewById(R.id.do_test_dialog_no);
        btnCancel.setOnClickListener(v -> {
            SuperUtil.vibrate(context);
            spHelper.setDialogAskToDoTestStatus(true);
            if (dialog.isShowing())
                dialog.dismiss();
        });

        Button btnOk = dialogView.findViewById(R.id.do_test_dialog_ok);
        btnOk.setOnClickListener(v -> {
            SuperUtil.vibrate(context);
            spHelper.setDialogSplitInfoStatus(true);
            SuperUtil.loadView(context, ScorerFragment.newInstance(verse),
                    ScorerFragment.TAG, true);
            if (dialog.isShowing())
                dialog.dismiss();
        });

        dialog.setContentView(dialogView);
        dialog.show();
        dialogView.startAnimation(anim);
        new Handler().postDelayed(() -> {
            if (dialog.isShowing()) {
                horizontalScrollView.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
            }
        }, 900);
    }

    public static void showSaveVerseDialog(FragmentActivity context,
                                           ViewGroup container, Animation anim,
                                           ItemVerse verse) {
        final Dialog dialog = new Dialog(context, R.style.alert_dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        LayoutInflater inflater = context.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_save_verse_view,
                container, false);
        TextView msg = dialogView.findViewById(R.id.text_dialog);
        msg.setText(R.string.msg_edit_to_save);

        /*onClick on dialog cancel button*/
        Button cancelBtn = dialogView.findViewById(R.id.cancel_dialog_button);
        cancelBtn.setOnClickListener(v -> {
            SuperUtil.vibrate(context);
            if (dialog.isShowing())
                dialog.dismiss();
        });

        /*onClick on dialog delete button*/
        Button editBtn = dialogView.findViewById(R.id.edit_save_dialog_button);
        editBtn.setOnClickListener(v -> {
            SuperUtil.vibrate(context);
            ItemVerse currentItemVerse = new ItemVerse(verse.getTitle(), verse.getVerseText());
            long idVerse = System.currentTimeMillis();
            currentItemVerse.setId(idVerse);

            SuperUtil.loadView(
                    context,
                    NewVerseFragment.newInstance(currentItemVerse, true),
                    NewVerseFragment.TAG,
                    true
            );

            if (dialog.isShowing())
                dialog.dismiss();

        });

        dialog.setContentView(dialogView);
        dialog.show();
        dialogView.startAnimation(anim);
    }
}
