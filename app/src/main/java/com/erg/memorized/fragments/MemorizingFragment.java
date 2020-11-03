package com.erg.memorized.fragments;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.erg.memorized.R;
import com.erg.memorized.helpers.MessagesHelper;
import com.erg.memorized.helpers.RealmHelper;
import com.erg.memorized.helpers.ScoreHelper;
import com.erg.memorized.helpers.SharedPreferencesHelper;
import com.erg.memorized.helpers.TimeHelper;
import com.erg.memorized.model.ItemVerse;
import com.erg.memorized.util.Constants;
import com.erg.memorized.util.SuperUtil;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

import static android.content.Context.AUDIO_SERVICE;
import static android.content.Context.CLIPBOARD_SERVICE;
import static com.erg.memorized.util.Constants.FIX_SIZE;
import static com.erg.memorized.util.Constants.STATUS_BAR_MSG_KEY;


public class MemorizingFragment extends Fragment {

    public static final String TAG = "MemorizingFragment";
    private FloatingActionMenu fam;

    private TextToSpeech tts;
    private Locale current_locale;

    private TextView tv_title, tv_verse, tv_text_size_status;
    private ImageView ivSplitText, ivTest;
    private SeekBar seekBar;
    private float userTextSize;
    private View rootView;

    private boolean checkIfStored = false;

    private AudioManager audioManager;
    private AudioManager.OnAudioFocusChangeListener focusChangeListener;

    private Animation animScaleUp, animScaleDown, animSlideInFromRight;
    private ViewGroup container;
    private SharedPreferencesHelper spHelper;
    private RealmHelper realmHelper;
    private ItemVerse verse;

    private BottomNavigationView bnv;

    public MemorizingFragment() {
        // Required empty public constructor
    }

    public MemorizingFragment(ItemVerse verse, boolean checkIfStored) {
        this.verse = verse;
        this.checkIfStored = checkIfStored;
    }

    public static MemorizingFragment newInstance(ItemVerse verse, boolean checkIfStored) {
        return new MemorizingFragment(verse, checkIfStored);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initTTS();
        audioManager = (AudioManager) Objects.requireNonNull(getContext())
                .getSystemService(AUDIO_SERVICE);

        spHelper = new SharedPreferencesHelper(requireActivity());
        realmHelper = new RealmHelper(getContext());

        if (!spHelper.getSectionViewStatus())
            userTextSize = spHelper.getUserTextSizePref(verse.getTitle());
        else
            userTextSize = spHelper.getUserTextSizePref(spHelper.getCurrentSectionKey());

        animScaleUp = AnimationUtils.loadAnimation(getContext(), R.anim.less_scale_up);
        animScaleDown = AnimationUtils.loadAnimation(getContext(), R.anim.scale_down);
        animSlideInFromRight = AnimationUtils.loadAnimation(getContext(),
                R.anim.fab_slide_in_from_right);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        this.container = container;
        rootView = inflater.inflate(R.layout.fragment_memorizing, container, false);

        setUpView();

        return rootView;
    }

    private void setUpView() {

        tv_title = rootView.findViewById(R.id.tv_title_of_memory_text);
        tv_verse = rootView.findViewById(R.id.tv_verse_text);
        tv_text_size_status = rootView.findViewById(R.id.tv_text_size_status);

        seekBar = rootView.findViewById(R.id.seekBar_text_size);

        ivSplitText = rootView.findViewById(R.id.iv_section_view);
        ivTest = rootView.findViewById(R.id.iv_test_button);
        fam = rootView.findViewById(R.id.material_design_android_floating_action_menu);
        FloatingActionButton fam_play = rootView.findViewById(R.id.fam_play);
        FloatingActionButton fam_stop = rootView.findViewById(R.id.fam_stop);

        fam.hideMenuButton(false);

        tv_title.setText(verse.getTitle());
        tv_verse.setText(verse.getVerseText());

        registerForContextMenu(tv_title);
        registerForContextMenu(tv_verse);

        tv_title.setTextSize(TypedValue.COMPLEX_UNIT_SP, userTextSize + FIX_SIZE + 4);
        tv_verse.setTextSize(TypedValue.COMPLEX_UNIT_SP, userTextSize + FIX_SIZE);

        ivSplitText.setOnClickListener(v -> {
            SuperUtil.vibrate(requireContext());
            SuperUtil.loadView(requireActivity(), SplitTextFragment.newInstance(verse),
                    SplitTextFragment.TAG, true);
        });

        ivTest.setOnClickListener(v -> {
            SuperUtil.vibrate(requireContext());
            if (spHelper.getUserLoginStatus()) {
                SuperUtil.loadView(requireActivity(), ScorerFragment.newInstance(verse),
                        ScorerFragment.TAG, true);
            } else {
                if (isVisible())
                    MessagesHelper.showInfoMessageFragment(rootView, requireActivity(),
                            getString(R.string.login_needed));
            }
        });

        fam_play.setOnClickListener(v -> {
            if (!tts.isSpeaking()) {
                int result = tts.isLanguageAvailable(current_locale);
                if (result == TextToSpeech.LANG_MISSING_DATA
                        || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    if (isVisible())
                        MessagesHelper.showInfoMessageWarning(requireActivity(),
                                getString(R.string.language_not_supported));
                    Log.d(TAG, "setUpView: " + result);
                } else {
                    if (requestAudioFocus()) {
                        tts.speak(verse.getVerseText(),
                                TextToSpeech.QUEUE_FLUSH, null, null);
                    }
                }
            }
        });

        fam_stop.setOnClickListener(v -> {
            if (tts.isSpeaking()) {
                abandonAudioFocus();
                tts.stop();
            }
        });

        setUpBottomNavigationBar();
    }

    private void showBottomNavigationView() {
        if (bnv != null) {
            SuperUtil.showView(animScaleUp, bnv);
        }
    }

    private void setUpBottomNavigationBar() {

        bnv = rootView.findViewById(R.id.bottom_navigation_full_screen);

        bnv.setOnNavigationItemSelectedListener(item -> {
            SuperUtil.vibrate(requireContext());

            switch (item.getItemId()) {
                case R.id.action_free_distraction:
                    fam.hideMenuButton(true);
                    changeVisibilityOfSeekBar(false);
                    break;
                case R.id.action_speak:
                    fam.showMenuButton(true);
                    changeVisibilityOfSeekBar(false);
                    if (!spHelper.isAudioMessageViewed()) {
                        if (isVisible())
                            MessagesHelper.showInfoMessageWithDismiss(rootView, requireActivity(),
                                    getString(R.string.hide_fam),
                                    Constants.AUDIO_MSG_KEY, spHelper);
                    }
                    break;
                case R.id.action_change_text_size:
                    fam.hideMenuButton(true);
                    changeVisibilityOfSeekBar(true);
                    configSeekBarForChangeTextSize();
                    if (!spHelper.isStatusBarMessageViewed()) {
                        if (isVisible())
                            MessagesHelper.showInfoMessageWithDismiss(rootView, requireActivity(),
                                    getString(R.string.hide_status_text_size_bar),
                                    STATUS_BAR_MSG_KEY, spHelper);
                    }
                    break;
                default:
                    break;
            }
            return true;
        });

        bnv.setOnNavigationItemReselectedListener(item -> {
            SuperUtil.vibrate(requireContext());

            switch (item.getItemId()) {
                case R.id.action_speak:
                    if (!fam.isMenuButtonHidden())
                        fam.hideMenuButton(true);
                    else
                        fam.showMenuButton(true);
                    break;
                case R.id.action_change_text_size:
                    if (seekBar.getVisibility() == View.VISIBLE)
                        changeVisibilityOfSeekBar(false);
                    else if (seekBar.getVisibility() == View.GONE)
                        changeVisibilityOfSeekBar(true);
                    break;
                default:
                    break;
            }
        });
    }

    private boolean requestAudioFocus() {
        focusChangeListener = focusChange -> {
        };
        if (audioManager != null) {
            int result = audioManager.requestAudioFocus(focusChangeListener,
                    AudioManager.STREAM_MUSIC,
                    AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_EXCLUSIVE);
            return result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
        }
        return false;
    }

    private void abandonAudioFocus() {
        audioManager.abandonAudioFocus(focusChangeListener);
    }

    private void configSeekBarForChangeTextSize() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            seekBar.setMin(Constants.MIN_TEXT_SIZE);
        }

        int defaultSeekProgress = Math.round(userTextSize);
        seekBar.setProgress(defaultSeekProgress);
        tv_text_size_status.setText(String.valueOf(defaultSeekProgress));

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && progress > 10 && progress <= 28) {
                    SuperUtil.vibrateMin(requireContext());
                    int aux = progress + FIX_SIZE;
                    tv_verse.setTextSize(TypedValue.COMPLEX_UNIT_SP, aux);
                    tv_title.setTextSize(TypedValue.COMPLEX_UNIT_SP, aux + 4);
                    tv_text_size_status.setText(String.valueOf(progress));
                    tv_verse.scrollTo(0, 0);

                    userTextSize = progress;
                    if (!spHelper.getSectionViewStatus())
                        spHelper.saveUserTextSizePref(verse.getTitle(), userTextSize);
                    else
                        spHelper.saveUserTextSizePref(spHelper.getCurrentSectionKey(), userTextSize);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    private void changeVisibilityOfSeekBar(boolean flag) {
        if (flag && seekBar.getVisibility() == View.GONE) {
            seekBar.setVisibility(View.VISIBLE);
            tv_text_size_status.setVisibility(View.VISIBLE);
        } else if (!flag && seekBar.getVisibility() == View.VISIBLE) {
            seekBar.setVisibility(View.GONE);
            tv_text_size_status.setVisibility(View.GONE);
        }
    }

    private void initTTS() {
        tts = new TextToSpeech(getContext(), status -> {
            if (status != TextToSpeech.ERROR) {
                current_locale = getResources().getConfiguration().locale;
                int result = tts.isLanguageAvailable(current_locale);
                if (result != TextToSpeech.LANG_MISSING_DATA
                        && result != TextToSpeech.LANG_NOT_SUPPORTED) {
                    tts.setLanguage(current_locale);
                }
            }
        });
    }

    private void saveUsageScoreByWeekDay() {

        String dayCodeKey;
        String[] dayCodes = getResources().getStringArray(R.array.day_codes);
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        int day = calendar.get(Calendar.DAY_OF_WEEK);


        long currentTime = System.currentTimeMillis();
        long openTimeUsage = spHelper.getMemorizingFragmentUsageOpenTime();
        long diff = TimeHelper.getDifferenceInMinutes(currentTime, openTimeUsage);
        float usageScore = ScoreHelper.calculateUsageScore(diff);

        Log.d(TAG, "saveUsageScoreByWeekDay: Memorizing difference: " + diff);
        Log.d(TAG, "saveUsageScoreByWeekDay: UsageScore: " + usageScore);

        switch (day) {
            case Calendar.MONDAY:
                dayCodeKey = dayCodes[0] + 1;
                spHelper.increasesUsageValue(dayCodeKey, usageScore);
                break;
            case Calendar.TUESDAY:
                dayCodeKey = dayCodes[1] + 2;
                spHelper.increasesUsageValue(dayCodeKey, usageScore);
                break;
            case Calendar.WEDNESDAY:
                dayCodeKey = dayCodes[2] + 3;
                spHelper.increasesUsageValue(dayCodeKey, usageScore);
                break;
            case Calendar.THURSDAY:
                dayCodeKey = dayCodes[3] + 4;
                spHelper.increasesUsageValue(dayCodeKey, usageScore);
                break;
            case Calendar.FRIDAY:
                dayCodeKey = dayCodes[4] + 5;
                spHelper.increasesUsageValue(dayCodeKey, usageScore);
                break;
            case Calendar.SATURDAY:
                dayCodeKey = dayCodes[5] + 6;
                spHelper.increasesUsageValue(dayCodeKey, usageScore);
                break;
            case Calendar.SUNDAY:
                dayCodeKey = dayCodes[6] + 7;
                spHelper.increasesUsageValue(dayCodeKey, usageScore);
                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        rootView.startAnimation(animSlideInFromRight);
        if (checkIfStored) {
            boolean verseExistByTitle = realmHelper.findItemVerseByTitle(verse.getTitle()) != null;
            boolean verseExistByVerseText = realmHelper.findItemVerseByText(verse.getVerseText()) != null;
            if (!verseExistByTitle && !verseExistByVerseText) {
                new Handler().postDelayed(() -> {
                    if (isVisible()) {
                        MessagesHelper.showSaveVerseDialog(requireActivity(), container,
                                animScaleUp, verse);
                        checkIfStored = false;
                    }
                }, 800);
            }
        }
        if (!spHelper.getDialogSplitInfoStatus()) {
            handleShowDialogtDialogSplit();
        } else if (!spHelper.getDialogAskToDoTestStatus()) {
            handleShowDialogAskToDoTest();
        }
        spHelper.saveMemorizingFragmentUsageOpenTime(System.currentTimeMillis());
    }

    private void handleShowDialogtDialogSplit() {
        new Handler().postDelayed(() -> {
            if (isVisible()) {
                MessagesHelper.showDialogtDialogSplit(requireActivity(),
                        container, animScaleUp);
            }
        }, 1000);
    }

    private void handleShowDialogAskToDoTest() {
        new Handler().postDelayed(() -> {
            if (isVisible()) {
                MessagesHelper.showDialogAskToDoTest(requireActivity(), container, animScaleUp,
                        verse);
            }
        }, 1000);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (tts != null) {
            tts.shutdown();
            abandonAudioFocus();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (tts != null) {
            tts.shutdown();
            abandonAudioFocus();
        }
        if (spHelper.getSectionViewStatus()) {
            rootView.startAnimation(animScaleDown);
        }
        saveUsageScoreByWeekDay();
        spHelper.saveLastVerseRead(verse);
        spHelper.saveLastVerseReadDate(System.currentTimeMillis());
    }

    @Override
    public void onResume() {
        super.onResume();
        initTTS();
        SuperUtil.hideMeoBottomBar(requireActivity(), animScaleDown);
        showBottomNavigationView();

        if (spHelper.getSectionViewStatus()) {
            SuperUtil.hideView(animScaleDown, ivSplitText);
            SuperUtil.hideView(animScaleDown, ivTest);
        }
    }

    @Override
    public void onCreateContextMenu(
            @NonNull ContextMenu menu, @NonNull View v,
            @Nullable ContextMenu.ContextMenuInfo menuInfo) {

        menu.add(0, v.getId(), 0, getString(R.string.copy));
        TextView tv = (TextView) v;
        ClipboardManager cm = (ClipboardManager)
                requireContext().getSystemService(CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText(getString(R.string.text), tv.getText());
        assert cm != null;
        cm.setPrimaryClip(clipData);
    }
}
