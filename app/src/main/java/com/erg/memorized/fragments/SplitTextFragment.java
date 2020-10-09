package com.erg.memorized.fragments;

import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CompoundButton;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.erg.memorized.R;
import com.erg.memorized.adapters.AdapterRecyclerViewForSplitVerse;
import com.erg.memorized.helpers.MessagesHelper;
import com.erg.memorized.helpers.ReaderHelper;
import com.erg.memorized.helpers.RealmHelper;
import com.erg.memorized.helpers.SharedPreferencesHelper;
import com.erg.memorized.interfaces.OnSectionListener;
import com.erg.memorized.model.ItemUser;
import com.erg.memorized.model.ItemVerse;
import com.erg.memorized.util.SuperUtil;
import com.etebarian.meowbottomnavigation.MeowBottomNavigation;

import java.util.ArrayList;

import static com.erg.memorized.util.Constants.SPACE;

public class SplitTextFragment extends Fragment implements OnSectionListener {

    public static final String TAG = "SplitTextFragment";
    private View rootView;

    private AdapterRecyclerViewForSplitVerse adapter;
    private RecyclerView recycler;
    private ArrayList<String> splitVerseList;

    private Animation animScaleUp, animScaleDown;
    private ViewGroup container;

    private SharedPreferencesHelper spHelper;

    private ItemVerse verse;
    private ItemUser currentUser;


    private MeowBottomNavigation meoBottomBar;
//    public boolean isSectionText = false;

    private boolean maybeWantToExit = false;

    public static SplitTextFragment newInstance(ItemVerse verse) {
        return new SplitTextFragment(verse);
    }

    public SplitTextFragment(ItemVerse verse) {
        this.verse = verse;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        spHelper = new SharedPreferencesHelper(requireActivity());
        RealmHelper realmHelper = new RealmHelper(requireContext());
        currentUser = realmHelper.getUser();

        meoBottomBar = requireActivity().findViewById(R.id.meow_bottom_navigation);

        animScaleUp = AnimationUtils.loadAnimation(getContext(), R.anim.less_scale_up);
        animScaleDown = AnimationUtils.loadAnimation(getContext(), R.anim.scale_down);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_split_text, container, false);
        this.container = container;
        setUpMainView(rootView);

        return rootView;
    }

    private void setUpMainView(View rootView) {

        recycler = rootView.findViewById(R.id.recycler_view_split);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recycler.setLayoutManager(layoutManager);
        recycler.setHasFixedSize(true);
        recycler.setItemAnimator(new DefaultItemAnimator());

        //Dividing text asynchronously
        new AsyncTaskSplit().execute();

    }

    private void showSelectedSection(int pos) {
        String sectionTitle = getString(R.string.section) + SPACE + (pos + 1);
        ItemVerse sectionVerse = new ItemVerse(sectionTitle, splitVerseList.get(pos));
        SuperUtil.loadView(requireActivity(), MemorizingFragment.newInstance(
                sectionVerse, false),
                MemorizingFragment.TAG, true);
    }

    @Override
    public void onSectionClick(int pos) {
        SuperUtil.vibrate(requireContext());
        String sectionKey = verse.getTitle() + getString(R.string.section) + (pos + 1);
        spHelper.setCurrentSectionKey(sectionKey);
        spHelper.setSectionViewStatus(true);
        showSelectedSection(pos);
    }


    @Override
    public void onCheckedChanged(int position, CompoundButton buttonView, boolean isChecked) {
        spHelper.setSectionCheckedStatus(verse.getTitle(), position, isChecked);
    }

    @Override
    public void isFullyCheckedListener() {
        if (currentUser != null && currentUser.getEmail() != null) {
            if (currentUser.isPremium()) {
                showTestDialog();
            } else {
                handleSimpleMsg();
            }
        }
    }

    private void handleSimpleMsg() {
        new Handler().postDelayed(() -> {
            if (isVisible()) {
                MessagesHelper.showInfoMessage(requireActivity(),
                        getString(R.string.memorizing_done_msg));
            }

        }, 1000);
    }

    private void showTestDialog() {
        new Handler().postDelayed(() -> {
            if (isVisible())
                MessagesHelper.showTestDialog(requireActivity(), verse);
        }, 1000);
    }

    private class AsyncTaskSplit extends AsyncTask<Void, Void, Void> {

        Dialog loadingDialog;
        ReaderHelper readerHelper;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingDialog = SuperUtil.showProgressDialog(getActivity(), container);
            splitVerseList = new ArrayList<>();
            readerHelper = new ReaderHelper(verse.getVerseText());
        }

        @Override
        protected Void doInBackground(Void... voids) {

            splitVerseList = readerHelper.getsSplitTextIntoList();
            adapter = new AdapterRecyclerViewForSplitVerse(splitVerseList,
                    verse.getTitle(), getContext(), SplitTextFragment.this);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            if (loadingDialog.isShowing())
                loadingDialog.dismiss();

            recycler.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        rootView.startAnimation(animScaleUp);
//        hideMeoBottomBar();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
//        showMeoBottomBar();
    }

/*
    private void showMeoBottomBar() {
        if (meoBottomBar != null) {
            meoBottomBar.setAnimation(animScaleUp);
            if (meoBottomBar.getVisibility() == View.GONE)
                meoBottomBar.setVisibility(View.VISIBLE);
        }
    }
    private void hideMeoBottomBar() {
        if (meoBottomBar != null) {
            meoBottomBar.setAnimation(animScaleUp);
            if (meoBottomBar.getVisibility() == View.GONE)
                meoBottomBar.setVisibility(View.VISIBLE);
        }
    }*/
}
