package com.erg.memorized;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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
        screenItems.add(new ScreenItem(getString(R.string.app_name),
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit," +
                        " sed do eiusmod tempor incididunt ut labore et dolore magna aliqua," +
                        " consectetur  consectetur adipiscing elit", R.drawable.ic_learning_launcher_2));
        screenItems.add(new ScreenItem("Section 1",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit," +
                        " sed do eiusmod tempor incididunt ut labore et dolore magna aliqua," +
                        " consectetur  consectetur adipiscing elit", R.drawable.ic_man_bible));
        screenItems.add(new ScreenItem("Section 2",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, " +
                        "sed do eiusmod tempor incididunt ut labore et dolore magna aliqua," +
                        " consectetur  consectetur adipiscing elit", R.drawable.ic_color_bible));
        screenItems.add(new ScreenItem("Section 3",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, " +
                        "sed do eiusmod tempor incididunt ut labore et dolore magna aliqua," +
                        " consectetur  consectetur adipiscing elit", R.drawable.ic_books));

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
        if (btnNext.getVisibility() == View.VISIBLE) {
            btnNext.startAnimation(animScaleDown);
            btnNext.setVisibility(View.GONE);
        }
        if (btnGetStarted.getVisibility() == View.GONE) {
            btnGetStarted.startAnimation(animScaleUp);
            btnGetStarted.setVisibility(View.VISIBLE);
        }
        if (tvSkip.getVisibility() == View.VISIBLE) {
            tvSkip.startAnimation(animScaleDown);
            tvSkip.setVisibility(View.GONE);
        }
        if (tabIndicator.getVisibility() == View.VISIBLE) {
            tabIndicator.startAnimation(animScaleDown);
            tabIndicator.setVisibility(View.GONE);
        }
    }

    // hide the GET_STARTED Button and show the indicator and the next button
    private void loadIntermediateScreen() {
        if (btnNext.getVisibility() == View.GONE) {
            btnNext.startAnimation(animScaleUp);
            btnNext.setVisibility(View.VISIBLE);
        }
        if (btnGetStarted.getVisibility() == View.VISIBLE) {
            btnGetStarted.startAnimation(animScaleDown);
            btnGetStarted.setVisibility(View.GONE);
        }
        if (tvSkip.getVisibility() == View.GONE) {
            tvSkip.startAnimation(animScaleUp);
            tvSkip.setVisibility(View.VISIBLE);
        }
        if (tabIndicator.getVisibility() == View.GONE) {
            tabIndicator.startAnimation(animScaleUp);
            tabIndicator.setVisibility(View.VISIBLE);
        }
    }
}
