package com.erg.memorized.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.erg.memorized.R;
import com.erg.memorized.helpers.TimeHelper;
import com.erg.memorized.interfaces.OnVerseListener;
import com.erg.memorized.model.ItemVerse;

import java.util.ArrayList;

public class AdapterRecyclerViewForVersesList extends RecyclerView.Adapter<AdapterRecyclerViewForVersesList.VerseHolder> {

    private ArrayList<ItemVerse> verses;
    private Context context;
    private OnVerseListener onVerseListener;
    private int selectedPos = RecyclerView.NO_POSITION;

    public AdapterRecyclerViewForVersesList(ArrayList<ItemVerse> verses, Context context,
                                            OnVerseListener onVerseListener) {
        this.verses = verses;
        this.context = context;
        this.onVerseListener = onVerseListener;
    }

    @NonNull
    @Override
    public VerseHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        // Inflate the layout view you have created for the list rows here
        View view = layoutInflater.inflate(R.layout.item_list_verse, parent, false);
        return new VerseHolder(view, onVerseListener);
    }

    @Override
    public void onBindViewHolder(@NonNull VerseHolder holder, int position) {
        ItemVerse verse = verses.get(position);

        holder.setVerseTitle(verse.getTitle());
        holder.setVerseText(verse.getVerseText());
        holder.setVerseDate(verse.getId());
        holder.setSelected();
    }

    @Override
    public int getItemCount() {
        return verses == null ? 0 : verses.size();
    }

    public class VerseHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener, View.OnLongClickListener {
        private TextView verseTitle;
        private TextView verseText;
        private TextView verseDate;
        private RelativeLayout relativeLayout;
        private OnVerseListener onVerseListener;

        VerseHolder(View itemView, OnVerseListener onVerseListener) {
            super(itemView);
            verseTitle = itemView.findViewById(R.id.tv_verse_title);
            verseText = itemView.findViewById(R.id.tv_verse_text);
            verseDate = itemView.findViewById(R.id.tv_date);
            relativeLayout = itemView.findViewById(R.id.relative_layout_item_list);
            this.onVerseListener = onVerseListener;

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        void setVerseTitle(String title) {
            verseTitle.setText(title);
        }

        void setVerseText(String text) {
            verseText.setText(text);
        }

        void setVerseDate(Long longDate) {
            verseDate.setText(TimeHelper.getDisplayableTime(context, longDate));
        }

        void setSelected() {
            relativeLayout.setBackgroundResource(R.drawable.selector_white);
        }

        @Override
        public void onClick(View v) {
            onVerseListener.onVerseClick(getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View v) {
            onVerseListener.onVerseLongClick(getAdapterPosition());
            return true;
        }
    }

    public void filterList(ArrayList<ItemVerse> filteredList) {
        verses = filteredList;
        notifyDataSetChanged();
    }

    public void refreshAdapter(ArrayList<ItemVerse> verses) {
        this.verses = verses;
        notifyDataSetChanged();
    }

    public ArrayList<ItemVerse> getVerses() {
        return verses;
    }
}
