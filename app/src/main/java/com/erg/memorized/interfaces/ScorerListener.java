package com.erg.memorized.interfaces;

import android.view.View;

import com.erg.memorized.adapters.AdapterScorerFragmentPager;
import com.erg.memorized.views.CustomViewPager;

public interface ScorerListener {
    void onUndoClick(View v);
    void onNextClick(View v, CustomViewPager viewPager,
                     AdapterScorerFragmentPager pagerAdapter);
    void onFinishClick(View v);
}
