package com.erg.memorized;

import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.erg.memorized.fragments.AboutFragment;
import com.erg.memorized.fragments.AdMobFragment;
import com.erg.memorized.fragments.GeneralSettingsFragment;
import com.erg.memorized.fragments.HomeFragment;
import com.erg.memorized.fragments.LeaderBoardFragment;
import com.erg.memorized.fragments.MemorizingFragment;
import com.erg.memorized.fragments.NewVerseFragment;
import com.erg.memorized.fragments.ScorerFragment;
import com.erg.memorized.fragments.SettingsFragment;
import com.erg.memorized.fragments.SignUpFragment;
import com.erg.memorized.fragments.SplitTextFragment;
import com.erg.memorized.fragments.SupportFragment;
import com.erg.memorized.fragments.UserInfoFragment;
import com.erg.memorized.fragments.VersesFragment;
import com.erg.memorized.fragments.scorer.ResultFragment;
import com.erg.memorized.helpers.MessagesHelper;
import com.erg.memorized.helpers.RealmHelper;
import com.erg.memorized.helpers.SharedPreferencesHelper;
import com.erg.memorized.helpers.TimeHelper;
import com.erg.memorized.model.ItemUser;
import com.erg.memorized.util.Constants;
import com.erg.memorized.util.SuperUtil;
import com.etebarian.meowbottomnavigation.MeowBottomNavigation;

import static com.erg.memorized.util.Constants.MENU_HOME;
import static com.erg.memorized.util.Constants.MIN_DAYS_UNTIL_PROMPT;
import static com.erg.memorized.util.Constants.MIN_LAUNCHES_UNTIL_PROMPT;


public class MainActivity extends FragmentActivity {

    public static String TAG = "MainActivity";

    private MeowBottomNavigation bottomNavigation;
    private FrameLayout frameLayoutMain;
    private Fragment actualFragment;
    private ProgressBar pgsBar;
    private boolean maybeWantToExit = false;
    private boolean maybeWantToLeaveScoreSection = false;
    private Animation animScaleUp, animScaleUpPlus;
    private SharedPreferencesHelper spHelper;
    private RealmHelper realmHelper;
    private ItemUser currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setUpViews(savedInstanceState);
        setUpMeowBottomBar();

        RealmHelper.startRealm(getApplicationContext());
        spHelper = new SharedPreferencesHelper(this);
        realmHelper = new RealmHelper(this);
        currentUser = realmHelper.getUser();

        animScaleUp = AnimationUtils.loadAnimation(this, R.anim.scale_up);
        animScaleUpPlus = AnimationUtils.loadAnimation(this, R.anim.less_scale_up);

        if (currentUser != null) {
            SuperUtil.logInUser(this, currentUser);
        }

        SuperUtil.retrieveCurrentToken();

        spHelper.setRateLaunchTimes(spHelper.getRateLaunchesTimes() + 1);
        spHelper.setPremiumLaunchTimes(spHelper.getPremiumLaunchesTimes() + 1);

        if (spHelper.getIsAgreeShowRateDialog() && !spHelper.isAlreadyRated()) {
            long daySinceLastLaunch = TimeHelper
                    .getDifferenceInDays(spHelper.getLastLaunchRateDialogDate(),
                            System.currentTimeMillis());

            if (spHelper.getRateLaunchesTimes() > MIN_LAUNCHES_UNTIL_PROMPT
                    || daySinceLastLaunch > MIN_DAYS_UNTIL_PROMPT) {
                handleToShowRateDialog();
            }

        }

        setUpAppLanguage();
    }

    private void handleToShowRateDialog() {
        new Handler().postDelayed(() -> {
            MessagesHelper.showRateDialog(MainActivity.this, animScaleUpPlus);
        }, 2000);
    }

    private void setUpAppLanguage() {
        String[] arrayLanguagesCodes = getResources().getStringArray(R.array.languages_codes);
        int langPos = spHelper.getLanguagePos();
        TimeHelper.setLocale(this, arrayLanguagesCodes[langPos]);
    }

    private void setUpViews(Bundle savedInstanceState) {

        if (findViewById(R.id.main_fragment_layout) != null) {
            if (savedInstanceState != null) {
                return;
            }
            frameLayoutMain = findViewById(R.id.main_fragment_layout);
            pgsBar = findViewById(R.id.pBar);
            bottomNavigation = findViewById(R.id.meow_bottom_navigation);
        }
    }

    void setUpMeowBottomBar() {
        bottomNavigation.add(new MeowBottomNavigation.Model(Constants.MENU_HOME, R.drawable.ic_people_home));
        bottomNavigation.add(new MeowBottomNavigation.Model(Constants.MENU_VERSES, R.drawable.ic_holy_bible));
        bottomNavigation.add(new MeowBottomNavigation.Model(Constants.MENU_CONFIG, R.drawable.ic_black_settings));
        bottomNavigation.setOnClickMenuListener(model -> {
            SuperUtil.vibrate(this);
            switch (model.getId()) {
                case Constants.MENU_HOME:
                    actualFragment = HomeFragment.newInstance();
                    new AsyncTaskViewLoader(actualFragment,
                            HomeFragment.TAG, false).execute();
                    break;
                case Constants.MENU_VERSES:
                    actualFragment = VersesFragment.newInstance();
                    new AsyncTaskViewLoader(actualFragment, VersesFragment.TAG,
                            false).execute();
                    break;
                case Constants.MENU_CONFIG:
                    actualFragment = SettingsFragment.newInstance(false);
                    new AsyncTaskViewLoader(actualFragment, SettingsFragment.TAG,
                            false).execute();
                    break;
            }
            return null;
        });

        bottomNavigation.setOnShowListener(model -> null);

        bottomNavigation.setSelectedIconColor(getColor(R.color.md_green_100));

        bottomNavigation.setBackgroundBottomColor(getColor(R.color.custom_white_text_color));

        // Default item for bottomNavigation
        bottomNavigation.show(MENU_HOME, true);
        actualFragment = HomeFragment.newInstance();
        new AsyncTaskViewLoader(actualFragment, HomeFragment.TAG, false).execute();
    }

    public class AsyncTaskViewLoader extends AsyncTask<Void, Void, Void> {

        Fragment fragment;
        String tag;
        boolean addToBackStack;
        Animation anim;

        AsyncTaskViewLoader(Fragment fragment, String tag, boolean addToBackStack) {
            this.fragment = fragment;
            this.tag = tag;
            this.addToBackStack = addToBackStack;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (pgsBar.getVisibility() == View.GONE) {
                anim = AnimationUtils
                        .loadAnimation(MainActivity.this, R.anim.scale_up);
                pgsBar.setAnimation(anim);
                pgsBar.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            SuperUtil.loadView(MainActivity.this, fragment, tag, addToBackStack);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (pgsBar.getVisibility() == View.VISIBLE) {
                anim = AnimationUtils
                        .loadAnimation(MainActivity.this, R.anim.scale_down);
                pgsBar.setVisibility(View.GONE);
            }
            if (frameLayoutMain.getVisibility() == View.GONE)
                frameLayoutMain.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        FragmentManager manager = getSupportFragmentManager();
        manager.executePendingTransactions();

        Fragment newVerseFragment = manager.findFragmentByTag(NewVerseFragment.TAG);
        Fragment memorizingFragment = manager.findFragmentByTag(MemorizingFragment.TAG);
        Fragment signUpFragment = manager.findFragmentByTag(SignUpFragment.TAG);
        Fragment splitTextFragment = manager.findFragmentByTag(SplitTextFragment.TAG);
        Fragment userInfoFragment = manager.findFragmentByTag(UserInfoFragment.TAG);
        Fragment leaderBoardFragment = manager.findFragmentByTag(LeaderBoardFragment.TAG);
        Fragment scorerFragment = manager.findFragmentByTag(ScorerFragment.TAG);
        Fragment generalSettingsFragment = manager.findFragmentByTag(GeneralSettingsFragment.TAG);
        Fragment resultFragment = manager.findFragmentByTag(ResultFragment.TAG);
        Fragment aboutFragment = manager.findFragmentByTag(AboutFragment.TAG);
        Fragment supportFragment = manager.findFragmentByTag(SupportFragment.TAG);
        Fragment adMobFragment = manager.findFragmentByTag(AdMobFragment.TAG);


        boolean isNewVerseFragmentOn = false;
        if (newVerseFragment != null)
            isNewVerseFragmentOn = newVerseFragment.isVisible();

        boolean isMemorizingFragmentOn = false;
        if (memorizingFragment != null) {
            isMemorizingFragmentOn = memorizingFragment.isVisible();
            SharedPreferencesHelper spHelper = new SharedPreferencesHelper(this);
            if (spHelper.getSectionViewStatus())
                spHelper.setSectionViewStatus(false);
        }

        boolean isSignUpFragmentOn = false;
        if (signUpFragment != null)
            isSignUpFragmentOn = signUpFragment.isVisible();

        boolean isSplitTextFragmentOn = false;
        if (splitTextFragment != null)
            isSplitTextFragmentOn = splitTextFragment.isVisible();

        boolean isUserInfoFragmentOn = false;
        if (userInfoFragment != null)
            isUserInfoFragmentOn = userInfoFragment.isVisible();

        boolean isLeaderBoardFragmentOn = false;
        if (leaderBoardFragment != null)
            isLeaderBoardFragmentOn = leaderBoardFragment.isVisible();

        boolean isScorerFragmentOn = false;
        if (scorerFragment != null) {
            isScorerFragmentOn = scorerFragment.isVisible();
            if (isScorerFragmentOn) {
                if (!maybeWantToLeaveScoreSection) {
                    showLivingAlertDialog();
                    return;
                }
            }
        }

        boolean isGeneralSettingsFragmentOn = false;
        if (generalSettingsFragment != null)
            isGeneralSettingsFragmentOn = generalSettingsFragment.isVisible();

        boolean isResultFragmentOn = false;
        if (resultFragment != null)
            isResultFragmentOn = resultFragment.isVisible();

        boolean isAboutFragmentOn = false;
        if (aboutFragment != null)
            isAboutFragmentOn = aboutFragment.isVisible();

        boolean isSupportFragmentOn = false;
        if (supportFragment != null)
            isSupportFragmentOn = supportFragment.isVisible();

        boolean isAdMobFragmentOn = false;
        if (adMobFragment != null)
            isAdMobFragmentOn = adMobFragment.isVisible();


        if (isNewVerseFragmentOn || isMemorizingFragmentOn
                || isSignUpFragmentOn || isSplitTextFragmentOn
                || isUserInfoFragmentOn || isLeaderBoardFragmentOn
                || isGeneralSettingsFragmentOn || isResultFragmentOn
                || isAboutFragmentOn || isSupportFragmentOn
                || isAdMobFragmentOn
                || maybeWantToLeaveScoreSection) {
            super.onBackPressed();
            maybeWantToLeaveScoreSection = false;
            return;
        }

        if (maybeWantToExit) {
            finish();
            return;
        }
        maybeWantToExit = true;
        MessagesHelper.showInfoMessage(this, getString(R.string.exit));
        new Handler().postDelayed(() -> maybeWantToExit = false, 2000);
    }

    private void showLivingAlertDialog() {
        final Dialog dialog = new Dialog(this, R.style.alert_dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_leaving_alert_view,
                null, false);
        dialog.setContentView(dialogView);

        /*onClick on dialog cancel button*/
        Button cancelBtn = dialog.findViewById(R.id.cancel_dialog_button);
        cancelBtn.setOnClickListener(v -> {
            SuperUtil.vibrate(MainActivity.this);
            if (dialog.isShowing())
                dialog.dismiss();
        });

        /*onClick on dialog leave button*/
        Button editBtn = dialog.findViewById(R.id.leave_dialog_button);
        editBtn.setOnClickListener(v -> {
            SuperUtil.vibrate(MainActivity.this);
            if (dialog.isShowing())
                dialog.dismiss();
            maybeWantToLeaveScoreSection = true;
            onBackPressed();
        });
        dialog.show();
        dialogView.startAnimation(animScaleUp);

    }

    @Override
    protected void onResume() {
        super.onResume();
        adminUsage();
    }

    private void adminUsage() {

        long currentDate = System.currentTimeMillis();
        long lastUsageDate = spHelper.getLastUsage();

        int currentWeek = TimeHelper.getWeekNumber(currentDate);
        int lastUsageWeek = TimeHelper.getWeekNumber(lastUsageDate);

        if (lastUsageWeek != currentWeek) {
            spHelper.resetMemorizingFragmentUsageOpenTime();
            wipeUsageActivity();
            Log.d(TAG, "adminUsage: Usage Activity Wiped!!");
        }
    }

    private void wipeUsageActivity() {
        String[] dayCodes = getResources().getStringArray(R.array.day_codes);
        for (int i = 0; i < dayCodes.length; i++) {
            String key = dayCodes[i] + (i + 1);
            spHelper.removeUsageValue(key);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        testOnInspector();
        adminUsage();
    }

    private void testOnInspector() {
        FragmentManager manager = getSupportFragmentManager();
        manager.executePendingTransactions();
        Fragment scorerFragment = manager.findFragmentByTag(ScorerFragment.TAG);
        boolean isScorerFragmentOn;
        if (scorerFragment != null) {
            isScorerFragmentOn = scorerFragment.isVisible();
            if (isScorerFragmentOn) {
                SuperUtil.removeViewByTag(MainActivity.this,
                        ScorerFragment.TAG, true);
                Toast.makeText(getApplicationContext(),
                        getString(R.string.test_stop), Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        spHelper.saveLastUsage(System.currentTimeMillis());
        Log.d(TAG, "onStop: LastUsage Saved: " + System.currentTimeMillis());
    }
}
