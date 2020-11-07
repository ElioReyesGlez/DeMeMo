package com.erg.memorized.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.erg.memorized.R;
import com.erg.memorized.adapters.AdapterRecyclerViewForLeaderBoardList;
import com.erg.memorized.helpers.MessagesHelper;
import com.erg.memorized.helpers.RealmHelper;
import com.erg.memorized.helpers.SharedPreferencesHelper;
import com.erg.memorized.model.ItemUser;
import com.erg.memorized.util.Constants;
import com.erg.memorized.util.SuperUtil;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

import static com.erg.memorized.util.Constants.LEADER_BOARD_FIRE_BASE_REFERENCE;
import static com.erg.memorized.util.Constants.USER_COLUMN_IMG;
import static com.erg.memorized.util.Constants.USER_COLUMN_NAME;
import static com.erg.memorized.util.Constants.USER_COLUMN_PREMIUM_STATUS;
import static com.erg.memorized.util.Constants.USER_COLUMN_VERSES_SCORE;

public class LeaderBoardFragment extends Fragment implements SearchView.OnQueryTextListener {

    public static final String TAG = "LeaderBoardFragment";

    private RecyclerView recycler;
    private SearchView searcher;
    private AdapterRecyclerViewForLeaderBoardList adapter;
    private View rootView;
    private ViewGroup container;

    private SharedPreferencesHelper spHelper;
    private ItemUser currentUser;
    private FirebaseAuth fAuth;

    private Animation animScaleUp, animScaleDown, animSlideInFromRight;
    private ArrayList<ItemUser> leaderBoardUsers;

    public LeaderBoardFragment() {
    }

    public static LeaderBoardFragment newInstance() {
        return new LeaderBoardFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RealmHelper realmHelper = new RealmHelper();
        spHelper = new SharedPreferencesHelper(requireContext());
        leaderBoardUsers = new ArrayList<>();
        fAuth = FirebaseAuth.getInstance();
        if (spHelper.getUserLoginStatus())
            currentUser = realmHelper.getUser();

        animScaleUp = AnimationUtils.loadAnimation(getContext(), R.anim.less_scale_up);
        animScaleDown = AnimationUtils.loadAnimation(getContext(), R.anim.scale_down);
        animSlideInFromRight = AnimationUtils.loadAnimation(getContext(),
                R.anim.fab_slide_in_from_right);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.container = container;
        rootView = inflater.inflate(R.layout.fragment_leader_board, container, false);
        if (spHelper.getUserLoginStatus()) {
            setUpView();
        }
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void setUpView() {
        recycler = rootView.findViewById(R.id.recycler_view_leader_board);
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        recycler.setLayoutManager(layoutManager);
        recycler.setHasFixedSize(true);
        recycler.setItemAnimator(new DefaultItemAnimator());

        searcher = rootView.findViewById(R.id.searcher_leader_board);
        searcher.setOnQueryTextListener(this);

        if (!leaderBoardUsers.isEmpty()) {
            showLeaderBoard(leaderBoardUsers);
            SuperUtil.showView(animScaleUp, searcher);
        } else {
            SuperUtil.hideView(animScaleDown, searcher);
        }
        setUpLeaderBoardList();
    }

    private void setUpLeaderBoardList() {
        Dialog pgsDialog = SuperUtil.showProgressDialog(getActivity(), container);
        DatabaseReference rootReference = FirebaseDatabase.getInstance()
                .getReference();
        DatabaseReference fReference = rootReference
                .child(LEADER_BOARD_FIRE_BASE_REFERENCE);
        fAuth.signInWithEmailAndPassword(currentUser.getEmail(), currentUser.getPass())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        fReference.orderByChild(USER_COLUMN_VERSES_SCORE)
                                .limitToLast(Constants.LEADER_BOARD_USERS_LIMIT)
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        ArrayList<ItemUser> auxList = new ArrayList<>();
                                        if (pgsDialog.isShowing())
                                            pgsDialog.dismiss();
                                        for (DataSnapshot snapShot : dataSnapshot.getChildren()) {
                                            ItemUser user = new ItemUser();
                                            user.setId(snapShot.getKey());
                                            user.setName(snapShot.child(USER_COLUMN_NAME)
                                                    .getValue(String.class));
                                            user.setImg(snapShot.child(USER_COLUMN_IMG)
                                                    .getValue(String.class));
                                            user.setScore(Float
                                                    .parseFloat(Objects.requireNonNull(snapShot
                                                            .child(USER_COLUMN_VERSES_SCORE)
                                                            .getValue(String.class))));
                                            user.setPremium(Boolean.parseBoolean(snapShot
                                                    .child(USER_COLUMN_PREMIUM_STATUS).getValue(String.class)));
                                            auxList.add(user);

                                            Log.d(TAG, "onDataChange: LeaderBoard Users: " + user.toString());
                                        }

                                        if (!leaderBoardUsers.containsAll(auxList)) {
                                            leaderBoardUsers = new ArrayList<>(auxList);
                                            if (isVisible())
                                                showLeaderBoard(leaderBoardUsers);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        Log.d(TAG, "onCancelled: " + databaseError.getMessage());
                                        if (pgsDialog.isShowing())
                                            pgsDialog.dismiss();
                                        if (isVisible())
                                            MessagesHelper.showInfoMessageError(requireActivity(),
                                                    getString(R.string.failed_loading_user_data));
                                    }
                                });

                    } else {
                        if (pgsDialog.isShowing())
                            pgsDialog.dismiss();

                        if (task.getException() instanceof FirebaseNetworkException) {
                            if (isVisible())
                                MessagesHelper.showInfoMessageWarning(requireActivity(),
                                        getString(R.string.network_error));
                        } else {
                            if (isVisible())
                                MessagesHelper.showInfoMessageError(requireActivity(),
                                        getString(R.string.failed_loading_user_data));
                        }
                    }
                });
    }

    private void showLeaderBoard(ArrayList<ItemUser> users) {
        Collections.sort(users, SuperUtil.userComparator);
        adapter = new AdapterRecyclerViewForLeaderBoardList(users, users, requireActivity());
        recycler.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        recycler.startAnimation(animSlideInFromRight);
        if (leaderBoardUsers.size() >= 4) {
            SuperUtil.showView(animScaleUp, searcher);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        //Resetting Search View
        if (searcher != null && searcher.getVisibility() == View.VISIBLE) {
            searcher.setQuery("", false);
            searcher.onActionViewCollapsed();
            searcher.clearFocus();
            searcher.setIconified(true);
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        filter(newText);
        return true;
    }

    private void filter(@NotNull String query) {
        final String lowerCaseQuery = query.toLowerCase();
        ArrayList<ItemUser> filteredLeaderBoardUsers = new ArrayList<>();
        for (ItemUser user : leaderBoardUsers) {
            final String name = user.getName().toLowerCase();
            if (name.contains(lowerCaseQuery)) {
                filteredLeaderBoardUsers.add(user);
            }
        }
        adapter.filterList(filteredLeaderBoardUsers);
    }
}
