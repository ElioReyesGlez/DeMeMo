package com.erg.memorized.interfaces;

import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

public interface OnPickersDateTimeChangeListener {
    void OnDateChangeListener(DatePicker datePicker, int year, int month, int dayOfMonth);
    void OnTimeChangeListener(TimePicker timePicker, int hourOfDay, int minute);
    void OnDailySwitchListener(CompoundButton buttonView, boolean isChecked,
                               RelativeLayout untilView);
    void OnWeeklySwitchListener(CompoundButton buttonView, boolean isChecked,
                                RelativeLayout untilView);
    void OnMonthlySwitchListener(CompoundButton buttonView, boolean isChecked,
                                 RelativeLayout untilView);
    void OnEndTimeViewListener(RelativeLayout untilView, TextView tvUntilDate);
}
