package com.erg.memorized.fragments;

import android.content.ActivityNotFoundException;
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

import static com.erg.memorized.util.Constants.GOOGLE_APP_DETAILS_URL;
import static com.erg.memorized.util.Constants.INSTAGRAM_APP_ACCOUNT_URL;
import static com.erg.memorized.util.Constants.INSTAGRAM_PACKAGE;
import static com.erg.memorized.util.Constants.LINKEDIN_PACKAGE;
import static com.erg.memorized.util.Constants.MARKET_APP_DETAILS_URL;
import static com.erg.memorized.util.Constants.SPACE;
import static com.erg.memorized.util.Constants.URL_DEVELOPER_1;
import static com.erg.memorized.util.Constants.URL_DEVELOPER_2;

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
                lookInLinkedinProfile(URL_DEVELOPER_1);
                break;
            case R.id.developer2:
                lookInLinkedinProfile(URL_DEVELOPER_2);
                break;
        }
    }

    //Set intent to go to MarketPlace on play store and rate the app*
    private void rateOnPlayStore() {
        try {
            Intent market = new Intent(Intent.ACTION_VIEW, Uri.parse(MARKET_APP_DETAILS_URL
                    + requireActivity().getPackageName()));
            startActivity(market);
        } catch (android.content.ActivityNotFoundException e) {
            Intent googlePlayStore = new Intent(Intent.ACTION_VIEW, Uri.parse(GOOGLE_APP_DETAILS_URL
                    + requireActivity().getPackageName()));
            startActivity(googlePlayStore);
        }
    }

    //Set intent to go to Linkedin with the exact profile of the developer*
    private void lookInLinkedinProfile(String developer) {
        try {
            Uri uri = Uri.parse(developer);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.setPackage(LINKEDIN_PACKAGE);
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(developer)));
        }
    }

    //Set intent to go to the exact address of DeMeMo Instagram page*
    private void fallowOnInstagram() {
        try {
            Uri uri = Uri.parse(INSTAGRAM_APP_ACCOUNT_URL);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.setPackage(INSTAGRAM_PACKAGE);
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse(INSTAGRAM_APP_ACCOUNT_URL)));
        }
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
