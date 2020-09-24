package com.erg.memorized.interfaces;

import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

public interface OnPickersDateTimeChangeListener {
    public void OnDateChangeListener(DatePicker datePicker, int year, int month, int dayOfMonth);
    public void OnTimeChangeListener(TimePicker timePicker, int hourOfDay, int minute);
    public boolean OnDailySwitchListener(CompoundButton buttonView, boolean isChecked,
                                         RelativeLayout untilView);
    public boolean OnWeeklySwitchListener(CompoundButton buttonView, boolean isChecked,
                                          RelativeLayout untilView);
    public boolean OnMonthlySwitchListener(CompoundButton buttonView, boolean isChecked,
                                           RelativeLayout untilView);
    public boolean OnUntilViewListener(RelativeLayout untilView, TextView tvUntilDate);
}
