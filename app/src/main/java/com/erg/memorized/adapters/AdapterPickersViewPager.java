package com.erg.memorized.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.DatePicker;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;

import com.erg.memorized.R;
import com.erg.memorized.helpers.SharedPreferencesHelper;
import com.erg.memorized.helpers.TimeHelper;
import com.erg.memorized.interfaces.OnPickersDateTimeChangeListener;
import com.erg.memorized.model.ItemVerse;
import com.erg.memorized.util.Constants;
import com.erg.memorized.util.SuperUtil;

import java.util.Calendar;

public class AdapterPickersViewPager extends PagerAdapter {

    private Activity context;
    private Integer[] arrayPickers = new Integer[]{
            R.layout.date_picker_view,
            R.layout.time_picker_view
    };

    private String[] tabTitles;

    private OnPickersDateTimeChangeListener pickersListener;
    private Calendar calendar;
    private ItemVerse itemVerse;
    private SharedPreferencesHelper spHelper;
    private Animation animScaleUp, animScaleDown;

    public AdapterPickersViewPager(Activity context, String[] tabsTitles,
                                   Calendar calendar,
                                   ItemVerse itemVerse,
                                   OnPickersDateTimeChangeListener pickersListener) {

        this.context = context;
        this.tabTitles = tabsTitles;
        this.calendar = calendar;
        this.itemVerse = itemVerse;
        this.pickersListener = pickersListener;

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
        View view = inflater.inflate(arrayPickers[position], null);

        container.addView(view);

        if (arrayPickers.length - 1 == position) {
            DatePicker datePicker = container.findViewById(R.id.date_picker);
            TimePicker timePicker = container.findViewById(R.id.time_picker);
            Switch dailySwitch = container.findViewById(R.id.switch_daily);
            Switch weeklySwitch = container.findViewById(R.id.switch_weekly);
            Switch monthlySwitch = container.findViewById(R.id.switch_monthly);
            RelativeLayout rlUntil = container.findViewById(R.id.rl_until);
            TextView tvEndDate = container.findViewById(R.id.tv_end_date);

            if (itemVerse != null && itemVerse.getTitle() != null) {
                dailySwitch.setChecked(spHelper.getRepeatingNotifyStatus(Constants.DAILY_KEY
                        + itemVerse.getTitle()));
                weeklySwitch.setChecked(spHelper.getRepeatingNotifyStatus(Constants.WEEKLY_KEY
                        + itemVerse.getTitle()));
                monthlySwitch.setChecked(spHelper.getRepeatingNotifyStatus(Constants.MONTHLY_KEY
                        + itemVerse.getTitle()));

                if (dailySwitch.isChecked() || weeklySwitch.isChecked() || monthlySwitch.isChecked()) {
                    SuperUtil.showView(null, rlUntil);
                }
            }

            if (itemVerse != null && itemVerse.getUntilAlarm() != -1) {
                tvEndDate.setText(TimeHelper.dateFormatterMedium(itemVerse.getUntilAlarm()));
            }

            datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH), (datePicker1, year, month, dayOfMonth) -> {
                        pickersListener.OnDateChangeListener(datePicker1, year, month, dayOfMonth);
                    });

            timePicker.setOnTimeChangedListener((view1, hourOfDay, minute) -> {
                pickersListener.OnTimeChangeListener(view1, hourOfDay, minute);
            });

            dailySwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                switchOff(isChecked, weeklySwitch, monthlySwitch);
                pickersListener.OnDailySwitchListener(buttonView, isChecked,
                        rlUntil);
            });

            weeklySwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (dailySwitch.isChecked() && isChecked)
                    dailySwitch.setChecked(false);

                pickersListener.OnWeeklySwitchListener(buttonView, isChecked,
                        rlUntil);
            });

            monthlySwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (dailySwitch.isChecked() && isChecked)
                    dailySwitch.setChecked(false);

                pickersListener.OnMonthlySwitchListener(buttonView, isChecked,
                        rlUntil);
            });

            rlUntil.setOnClickListener(v -> {
                pickersListener.OnUntilViewListener(rlUntil, tvEndDate);
            });

        }

        return view;
    }

    private void switchOff(boolean isChecked, Switch weeklySwitch, Switch monthlySwitch) {
        if (isChecked) {
            if (weeklySwitch.isChecked())
                weeklySwitch.setChecked(false);
            if (monthlySwitch.isChecked())
                monthlySwitch.setChecked(false);
        }
    }

    @Override
    public int getCount() {
        return arrayPickers.length;
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
