package com.erg.memorized.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;

import com.erg.memorized.R;
import com.erg.memorized.helpers.SharedPreferencesHelper;

public class AdapterGetPremiumViewPager extends PagerAdapter {

    private Activity context;
    private Integer[] arrayViewItems = new Integer[]{
            R.layout.intro_get_premium_view,
            R.layout.exam_view,
            R.layout.leader_board_view
    };

    private String[] tabTitles;

    private SharedPreferencesHelper spHelper;
    private Animation animScaleUp, animScaleDown;

    public AdapterGetPremiumViewPager(Activity context, String[] tabsTitles) {

        this.context = context;
        this.tabTitles = tabsTitles;

        spHelper = new SharedPreferencesHelper(context);
        animScaleUp = AnimationUtils.loadAnimation(context, R.anim.scale_up);
        animScaleDown = AnimationUtils.loadAnimation(context, R.anim.scale_down);

    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        LayoutInflater inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        assert inflater != null;
        View view = inflater.inflate(arrayViewItems[position], null);

        container.addView(view);

        return view;
    }


    @Override
    public int getCount() {
        return arrayViewItems.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }

}
