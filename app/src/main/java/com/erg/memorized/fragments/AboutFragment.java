package com.erg.memorized.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.erg.memorized.BuildConfig;
import com.erg.memorized.R;
import com.erg.memorized.util.SuperUtil;

import static com.erg.memorized.util.Constants.SPACE;

public class AboutFragment extends Fragment implements View.OnClickListener {

    public static final String TAG = "AboutFragment";


    private Animation animSlideInFromRight;

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

        animSlideInFromRight = AnimationUtils.loadAnimation(requireContext(),
                R.anim.fab_slide_in_from_right);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_about, container, false);

        setUpView();
        return rootView;
    }


    private void setUpView() {
        RelativeLayout rate_on_play_store_layout = rootView.findViewById(R.id.rate_on_play_store);
        RelativeLayout fallow_instagram_layout = rootView.findViewById(R.id.fallow_instagram);
        RelativeLayout contact_us_layout = rootView.findViewById(R.id.contact_us);
        RelativeLayout developer1_layout = rootView.findViewById(R.id.developer1);
        RelativeLayout developer2_layout = rootView.findViewById(R.id.developer2);

        setVersion();

        rate_on_play_store_layout.setOnClickListener(this);
        fallow_instagram_layout.setOnClickListener(this);
        contact_us_layout.setOnClickListener(this);
        developer1_layout.setOnClickListener(this);
        developer2_layout.setOnClickListener(this);
    }

    private void setVersion() {
        TextView tvVersion = rootView.findViewById(R.id.tv_app_version);
        String stringBuilder = getString(R.string.version)
                + SPACE + BuildConfig.VERSION_NAME;
        tvVersion.setText(stringBuilder);
    }

    @Override
    public void onClick(View v) {
        SuperUtil.vibrate(requireContext());
        switch (v.getId()) {
            case R.id.rate_on_play_store:
                rateOnPlayStore();
                break;
            case R.id.fallow_instagram:
                fallowOnInstagram();
                break;
            case R.id.contact_us:
                contactUs(getString(R.string.email_about_page));
                break;
            case R.id.developer1:
                lookInLinkedinProfile(getString(R.string.url_developer1_about_page));
                break;
            case R.id.developer2:
                lookInLinkedinProfile(getString(R.string.url_developer2_about_page));
                break;
        }
    }

    //Set intent to go to MarketPlace on play store and rate the app*
    private void rateOnPlayStore() {
        Uri uri = Uri.parse(getString(R.string.play_store_app_url));
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.setData(uri);
        rootView.getContext().startActivity(intent);
    }

    //Set intent to go to Linkedin with the exact profile of the developer*
    private void lookInLinkedinProfile(String developer) {
        Uri uri = Uri.parse(developer);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.setData(uri);
        startActivity(intent);
    }

    //Set intent to go to the exact address of DeMeMo Instagram page*
    private void fallowOnInstagram() {
        Uri uri = Uri.parse(getString(R.string.instagram_app_url));
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.setData(uri);
        startActivity(intent);
    }

    //Set intent to choose mail app and send to*
    private void contactUs(String email) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
        startActivity(intent);
    }

    @Override
    public void onStart() {
        super.onStart();
        rootView.startAnimation(animSlideInFromRight);
    }

}
