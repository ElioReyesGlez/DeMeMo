package com.erg.memorized.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.erg.memorized.R;
import com.erg.memorized.model.ScreenItem;

import java.util.List;

public class AdapterIntroViewPager extends PagerAdapter {

   final Context context;
   final List<ScreenItem> screenItems;

    public AdapterIntroViewPager(Context context, List<ScreenItem> screenItems) {
        this.context = context;
        this.screenItems = screenItems;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        @SuppressLint("InflateParams") View layoutScreen = inflater.inflate(R.layout.layout_screen, null);

        ImageView imgSlide = layoutScreen.findViewById(R.id.intro_img);
        TextView title = layoutScreen.findViewById(R.id.intro_title);
        TextView description = layoutScreen.findViewById(R.id.intro_description);

        title.setText(screenItems.get(position).getTitle());
        description.setText(screenItems.get(position).getDescription());
        imgSlide.setImageResource(screenItems.get(position).getScreenImg());

        container.addView(layoutScreen);

        return layoutScreen;

    }

    @Override
    public int getCount() {
        return screenItems.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }
}
