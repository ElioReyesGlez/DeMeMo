package com.erg.memorized.helpers;


import android.util.Log;

import java.util.ArrayList;

import static com.erg.memorized.util.Constants.DOT_CHAR;
import static com.erg.memorized.util.Constants.EAT_CHAR;
import static com.erg.memorized.util.Constants.EXCLAMATION_MARK_CHAR_DOWN;
import static com.erg.memorized.util.Constants.QUESTION_MARK_CHAR_DOWN;
import static com.erg.memorized.util.Constants.REGEX_SPACE;
import static com.erg.memorized.util.Constants.SEMICOLON_CHAR;
import static com.erg.memorized.util.Constants.SENTENCE_IDEAL_LENGTH;
import static com.erg.memorized.util.Constants.SPACE;

public class ReaderHelper {

    public static final String TAG = "ReaderHelper";

    private final String text;
    private boolean againFlag = false;

    public ReaderHelper(String text) {
        this.text = text;
    }

    public ArrayList<String> getsSplitTextIntoList() {

        String word;
        char token;
        int sentencesCont = SENTENCE_IDEAL_LENGTH;

        ArrayList<String> result = new ArrayList<>();
        StringBuilder stringBuilder = new StringBuilder();
        String[] textSplitBySpace = text.split(REGEX_SPACE);

        for (int i = 0; i < textSplitBySpace.length; i++) {
            word = textSplitBySpace[i];
            if (i >= sentencesCont) {
                for (int k = 0; k < word.length(); k++) {
                    token = word.charAt(k);

                    boolean breakingPointFlag  = isBreakingPoint(token);

                    if (breakingPointFlag) {
                        stringBuilder.append(word).append(SPACE);
                        result.add(stringBuilder.toString());
                        stringBuilder = new StringBuilder();
                        sentencesCont += SENTENCE_IDEAL_LENGTH;
                        word = "";
                    }
                }
            }
            stringBuilder.append(word).append(SPACE);
        }
        result.add(stringBuilder.toString());

        return cleanResult(result);
    }

    private ArrayList<String> cleanResult(ArrayList<String> result) {
        for (int i = 0; i < result.size(); i++) {
            if (result.get(i).equalsIgnoreCase(SPACE)) {
                String remove = result.remove(i);
                Log.d(TAG, "cleanResult: pos removed" + remove + i);
            }
        }
        return result;
    }

/*
    public String[] getsSplitTextIntoArray() {
        if (text.split(String.valueOf(DOT_CHAR)).length > 1) {
            return text.split(String.valueOf(DOT_CHAR));
        } else if (text.split(String.valueOf(EXCLAMATION_MARK_CHAR)).length > 1) {
            return text.split(String.valueOf(EXCLAMATION_MARK_CHAR));
        } else if (text.split(String.valueOf(QUESTION_MARK_CHAR)).length > 1) {
            return text.split(String.valueOf(QUESTION_MARK_CHAR));
        } else if (text.split(String.valueOf(SEMICOLON_CHAR)).length > 1) {
            return text.split(String.valueOf(SEMICOLON_CHAR));
        } else {
            return new String[]{"breaking point do not found"};
        }
    }
*/

    private boolean isBreakingPoint(char token) {
        return token == DOT_CHAR
                || token == EXCLAMATION_MARK_CHAR_DOWN
                || token == QUESTION_MARK_CHAR_DOWN
                || token == SEMICOLON_CHAR;
    }

    private boolean isBreakingPointWithEatChar(char token) {
        return token == DOT_CHAR
                || token == EXCLAMATION_MARK_CHAR_DOWN
                || token == QUESTION_MARK_CHAR_DOWN
                || token == EAT_CHAR;
    }

    private boolean isSectionBreakingPoint(char token) {
        return token == DOT_CHAR
                || token == EXCLAMATION_MARK_CHAR_DOWN
                || token == QUESTION_MARK_CHAR_DOWN
                || token == SEMICOLON_CHAR;
    }
}
