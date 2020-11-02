package com.erg.memorized.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
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
import com.erg.memorized.R;
import com.erg.memorized.helpers.MessagesHelper;
import com.erg.memorized.helpers.SharedPreferencesHelper;
import com.erg.memorized.model.ItemUser;
import com.erg.memorized.model.ItemVerse;
import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Objects;

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
        Log.d(TAG, "vibrate: ");
        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        assert v != null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(VIBRATE_TIME,
                    VibrationEffect.DEFAULT_AMPLITUDE));
            Log.d(TAG, "vibrate: VIBRATE_TIME VibrationEffect :" + VIBRATE_TIME);
        } else {
            v.vibrate(70);
            Log.d(TAG, "vibrate: VIBRATE_TIME :" + 70);
        }
    }

    public static void vibrateMin(Context context) {
        Log.d(TAG, "vibrateMin: ");
        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        assert v != null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(MIN_VIBRATE_TIME,
                    VibrationEffect.DEFAULT_AMPLITUDE));
            Log.d(TAG, "vibrateMin: MIN_VIBRATE_TIME VibrationEffect " + MIN_VIBRATE_TIME);
        } else {
            v.vibrate(70);
            Log.d(TAG, "vibrateMin: MIN_VIBRATE_TIME " + 70);
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


 /*   public static void retrieveCurrentToken() {
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
    }*/

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


    public static void showDialogWithLottie(Activity context, String lottieAssetName,
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

                                try {
                                    boolean isVerified = Objects
                                            .requireNonNull(fAuth.getCurrentUser()).isEmailVerified();
                                    spHelper.setEmailVerifiedStatus(isVerified);
                                } catch (NullPointerException e) {
                                    Log.e(TAG, "logInUser NullPointerException :" +
                                            e.getMessage());
                                }

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


    public static void showView(Animation anim, View view) {
        if (view != null)
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

    public static void showViewWhitStartAnimation(Animation anim, View view) {
        if (view != null)
            if (view.getVisibility() == View.GONE || view.getVisibility() == View.INVISIBLE) {
                if (anim != null)
                    view.startAnimation(anim);
                view.setVisibility(View.VISIBLE);
            }
    }

    public static void hideView(Animation anim, View view) {
        if (view != null)
            if (view.getVisibility() == View.VISIBLE) {
                if (anim != null)
                    view.setAnimation(anim);
                view.setVisibility(View.GONE);
            }
    }

    public static void hideViewInvisibleWay(Animation anim, View view) {
        if (view != null)
            if (view.getVisibility() == View.VISIBLE) {
                if (anim != null)
                    view.setAnimation(anim);
                view.setVisibility(View.INVISIBLE);
            }
    }

    public static ArrayList<HashMap<String, String>> getVersesIntoHasMapList(
            ArrayList<ItemVerse> verses) {
        ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();
        for (ItemVerse verse : verses) {
            arrayList.add(verse.getVerseIntoHasMap());
        }
        return arrayList;
    }

    public static final Comparator<ItemUser> userComparator = (u1, u2) -> {
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

/*    public static int getDisplayHeight(FragmentActivity context) {
        Display display = context.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.y;
    }*/

    public static boolean containsAll(@NotNull ArrayList<ItemVerse> verses,
                                      ArrayList<ItemVerse> container) {
        for (ItemVerse verse : verses) {
            if (!container.contains(verse)) {
                return false;
            }
        }
        return true;
    }
}
