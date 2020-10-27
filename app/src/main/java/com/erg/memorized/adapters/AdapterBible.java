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
import com.erg.memorized.model.bible_api_models.Bible;
import com.erg.memorized.model.bible_api_models.BibleLanguage;

import java.util.ArrayList;

public class AdapterBible extends ArrayAdapter<Bible> {

    private ArrayList<Bible> bibles;
    private final SharedPreferencesHelper spHelper;

    public AdapterBible(@NonNull Context context, int resource, @NonNull ArrayList<Bible> bibles) {
        super(context, resource, bibles);
        this.bibles = bibles;
        spHelper = new SharedPreferencesHelper(context);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        ViewHolder viewHolder;
        Bible bible = bibles.get(position);

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_bible_version, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.setAbbreviationLocal(bible.getAbbreviationLocal());
        viewHolder.setNameLocal(bible.getNameLocal());
        viewHolder.setDescriptionLocal(bible.getDescriptionLocal());
        BibleLanguage bibleLanguage = bible.getLanguage();
        viewHolder.setLanguage(bibleLanguage.getNameLocal());

        if (viewHolder.getRadioButton().getVisibility() == View.INVISIBLE) {
            viewHolder.getRadioButton().setVisibility(View.VISIBLE);
        }

        viewHolder.setCheckedRadioButton(spHelper.getBibleSelectedStatus(bible.getId()));

        return convertView;
    }

    @Override
    public int getCount() {
        return bibles == null ? 0 : bibles.size();
    }

    public static class ViewHolder {
        private final ImageView ivBible;
        private final TextView tvAbbreviationLocal;
        private final TextView tvNameLocal;
        private final TextView tvDescriptionLocal;
        private final TextView tvLanguage;
        private final RadioButton radioButton;

        public ViewHolder(View convertView) {
            this.ivBible = convertView.findViewById(R.id.iv_bible);
            this.tvAbbreviationLocal = convertView.findViewById(R.id.tv_abbreviationLocal);
            this.tvNameLocal = convertView.findViewById(R.id.tv_nameLocal);
            this.tvDescriptionLocal = convertView.findViewById(R.id.tv_descriptionLocal);
            this.tvLanguage = convertView.findViewById(R.id.tv_app_language);
            this.radioButton = convertView.findViewById(R.id.radioButton);
        }

        public void setAbbreviationLocal(String abbreviationLocal) {
            this.tvAbbreviationLocal.setText(abbreviationLocal);
        }

        public void setNameLocal(String nameLocal) {
            this.tvNameLocal.setText(nameLocal);
        }

        public void setDescriptionLocal(String descriptionLocal) {
            this.tvDescriptionLocal.setText(descriptionLocal);
        }

        public void setLanguage(String language) {
            this.tvLanguage.setText(language);
        }

        public void setCheckedRadioButton(boolean flag) {
            this.radioButton.setChecked(flag);
        }

        public ImageView getIvBible() {
            return ivBible;
        }

        public TextView getTvAbbreviationLocal() {
            return tvAbbreviationLocal;
        }

        public TextView getTvNameLocal() {
            return tvNameLocal;
        }

        public TextView getTvLanguage() {
            return tvLanguage;
        }

        public RadioButton getRadioButton() {
            return radioButton;
        }
    }

    public void filterList(ArrayList<Bible> filteredList) {
        bibles = filteredList;
        notifyDataSetChanged();
    }

    public ArrayList<Bible> getBibles() {
        return bibles;
    }
}
