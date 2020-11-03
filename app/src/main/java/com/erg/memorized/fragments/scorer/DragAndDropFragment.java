package com.erg.memorized.fragments.scorer;

import android.content.ClipData;
import android.content.ClipDescription;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.erg.memorized.R;
import com.erg.memorized.adapters.AdapterScorerFragmentPager;
import com.erg.memorized.fragments.ScorerFragment;
import com.erg.memorized.helpers.ScoreHelper;
import com.erg.memorized.helpers.TextHelper;
import com.erg.memorized.interfaces.BoxTestListener;
import com.erg.memorized.interfaces.ScorerListener;
import com.erg.memorized.model.ItemVerse;
import com.erg.memorized.model.Score;
import com.erg.memorized.util.SuperUtil;
import com.erg.memorized.views.CustomViewPager;

import java.util.ArrayList;
import java.util.Collections;

import static com.erg.memorized.util.Constants.SPACE;
import static com.erg.memorized.util.Constants.TEXT_SIZE;


public class DragAndDropFragment extends Fragment implements View.OnClickListener, ScorerListener {

    public static final String TAG = "DragAndDropFragment";

    private View rootView;

    private ArrayList<String> dividedText;
    private ArrayList<String> textShuffled;

    private final ItemVerse verse;
    private BoxTestListener boxTestListener;
    private final ScorerFragment scorerFragment;

    private LinearLayout boxesContainer;
    private ArrayList<TextView> textViews;
    private ScrollView scrollView;

    public DragAndDropFragment(ItemVerse verse,
                               BoxTestListener boxTestListener,
                               ScorerFragment scorerFragment) {
        this.boxTestListener = boxTestListener;
        this.scorerFragment = scorerFragment;
        this.verse = verse;
    }


    public static DragAndDropFragment newInstance(ItemVerse verse,
                                                  BoxTestListener boxTestListener,
                                                  ScorerFragment scorerFragment) {
        return new DragAndDropFragment(verse, boxTestListener, scorerFragment);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        textViews = new ArrayList<>();
        dividedText = new ArrayList<>();
        if (verse != null) {
            dividedText = TextHelper.getDividedText(verse.getVerseText());
        }

        textShuffled = new ArrayList<>(dividedText);
        Collections.shuffle(textShuffled);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_drag_and_drop, container, false);
        setUpView();
        return rootView;
    }

    private void setUpView() {
        boxesContainer = rootView.findViewById(R.id.ll_boxes_container);
        scrollView = rootView.findViewById(R.id.scroll_view_boxes_container);
        TextView tvTitle = rootView.findViewById(R.id.tv_title);
        Button btnDone = rootView.findViewById(R.id.btn_done_drag_and_drop_fragment);

        tvTitle.setText(verse.getTitle());
        btnDone.setOnClickListener(this);
        setTextIntoBoxes();
    }

    private void setTextIntoBoxes() {
        addText(textShuffled);
    }

    public void addText(ArrayList<String> textShuffled) {

        LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        final int SCREEN_WITH = SuperUtil.getDisplayWidth(requireActivity());

        textViews.clear();
        StringBuilder text = new StringBuilder();
        LinearLayout row = new LinearLayout(getContext());
        row.setLayoutParams(rowParams);
        int textWith = TextHelper.getTextWith(text.toString(), requireContext());

        for (int i = 0; i < textShuffled.size(); ) {
            while (textWith < SCREEN_WITH - (SCREEN_WITH / 3) && i < textShuffled.size()) {
                String word = textShuffled.get(i);

                TextView textView = createTextView(word, i);
                row.addView(textView);
                textViews.add(textView);

                text.append(word).append(SPACE);
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

    private TextView createTextView(String word, int i) {
        String WORD_TAG = word + "_" + i;
        LinearLayout.LayoutParams boxParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        boxParams.setMargins(7, 7, 7, 7);

        TextView textView = new TextView(requireContext());
        textView.setId(i);
        textView.setTag(WORD_TAG);
        textView.setTextSize(TEXT_SIZE);
        textView.setBackground(ContextCompat.getDrawable(requireContext(),
                R.drawable.background_green_light));
        textView.setElevation(1);
        textView.setMaxLines(1);
        textView.setLayoutParams(boxParams);
        textView.setPadding(10, 10, 10, 10);
        textView.setText(word);

        setOnLongClick(textView);
        textView.setOnDragListener(new DragEventListener());
        return textView;
    }

    @Override
    public void onUndoClick(View v) {
        Toast.makeText(requireContext(), TAG, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNextClick(View v, CustomViewPager viewPager,
                            AdapterScorerFragmentPager pagerAdapter) {
        SuperUtil.vibrate(requireContext());
        viewPager.moveNext();

    }

    @Override
    public void onFinishClick(View v) {
        //Empty
    }

    private class DragEventListener implements View.OnDragListener {

        String dragData = "";
        String dataTarget = "";

        @Override
        public boolean onDrag(View v, DragEvent event) {
            final int action = event.getAction();
            switch (action) {
                case DragEvent.ACTION_DRAG_STARTED:
                    if (event.getClipDescription()
                            .hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
                        v.setBackgroundResource(R.drawable.background_gray);
                        v.invalidate();
                        return true;
                    }
                    return false;
                case DragEvent.ACTION_DRAG_ENTERED:
                    SuperUtil.vibrateMin(requireContext());
                    v.setBackgroundResource(R.drawable.background_green_light);
                    v.invalidate();
                    return true;
                case DragEvent.ACTION_DRAG_LOCATION:

//                    int boxY = Math.round(v.getY());
                    Point touchPosition = getTouchPositionFromDragEvent(v, event);
                    int translatedY = Math.round(touchPosition.y);

                    int topOfDropZone = scrollView.getTop();
                    int bottomOfDropZone = scrollView.getBottom();

                    int fixedTraTopTranslate = translatedY - topOfDropZone;
                    int viewMeasureHeight = v.getMeasuredHeight();

                    Log.d(TAG, "View MeasuredHeight: " + viewMeasureHeight +
                            " onDrag: TranslatedY: " + fixedTraTopTranslate
                            + " topOfDropZone: " + topOfDropZone
                            + " bottomOfDropZone: " + bottomOfDropZone
                    );
                    // scrolling Up due the y has passed the threshold
                    if (fixedTraTopTranslate - viewMeasureHeight < topOfDropZone) {
                        Log.d(TAG, "Scrolling Up: " + (translatedY - topOfDropZone));
                        scrollView.smoothScrollBy(0, -30);
                    }
                    // scrolling Down due y has passed the bottomOfDropZone border
                    if (translatedY - viewMeasureHeight > bottomOfDropZone) {
                        Log.d(TAG, "Scrolling Down: " + translatedY);
                        scrollView.smoothScrollBy(0, 30);
                    }

                    return true;
                case DragEvent.ACTION_DRAG_EXITED:
                    v.setBackgroundResource(R.drawable.background_gray);
                    v.invalidate();
                    return true;
                case DragEvent.ACTION_DROP:
                    SuperUtil.vibrate(requireContext());
                    TextView dropped = (TextView) event.getLocalState();
                    TextView dropTarget = (TextView) v;
                    ClipData.Item item = event.getClipData().getItemAt(0);

                    dragData = String.valueOf(item.getText());
                    dataTarget = String.valueOf(dropTarget.getText());

                    dropTarget.setText(dragData);
                    if (dropped != null) {
                        dropped.setText(dataTarget);

                        //Swapping Data on Lists
                        int droppedPos = dropped.getId();
                        int dropTargetPos = dropTarget.getId();
                        Collections.swap(textShuffled, droppedPos, dropTargetPos);
                        Collections.swap(textViews, droppedPos, droppedPos);

                        //Swapping TexViews Id/Positions
                        dropped.setId(dropTargetPos);
                        dropTarget.setId(droppedPos);

                    }

                    v.setBackgroundColor(Color.WHITE);
                    v.invalidate();
                    return true;

                case DragEvent.ACTION_DRAG_ENDED:
                    v.setBackgroundResource(R.drawable.background_green_light);
                    v.invalidate();
                    if (event.getResult()) {
                        Log.d(TAG, "onDrag: The drop was handled.");
                    } else {
                        Log.d(TAG, "onDrag: The drop didn't work.");
                    }
                    return true;
                default:
                    Log.e("DragDrop Example",
                            "Unknown action type received by OnDragListener.");
                    break;
            }
            return false;
        }
    }

    public static Point getTouchPositionFromDragEvent(View item, DragEvent event) {
        Rect rItem = new Rect();
        item.getGlobalVisibleRect(rItem);
        return new Point(rItem.left + Math.round(event.getX()),
                rItem.top + Math.round(event.getY()));
    }

    private void setOnLongClick(TextView boxTextView) {
        boxTextView.setOnLongClickListener(v -> {
            SuperUtil.vibrateMin(requireContext());
            TextView textView = (TextView) v;
            ClipData.Item item = new ClipData.Item(textView.getText());
            ClipData dragData = new ClipData(
                    textView.getText(),
                    new String[]{ClipDescription.MIMETYPE_TEXT_PLAIN},
                    item);
            View.DragShadowBuilder shadow = new View.DragShadowBuilder(textView);
            textView.startDrag(dragData,  // the data to be dragged
                    shadow,  // the drag shadow builder
                    textView,      //  local data
                    0          // flags (not currently used, set to 0)
            );
            return true;
        });
    }

    @Override
    public void onClick(View v) {
        SuperUtil.vibrate(requireContext());
        if (v.getId() == R.id.btn_done_drag_and_drop_fragment) {
            scorerFragment.scorerViewPager.setSwipingEnabled(true);
            scorerFragment.scorerViewPager.moveNext();
            saveScore();
        }
    }

    private void saveScore() {
        Score score = ScoreHelper.getDragAndDropScore(dividedText, textViews);
        scorerFragment.getScores().add(score);
    }

    @Override
    public void onResume() {
        super.onResume();
        scorerFragment.bindInterface(this);
    }
}