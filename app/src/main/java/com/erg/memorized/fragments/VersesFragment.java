package com.erg.memorized.fragments;


import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.erg.memorized.R;
import com.erg.memorized.adapters.AdapterRecyclerViewForVersesList;
import com.erg.memorized.helpers.MessagesHelper;
import com.erg.memorized.helpers.RealmHelper;
import com.erg.memorized.helpers.SharedPreferencesHelper;
import com.erg.memorized.interfaces.OnVerseListener;
import com.erg.memorized.model.ItemUser;
import com.erg.memorized.model.ItemVerse;
import com.erg.memorized.util.SuperUtil;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import static com.erg.memorized.util.Constants.USER_COLUMN_VERSES;
import static com.erg.memorized.util.Constants.USER_FIRE_BASE_REFERENCE;


public class VersesFragment extends Fragment implements View.OnClickListener, OnVerseListener,
        SearchView.OnQueryTextListener {

    public static String TAG = "AddEditTextFragment";

    private AdapterRecyclerViewForVersesList adapterForVersesList;
    private ArrayList<ItemVerse> verses;
    private ArrayList<ItemVerse> filteredVerseList;
    private RecyclerView recycler;
    private SearchView searcher;
    private FloatingActionButton fabAddVerse;
    private LinearLayout emptyContainerSignal;
    private View rootView;
    private ViewGroup container;

    private ItemVerse currentItemVerse;
    private ItemUser currentUser;
    private Animation animScaleUp, animScaleDown;
    private RealmHelper realmHelper;
    private SharedPreferencesHelper spHelper;
    private FirebaseAuth fAuth;

    public VersesFragment() {
        // Required empty public constructor
    }

    public static VersesFragment newInstance() {
        return new VersesFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        realmHelper = new RealmHelper(requireContext());

        animScaleUp = AnimationUtils.loadAnimation(requireContext(), R.anim.scale_up);
        animScaleDown = AnimationUtils.loadAnimation(requireContext(), R.anim.scale_down);

        verses = new ArrayList<>(realmHelper.getSavedVerses());
        spHelper = new SharedPreferencesHelper(requireContext());
        currentUser = realmHelper.getUser();
        fAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_verses, container, false);
        this.container = container;
        setUpView();
        return rootView;
    }

    private void setUpView() {

        emptyContainerSignal = rootView.findViewById(R.id.linear_layout_empty_container);
        emptyContainerSignal.setOnClickListener(this);

        fabAddVerse = rootView.findViewById(R.id.fab_add_verse);
        fabAddVerse.setOnClickListener(this);

        searcher = rootView.findViewById(R.id.searcher);
        searcher.setOnQueryTextListener(this);

        adapterForVersesList = new AdapterRecyclerViewForVersesList(
                verses, requireContext(), this);
        recycler = rootView.findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recycler.setLayoutManager(layoutManager);
        recycler.setHasFixedSize(true);
        recycler.setItemAnimator(new DefaultItemAnimator());
        recycler.setAdapter(adapterForVersesList);
        adapterForVersesList.notifyDataSetChanged();

        recycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    fabAddVerse.show();
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 || dy < 0 && fabAddVerse.isShown()) {
                    fabAddVerse.hide();
                }
            }
        });

        if (recycler.getVisibility() == View.GONE) {
            recycler.setVisibility(View.VISIBLE);
        }

        if (!verses.isEmpty()) {
            SuperUtil.showView(animScaleUp, searcher);
        }

        new Handler().postDelayed(() -> {
            if (isVisible()) {
                if (fabAddVerse.getVisibility() == View.GONE) {
                    fabAddVerse.setVisibility(View.VISIBLE);
                    fabAddVerse.startAnimation(animScaleUp);
                }
            }
        }, 600);

    }

    private void setUpEmptyContainer() {
        if (verses.isEmpty()) {
            SuperUtil.showView(animScaleUp, emptyContainerSignal);
            SuperUtil.hideView(animScaleUp, searcher);
        } else {
            SuperUtil.hideView(animScaleUp, emptyContainerSignal);
            SuperUtil.showView(animScaleUp, searcher);
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        filter(query);
        return true;
    }

    private void filter(@NotNull String query) {
        final String lowerCaseQuery = query.toLowerCase();
        filteredVerseList = new ArrayList<>();
        for (ItemVerse item : verses) {
            final String title = item.getTitle().toLowerCase();
            final String verseText = item.getVerseText().toLowerCase();
            if (title.contains(lowerCaseQuery) || verseText.contains(lowerCaseQuery)) {
                filteredVerseList.add(item);
            }
        }
        adapterForVersesList.filterList(filteredVerseList);
    }

    /*onClick on recycle view item*/
    @Override
    public void onVerseClick(int pos) {
        SuperUtil.vibrate(requireContext());
        spHelper.setSectionViewStatus(false);
        currentItemVerse = adapterForVersesList.getVerses().get(pos);
        SuperUtil.loadView(requireActivity(),
                MemorizingFragment.newInstance(currentItemVerse, false),
                MemorizingFragment.TAG, true);
    }

    @Override
    public void onVerseLongClick(int pos) {
        SuperUtil.vibrate(requireContext());
        showOnLongClickDialog(pos);
    }

    private void showOnLongClickDialog(int pos) {

        currentItemVerse = adapterForVersesList.getVerses().get(pos);

        final Dialog dialog = new Dialog(requireContext(), R.style.alert_dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_info_long_click_opt_view, null, false);
        TextView msg = dialogView.findViewById(R.id.text_dialog);
        Switch switchRemoveCloud = dialogView.findViewById(R.id.switch_remove_cloud_backup);

        HorizontalScrollView horizontalScrollView = dialogView
                .findViewById(R.id.horizontal_scroll_btn_container);

        if (spHelper.getUserLoginStatus()) {
            SuperUtil.showView(null, switchRemoveCloud);
        } else {
            SuperUtil.hideView(null, switchRemoveCloud);
        }

        msg.setText(R.string.msg_long_click_option);
        dialog.setContentView(dialogView);

        /*onClick on dialog cancel button*/
        Button cancelBtn = dialog.findViewById(R.id.cancel_dialog_button);
        cancelBtn.setOnClickListener(v -> {
            SuperUtil.vibrate(requireContext());
            if (dialog.isShowing())
                dialog.dismiss();
        });

        /*onClick on dialog edit button*/
        Button editBtn = dialog.findViewById(R.id.edit_dialog_button);
        editBtn.setOnClickListener(v -> {
            SuperUtil.vibrate(requireContext());
            NewVerseFragment newVerseFragment = NewVerseFragment
                    .newInstance(currentItemVerse, true);
            SuperUtil.loadView(requireActivity(), newVerseFragment,
                    NewVerseFragment.TAG, true);
            if (dialog.isShowing())
                dialog.dismiss();
        });

        /*onClick on dialog delete button*/
        Button deleteBtn = dialog.findViewById(R.id.delete_dialog_button);
        deleteBtn.setOnClickListener(v -> {
            SuperUtil.vibrate(requireContext());
            realmHelper.deleteVerseFromRealmDataBase(currentItemVerse);
            if (spHelper.getUserLoginStatus() && currentUser != null) {
                if (switchRemoveCloud.isChecked())
                    removeFromCloud();
            }
            if (isVisible())
                MessagesHelper.showInfoMessage(requireActivity(),
                        getString(R.string.successfully_deleted));
            if (currentItemVerse != null && !currentItemVerse.getTitle().isEmpty()
                    && spHelper.existKey(currentItemVerse.getTitle())) {
                spHelper.removeTextSizePref(currentItemVerse.getTitle());
            }
            verses = realmHelper.getSavedVerses();
            adapterForVersesList.refreshAdapter(verses);
            setUpEmptyContainer();

            if (dialog.isShowing())
                dialog.dismiss();
        });
        
        dialog.show();
        dialogView.startAnimation(animScaleUp);

        new Handler().postDelayed(() -> {
            if (dialog.isShowing()) {
                horizontalScrollView.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
            }
        }, 1000);

    }

    private void removeFromCloud() {

        verses = new ArrayList<>(realmHelper.getSavedVerses());

        DatabaseReference fReference = FirebaseDatabase.getInstance()
                .getReference(USER_FIRE_BASE_REFERENCE)
                .child(currentUser.getId())
                .child(USER_COLUMN_VERSES);

        Dialog pgsDialog = SuperUtil.showProgressDialog(requireActivity(), container);
        fAuth.signInWithEmailAndPassword(currentUser.getEmail(), currentUser.getPass())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        fReference.setValue(SuperUtil.getVersesIntoHasMapList(verses))
                                .addOnCompleteListener(task1 -> {
                                    Log.d(TAG, "removeFromCloud: " + task1.toString());
                                    if (pgsDialog.isShowing())
                                        pgsDialog.dismiss();

                                    if (task1.isSuccessful()) {
                                        if (isVisible())
                                            MessagesHelper.showInfoMessage(requireActivity(),
                                                    getString(R.string.cloud_successfully_deleted));
                                    } else {
                                        if (isVisible())
                                            MessagesHelper.showInfoMessageWarning(requireActivity(),
                                                    getString(R.string.failed_removing));
                                    }
                                });
                    } else {
                        if (pgsDialog.isShowing())
                            pgsDialog.dismiss();

                        if (task.getException() instanceof FirebaseNetworkException) {
                            if (isVisible())
                                MessagesHelper.showInfoMessageWarning(requireActivity(),
                                        getString(R.string.network_error));
                            Log.d(TAG, "upload: " + task.getException().getMessage());
                        } else {
                            if (isVisible())
                                MessagesHelper.showInfoMessageError(requireActivity(),
                                        getString(R.string.failed_uploading));
                        }
                    }
                });

    }

    /*onClick on Fab Button and empty view indicator*/
    @Override
    public void onClick(View v) {
        SuperUtil.vibrate(requireContext());
        int id = v.getId();
        if (id == R.id.linear_layout_empty_container || id == R.id.fab_add_verse) {
            NewVerseFragment frag = new NewVerseFragment(null, false);
            SuperUtil.loadView(requireActivity(), frag,
                    NewVerseFragment.TAG, true);
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
    public void onResume() {
        super.onResume();
        SuperUtil.showMeoBottomBar(requireActivity(), animScaleUp);
        verses = realmHelper.getSavedVerses();
        setUpEmptyContainer();
        if (adapterForVersesList != null && verses != null) {
            adapterForVersesList.refreshAdapter(verses);
        }
    }
}
