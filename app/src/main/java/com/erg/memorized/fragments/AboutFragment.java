package com.erg.memorized.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.erg.memorized.R;

public class AboutFragment extends Fragment {

    public static final String TAG = "AboutFragment";

    private View rootView;

    public AboutFragment() {
        // Required empty public constructor
    }
    public static AboutFragment newInstance() {
        return new AboutFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_about, container, false);
        setUpView();
        return rootView;
    }

    private void setUpView() {

    }
}