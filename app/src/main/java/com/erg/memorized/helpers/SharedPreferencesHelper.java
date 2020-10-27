package com.erg.memorized.helpers;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.erg.memorized.R;
import com.erg.memorized.model.ItemVerse;
import com.erg.memorized.model.bible_api_models.Bible;
import com.erg.memorized.model.bible_api_models.BibleLanguage;
import com.erg.memorized.util.Constants;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.erg.memorized.util.Constants.APP_LANGUAGE_POSITION_KEY;
import static com.erg.memorized.util.Constants.AUDIO_MSG_KEY;
import static com.erg.memorized.util.Constants.BIBLE_ABBREVIATION_KEY;
import static com.erg.memorized.util.Constants.BIBLE_DESCRIPTION_KEY;
import static com.erg.memorized.util.Constants.BIBLE_ID_KEY;
import static com.erg.memorized.util.Constants.BIBLE_LANGUAGE_KEY;
import static com.erg.memorized.util.Constants.BIBLE_NAME_LOCAL_KEY;
import static com.erg.memorized.util.Constants.BIBLE_SELECTED_KEY;
import static com.erg.memorized.util.Constants.BIBLE_VERSION_KEY;
import static com.erg.memorized.util.Constants.CURRENT_SECTION_KEY;
import static com.erg.memorized.util.Constants.DAILY_VERSE_KEY;
import static com.erg.memorized.util.Constants.DECIMAL_PLACE;
import static com.erg.memorized.util.Constants.DIALOG_SPLIT_INFO_STATUS_KEY;
import static com.erg.memorized.util.Constants.EMAIL_VERIFIED_STATUS_KEY;
import static com.erg.memorized.util.Constants.INTRO_STATUS_KEY;
import static com.erg.memorized.util.Constants.LAST_USAGE_KEY;
import static com.erg.memorized.util.Constants.LAST_VERSE_READ_DATE;
import static com.erg.memorized.util.Constants.LAST_VERSE_READ_KEY;
import static com.erg.memorized.util.Constants.LAST_VERSE_TEXT_KEY;
import static com.erg.memorized.util.Constants.LOGIN_STATUS_KEY;
import static com.erg.memorized.util.Constants.OPEN_TIME_USAGE_KEY;
import static com.erg.memorized.util.Constants.PREFS_NAME;
import static com.erg.memorized.util.Constants.PREF_ALREADY_RATED_KEY;
import static com.erg.memorized.util.Constants.PREF_APP_FIRST_LAUNCH_KEY;
import static com.erg.memorized.util.Constants.PREF_LAST_LAUNCH_PREMIUM_DIALOG_DATE_KEY;
import static com.erg.memorized.util.Constants.PREF_LAST_LAUNCH_RATE_DIALOG_DATE_KEY;
import static com.erg.memorized.util.Constants.PREF_PREMIUM_LAUNCH_TIMES_KEY;
import static com.erg.memorized.util.Constants.PREF_PREMIUM_SHOW_DIALOG_FLAG_KEY;
import static com.erg.memorized.util.Constants.PREF_RATE_LAUNCH_TIMES_KEY;
import static com.erg.memorized.util.Constants.PREF_SHOW_DIALOG_FLAG_KEY;
import static com.erg.memorized.util.Constants.SECTION_STATUS_KEY;
import static com.erg.memorized.util.Constants.STATUS_BAR_MSG_KEY;

public class SharedPreferencesHelper {

    private static final String TAG = "SharedPreferencesHelper";

    private final Context context;
    private final SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;


    public SharedPreferencesHelper(Context context) {
        this.context = context;
        this.sharedPref = context.getSharedPreferences(PREFS_NAME, Activity.MODE_PRIVATE);
    }

    public boolean isEmpty() {
        return sharedPref.getAll().isEmpty();
    }

    public boolean clear() {
        editor = sharedPref.edit();
        return editor.clear().commit();
    }

    public void setOnAudioMessageViewed(String key) {
        editor = sharedPref.edit();
        editor.putBoolean(key, true);
        editor.apply();
    }

    public void setOnStatusBarMessageViewed(String key) {
        editor = sharedPref.edit();
        editor.putBoolean(key, true);
        editor.apply();
    }

    public boolean isAudioMessageViewed() {
        return sharedPref.getBoolean(AUDIO_MSG_KEY, false);
    }

    public boolean isStatusBarMessageViewed() {
        return sharedPref.getBoolean(STATUS_BAR_MSG_KEY, false);
    }

    public void setUserLoginState(boolean flag) {
        editor = sharedPref.edit();
        editor.putBoolean(LOGIN_STATUS_KEY, flag);
        editor.apply();
    }

    public boolean getUserLoginStatus() {
        return sharedPref.getBoolean(LOGIN_STATUS_KEY, false);
    }

    public void setIntroActivityStatus(boolean flag) {
        editor = sharedPref.edit();
        editor.putBoolean(INTRO_STATUS_KEY, flag);
        editor.apply();
    }

    public boolean getIntroStatus() {
        return sharedPref.getBoolean(INTRO_STATUS_KEY, false);
    }

    public void setDialogSplitInfoStatus(boolean flag) {
        editor = sharedPref.edit();
        editor.putBoolean(DIALOG_SPLIT_INFO_STATUS_KEY, flag);
        editor.apply();
    }

    public boolean getDialogSplitInfoStatus() {
        return sharedPref.getBoolean(DIALOG_SPLIT_INFO_STATUS_KEY, false);
    }

    public void setSectionCheckedStatus(String title, int position, boolean isChecked) {
        editor = sharedPref.edit();
        editor.putBoolean(title + position, isChecked);
        editor.apply();
    }

    public boolean getSectionCheckedStatus(String title, int position) {
        return sharedPref.getBoolean(title + position, false);
    }

    public void setSectionViewStatus(boolean flag) {
        editor = sharedPref.edit();
        editor.putBoolean(SECTION_STATUS_KEY, flag);
        editor.apply();
    }

    public boolean getSectionViewStatus() {
        return sharedPref.getBoolean(SECTION_STATUS_KEY, false);
    }

    public int getCheckedSections(String title, ArrayList<String> splitVerseList) {
        int checkedCont = 0;
        for (int i = 0; i < splitVerseList.size(); i++) {
            if (getSectionCheckedStatus(title, i)) {
                checkedCont++;
            }
        }
        return checkedCont;
    }

    public void setEmailVerifiedStatus(boolean flag) {
        editor = sharedPref.edit();
        editor.putBoolean(EMAIL_VERIFIED_STATUS_KEY, flag);
        editor.apply();
    }

    public boolean getEmailVerifiedStatus() {
        return sharedPref.getBoolean(EMAIL_VERIFIED_STATUS_KEY, false);
    }

    public void saveUserTextSizePref(String key, float size) {
        editor = sharedPref.edit();
        editor.putFloat(key, size);
        editor.apply();
    }

    public boolean existKey(String key) {
        return sharedPref.contains(key);
    }

    public float getUserTextSizePref(String key) {
        return sharedPref.getFloat(key, 15);
    }

    public void removeTextSizePref(String key) {
        editor.remove(key);
    }

    public void setCurrentSectionKey(String key) {
        editor = sharedPref.edit();
        editor.putString(CURRENT_SECTION_KEY, key);
        editor.apply();
    }

    public String getCurrentSectionKey() {
        return sharedPref.getString(CURRENT_SECTION_KEY, "");
    }

    public void setRepeatingNotifyStatus(String key, boolean flag) {
        editor = sharedPref.edit();
        editor.putBoolean(key, flag);
        editor.apply();
    }

    public boolean getRepeatingNotifyStatus(String key) {
        return sharedPref.getBoolean(key, false);
    }

    public Long getLastUploadDate(String key) {
        return sharedPref.getLong(key, 0);
    }

    public void setLastUploadDate(String key, long date) {
        editor = sharedPref.edit();
        editor.putLong(key, date);
        editor.apply();
    }

    public void setLanguagePosition(int pos) {
        editor = sharedPref.edit();
        editor.putInt(APP_LANGUAGE_POSITION_KEY, pos);
        editor.apply();
    }

    public int getLanguagePos() {
        return sharedPref.getInt(APP_LANGUAGE_POSITION_KEY, 0);
    }

    public void setBibleSelectedStatus(String id) {
        editor = sharedPref.edit();
        editor.putString(BIBLE_SELECTED_KEY, id);
        editor.apply();
    }

    public boolean getBibleSelectedStatus(String id) {
        return sharedPref.getString(BIBLE_SELECTED_KEY, "").equals(id);
    }


    public void saveBibleVersion(Bible bible) {
        editor = sharedPref.edit();
        Gson gson = new Gson();
        Map<String, String> hasBible = new HashMap<>();
        hasBible.put(BIBLE_ID_KEY, bible.getId());
        hasBible.put(BIBLE_NAME_LOCAL_KEY, bible.getNameLocal());
        hasBible.put(BIBLE_ABBREVIATION_KEY, bible.getAbbreviationLocal());
        hasBible.put(BIBLE_DESCRIPTION_KEY, bible.getDescriptionLocal());
        hasBible.put(BIBLE_LANGUAGE_KEY, bible.getLanguage().getNameLocal());
        String json = gson.toJson(hasBible);
        editor.putString(BIBLE_VERSION_KEY, json);
        editor.apply();
    }


    public Bible getBibleVersion() {
        Gson gson = new Gson();
        String json = sharedPref.getString(BIBLE_VERSION_KEY, null);

        if (json == null)
            return getDefaultBibleVersion();

        Type type = new TypeToken<HashMap<String, String>>() {
        }.getType();
        HashMap<String, String> bibleHashMap = gson.fromJson(json, type);
        if (bibleHashMap != null) {
            Bible bible = new Bible();
            bible.setId(bibleHashMap.get(BIBLE_ID_KEY));
            bible.setNameLocal(bibleHashMap.get(BIBLE_NAME_LOCAL_KEY));
            bible.setAbbreviationLocal(bibleHashMap.get(BIBLE_ABBREVIATION_KEY));
            bible.setDescriptionLocal(bibleHashMap.get(BIBLE_DESCRIPTION_KEY));
            BibleLanguage language = new BibleLanguage();
            language.setNameLocal(bibleHashMap.get(BIBLE_LANGUAGE_KEY));
            bible.setLanguage(language);
            return bible;
        } else {
            return getDefaultBibleVersion();
        }
    }


    private Bible getDefaultBibleVersion() {
        String localeCode = Locale.getDefault().getLanguage();
        String es = new Locale("es").getLanguage();
        String pt = new Locale("pt").getLanguage();
        String[] arrayCodes = context.getResources().getStringArray(R.array.languages_codes);
        ArrayList<String> codes = new ArrayList<>(Arrays.asList(arrayCodes));

        if (codes.get(getLanguagePos()).equals(es))
            return Constants.Reina_Valera_1909();

        if (codes.get(getLanguagePos()).equals(pt))
            return Constants.Translation_Brasilian_Portuguese();

        return Constants.King_James_Version();
    }

    public void saveDailyVerse(ItemVerse dailyVerse) {
        editor = sharedPref.edit();
        Gson gson = new Gson();
        String json = gson.toJson(dailyVerse);
        editor.putString(DAILY_VERSE_KEY, json);
        editor.apply();
    }

    public ItemVerse getDailyVerse() {
        Gson gson = new Gson();
        String json = sharedPref.getString(DAILY_VERSE_KEY, null);
        Type type = new TypeToken<ItemVerse>() {
        }.getType();
        return gson.fromJson(json, type);
    }

    public void setEvaluatorInfoMessageStatus(String tag, boolean flag) {
        editor = sharedPref.edit();
        editor.putBoolean(tag, flag);
        editor.apply();
    }

    public boolean getEvaluatorInfoMessageStatus(String tag) {
        return sharedPref.getBoolean(tag, false);
    }

    public ArrayList<Float> getUserActivity() {
        ArrayList<Float> temp = new ArrayList<>();
        String[] dayCodes = context.getResources().getStringArray(R.array.day_codes);
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);

        for (int i = 0; i < TimeHelper.getCurrentDayOfWeekInNumber(); i++) {
            String key = dayCodes[i] + (i+1);
            float value = getUsageValue(key);
            float roundValue = ScoreHelper.round(value, DECIMAL_PLACE);
            temp.add(roundValue);
        }
        return temp;
    }


    public String getLastVerseText() {
        return sharedPref.getString(LAST_VERSE_TEXT_KEY, "");
    }

    public void saveLastUsage(long date) {
        editor = sharedPref.edit();
        editor.putLong(LAST_USAGE_KEY, date);
        editor.apply();
    }

    public long getLastUsage() {
        return sharedPref.getLong(LAST_USAGE_KEY, 0);
    }


    public void increasesUsageValue(String key, float value) {
        editor = sharedPref.edit();
        float usageValue = getUsageValue(key);
        usageValue += value;
        editor.putFloat(key, usageValue);
        editor.apply();
    }


    public float getUsageValue(String key) {
        return sharedPref.getFloat(key, 0.0f);
    }

    public void removeUsageValue(String key) {
        editor = sharedPref.edit();
        editor.remove(key);
        editor.apply();
    }

    public void saveMemorizingFragmentUsageOpenTime(long currentTimeMillis) {
        editor = sharedPref.edit();
        editor.putLong(OPEN_TIME_USAGE_KEY, currentTimeMillis);
        editor.apply();
    }

    public long getMemorizingFragmentUsageOpenTime() {
        return sharedPref.getLong(OPEN_TIME_USAGE_KEY, 0);
    }

    public void resetMemorizingFragmentUsageOpenTime() {
        editor = sharedPref.edit();
        editor.remove(OPEN_TIME_USAGE_KEY);
        editor.apply();
    }

    public void saveLastVerseRead(ItemVerse verse) {
        editor = sharedPref.edit();
        Gson gson = new Gson();
        String json = gson.toJson(verse);
        editor.putString(LAST_VERSE_READ_KEY, json);
        editor.apply();
    }

    public ItemVerse getLastVerseRead() {
        Gson gson = new Gson();
        String json = sharedPref.getString(LAST_VERSE_READ_KEY, null);
        Type type = new TypeToken<ItemVerse>() {}.getType();
        try {
            return gson.fromJson(json, type);
        } catch (Exception e) {
            Log.e(TAG, "getLastVerseRead: ERROR: " + e.getMessage());
            return  null;
        }
    }

    public void saveLastVerseReadDate(long date) {
        editor = sharedPref.edit();
        editor.putLong(LAST_VERSE_READ_DATE, date);
        editor.apply();
    }

    public long getLastVerseReadDate() {
        return sharedPref.getLong(LAST_VERSE_READ_DATE, 0);
    }


    /*--------------------------Rate Dialog Flags--------------------------------------*/

    public void setAgreeToShowRateDialog(boolean flag) {
        editor = sharedPref.edit();
        editor.putBoolean(PREF_SHOW_DIALOG_FLAG_KEY, flag);
        editor.apply();
    }

    public boolean getIsAgreeShowRateDialog() {
        return sharedPref.getBoolean(PREF_SHOW_DIALOG_FLAG_KEY, true);
    }

    public void setLastLaunchRateDialogDate() {
        editor = sharedPref.edit();
        editor.putLong(PREF_LAST_LAUNCH_RATE_DIALOG_DATE_KEY, System.currentTimeMillis());
        editor.apply();
    }

    public long getLastLaunchRateDialogDate() {
        return sharedPref.getLong(PREF_LAST_LAUNCH_RATE_DIALOG_DATE_KEY, 0);
    }

    public void setRateLaunchTimes(int launchTimes) {
        editor = sharedPref.edit();
        editor.putInt(PREF_RATE_LAUNCH_TIMES_KEY, launchTimes);
        editor.apply();
    }

    public int getRateLaunchesTimes() {
        return sharedPref.getInt(PREF_RATE_LAUNCH_TIMES_KEY, 0);
    }

    public void setAppFirstLaunchDate() {
        editor = sharedPref.edit();
        editor.putLong(PREF_APP_FIRST_LAUNCH_KEY, System.currentTimeMillis());
        editor.apply();
    }

    public boolean isFirstLaunch() {
        return sharedPref.getLong(PREF_APP_FIRST_LAUNCH_KEY, 0) == 0L;
    }

    public void setAlreadyRatedFlag() {
        editor = sharedPref.edit();
        editor.putBoolean(PREF_ALREADY_RATED_KEY, true);
        editor.apply();
    }

    public boolean isAlreadyRated() {
        return sharedPref.getBoolean(PREF_ALREADY_RATED_KEY, false) ;
    }

    public void resetRateDialogFlags() {
        editor = sharedPref.edit();
        editor.remove(PREF_ALREADY_RATED_KEY);
        editor.remove(PREF_RATE_LAUNCH_TIMES_KEY);
        editor.apply();
    }

    /*--------------------------Premium Dialog Flags--------------------------------------*/

    public void setAgreeToShowPremiumDialog(boolean flag) {
        editor = sharedPref.edit();
        editor.putBoolean(PREF_PREMIUM_SHOW_DIALOG_FLAG_KEY, flag);
        editor.apply();
    }

    public boolean getIsAgreeShowPremiumDialog() {
        return sharedPref.getBoolean(PREF_PREMIUM_SHOW_DIALOG_FLAG_KEY, true);
    }

    public void setPremiumLaunchTimes(int launchTimes) {
        editor = sharedPref.edit();
        editor.putInt(PREF_PREMIUM_LAUNCH_TIMES_KEY, launchTimes);
        editor.apply();
    }

    public int getPremiumLaunchesTimes() {
        return sharedPref.getInt(PREF_PREMIUM_LAUNCH_TIMES_KEY, 0);
    }

    public void setLastPremiumRateDialogDate() {
        editor = sharedPref.edit();
        editor.putLong(PREF_LAST_LAUNCH_PREMIUM_DIALOG_DATE_KEY, System.currentTimeMillis());
        editor.apply();
    }

    public long getLastLaunchPremiumDialogDate() {
        return sharedPref.getLong(PREF_LAST_LAUNCH_PREMIUM_DIALOG_DATE_KEY, 0);
    }

    public void resetPremiumDialogFlags() {
        editor = sharedPref.edit();
        editor.remove(PREF_PREMIUM_LAUNCH_TIMES_KEY);
        editor.apply();
    }
}
