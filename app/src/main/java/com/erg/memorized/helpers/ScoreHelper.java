package com.erg.memorized.helpers;

import android.util.Log;
import android.widget.TextView;

import com.erg.memorized.model.ItemVerse;
import com.erg.memorized.model.Score;
import com.google.android.material.textfield.TextInputEditText;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class ScoreHelper {

    private static final String TAG = "ScoreHelper";

    @NotNull
    @Contract("_, _, _ -> new")
    public static Score getFillMissingScore(ArrayList<String> dividedText,
                                            ArrayList<Integer> posReplacementsWords,
                                            ArrayList<String> textWithLowLines) {
        int hitsCont = 0;
        int almostHitsCont = 0;
        int missCont = 0;
        int ops = posReplacementsWords.size();
        for (int pos : posReplacementsWords) {
            String rightWord = dividedText.get(pos);
            String userWord = textWithLowLines.get(pos);
            if (rightWord.equals(userWord)) {
                hitsCont++;
            } else {
                missCont++;
            }
        }
        return new Score(
                hitsCont,
                almostHitsCont,
                missCont, ops);
    }

    public static Score getDragAndDropScore(ArrayList<String> dividedText,
                                            ArrayList<TextView> textViews) {
        int hitsCont = 0;
        int almostHitsCont = 0;
        int missCont = 0;
        int ops = textViews.size();

        for (int i = 0; i < dividedText.size(); i++) {
            String rightWord = dividedText.get(i);
            String userWord = textViews.get(i).getText().toString();
            if (rightWord.equals(userWord)) {
                hitsCont++;
            } else {
                missCont++;
            }
        }
        return new Score( hitsCont, almostHitsCont, missCont, ops);
    }

    public static Score getWriterScore(ArrayList<String> dividedText,
                                       ArrayList<TextInputEditText> inputEditTexts) {
        int hitsCont = 0;
        int almostHitsCont = 0;
        int missCont = 0;
        int ops = inputEditTexts.size();

        for (int i = 0; i < inputEditTexts.size(); i++) {
            TextInputEditText inputEditText = inputEditTexts.get(i);
            String rightWord = dividedText.get(inputEditText.getId());
            String userWord = inputEditText.getText().toString();
            if (rightWord.equals(userWord)) {
                hitsCont++;
            } else {
                char userTokenChar;
                char rightTokenChar;
                int cont = 0;
                for (int k = 0; k < userWord.length()
                        && k < rightWord.length(); k++) {
                    userTokenChar = userWord.charAt(k);
                    rightTokenChar = rightWord.charAt(k);
                    if (userTokenChar == rightTokenChar) {
                        cont++;
                    }
                }

                if (cont >= userWord.length() / 2) {
                    almostHitsCont++;
                } else {
                    missCont++;
                }
            }
            ops++;
        }

        return new Score( hitsCont, almostHitsCont, missCont, ops);
    }

    public static float getTotalScore(ArrayList<Score> scores) {
        float TOTAL = 0;
        for (Score score : scores) {
            TOTAL += getEvaluatorScore(score);
        }
        return TOTAL;
    }

    public static float getEvaluatorScore(Score score) {
        float TOTAL = 0;
        int hitsCont = score.getHitsCont();
        int almostHitsCont = score.getAlmostHitsCont();
        int missCont = score.getMissCont();
        int ops = score.getOps();

        TOTAL = (float) hitsCont + ((float) (almostHitsCont / 2)) - ((float) (missCont / ops));

        return TOTAL;
    }


    public static String getRoundOut(Float number) {
        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.CEILING);
        Double d = number.doubleValue();
        return df.format(d);
    }

    public static float round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }

    public static float calculateUsageScore(Long time) {

        float aux = Math.round(time.floatValue());
        Log.d(TAG, "calculateUsageScore: Time Float value: " + aux);

        if (aux == 0.0f)
            return 0.073f;
        if ( aux > 0.0f  && aux < 4.9f)
            return 0.5f;
        if ( aux > 5.0f  && aux < 5.9f)
            return 1.0f;
        if ( aux > 6.0f  && aux < 6.9f)
            return 1.5f;
        if ( aux > 7.0f  && time < 7.9f)
            return 2.0f;
        if ( aux > 8.0f  && aux < 8.9f)
            return 2.5f;
        if ( aux > 9.0f  && aux < 9.9f)
            return 3.0f;
        if ( aux > 10f)
            return 4.0f;

        return 0.0f;
    }

    public static float getUserScoreByVersesList(ArrayList<ItemVerse> verses) {
        float totalScore = 0;
        for (ItemVerse verse : verses) {
            totalScore += verse.getVerseScore();
        }
        return totalScore;
    }
}
