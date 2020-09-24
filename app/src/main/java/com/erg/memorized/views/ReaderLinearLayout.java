package com.erg.memorized.views;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.erg.memorized.R;

import java.util.ArrayList;

import static com.erg.memorized.util.Constants.SPACE;

public class ReaderLinearLayout extends LinearLayout {

    private static final String TAG = "ReaderLinearLayout";

    private static final int TEXT_SIZE = 15;
    private final DisplayMetrics dm = new DisplayMetrics();
    private StringBuilder text = new StringBuilder();
    private ArrayList<TextView> textViews = new ArrayList<>();

    public ReaderLinearLayout(Context context) {
        super(context);
    }

    public ReaderLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LinearLayout addText(ArrayList<String> textShuffled) {

        LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        LayoutParams boxParams = new LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
        boxParams.setMargins(4, 4, 4, 4);
        final int  SCREEN_WITH = getScreenWidth();

        LinearLayout row = new LinearLayout(getContext());
        row.setLayoutParams(rowParams);
        int textWith = getTextWith(text.toString());

        for (int i = 0; i < textShuffled.size();) {
            while (textWith < SCREEN_WITH - (SCREEN_WITH / 3) && i < textShuffled.size()) {
                String word = textShuffled.get(i);

                TextView textView = createTextView(word, i, boxParams);
                row.addView(textView);
                textViews.add(textView);

                text.append(word).append(SPACE);
                textWith = getTextWith(text.toString());
                i++;
            }
            addView(row);

            // Resetting Everything
            row = new LinearLayout(getContext());
            row.setLayoutParams(rowParams);
            text = new StringBuilder();
            textWith = 0;
        }
        return row;
    }

    private TextView createTextView(String word, int i, LayoutParams boxParams) {
        String WORD_TAG = word + "_" + i;

        TextView textView = new TextView(getContext());
        textView.setId(i);
        textView.setTag(WORD_TAG);
        textView.setText(word);
        textView.setTextSize(TEXT_SIZE);
        textView.setBackground(getContext().getDrawable(R.drawable.background_gray_light));
        textView.setElevation(1);
        textView.setMaxLines(1);
        textView.setEllipsize(TextUtils.TruncateAt.END);
        textView.setLayoutParams(boxParams);
        textView.setPadding(4, 4, 4, 4);

        setOnLongClick(textView);
        textView.setOnDragListener(new DragEventListener());
        return textView;
    }

    private void setOnLongClick(TextView boxTextView) {
        boxTextView.setOnLongClickListener(v -> {
            TextView textView = (TextView) v;
            ClipData.Item item = new ClipData.Item(textView.getText());
            ClipData dragData = new ClipData(
                    textView.getText(),
                    new String[]{ClipDescription.MIMETYPE_TEXT_PLAIN},
                    item);
            View.DragShadowBuilder shadow = new DragShadowBuilder((TextView) v);
            v.startDrag(dragData,  // the data to be dragged
                    shadow,  // the drag shadow builder
                    null,      // no need to use local data
                    0          // flags (not currently used, set to 0)
            );


            return true;
        });
    }

    private class DragEventListener implements View.OnDragListener {

        String dragData = "";
        String dataTarget = "";

        @Override
        public boolean onDrag(View v, DragEvent event) {
            final int action = event.getAction();
            switch (action) {

                case DragEvent.ACTION_DRAG_STARTED:
                    if (event.getClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
                        v.setBackgroundResource(R.drawable.background_gray);
                        v.invalidate();
                        return true;

                    }
                    return false;
                case DragEvent.ACTION_DRAG_ENTERED:
                    v.setBackgroundResource(R.drawable.background_gray_light);
                    v.invalidate();
                    return true;
                case DragEvent.ACTION_DRAG_LOCATION:
                    return true;
                case DragEvent.ACTION_DRAG_EXITED:
                    v.setBackgroundResource(R.drawable.background_gray);
                    v.invalidate();
                    return true;
                case DragEvent.ACTION_DROP:

                    TextView dropTarget = (TextView) v;
                    ClipData.Item item = event.getClipData().getItemAt(0);

                    dragData = String.valueOf(item.getText());
                    dataTarget = String.valueOf(dropTarget.getText());

                    dropTarget.setText(dragData);

                    TextView dropped = findDropped(dragData);
                    if (dropped != null)
                        dropped.setText(dataTarget);
                    v.setBackgroundColor(Color.WHITE);
                    v.invalidate();
                    return true;

                case DragEvent.ACTION_DRAG_ENDED:
                    v.setBackgroundResource(R.drawable.background_gray_light);
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

    private TextView findDropped(String dragData) {
        for (TextView textView : textViews) {
            if (textView.getText().toString().contains(dragData)) {
                return textView;
            }
        }
        return null;
    }

    public int getTextHeight(final String text) {
        TextView textView = new TextView(getContext());
        textView.setText(text);
        textView.setTextSize(TEXT_SIZE);
        TextPaint textPaint = textView.getPaint();

        return new StaticLayout(text, textPaint, getScreenWidth(),
                Layout.Alignment.ALIGN_NORMAL,
                1.0f, 0.0f, true).getHeight();
    }

    public int getTextWith(final String text) {
        TextView textView = new TextView(getContext());
        textView.setText(text);
        Paint textPaint = textView.getPaint();
        Rect bounds = new Rect();
        textPaint.getTextBounds(text, 0, text.length(), bounds);
        int height = bounds.height();
        return bounds.width();
    }


    public int getScreenHeight() {
        WindowManager wm = (WindowManager) getContext().getSystemService(
                Context.WINDOW_SERVICE);
        if (wm != null)
            wm.getDefaultDisplay().getMetrics(dm);

        return dm.heightPixels;
    }

    public int getScreenWidth() {
        WindowManager wm = (WindowManager) getContext().getSystemService(
                Context.WINDOW_SERVICE);
        if (wm != null)
            wm.getDefaultDisplay().getMetrics(dm);

        return dm.widthPixels;
    }

}
