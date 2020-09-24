package com.erg.memorized.interfaces;

import android.widget.CompoundButton;

public interface OnSectionListener {
    void onSectionClick(int pos);
    void onCheckedChanged(int position, CompoundButton buttonView, boolean isChecked);
    void isFullyCheckedListener();
}
