package com.erg.memorized.fragments.scorer;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.erg.memorized.R;
import com.erg.memorized.adapters.AdapterScorerFragmentPager;
import com.erg.memorized.fragments.ScorerFragment;
import com.erg.memorized.helpers.MessagesHelper;
import com.erg.memorized.helpers.ScoreHelper;
import com.erg.memorized.helpers.SharedPreferencesHelper;
import com.erg.memorized.helpers.TextHelper;
import com.erg.memorized.interfaces.BoxTestListener;
import com.erg.memorized.interfaces.ScorerListener;
import com.erg.memorized.model.ItemVerse;
import com.erg.memorized.model.Score;
import com.erg.memorized.util.Constants;
import com.erg.memorized.util.SuperUtil;
import com.erg.memorized.views.CustomViewPager;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;

import static androidx.annotation.Dimension.SP;
import static com.erg.memorized.util.Constants.SPACE;

public class BoxSelectorFragment extends Fragment implements View.OnClickListener, ScorerListener {

    public static final String TAG = "BoxSelectorFragment";
    private ItemVerse verse;
    private ArrayList<String> textWithLowLinesAuxList;
    private ArrayList<Integer> posReplacementsWords;
    private ArrayList<Integer> sortedPosReplacementsWords;
    private ArrayList<String> dividedText;
    private ArrayList<TextView> changesBoxesHistory;
    private ArrayList<TextView> boxesList;

    private View rootView;

    private TextView tvVerse, tvTitle;
    private Button btnDone;
    private LinearLayout boxesContainer;

    private Animation animScaleUp, animScaleDown;
    private BoxTestListener boxTestListener;
    private ScorerFragment scorerFragment;

    private SharedPreferencesHelper spHelper;

    public BoxSelectorFragment() {
        // Required empty public constructor
    }

    public BoxSelectorFragment(ItemVerse verse,
                               BoxTestListener boxTestListener,
                               ScorerFragment scorerFragment) {
        this.boxTestListener = boxTestListener;
        this.scorerFragment = scorerFragment;
        this.verse = verse;
    }

    public static BoxSelectorFragment newInstance(ItemVerse verse,
                                                  BoxTestListener boxTestListener,
                                                  ScorerFragment scorerFragment) {
        return new BoxSelectorFragment(verse, boxTestListener, scorerFragment);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        dividedText = new ArrayList<>();
        if (verse != null) {
            dividedText = TextHelper.getDividedText(verse.getVerseText());
        }

        changesBoxesHistory = new ArrayList<>();
        boxesList = new ArrayList<>();

        posReplacementsWords = new ArrayList<>(
                TextHelper.getPosReplacementsWords(dividedText,
                        Constants.getMarks(requireActivity()))
        );

        sortedPosReplacementsWords = new ArrayList<>(posReplacementsWords);
        Collections.sort(sortedPosReplacementsWords);

        textWithLowLinesAuxList = new ArrayList<>(
                TextHelper.getTextWithLowLines(dividedText, posReplacementsWords)
        );

        animScaleUp = AnimationUtils.loadAnimation(getContext(), R.anim.less_scale_up);
        animScaleDown = AnimationUtils.loadAnimation(getContext(), R.anim.scale_down);

        spHelper = new SharedPreferencesHelper(requireContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_box_selecter, container, false);
        setUpView();
        return rootView;
    }

    private void setUpView() {
        tvTitle = rootView.findViewById(R.id.tv_title);
        tvVerse = rootView.findViewById(R.id.tv_verse);
        boxesContainer = rootView.findViewById(R.id.ll_boxes_container);
        btnDone = rootView.findViewById(R.id.btn_done_box_fragment);
        btnDone.setOnClickListener(this);

        tvTitle.setText(verse.getTitle());

        String firstText = TextHelper.getTextIntoString(textWithLowLinesAuxList);
        tvVerse.setText(firstText);
        boxCreator();
    }

    private void boxCreator() {
        LinearLayout.LayoutParams boxParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        boxParams.setMargins(4, 4, 4, 4);
        LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        StringBuilder text = new StringBuilder();
        LinearLayout row = new LinearLayout(getContext());
        row.setLayoutParams(rowParams);
        int textWith = TextHelper.getTextWith(text.toString(), requireContext());
        final int SCREEN_WITH = SuperUtil.getDisplayWidth(requireActivity());

        for (int i = 0; i < posReplacementsWords.size(); ) {
            while (textWith < SCREEN_WITH - (SCREEN_WITH / 2) && i < posReplacementsWords.size()) {
                //Creating Box TextView
                int posReplacement = posReplacementsWords.get(i);
                TextView boxTextView = new TextView(requireContext());
                boxTextView.setId(posReplacement);
                boxTextView.setBackground(requireContext().getDrawable(R.drawable.selector_gray));
                boxTextView.setElevation(2);
                boxTextView.setMaxLines(1);
                boxTextView.setEllipsize(TextUtils.TruncateAt.END);
                boxTextView.setLayoutParams(boxParams);
                boxTextView.setPadding(8, 8, 8, 8);
                boxTextView.setTextSize(SP, 16);

                String wordToSelect = dividedText.get(posReplacement);
                boxTextView.setText(wordToSelect);
                registerForContextMenu(boxTextView);

                setOnClickListener(boxTextView);

                text.append(wordToSelect).append(SPACE);
                textWith = TextHelper.getTextWith(text.toString(), requireContext());
                boxesList.add(boxTextView);
                row.addView(boxTextView);
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

    private void setOnClickListener(@NotNull TextView boxTextView) {
        boxTextView.setOnClickListener(v -> {
            SuperUtil.vibrate(requireContext());
            TextView currentTextView = ((TextView) v);
            String selectedWord = currentTextView.getText().toString();
            String textAfterSetWord = TextHelper
                    .setSelectedWord(textWithLowLinesAuxList, selectedWord,
                            sortedPosReplacementsWords);

            changesBoxesHistory.add(currentTextView);

            tvVerse.setText(textAfterSetWord);

            if (!boxesList.isEmpty())
                boxesList.remove(currentTextView);
            SuperUtil.hideView(animScaleDown, v);
            boxTestListener.onBoxClick(boxesList);

            if (boxesList.isEmpty()) {
                SuperUtil.showView(animScaleUp, btnDone);
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
        SuperUtil.vibrate(requireContext());
        if (!changesBoxesHistory.isEmpty()) {
            int boxLastPos = changesBoxesHistory.size() - 1;
            TextView box = changesBoxesHistory.get(boxLastPos);
            String undoneWord = box.getText().toString();

            SuperUtil.showView(animScaleUp, box);
            boxesList.add(box);
            String textAfterSetLowLine = TextHelper
                    .setLowLine(textWithLowLinesAuxList,
                            undoneWord, sortedPosReplacementsWords);

            tvVerse.setText(textAfterSetLowLine);
            changesBoxesHistory.remove(boxLastPos);
            if (changesBoxesHistory.isEmpty()) {
                SuperUtil.hideView(animScaleDown, v);
            } else {
                SuperUtil.hideView(null, btnDone);
            }
        }
    }

    @Override
    public void onNextClick(View v, CustomViewPager viewPager,
                            AdapterScorerFragmentPager pagerAdapter) {
        SuperUtil.vibrate(requireContext());
        if (boxesList != null && boxesList.isEmpty()) {
            viewPager.moveNext();
        } else {
            MessagesHelper.showInfoMessageWarning(requireActivity(),
                    getString(R.string.finish_first));
        }
    }

    @Override
    public void onFinishClick(View v) {
        //Empty
    }

    @Override
    public void onClick(View v) {
        SuperUtil.vibrate(requireContext());
        if (v.getId() == R.id.btn_done_box_fragment) {
            scorerFragment.scorerViewPager.moveNext();
            saveScore();
        }
    }

    private void saveScore() {
        Score score = ScoreHelper.getFillMissingScore(dividedText,
                posReplacementsWords, textWithLowLinesAuxList);
        scorerFragment.getScores().add(score);
    }

    @Override
    public void onResume() {
        super.onResume();
        scorerFragment.bindInterface(this);
    }
}
