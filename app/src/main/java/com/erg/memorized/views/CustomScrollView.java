package com.erg.memorized.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

public class CustomScrollView extends ScrollView {

    private OnScrollViewListener onScrollViewListener;

    public CustomScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (onScrollViewListener != null) {
            onScrollViewListener.onScrollChanged((OnScrollViewListener) this, this);
        }
    }

    public void setOnScrollViewListener(OnScrollViewListener listener) {
        onScrollViewListener = listener;
    }

    public interface OnScrollViewListener {
        void onScrollChanged(OnScrollViewListener listener, ScrollView scrollView);
    }
}
