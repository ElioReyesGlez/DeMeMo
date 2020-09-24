package com.erg.memorized.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.airbnb.lottie.LottieAnimationView;
import com.erg.memorized.MainActivity;
import com.erg.memorized.R;
import com.erg.memorized.helpers.MessagesHelper;
import com.erg.memorized.helpers.SharedPreferencesHelper;
import com.erg.memorized.model.ItemUser;
import com.erg.memorized.model.ItemVerse;
import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;

import io.realm.RealmList;

import static com.erg.memorized.util.Constants.AT;
import static com.erg.memorized.util.Constants.DOT;
import static com.erg.memorized.util.Constants.LOW_LINE;
import static com.erg.memorized.util.Constants.MIN_VIBRATE_TIME;
import static com.erg.memorized.util.Constants.VIBRATE_TIME;

public class SuperUtil {

    private static final String TAG = "SuperUtil";

    public SuperUtil() {
    }

    public static void vibrate(Context context) {
        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        assert v != null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(VIBRATE_TIME,
                    VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            v.vibrate(VIBRATE_TIME);
        }
    }

    public static void vibrateMin(Context context) {
        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        assert v != null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(MIN_VIBRATE_TIME,
                    VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            v.vibrate(VIBRATE_TIME);
        }
    }


    public static void loadView(FragmentActivity context, Fragment fragment,
                                String tag, boolean addToBackStack) {
        FragmentManager manager = context.getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.main_fragment_layout, fragment, tag);
        if (addToBackStack)
            transaction.addToBackStack(tag);
        transaction.commit();
    }

    public static void refreshFragment(FragmentActivity context, String tag) {
        FragmentManager manager = context.getSupportFragmentManager();
        Fragment frg = manager.findFragmentByTag(tag);

        if (frg != null) {
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.detach(frg);
            transaction.attach(frg);
            transaction.commit();
        }
    }

    public static void removeViewByTag(FragmentActivity context, String tag, boolean popBackStack) {
        FragmentManager manager = context.getSupportFragmentManager();
        Fragment fragment = manager.findFragmentByTag(tag);
        if (fragment != null) {
            FragmentTransaction trans = manager.beginTransaction();
            trans.remove(fragment);
            trans.commit();
            if (popBackStack)
                manager.popBackStack();
        }
    }

    public static boolean isFragmentOnBackStack(FragmentActivity context, String tag) {
        FragmentManager manager = context.getSupportFragmentManager();
        Fragment fragment = manager.findFragmentByTag(tag);
        if (fragment != null)
            return manager.getFragments().contains(fragment);
        else
            return false;
    }

    public static String getUserFromEmail(String email) {
        String aux = email;
        if (email.contains(AT)) {
            aux = email.replace(AT, LOW_LINE);
        }
        if (aux.contains(DOT)) {
            email = aux.replace(DOT, LOW_LINE);
        }
        return email;
    }


    public static void retrieveCurrentToken() {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w(MainActivity.TAG, "getInstanceId failed", task.getException());
                        return;
                    }

                    // Get new Instance ID token
                    String token = task.getResult().getToken();
                    Log.d(MainActivity.TAG, "onComplete: Token: " + token);
                    // Log and toast
//                        String msg = context.getString(R.string.msg_token_fmt, token);
//                        Log.d(MainActivity.TAG, msg);
//                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                });
    }

    public String encodeBase64ToString(Bitmap bitmap, String extension) {

        String result = null;

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        if (extension.equalsIgnoreCase("png"))
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        else
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);

        byte[] byteArray = byteArrayOutputStream.toByteArray();

        try {
            result = Base64.encodeToString(byteArray, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("SuperUtil", "encodeBase64ToString: " + e.getMessage());
        } catch (OutOfMemoryError e) {
            Log.d("SuperUtil", "encodeBase64ToString: " + e.getMessage());

            byteArrayOutputStream = new ByteArrayOutputStream();

            if (extension.equalsIgnoreCase("png"))
                bitmap.compress(Bitmap.CompressFormat.PNG, 50, byteArrayOutputStream);
            else
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);

            byteArray = byteArrayOutputStream.toByteArray();
            result = Base64.encodeToString(byteArray, Base64.DEFAULT);
        }
        return result;
    }

    public static String encodeToBase64(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        String temp = null;
        try {
            System.gc();
            temp = Base64.encodeToString(b, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            baos = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 50, baos);
            b = baos.toByteArray();
            temp = Base64.encodeToString(b, Base64.DEFAULT);
            Log.e("EWN", "Out of memory error catched");
        }
        return temp;
    }

    public static Bitmap decodeBase64ToBitmap(String encodedImage) {
        byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }

    public static String getExtensionFromUri(Uri uri) {
        return MimeTypeMap.getFileExtensionFromUrl(uri.getPath());
    }

    public static Dialog showProgressDialog(Activity context, ViewGroup container) {
        Dialog dialog = new Dialog(context, R.style.alert_dialog);
//        dialog.setCancelable(false);
        LayoutInflater inflater = context.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.progress, container, false);
        Animation anim = AnimationUtils.loadAnimation(context, R.anim.scale_up);
        dialogView.setAnimation(anim);
        dialog.setContentView(dialogView);
        dialog.show();
        return dialog;
    }


    public static Dialog showDialogWithLottie(Activity context, String lottieAssetName,
                                              String msg, String btnMsg, ViewGroup container,
                                              SharedPreferencesHelper spHelper) {
        Dialog dialog = new Dialog(context, R.style.alert_dialog);
        dialog.setCancelable(false);
        LayoutInflater inflater = context.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_lottie, container, false);

        LottieAnimationView lottie = dialogView.findViewById(R.id.lottie_anim);
        lottie.setAnimation(lottieAssetName);

        TextView tvMsg = dialogView.findViewById(R.id.tv_msg_dialog);
        tvMsg.setText(msg);

        Button btn = dialogView.findViewById(R.id.btn_dialog);
        btn.setText(btnMsg);
        btn.setOnClickListener(v -> {
            vibrate(context);
            spHelper.setDialogSplitInfoStatus(true);
            dialog.dismiss();
        });

        Animation anim = AnimationUtils.loadAnimation(context, R.anim.less_scale_up);
        dialogView.setAnimation(anim);
        dialog.setContentView(dialogView);
        dialog.show();
        return dialog;
    }

    public static void showMeoBottomBar(Activity context, Animation anim) {
        MeowBottomNavigation meoBottomBar = context.findViewById(R.id.meow_bottom_navigation);
        if (meoBottomBar != null) {
            if (meoBottomBar.getVisibility() == View.GONE) {
                meoBottomBar.setVisibility(View.VISIBLE);
                meoBottomBar.startAnimation(anim);
            }
        }
    }

    public static void hideMeoBottomBar(Activity context, Animation anim) {
        MeowBottomNavigation meoBottomBar = context.findViewById(R.id.meow_bottom_navigation);
        if (meoBottomBar != null) {
            if (meoBottomBar.getVisibility() == View.VISIBLE) {
                meoBottomBar.setVisibility(View.GONE);
                meoBottomBar.startAnimation(anim);
            }
        }
    }

    public static void logInUser(Activity context, ItemUser currentUser) {

        SharedPreferencesHelper spHelper = new SharedPreferencesHelper(context);
        if (spHelper.getUserLoginStatus()) {
            if (currentUser != null) {
                FirebaseAuth fAuth = FirebaseAuth.getInstance();
                fAuth.signInWithEmailAndPassword(currentUser.getEmail(), currentUser.getPass())
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                spHelper.setUserLoginState(true);
                                boolean isVerified = fAuth.getCurrentUser().isEmailVerified();
                                spHelper.setEmailVerifiedStatus(isVerified);

                                Log.d(TAG, "logInUser: User is Logged!!");
                            } else {
                                if (task.getException() instanceof FirebaseNetworkException) {
                                    if (!context.isFinishing())
                                        MessagesHelper.showInfoMessageWarning(context,
                                                context.getString(R.string.network_error));
                                    Log.d("signInUser", "" + task.getException().getMessage());
                                }
                            }
                        });
            }
        }
    }

    public static void signOutUser() {
        FirebaseAuth.getInstance().signOut();
    }


    /* Interval between today in exactly one month after */
    public static long getMonthlyDuration() {
        // get today's date
        Calendar cal = Calendar.getInstance();
        // get current month
        int currentMonth = cal.get(Calendar.MONTH);

        // move month ahead
        currentMonth++;

        // check if has not exceeded threshold of december
        if (currentMonth > Calendar.DECEMBER) {
            // alright, reset month to jan and forward year by 1 e.g fro 2013 to 2014
            currentMonth = Calendar.JANUARY;
            // Move year ahead as well
            cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) + 1);
        }

        // reset calendar to next month
        cal.set(Calendar.MONTH, currentMonth);
        // get the maximum possible days in this month
        int maximumDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

        // set the calendar to maximum day (e.g in case of fEB 28th, or leap 29th)
        cal.set(Calendar.DAY_OF_MONTH, maximumDay);
        long thenTime = cal.getTimeInMillis(); // this is time one month ahead

        return (thenTime); // this is what you set as trigger point time i.e one month after
    }


    public static void showView(Animation anim, View view) {
        if (view.getVisibility() == View.GONE || view.getVisibility() == View.INVISIBLE) {
            if (anim != null)
                view.setAnimation(anim);
            view.setVisibility(View.VISIBLE);
        } else {
            if (anim != null) {
                view.startAnimation(anim);
            }
        }
    }

    public static void hideView(Animation anim, View view) {
        if (view.getVisibility() == View.VISIBLE) {
            if (anim != null)
                view.setAnimation(anim);
            view.setVisibility(View.GONE);
        }
    }

    public static void hideViewInvisibleWay(Animation anim, View view) {
        if (view.getVisibility() == View.VISIBLE) {
            if (anim != null)
                view.setAnimation(anim);
            view.setVisibility(View.INVISIBLE);
        }
    }

    public static RealmList<HashMap<String, String>> getVersesIntoRealmList(
            ArrayList<HashMap<String, String>> verses) {
        RealmList<HashMap<String, String>> realmList = new RealmList<>();
        realmList.addAll(verses);
        return realmList;
    }

    public static ArrayList<HashMap<String, String>> getVersesIntoHasMapList(
            ArrayList<ItemVerse> verses) {
        ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();
        for (ItemVerse verse : verses) {
            arrayList.add(verse.getVerseIntoHasMap());
        }
        return arrayList;
    }

    public static Comparator<ItemUser> userComparator = (u1, u2) -> {
        float u1Score = u1.getScore();
        float u2Score = u2.getScore();

        //ascending order
//        return Float.compare(u1Score, u2Score);
        //descending order
        return Float.compare(u2Score, u1Score);
    };

    public static int getDisplayWidth(FragmentActivity context) {
        Display display = context.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.x;
    }

    public static int getDisplayHeight(FragmentActivity context) {
        Display display = context.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.y;
    }

    /*
    *         verseHasMap.put(VERSE_COLUMN_ID, String.valueOf(getId()));
        verseHasMap.put(VERSE_COLUMN_TITLE, getTitle());
        verseHasMap.put(VERSE_COLUMN_VERSE, getVerseText());
        verseHasMap.put(VERSE_COLUMN_MEMORIZING_STATUS, String.valueOf(isMemorized()));
        verseHasMap.put(VERSE_ALARM_STATUS, String.valueOf(isOnAlarm()));
        verseHasMap.put(VERSE_COLUMN_DATE_ALARM, String.valueOf(getDateAlarm()));
        verseHasMap.put(VERSE_COLUMN_UNTIL_DATE_ALARM, String.valueOf(getUntilAlarm()));
        verseHasMap.put(VERSE_COLUMN_REPEATING_STATUS, String.valueOf(getRepeatingAlarmStatus()));
    *
    * */

    public static boolean containsAll(@NotNull ArrayList<ItemVerse> verses,
                                      ArrayList<ItemVerse> container) {
        for (ItemVerse verse : verses) {
            if (!container.contains(verse)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isTouchInsideOfView(View view, Point touchPosition) {
        Rect rScroll = new Rect();
        view.getGlobalVisibleRect(rScroll);
        return isTouchInsideOfRect(touchPosition, rScroll);
    }

    public static boolean isTouchInsideOfRect(Point touchPosition, Rect rScroll) {
        return touchPosition.x > rScroll.left && touchPosition.x < rScroll.right //within x axis / width
                && touchPosition.y > rScroll.top && touchPosition.y < rScroll.bottom; //withing y axis / height
    }
}
