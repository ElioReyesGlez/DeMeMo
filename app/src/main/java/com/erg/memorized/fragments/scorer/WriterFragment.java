package com.erg.memorized.fragments.scorer;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.erg.memorized.R;
import com.erg.memorized.adapters.AdapterScorerFragmentPager;
import com.erg.memorized.fragments.ScorerFragment;
import com.erg.memorized.helpers.MessagesHelper;
import com.erg.memorized.helpers.ScoreHelper;
import com.erg.memorized.helpers.SharedPreferencesHelper;
import com.erg.memorized.helpers.TextHelper;
import com.erg.memorized.interfaces.ScorerListener;
import com.erg.memorized.model.ItemVerse;
import com.erg.memorized.model.Score;
import com.erg.memorized.util.Constants;
import com.erg.memorized.util.SuperUtil;
import com.erg.memorized.views.CustomViewPager;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Objects;

import static androidx.annotation.Dimension.SP;
import static com.erg.memorized.util.Constants.REGEX_SPACE;
import static com.erg.memorized.util.Constants.SPACE;

public class WriterFragment extends Fragment implements ScorerListener {

    public static final String TAG = "WriterFragment";

    private View rootView;
    private LinearLayout boxesContainer;

    private ItemVerse verse;

    private Animation animScaleDown;
    private ScorerFragment scorerFragment;

    private ArrayList<String> dividedText;
    private ArrayList<String> textWithLowLinesAuxList;
    private ArrayList<Integer> posReplacementsWords;
    private ArrayList<TextInputEditText> inputEditTexts;

    SharedPreferencesHelper spHelper;

    public WriterFragment() {
        // Required empty public constructor
    }

    public WriterFragment(ItemVerse verse,
                          ScorerFragment scorerFragment) {
        this.scorerFragment = scorerFragment;
        this.verse = verse;
    }


    public static WriterFragment newInstance(ItemVerse verse,
                                             ScorerFragment scorerFragment) {
        return new WriterFragment(verse, scorerFragment);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dividedText = new ArrayList<>();
        if (verse != null) {
            dividedText = TextHelper.getDividedText(verse.getVerseText());
        }

        posReplacementsWords = new ArrayList<>(
                TextHelper.getPosReplacementsWords(dividedText,
                        Constants.getMarks(requireActivity()))
        );

        textWithLowLinesAuxList = new ArrayList<>(
                TextHelper.getTextWithLowLines(dividedText, posReplacementsWords)
        );

        inputEditTexts = new ArrayList<>();

        animScaleDown = AnimationUtils.loadAnimation(requireContext(), R.anim.scale_down);

        spHelper = new SharedPreferencesHelper(requireContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_writer, container, false);
        setUpView();
        return rootView;
    }

    private void setUpView() {
        TextView tvTitle = rootView.findViewById(R.id.tv_title);
        boxesContainer = rootView.findViewById(R.id.ll_boxes_container);

        tvTitle.setText(verse.getTitle());
        setTextIntoBoxes();
    }

    private void setTextIntoBoxes() {
        LinearLayout.LayoutParams boxParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        boxParams.setMargins(4, 2, 4, 2);
        LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        StringBuilder text = new StringBuilder();
        LinearLayout row = new LinearLayout(requireContext());
        row.setLayoutParams(rowParams);

        int textWith = TextHelper.getTextWith(text.toString(), requireContext());
        final int SCREEN_WITH = SuperUtil.getDisplayWidth(requireActivity());

        for (int i = 0; i < textWithLowLinesAuxList.size(); ) {
            while (textWith < SCREEN_WITH - (SCREEN_WITH / 4)
                    && i < textWithLowLinesAuxList.size()) {
                String wordToSelect = dividedText.get(i);

                if (!posReplacementsWords.contains(i)) {
                    TextView boxTextView = new TextView(requireContext());
                    boxTextView.setId(i);
                    boxTextView.setMaxLines(1);
                    boxTextView.setEllipsize(TextUtils.TruncateAt.END);
                    boxTextView.setLayoutParams(boxParams);
                    boxTextView.setPadding(4, 4, 4, 4);
                    boxTextView.setTextSize(SP, 17);
                    boxTextView.setText(wordToSelect);
                    registerForContextMenu(boxTextView);
                    row.addView(boxTextView);
                } else {
                    TextInputEditText inputEditText = new TextInputEditText(requireContext());
                    inputEditText.setId(i);
                    inputEditText.setMaxLines(1);
                    inputEditText.setEllipsize(TextUtils.TruncateAt.END);
                    inputEditText.setLayoutParams(boxParams);
                    inputEditText.setBackground(ContextCompat.getDrawable(requireContext(),
                            R.drawable.background_green_light));
                    inputEditText.setPadding(4, 4, 4, 4);
                    inputEditText.setTextSize(SP, 17);
                    inputEditText.setTextColor(requireContext()
                            .getColor(R.color.text_default_dark_gray));
                    String lowLine = TextHelper.getLowLineFromWord(wordToSelect);
                    inputEditText.setHint(lowLine);
                    row.addView(inputEditText);
                    inputEditTexts.add(inputEditText);
                    addTextChangedListener(inputEditText);
                }

                text.append(wordToSelect).append(SPACE);
                textWith = TextHelper.getTextWith(text.toString(), requireContext());
                i++;
            }

            LinearLayout.LayoutParams scrollViewParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            scrollViewParams.setMargins(2, 2, 2, 2);
            HorizontalScrollView horizontalScrollView = new HorizontalScrollView(requireContext());
            horizontalScrollView.setFillViewport(true);
            horizontalScrollView.setHorizontalScrollBarEnabled(false);
            horizontalScrollView.setLayoutParams(scrollViewParams);
            horizontalScrollView.addView(row);
            boxesContainer.addView(horizontalScrollView);

            // Resetting Everything
            row = new LinearLayout(requireContext());
            row.setLayoutParams(rowParams);
            text = new StringBuilder();
            textWith = 0;
        }
    }

    private void addTextChangedListener(TextInputEditText inputEditText) {
        inputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    boolean flag = true;
                    for (TextInputEditText editText : inputEditTexts) {
                        if (Objects.requireNonNull(editText.getText()).toString().isEmpty()) {
                            flag = false;
                            break;
                        }
                    }
                    if (flag) {
                        SuperUtil.showView(null, scorerFragment.btnFinish);
                    } else {
                        SuperUtil.hideView(animScaleDown, scorerFragment.btnFinish);
                    }
                } else {
                    SuperUtil.hideView(animScaleDown, scorerFragment.btnFinish);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                String result = s.toString().replaceAll(REGEX_SPACE, "");
                if (!s.toString().equals(result)) {
                    inputEditText.setText(result);
                    inputEditText.setSelection(result.length());
                    // alert the user
                }
            }
        });
    }

    @Override
    public void onCreateContextMenu(
            @NonNull ContextMenu menu, @NonNull View v,
            @Nullable ContextMenu.ContextMenuInfo menuInfo) {
        TextView tv = (TextView) v;
        menu.add(0, v.getId(), 0, tv.getText().toString());
    }

    @Override
    public void onUndoClick(View v) {
        //Empty method
    }

    @Override
    public void onNextClick(View v, CustomViewPager viewPager,
                            AdapterScorerFragmentPager pagerAdapter) {
        SuperUtil.vibrate(requireContext());
        if (scorerFragment.btnFinish.getVisibility() == View.VISIBLE) {
            viewPager.moveNext();
        } else {
            MessagesHelper.showInfoMessageWarning(requireActivity(),
                    getString(R.string.finish_first));
        }
    }

    @Override
    public void onFinishClick(View v) {
        SuperUtil.vibrate(requireContext());
        Score score = ScoreHelper.getWriterScore(dividedText, inputEditTexts);
        scorerFragment.getScores().add(score);
        scorerFragment.showScorer();
    }

    @Override
    public void onResume() {
        super.onResume();
        scorerFragment.bindInterface(this);
    }
}