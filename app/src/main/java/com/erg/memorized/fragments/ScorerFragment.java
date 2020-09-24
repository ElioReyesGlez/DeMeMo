package com.erg.memorized.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.erg.memorized.R;
import com.erg.memorized.adapters.AdapterScorerFragmentPager;
import com.erg.memorized.fragments.scorer.BoxSelectorFragment;
import com.erg.memorized.fragments.scorer.DragAndDropFragment;
import com.erg.memorized.fragments.scorer.ResultFragment;
import com.erg.memorized.fragments.scorer.WriterFragment;
import com.erg.memorized.helpers.MessagesHelper;
import com.erg.memorized.helpers.SharedPreferencesHelper;
import com.erg.memorized.helpers.TextHelper;
import com.erg.memorized.interfaces.BoxTestListener;
import com.erg.memorized.interfaces.ScorerListener;
import com.erg.memorized.model.ItemVerse;
import com.erg.memorized.model.Score;
import com.erg.memorized.util.SuperUtil;
import com.erg.memorized.views.CustomViewPager;

import java.util.ArrayList;

import static androidx.fragment.app.FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT;

public class ScorerFragment extends Fragment implements BoxTestListener {

    public static final String TAG = "ScorerFragment";

    private ItemVerse verse;

    private View rootView;
    private ViewGroup container;
    public CustomViewPager scorerViewPager;
    public AdapterScorerFragmentPager scoreViewPagerAdapter;
    private TextView tvIndicator, tvCountdown;
    public ImageButton ibUndo;
    private LinearLayout llCountdownContainer;
    private ImageButton ibNext;
    public Button btnFinish;
    private Animation animScaleUp, animScaleDown;
    private SharedPreferencesHelper spHelper;
    private ScorerListener scorerListener;

    private CountDownTimer countDownTimer;
    private boolean isOnTick = false;
    public Dialog timeFinishDialog = null;

    protected ArrayList<Score> scores;

    private int millisInFuture = 10000;

    public ScorerFragment() {
    }

    public ScorerFragment(ItemVerse verse) {
        this.verse = verse;
    }

    public static ScorerFragment newInstance(ItemVerse verse) {
        return new ScorerFragment(verse);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        spHelper = new SharedPreferencesHelper(requireContext());
        animScaleUp = AnimationUtils.loadAnimation(requireContext(), R.anim.less_scale_up);
        animScaleDown = AnimationUtils.loadAnimation(requireContext(), R.anim.scale_down);
        scores = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_scorer, container, false);
        this.container = container;
        setUpView();
        return rootView;
    }

    public void bindInterface(ScorerListener scorerListener) {
        this.scorerListener = scorerListener;
    }

    private void setUpView() {
        ibNext = rootView.findViewById(R.id.ib_next);
        btnFinish = rootView.findViewById(R.id.btn_finish);
        ibUndo = rootView.findViewById(R.id.ib_undo);
        tvIndicator = rootView.findViewById(R.id.tv_evaluator_cont);
        tvCountdown = rootView.findViewById(R.id.tv_countdown);
        llCountdownContainer = rootView.findViewById(R.id.ll_countdown_container);
        scorerViewPager = rootView.findViewById(R.id.score_viewpager);

        scorerViewPager.setSwipingEnabled(false);

        scoreViewPagerAdapter = new AdapterScorerFragmentPager(
                getChildFragmentManager(),
                BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT,
                verse, this, this);
        scorerViewPager.setAdapter(scoreViewPagerAdapter);

        SuperUtil.hideViewInvisibleWay(null, ibNext);
        int realCont = scoreViewPagerAdapter.getCount() - 1;
        String posIndicator = "0/" + realCont;
        tvIndicator.setText(posIndicator);

        scorerViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                int realCont = scoreViewPagerAdapter.getCount() - 1;
                String posIndicator = position + "/" + realCont;
                tvIndicator.setText(posIndicator);
                Log.d(TAG, "onPageSelected: Page: " + position);
                switch (position) {
                    case 0:
                        SuperUtil.hideViewInvisibleWay(null, ibNext);
                        break;
                    case 1:
                        scorerViewPager.setSwipingEnabled(false);
                        SuperUtil.showView(null, ibNext);
                        showEvaluatorDialogInfo(BoxSelectorFragment.TAG);
                        if (!isOnTick)
                            startCountdown();
                        break;
                    case 2:
                        showEvaluatorDialogInfo(DragAndDropFragment.TAG);
                        SuperUtil.hideView(animScaleDown, ibUndo);
                        scorerViewPager.setSwipingEnabled(false);
                        break;
                    case 3:
                        showEvaluatorDialogInfo(WriterFragment.TAG);
                        scorerViewPager.setSwipingEnabled(false);
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        ibUndo.setOnClickListener(v -> scorerListener.onUndoClick(v));
        ibNext.setOnClickListener(v -> scorerListener.onNextClick(v, scorerViewPager,
                scoreViewPagerAdapter));
        btnFinish.setOnClickListener(v -> scorerListener.onFinishClick(v));
    }

    private void showEvaluatorDialogInfo(String tag) {
        if (!spHelper.getEvaluatorInfoMessageStatus(tag)) {
            String msg = "";
            int image;
            switch (tag) {
                case BoxSelectorFragment.TAG:
                    msg = getString(R.string.set_missing_word);
                    image = R.drawable.ic_indent;
                    break;
                case DragAndDropFragment.TAG:
                    msg = getString(R.string.drag_and_drop_evaluator_message);
                    image = R.drawable.ic_swipe;
                    break;
                case WriterFragment.TAG:
                    msg = getString(R.string.writer_message);
                    image = R.drawable.ic_edit_text;
                    break;
                default:
                    image = R.drawable.ic_learning_launcher;
                    break;
            }

            final String finalMsg = msg;
            final int finalImage = image;
            new Handler().postDelayed(() -> {
                if (isVisible()) {
                    MessagesHelper.showEvaluatorDialogInfoMessage(
                            requireActivity(),
                            finalImage,
                            finalMsg,
                            tag
                    );

                }
            }, 800);
        }
    }

    private void startCountdown() {
        SuperUtil.showView(animScaleUp, llCountdownContainer);
        final int countDownInterval = 1000;
        millisInFuture = TextHelper.getDividedText(verse.getVerseText()).size() * 10000;
        countDownTimer = new CountDownTimer(millisInFuture, countDownInterval) {
            public void onTick(long millisUntilFinished) {
                long auxVar = millisUntilFinished / countDownInterval;
                tvCountdown.setText(String.valueOf((int) auxVar));
                isOnTick = true;
            }

            public void onFinish() {
                if (isVisible()) {
                    tvCountdown.setText("");
                    SuperUtil.hideView(animScaleDown, llCountdownContainer);
                    isOnTick = false;
                    timeFinishDialog = MessagesHelper.showTimeFinishedDialog(requireActivity(),
                            ScorerFragment.this);
                }
            }
        };
        countDownTimer.start();
    }

    private void cancelTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    public void showScorer() {

        if (SuperUtil.isFragmentOnBackStack(requireActivity(), ScorerFragment.TAG)) {
            SuperUtil.removeViewByTag(requireActivity(), ScorerFragment.TAG, true);
        }
        SuperUtil.loadView(requireActivity(),
                ResultFragment.newInstance(verse, getScores()),
                ResultFragment.TAG, true);

    }

    @Override
    public void onStart() {
        super.onStart();
        SuperUtil.hideMeoBottomBar(requireActivity(), animScaleDown);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cancelTimer();
        if (timeFinishDialog != null) {
            if (timeFinishDialog.isShowing())
                timeFinishDialog.dismiss();
        }
    }

    @Override
    public void onBoxClick(ArrayList<TextView> boxes) {
        if (!boxes.isEmpty()) {
            SuperUtil.showView(null, ibUndo);
        }
    }

    public ArrayList<Score> getScores() {
        return scores;
    }
}
