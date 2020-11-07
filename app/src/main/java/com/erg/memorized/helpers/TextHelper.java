package com.erg.memorized.helpers;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Rect;
import android.widget.TextView;

import com.erg.memorized.util.Constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import static com.erg.memorized.util.Constants.LOW_LINE;
import static com.erg.memorized.util.Constants.REGEX_SPACE;
import static com.erg.memorized.util.Constants.SPACE;
import static com.erg.memorized.util.Constants.TEXT_SIZE;

public class TextHelper {

    public static ArrayList<String> getDividedText(String text) {
        ArrayList<String> list = new ArrayList<>();
        Collections.addAll(list, text.split(REGEX_SPACE));
        return list;
    }

    public static ArrayList<String> getTextWithLowLines(ArrayList<String> dividedText,
                                                        ArrayList<Integer> posReplacementsWords) {
        ArrayList<String> textWithLowLines = new ArrayList<>(dividedText);
        for (int i = 0; i < posReplacementsWords.size(); i++) {
            int pos = posReplacementsWords.get(i);
            String word = textWithLowLines.get(pos);
            if (textWithLowLines.contains(word)) {
                StringBuilder lowLine = new StringBuilder();
                for (int j = 0; j < word.length(); j++) {
                    lowLine.append(LOW_LINE);
                }
                textWithLowLines.set(pos, lowLine.toString());
            }
        }
        return textWithLowLines;
    }

    public static String getTextIntoString(ArrayList<String> arrayList) {
        StringBuilder textIntoString = new StringBuilder();
        for (String word : arrayList) {
            textIntoString.append(word).append(SPACE);
        }
        return textIntoString.toString();
    }

    public static String setSelectedWord(
            ArrayList<String> textWithLowLines,
            String selectedWord,
            ArrayList<Integer> sortedPosReplacementsWords) {

        for (int pos : sortedPosReplacementsWords) {
            String lowLine = textWithLowLines.get(pos);
            if (lowLine.contains(Constants.LOW_LINE_2X)) {
                textWithLowLines.set(pos, selectedWord);
                return getTextIntoString(textWithLowLines);
            }
        }
        return getTextIntoString(textWithLowLines);
    }

    public static String setLowLine(ArrayList<String> textWithLowLines,
                                    String selectedWord,
                                    ArrayList<Integer> sortedPosReplacementsWords) {
        StringBuilder lowLine = new StringBuilder();
        for (int j = 0; j < selectedWord.length(); j++) {
            lowLine.append(LOW_LINE);
        }

        for (int pos : sortedPosReplacementsWords) {
            String word = textWithLowLines.get(pos);
            if (word.equals(selectedWord)) {
                textWithLowLines.set(pos, lowLine.toString());
                return getTextIntoString(textWithLowLines);
            }
        }
        return getTextIntoString(textWithLowLines);
    }


/*    public static ArrayList<HashMap<String, Integer>> saveWord
            (String word, int pos, ArrayList<HashMap<String, Integer>>
            hashMapWordPos) {

        for (int i = 0; i < hashMapWordPos.size(); i++) {
            HashMap<String, Integer> hasMap = hashMapWordPos.get(i);
            if (hasMap.get(word) != null) {
                hasMap.put(word, pos);
                hashMapWordPos.set(i, hasMap);
                return hashMapWordPos;
            }
        }
        return hashMapWordPos;
    }*/

/*    public static int getPosFromWord (String word, ArrayList<HashMap<String, Integer>>
            hashMapWordPos) {

        for (HashMap<String, Integer> hasMap : hashMapWordPos) {
            if (hasMap.get(word) != null) {
                return hasMap.get(word);
            }
        }
        return -1;
    }

    private static boolean thereAreNoLinesBefore(ArrayList<String> textWithLowLines, int i) {
        for (int j = i; j > 0; j--) {
            if (textWithLowLines.get(j).contains(LOW_LINE_2X)) {
                return false;
            }
        }
        return true;
    }

    private static String[] removeElement(String[] array, int pos) {
        List<String> list = new ArrayList<>();
        Collections.addAll(list, array);
        list.remove(pos);
        array = list.toArray(new String[]{});
        return array;
    }*/

    public static ArrayList<Integer> getPosReplacementsWords(ArrayList<String> dividedText,
                                                             ArrayList<String> marks) {
        int wordsToReplace = (dividedText.size() / 2) / 2;
        ArrayList<Integer> replacementsPos = new ArrayList<>();
        ArrayList<String> wordsHistory = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < wordsToReplace; i++) {
            int currentRandom = random.nextInt(dividedText.size());
            String word = dividedText.get(currentRandom);
            if (!replacementsPos.contains(currentRandom) && word.length() >= 3
                    && isMarksFree(word, marks) && !wordsHistory.contains(word)) {
                wordsHistory.add(word);
                replacementsPos.add(currentRandom);
            } else {
                i--;
            }
        }
        return replacementsPos;
    }

    private static boolean isMarksFree(String word, ArrayList<String> marks) {
        char token;
        for (int k = 0; k < word.length(); k++) {
            token = word.charAt(k);
            if (marks.contains(String.valueOf(token))) {
                return false;
            }
        }
        return true;
    }

/*    private static String cleanWord(String word, ArrayList<String> marks) {
        for (String mark : marks) {
            if (word.contains(mark)) {
                word = word.replace(mark, "");
            }
        }
        return word;
    }*/

    public static int getTextWith(final String text, Context context) {
        TextView textView = new TextView(context);
        textView.setTextSize(TEXT_SIZE);
        textView.setText(text);
        Paint textPaint = textView.getPaint();
        Rect bounds = new Rect();
        textPaint.getTextBounds(text, 0, text.length(), bounds);
//        int height = bounds.height();
        return bounds.width();
    }

    public static String getLowLineFromWord(String word) {
        StringBuilder lowLine = new StringBuilder();
        for (int j = 0; j < word.length(); j++) {
            lowLine.append(LOW_LINE);
        }
        return lowLine.toString();
    }

/*    public static boolean validate(Activity context,
                                   TextInputLayout tilMail, TextInputLayout tilPass,
                                   String email, String password) {
        boolean valid = true;

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilMail.setError(context.getString(R.string.error_email));
            valid = false;
        } else {
            tilMail.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            tilPass.setError(context.getString(R.string.error_pass));
            valid = false;
        } else {
            tilPass.setError(null);
        }
        return valid;
    }*/
}
