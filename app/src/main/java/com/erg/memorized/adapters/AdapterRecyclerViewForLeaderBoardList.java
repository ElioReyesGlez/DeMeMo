package com.erg.memorized.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.erg.memorized.R;
import com.erg.memorized.helpers.RealmHelper;
import com.erg.memorized.model.ItemUser;
import com.erg.memorized.util.Constants;
import com.erg.memorized.util.SuperUtil;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import static com.erg.memorized.util.Constants.DOUBLE_DOT;
import static com.erg.memorized.util.Constants.SPACE;

public class AdapterRecyclerViewForLeaderBoardList extends
        RecyclerView.Adapter<AdapterRecyclerViewForLeaderBoardList.VerseHolder> {

    private ArrayList<ItemUser> users;
    private Context context;
    private int selectedPos = RecyclerView.NO_POSITION;

    public AdapterRecyclerViewForLeaderBoardList(ArrayList<ItemUser> users, Context context) {
        this.users = users;
        this.context = context;
    }

    @NonNull
    @Override
    public VerseHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        // Inflate the layout view you have created for the list rows here
        View view = layoutInflater.inflate(R.layout.item_list_leader_board, parent, false);
        return new VerseHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VerseHolder holder, int position) {
        ItemUser user = users.get(position);
        setUserAvatar(user, holder);
        int rank = position + 1;
        holder.setRank(String.valueOf(rank));
        holder.setUserName(user.getName());
        holder.setScore(context.getString(R.string.score) +
                DOUBLE_DOT  + SPACE + user.getScore());

        setBadge(user, rank, holder);

        if (user.isPremium())
            holder.activateBadgePremium();

        ItemUser currentUser = new RealmHelper(context).getUser();

        if (currentUser != null && currentUser.getId() != null) {
            if (user.getId().equals(currentUser.getId())) {
                holder.setBackground(holder.getRootView(), R.drawable.background_yellow);
                holder.setBackground(holder.getRank(), R.drawable.background_yellow);
            }
        }
    }

    private void setBadge(ItemUser user, int rank, @NotNull VerseHolder holder) {

        if (user.isPremium()) {
            holder.activateBadgePremium();
        }

        switch (rank) {
            case 1:
                holder.setBadge(R.drawable.ic_badge_yellow);
                break;
            case 2:
                holder.setBadge(R.drawable.ic_badge_gray);
                break;
            case 3:
                holder.setBadge(R.drawable.ic_badge_brown);
                break;
        }
    }

    private void setUserAvatar(@NotNull ItemUser user, VerseHolder holder) {
        Bitmap bitmapFromBase64 = null;
        if (user.getImg() != null && !user.getImg().equals(Constants.DEFAULT))
            bitmapFromBase64 = SuperUtil.decodeBase64ToBitmap(user.getImg());

        if (bitmapFromBase64 == null && user.getImg() != null &&
                user.getImg().equals(Constants.DEFAULT)) {
            holder.setUserAvatarDefault(R.drawable.ic_user_profile);
        } else {
            holder.setUserAvatarBitmap(bitmapFromBase64);
        }

    }

    @Override
    public int getItemCount() {
        return users == null ? 0 : users.size();
    }

    public void filterList(ArrayList<ItemUser> filteredList) {
        users = filteredList;
        notifyDataSetChanged();
    }

    public void refreshAdapter(ArrayList<ItemUser> users) {
        this.users = users;
        notifyDataSetChanged();
    }

    public ArrayList<ItemUser> getUsers() {
        return users;
    }

    static class VerseHolder extends RecyclerView.ViewHolder {
        private TextView userName;
        private TextView score;
        private TextView rank;
        private ImageView badgePremiumStatus;
        private ImageView userAvatar;
        private ImageView badge;
        private View rootView;

        VerseHolder(View itemView) {
            super(itemView);
            rootView = itemView;
            userName = itemView.findViewById(R.id.tv_user_name);
            score = itemView.findViewById(R.id.tv_user_score);
            userAvatar = itemView.findViewById(R.id.iv_user_avatar);
            badge = itemView.findViewById(R.id.iv_badge);
            badgePremiumStatus = itemView.findViewById(R.id.iv_premium_badge);
            rank = itemView.findViewById(R.id.tv_rank);
        }

        void setBackground(View view, int res) {
            view.setBackgroundResource(res);
        }

        void setUserName(String userName) {
            this.userName.setText(userName);
        }

        void setRank(String rank) {
            this.rank.setText(rank);
        }

        void setScore(String memorizedCont) {
            this.score.setText(memorizedCont);
        }

        void setUserAvatarBitmap(Bitmap userAvatar) {
            this.userAvatar.setImageBitmap(userAvatar);
        }

        void setUserAvatarDefault(int res) {
            this.userAvatar.setImageResource(res);
        }

        void setBadge(int badge) {
            SuperUtil.showView(null, this.badge);
            this.badge.setImageResource(badge);
        }

        void activateBadgePremium() {
            SuperUtil.showView(null, this.badgePremiumStatus);
        }

        public TextView getUserName() {
            return userName;
        }

        public TextView getScore() {
            return score;
        }

        public TextView getRank() {
            return rank;
        }

        public ImageView getBadgePremiumStatus() {
            return badgePremiumStatus;
        }

        public ImageView getUserAvatar() {
            return userAvatar;
        }

        public ImageView getBadge() {
            return badge;
        }

        public View getRootView() {
            return rootView;
        }
    }
}
