package com.erg.memorized.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.erg.memorized.R;
import com.erg.memorized.helpers.SharedPreferencesHelper;
import com.erg.memorized.util.Constants;

import java.util.ArrayList;

public class AdapterLanguage extends ArrayAdapter<String> {

    private final ArrayList<String> languages;
    private final int[] flagsResIds;
    private final int savedLanguagePos;

    public AdapterLanguage(@NonNull Context context, int resource,
                           @NonNull ArrayList<String> languages) {
        super(context, resource, languages);
        this.languages = languages;

        flagsResIds = Constants.FlagResIds;
        SharedPreferencesHelper spHelper = new SharedPreferencesHelper(context);
        savedLanguagePos = spHelper.getLanguagePos();
    }

    @Override
    public int getCount() {
        return languages == null ? 0 : languages.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        ViewHolder viewHolder;
        final View result;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_language, parent, false);
            viewHolder = new ViewHolder(convertView);
            result = convertView;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

        viewHolder.setLanguage(languages.get(position));
        viewHolder.setFlag(flagsResIds[position]);

        if (viewHolder.getRadioButton().getVisibility() == View.INVISIBLE) {
            viewHolder.getRadioButton().setVisibility(View.VISIBLE);
        }

        if (position == savedLanguagePos) {
            viewHolder.setCheckedRadioButton(true);
        } else {
            viewHolder.setCheckedRadioButton(false);
        }

        return convertView;
    }

    public static class ViewHolder {
        private final ImageView flag;
        private final TextView language;
        private final RadioButton radioButton;

        public ViewHolder(View convertView) {
            this.flag = convertView.findViewById(R.id.iv_flag);
            this.language = convertView.findViewById(R.id.tv_app_language);
            this.radioButton = convertView.findViewById(R.id.radioButton);
        }

        public void setFlag(int resFlag) {
            this.flag.setImageResource(resFlag);
        }

        public void setLanguage(String language) {
            this.language.setText(language);
        }

        public void setCheckedRadioButton(boolean flag) {
            this.radioButton.setChecked(flag);
        }

        public ImageView getFlag() {
            return flag;
        }

        public TextView getLanguage() {
            return language;
        }

        public RadioButton getRadioButton() {
            return radioButton;
        }
    }
}
