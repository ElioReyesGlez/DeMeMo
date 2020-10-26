package com.erg.memorized;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import androidx.viewpager.widget.ViewPager;

import com.erg.memorized.adapters.AdapterIntroViewPager;
import com.erg.memorized.helpers.SharedPreferencesHelper;
import com.erg.memorized.model.ScreenItem;
import com.erg.memorized.util.SuperUtil;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class IntroActivity extends Activity {

    public static String TAG = "IntroActivity";

    private ViewPager screenPager;
    private AdapterIntroViewPager adapterIntroViewPager;
    private TabLayout tabIndicator;
    private Button btnNext;
    private int position = 0 ;
    private Button btnGetStarted;
    private Animation animScaleUp, animScaleDown ;
    private TextView tvSkip;
    private SharedPreferencesHelper spHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        spHelper = new SharedPreferencesHelper(this);

        if (spHelper.isFirstLaunch()) {
            spHelper.setLastLaunchRateDialogDate();
            spHelper.setLastPremiumRateDialogDate();
        }

        if (spHelper.getIntroStatus()) {
            jumpToMainActivity();
            return;
        }

        setContentView(R.layout.activity_intro);
        btnNext = findViewById(R.id.btn_next);
        btnGetStarted = findViewById(R.id.btn_get_started);
        tabIndicator = findViewById(R.id.tab_indicator);
        tvSkip = findViewById(R.id.tv_skip);
        animScaleUp = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.less_scale_up);
        animScaleDown = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.scale_down);

        final List<ScreenItem> screenItems = new ArrayList<>();
        screenItems.add(new ScreenItem(getString(R.string.app_name), getString(R.string.dememo_intro_description),
                R.drawable.ic_learning_launcher_2));
        screenItems.add(new ScreenItem(getString(R.string.memory_evaluator),
                getString(R.string.evaluator_intro_description), R.drawable.ic_exam_main_2));
        screenItems.add(new ScreenItem(getString(R.string.cloud_backup),
                getString(R.string.cloud_backup_intro_description), R.drawable.ic_cloud));
        screenItems.add(new ScreenItem(getString(R.string.leader_board),
                getString(R.string.leader_board_intro_description), R.drawable.ic_podium_2));

        // setup viewpager
        screenPager =findViewById(R.id.screen_viewpager);
        adapterIntroViewPager = new AdapterIntroViewPager(this, screenItems);
        screenPager.setAdapter(adapterIntroViewPager);

        tabIndicator.setupWithViewPager(screenPager);

        btnNext.setOnClickListener(v -> {

            SuperUtil.vibrate(this);

            position = screenPager.getCurrentItem();
            if (position < screenItems.size()) {
                position++;
                screenPager.setCurrentItem(position);
            }
            if (position == screenItems.size()-1) {
                loadLastScreen();
            }
        });

        tabIndicator.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                if (tab.getPosition() == screenItems.size()-1) {
                    loadLastScreen();
                } else {
                    loadIntermediateScreen();
                }
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        btnGetStarted.setOnClickListener(v -> {
            SuperUtil.vibrate(this);
            jumpToMainActivity();
        });

        tvSkip.setOnClickListener(v -> {
            SuperUtil.vibrate(this);
            screenPager.setCurrentItem(screenItems.size());
        });
        setUpLanguage();
    }

    private void setUpLanguage() {
        String[] arrayLanguagesCodes = getResources().getStringArray(R.array.languages_codes);
        ArrayList<String> languagesCodes = new ArrayList<>(Arrays.asList(arrayLanguagesCodes));
        Locale defaultLocale = Locale.getDefault();
        int langPos = languagesCodes.indexOf(defaultLocale.getLanguage());
        if (langPos != -1) {
            spHelper.setLanguagePosition(langPos);
        }
    }

    public void jumpToMainActivity(){
        Intent mainActivity = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(mainActivity);
        overridePendingTransition(R.anim.fab_slide_in_from_right, R.anim.fade_out);
        spHelper.setIntroActivityStatus(true);
        spHelper.setAppFirstLaunchDate();
        finish();
    }

    // show the GET_STARTED Button and hide the indicator and the next button
    private void loadLastScreen() {
        SuperUtil.hideViewInvisibleWay(animScaleDown, btnNext);
        SuperUtil.showViewWhitStartAnimation(animScaleUp, btnGetStarted);
        SuperUtil.hideViewInvisibleWay(animScaleDown, tvSkip);
        SuperUtil.hideViewInvisibleWay(animScaleDown, tabIndicator);
    }

    // hide the GET_STARTED Button and show the indicator and the next button
    private void loadIntermediateScreen() {
        SuperUtil.showViewWhitStartAnimation(animScaleUp, btnNext);
        SuperUtil.hideViewInvisibleWay(animScaleDown, btnGetStarted);
        SuperUtil.showViewWhitStartAnimation(animScaleUp, tvSkip);
        SuperUtil.showViewWhitStartAnimation(animScaleUp, tabIndicator);

    }
}
