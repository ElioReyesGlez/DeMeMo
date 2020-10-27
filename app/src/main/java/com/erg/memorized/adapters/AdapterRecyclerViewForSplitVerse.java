package com.erg.memorized.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.erg.memorized.R;
import com.erg.memorized.helpers.SharedPreferencesHelper;
import com.erg.memorized.interfaces.OnSectionListener;
import com.erg.memorized.util.SuperUtil;

import java.util.ArrayList;

import static com.erg.memorized.util.Constants.SPACE;

public class AdapterRecyclerViewForSplitVerse extends
        RecyclerView.Adapter<AdapterRecyclerViewForSplitVerse.VerseHolder> {

    private static final String TAG = "AdapterRecyclerViewForS";

    private final ArrayList<String> splitVerseList;
    private final Context context;
    private final String title;
    private final OnSectionListener onSectionListener;
    private final SharedPreferencesHelper spHelper;
    public int contChecked = 0;

    public AdapterRecyclerViewForSplitVerse(ArrayList<String> splitVerseList,
                                            String title, Context context,
                                            OnSectionListener onSectionListener) {

        spHelper = new SharedPreferencesHelper(context);
        this.splitVerseList = splitVerseList;
        this.title = title;
        this.context = context;
        this.onSectionListener = onSectionListener;
    }

    @NonNull
    @Override
    public VerseHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        // Inflate the layout view you have created for the list rows here
        View view = layoutInflater.inflate(R.layout.item_list_split_verse, parent, false);
        return new VerseHolder(view,onSectionListener);
    }

    @Override
    public void onBindViewHolder(@NonNull VerseHolder holder, int position) {
        String text = splitVerseList.get(position);

        holder.setVerseTitle(context.getString(R.string.section) + SPACE + (position + 1));
        holder.setVerseText(text);
        holder.setCheckBox(spHelper.getSectionCheckedStatus(title, position));
    }

    @Override
    public int getItemCount() {
        return splitVerseList == null ? 0 : splitVerseList.size();
    }

    public class VerseHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener, CompoundButton.OnCheckedChangeListener {
        private final TextView verseTitle;
        private final TextView verseText;
        private final CheckBox checkBox;
        private final OnSectionListener onSectionListener;

        VerseHolder(View itemView, OnSectionListener onSectionListener) {
            super(itemView);
            verseTitle = itemView.findViewById(R.id.tv_verse_title);
            verseText = itemView.findViewById(R.id.tv_verse_text);
            checkBox = itemView.findViewById(R.id.check_box);

            this.onSectionListener = onSectionListener;

            itemView.setOnClickListener(this);
            checkBox.setOnCheckedChangeListener(this);
            checkBox.setOnClickListener(v -> SuperUtil.vibrate(context));

        }

        void setVerseTitle(String title) {
            verseTitle.setText(title);
        }

        void setVerseText(String text) {
            verseText.setText(text);
        }

        void setCheckBox(boolean flag) {
            checkBox.setChecked(flag);
        }

        @Override
        public void onClick(View v) {
            onSectionListener.onSectionClick(getAdapterPosition());
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(isChecked) {
                contChecked++;
                verseTitle.setTextColor(context.getColor(R.color.colorPrimary));
                verseText.setTextColor(context.getColor(R.color.colorPrimary));
            } else {
                verseTitle.setTextColor(context.getColor(R.color.md_black_1000));
                verseText.setTextColor(context.getColor(R.color.md_grey_600));
                contChecked--;
            }
            Log.d(TAG, "contChecked: " + contChecked);
            onSectionListener.onCheckedChanged(getAdapterPosition(), buttonView, isChecked);

            if (contChecked == splitVerseList.size()) {
                if (spHelper.getCheckedSections(title, splitVerseList) == splitVerseList.size()) {
                    Log.d(TAG, "onCheckedChanged: isFullyCheckedListener");
                    onSectionListener.isFullyCheckedListener();
                }
            }
        }
    }
}
