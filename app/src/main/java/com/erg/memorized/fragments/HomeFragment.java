package com.erg.memorized.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;
import com.erg.memorized.R;
import com.erg.memorized.helpers.MessagesHelper;
import com.erg.memorized.helpers.SharedPreferencesHelper;
import com.erg.memorized.helpers.TimeHelper;
import com.erg.memorized.interfaces.BibleApi;
import com.erg.memorized.interfaces.YourVersionApi;
import com.erg.memorized.model.DailyVerse;
import com.erg.memorized.model.ItemVerse;
import com.erg.memorized.model.VerseBible;
import com.erg.memorized.model.bible_api_models.Bible;
import com.erg.memorized.model.bible_api_models.Child;
import com.erg.memorized.model.bible_api_models.Children;
import com.erg.memorized.model.bible_api_models.Item;
import com.erg.memorized.util.SuperUtil;
import com.erg.memorized.views.CustomLineView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.erg.memorized.util.Constants.BIBLE_API_BASE_URL;
import static com.erg.memorized.util.Constants.DEFAULT_VERSION_ID;
import static com.erg.memorized.util.Constants.JSON;
import static com.erg.memorized.util.Constants.YOUR_VERSION_BASE_URL;

public class HomeFragment extends Fragment implements View.OnClickListener {

    public static String TAG = "HomeFragment";

    private View rootView;
    private ViewGroup container;
    private TextView tvDailyVerseTitle, tvDailyVerseText, tvDailyDate;
    private ImageButton ibRefreshDailyVerse;
    private LinearLayout userActivityContainer;
    private RelativeLayout rlVDailyVerse;
    private ProgressBar pgrView, refreshPgrView;
    private LottieAnimationView lottieNotFound;
    private LinearLayout lastReading, staticsGraph;
    private SharedPreferencesHelper spHelper;
    private Animation animScaleUp, animScaleDown;
    private boolean isRefreshingAction = false;

    private ItemVerse dailyVerse;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        spHelper = new SharedPreferencesHelper(requireContext());
        animScaleUp = AnimationUtils.loadAnimation(requireContext(), R.anim.scale_up);
        animScaleDown = AnimationUtils.loadAnimation(requireContext(), R.anim.scale_down);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.container = container;
        rootView = inflater.inflate(R.layout.fragment_home, container, false);
        setUpView();
        return rootView;
    }

    private void setUpView() {
        tvDailyVerseTitle = rootView.findViewById(R.id.tv_daily_verse_title);
        tvDailyVerseText = rootView.findViewById(R.id.tv_daily_verse_text);
        tvDailyDate = rootView.findViewById(R.id.tv_daily_date);
        ibRefreshDailyVerse = rootView.findViewById(R.id.ib_refresh_daily_verse);
        LinearLayout dailyRootVerseContainer = rootView.findViewById(R.id.ll_daily_verse_container);
        userActivityContainer = rootView.findViewById(R.id.ll_activity_history_container);
        rlVDailyVerse = rootView.findViewById(R.id.rl_daily_verse);
        pgrView = rootView.findViewById(R.id.daily_progress_circular);
        refreshPgrView = rootView.findViewById(R.id.refresh_daily_progress_circular);
        lottieNotFound = rootView.findViewById(R.id.lottie_anim_empty);
        dailyRootVerseContainer.setOnClickListener(this);
        ibRefreshDailyVerse.setOnClickListener(this);

        dailyVerse = spHelper.getDailyVerse();
        String verseDate = "";
        if (dailyVerse != null) {
            verseDate = TimeHelper.dateFormatterShort(dailyVerse.getId());
        }

        String currentDate = TimeHelper.dateFormatterShort(System.currentTimeMillis());

        Log.d(TAG, "setUpView: CurrentDate: " + currentDate + "\n"
                + " VerseDate: " + verseDate);
        if (currentDate.equals(verseDate)) {
            setDailyVerse(dailyVerse, false);
        } else {
            int dayOfTheYear = TimeHelper.getDayOfTheYear();
            getDailyVerse(dayOfTheYear);
        }

        addUserActivityViews();
    }

    private void addUserActivityViews() {
        lastReading = (LinearLayout) getLayoutInflater()
                .inflate(R.layout.user_activity_last_reading_view, container, false);
        staticsGraph = (LinearLayout) getLayoutInflater()
                .inflate(R.layout.user_statics_graphic_view, container, false);

        setLastReading();
        setStatics();

        userActivityContainer.addView(lastReading);
        userActivityContainer.addView(staticsGraph);
    }

    private void setLastReading() {

        TextView tvDate = lastReading.findViewById(R.id.tv_user_last_reading_date);
        TextView tvTitle = lastReading.findViewById(R.id.tv_last_reading_verse_title);
        TextView tvVerseText = lastReading.findViewById(R.id.tv_last_reading_verse_text);

        ItemVerse verse = spHelper.getLastVerseRead();
        long lastReadDate = spHelper.getLastVerseReadDate();

        if (verse != null) {
            String lastDate = TimeHelper.getDisplayableTime(requireContext(), lastReadDate);
            String lastVerseTitle = verse.getTitle();
            String lastVerseText = verse.getVerseText();

            tvDate.setText(lastDate);
            tvTitle.setText(lastVerseTitle);
            tvVerseText.setText(lastVerseText);

            setUpOnClinkListener(verse);

        } else {
            SuperUtil.hideView(null, lastReading);
        }

    }

    private void setUpOnClinkListener(ItemVerse verse) {
        lastReading.setOnClickListener(v -> {
            SuperUtil.vibrate(requireContext());
            SuperUtil.loadView(
                    requireActivity(), MemorizingFragment.newInstance(verse, true),
                    MemorizingFragment.TAG,
                    true
            );
        });
    }

    private void setStatics() {

        ArrayList<Float> userActivity = spHelper.getUserActivity();
        ArrayList<String> weekDays = TimeHelper.getCurrentWeekDays();
        int[] colors = new int[]{
                requireActivity().getColor(R.color.red_default),
        };

        Log.d(TAG, " weekDays: " + weekDays.toString());
        Log.d(TAG, " userActivity: " + userActivity.toString());

        CustomLineView customLineView = staticsGraph.findViewById(R.id.custom_line_view);
        customLineView.setDrawDotLine(false);
        customLineView.setShowPopup(CustomLineView.SHOW_POPUPS_MAX_MIN_ONLY);
        customLineView.setColorArray(colors);

        customLineView.setBottomTextList(weekDays);
        ArrayList<ArrayList<Float>> dataList = new ArrayList<>(Collections.singleton(userActivity));
        customLineView.setFloatDataList(dataList);
    }

    private void setDailyVerse(ItemVerse dailyVerse, boolean animOn) {
        if (animOn) {
            showDailyContentWhitAnim();
        } else {
            showDailyContentWithOtAnim();
        }

        SuperUtil.hideView(animScaleDown, lottieNotFound);
        if (isRefreshingAction) {
            hideRefreshingProgress();
        }

        tvDailyDate.setText(TimeHelper.dateFormatterShort(dailyVerse.getId()));
        tvDailyVerseTitle.setText(dailyVerse.getTitle());
        tvDailyVerseText.setText(dailyVerse.getVerseText());

    }

    private void showDailyContentWhitAnim() {
        SuperUtil.hideView(animScaleDown, pgrView);
        SuperUtil.showView(animScaleUp, rlVDailyVerse);
    }

    private void response404() {
        SuperUtil.hideView(null, pgrView);
        SuperUtil.hideView(null, rlVDailyVerse);
        SuperUtil.showView(animScaleUp, lottieNotFound);

        if (isVisible()) {
            MessagesHelper.showInfoMessageWarning(requireActivity(),
                    getString(R.string.response_404));
        }
    }

    private void showDailyContentWithOtAnim() {
        SuperUtil.hideView(null, pgrView);
        SuperUtil.showView(null, rlVDailyVerse);
    }

    @Override
    public void onClick(View v) {
        SuperUtil.vibrate(requireContext());
        switch (v.getId()) {
            case R.id.ll_daily_verse_container:
                SuperUtil.loadView(requireActivity(),
                        MemorizingFragment.newInstance(dailyVerse,
                                true),
                        MemorizingFragment.TAG, true);
                break;
            case R.id.ib_refresh_daily_verse:
                isRefreshingAction = true;
                int dayOfTheYear = TimeHelper.getDayOfTheYear();
                getDailyVerse(dayOfTheYear);
                break;
        }
    }

    private void showRefreshingProgress() {
        SuperUtil.hideView(null, ibRefreshDailyVerse);
        SuperUtil.showView(null, refreshPgrView);
    }

    private void hideRefreshingProgress() {
        SuperUtil.showView(null, ibRefreshDailyVerse);
        SuperUtil.hideView(null, refreshPgrView);
    }

    private void getDailyVerse(int dayOfTheYear) {

        if (isRefreshingAction) {
            showRefreshingProgress();
        } else {
            SuperUtil.showView(animScaleUp, pgrView);
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(YOUR_VERSION_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        YourVersionApi yourVersionApi = retrofit.create(YourVersionApi.class);
        Call<DailyVerse> call = yourVersionApi.getDailyVerse(dayOfTheYear, DEFAULT_VERSION_ID);

        call.enqueue(new Callback<DailyVerse>() {
            @Override
            public void onResponse(Call<DailyVerse> call, Response<DailyVerse> response) {
                if (!response.isSuccessful()) {
                    Log.d(TAG, "YourVersionApi onResponse: Not Successful  Code: "
                            + response.code()
                            + "\n" + response.message());
                    SuperUtil.hideView(animScaleDown, pgrView);
                    if (isRefreshingAction)
                        hideRefreshingProgress();
                    response404();
                    return;
                }

                DailyVerse dailyVerse = response.body();
                if (dailyVerse != null) {
                    ArrayList<String> versesIds = dailyVerse.getVerse().getUsfms();
                    if (!versesIds.isEmpty()) {
                        getVerseFromBibleApi(versesIds.get(0));
                    }
                }

                assert dailyVerse != null;
                Log.d(TAG, "YourVersionApi onResponse: Success VERSE: "
                        + dailyVerse.getVerse().toString());
            }

            @Override
            public void onFailure(Call<DailyVerse> call, Throwable t) {
                Log.e(TAG, "YourVersionApi onFailure: " + t.getMessage());
                SuperUtil.hideView(animScaleDown, pgrView);
                if (isRefreshingAction)
                    hideRefreshingProgress();
                response404();
            }
        });
    }

    private void getVerseFromBibleApi(String verseId) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BIBLE_API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        Bible bible = spHelper.getBibleVersion();
        BibleApi bibleApi = retrofit.create(BibleApi.class);
        Log.d(TAG, "getVerseFromBibleApi: PARAMS: "
                + "BIBLE ID: " + bible.getId() + " VERSE ID: " + verseId
                + "\n Bible: " + bible.toString());

        Call<VerseBible> call = bibleApi.getVerse(bible.getId(), verseId, JSON,
                false, false, false,
                false, false, false);

        call.enqueue(new Callback<VerseBible>() {
            @Override
            public void onResponse(@NotNull Call<VerseBible> call, @NotNull Response<VerseBible> response) {
                if (!response.isSuccessful()) {
                    Log.d(TAG, "BibleApi onResponse: Not Successful  Code: " + response.code()
                            + "\n" + response.message());
                    SuperUtil.hideView(animScaleDown, pgrView);
                    if (isRefreshingAction)
                        hideRefreshingProgress();
                    response404();
                    return;
                }
                VerseBible verseBible = response.body();
                assert response.body() != null;
                Log.d(TAG, "onResponse: BODY: " + response.body().toString());
                assert verseBible != null;
                Log.d(TAG, " onResponse: VerseBible: " + verseBible.toString());

                setDailyVerse(toItemVerse(verseBible), true);

                Log.d(TAG, "BibleApi onResponse: " + verseBible.toString());
            }

            @Override
            public void onFailure(Call<VerseBible> call, Throwable t) {
                Log.e(TAG, "BibleApi onFailure: Error: " + t.getMessage());
                SuperUtil.hideView(animScaleDown, pgrView);
                if (isRefreshingAction)
                    hideRefreshingProgress();
                response404();
            }
        });
    }

    private ItemVerse toItemVerse(VerseBible verseBible) {
        String title = verseBible.getData().getReference();
        String verse = getVerseText(verseBible.getData().getContent());
        long date = System.currentTimeMillis();

        dailyVerse = new ItemVerse(title, verse);
        dailyVerse.setId(date);
        spHelper.saveDailyVerse(dailyVerse);

        return dailyVerse;
    }


    private String getVerseText(ArrayList<Children> children) {

        StringBuilder builder = new StringBuilder();
        for (Children childrenChild : children) {
            if (childrenChild != null && childrenChild.getItems() != null) {
                for (Item item : childrenChild.getItems()) {
                    if (item != null) {
                        if (item.getText() != null) {
                            builder.append(item.getText());
                        } else if (item.getItems() != null)
                            for (Child child : item.getItems()) {
                                if (child != null) {
                                    if (child.getText() != null) {
                                        builder.append(child.getText());
                                    } else if (child.getItems() != null) {
                                        for (Item subChild : child.getItems()) {
                                            if (subChild != null) {
                                                if (subChild.getText() != null) {
                                                    builder.append(subChild.getText());
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                    }
                }
            }
        }
        return builder.toString();
    }

    @Override
    public void onResume() {
        super.onResume();
        SuperUtil.showMeoBottomBar(requireActivity(), animScaleUp);
        setLastReading();
        setStatics();
    }
}
