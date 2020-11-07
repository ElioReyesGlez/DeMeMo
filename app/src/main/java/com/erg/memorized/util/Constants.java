package com.erg.memorized.util;

import android.content.Context;

import androidx.fragment.app.FragmentActivity;

import com.erg.memorized.R;
import com.erg.memorized.model.bible_api_models.Bible;
import com.erg.memorized.model.bible_api_models.BibleLanguage;

import java.util.ArrayList;
import java.util.Collections;

/**
 * by elioenai
 */
public class Constants {

//    public static final String APP_NAME = "DeMeMo";

    public static final String VERSES_REALM_DB_NAME = "verses_realm_db";
    public static final String USERS_REALM_DB_NAME = "users_realm_db";

    public static final String VERSES_REALM_CLASS = "ItemVerse";
//    public static final String USERS_REALM_CLASS = "ItemUser";

    /*Verses Rows*/
    public static final String VERSE_COLUMN_POS = "pos";
    public static final String VERSE_COLUMN_TITLE = "title";
    public static final String VERSE_COLUMN_VERSE = "verseText";
    public static final String VERSE_COLUMN_ID = "id";
    public static final String VERSE_COLUMN_DATE_ALARM = "dateAlarm";
    public static final String VERSE_COLUMN_UNTIL_DATE_ALARM = "untilAlarm";
    public static final String VERSE_COLUMN_REPEATING_STATUS = "repeatingAlarmStatus";
    public static final String VERSE_COLUMN_CHECKED = "isChecked";
    public static final String VERSE_COLUMN_MEMORIZING_STATUS = "isMemorized";
    public static final String VERSE_ALARM_STATUS = "isOnAlarm";
    public static final String VERSE_COLUMN_DAYS_ON = "daysOn";
    public static final String VERSE_COLUMN_SCORE = "verse_score";

    /*LeaderBoa Rows*/
    public static final String LEADER_BOARD_COLUMN_IMG = "img";
    public static final String LEADER_BOARD_COLUMN_IS_PREMIUM = "isPremium";
    public static final String LEADER_BOARD_COLUMN_NAME = "name";
    public static final String LEADER_BOARD_COLUMN_SCORE = "score";

    public static final String ON_OFF = "isOnAlarm";
    public static final String AT = "@";
    public static final CharSequence LOW_LINE = "_";
    public static final CharSequence LOW_LINE_2X = "__";

    /*Shared Preferences*/
//    public static final String ID_RESOURCE = "key_resource";
    public static final String PREFS_NAME = "SharedPreferencesMemorized";
    public static final String AUDIO_MSG_KEY = "audio_message_key";
    public static final String STATUS_BAR_MSG_KEY = "status_bar_message_key";
    public static final String LOGIN_STATUS_KEY = "login_user_state";
    public static final String SIGN_ACTION_KEY = "signing_flag";
    public static final String INTRO_STATUS_KEY = "intro_flag";
    public static final String DIALOG_SPLIT_INFO_STATUS_KEY = "split_info_flag";
    public static final String DIALOG_ASK_TO_DO_TEST_STATUS_KEY = "ask_to_do_test_flag";
    public static final String SECTION_STATUS_KEY = "section_view_flag";
    public static final String EMAIL_VERIFIED_STATUS_KEY = "email_verified_flag";
    public static final String BIBLE_VERSION_KEY = "bible_version_key";

    /*URI CALENDARS*/
    public static final String URI_CALENDARS = "content://com.android.calendar/calendars";
    public static final int DEFAULT_SELECTED_REMAINDER = 10;
    public static final String ONE_HOUR = "+P1H";
    public static final String DAILY_KEY = "key_daily_";
    public static final String MONTHLY_KEY = "key_monthly_";
    public static final String WEEKLY_KEY = "key_weekly_";
    public static final String LAST_SYNC = "last_upload_";
//    public static final String UPLOAD_CONT = "upload_cont_";
    public static final int LEADER_BOARD_USERS_LIMIT = 20;


    /*VIBRATION*/
    public static final long VIBRATE_TIME = 12;
    public static final long SPECIAL_VIBRATE_TIME = 70;
    public static final long SPECIAL_MIN_VIBRATE_TIME = 50;
    public static final long MIN_VIBRATE_TIME = 10;
    public static final int FIX_SIZE = 4;

    public static final String DAILY_VERSE_KEY = "daily_verse_key";

    /*TEXT SIZES*/
    public static final int MIN_TEXT_SIZE = 12;
    public static final int TEXT_SIZE = 19;

    public static final int MENU_HOME = 1;
    public static final int MENU_VERSES = 2;
    public static final int MENU_CONFIG = 3;

    public static final int MIN_LAUNCHES_UNTIL_PROMPT = 20;
    public static final int MIN_DAYS_UNTIL_PROMPT = 7;

    /*Firebase*/
    public static final String USER_FIRE_BASE_REFERENCE = "users";
    public static final String LEADER_BOARD_FIRE_BASE_REFERENCE = "leader_board";
    public static final String PREMIUM_USER_FIRE_BASE_REFERENCE = "isPremium";

    /*User Rows*/
    public static final String USER_COLUMN_ID = "id";
//    public static final String USER_COLUMN_POS = "pos";
    public static final String USER_COLUMN_NAME = "name";
    public static final String USER_COLUMN_EMAIL = "email";
    public static final String USER_COLUMN_MOBILE = "mobile";
    public static final String USER_COLUMN_PASS = "pass";
    public static final String USER_COLUMN_IMG = "img";
    public static final String USER_COLUMN_VERSES = "verses";
    public static final String USER_COLUMN_VERSES_SCORE = "score";
    public static final String USER_COLUMN_PREMIUM_STATUS = "isPremium";

    public static final String DEFAULT = "default";


    public static final int DECIMAL_PLACE = 1;

    public static final String SPACE = " ";
    public static final String DOT = ".";
    public static final String DOUBLE_DOT = ":";
    public static final char DOT_CHAR = '.';
    public static final char SEMICOLON_CHAR = ';';
//    public static final char EAT_CHAR = ',';
//    public static final char EXCLAMATION_MARK_CHAR_UP = '¡';
    public static final char EXCLAMATION_MARK_CHAR_DOWN = '!';
//    public static final char QUESTION_MARK_CHAR_UP = '¿';
    public static final char QUESTION_MARK_CHAR_DOWN = '?';
//    public static final char DOUBLE_QUOTE = '"';
    public static final String REGEX_SPACE = "\\s+";
    public static final int SENTENCE_IDEAL_LENGTH = 7;
//    public static final int SECTION_SENTENCE_IDEAL_LENGTH = 10;

    //API YourVersion
    public static final String YOUR_VERSION_BASE_URL = "https://developers.youversionapi.com/1.0/";
    public static final String YOUR_VERSION_TOKEN = "uLOq9txaRAj3lfYxozBBecj4q-s";
    public static final String GET_VERSE_OF_THE_DAY = "verse_of_the_day";
    public static final String DAY_OF_THE_YEAR = "{day_of_year}";
    public static final String BAR = "/";
    public static final int DEFAULT_VERSION_ID = 1;
    //API BIBLE
    public static final String BIBLE_API_BASE_URL = "https://api.scripture.api.bible/";
    public static final String GET_BIBLES = "/v1/bibles";
    public static final String USER_KEY_API_BIBLE = "da95b5f585aaf284ae5e5ea4a4eefcd1";
    public static final String BIBLE_ID = "{bibleId}";
    public static final String VERSE_ID = "{verseId}";
    public static final String JSON = "json";

    //Bible HasMap Keys
    public static final String BIBLE_ID_KEY = "id";
    public static final String BIBLE_NAME_LOCAL_KEY = "nameLocal";
    public static final String BIBLE_ABBREVIATION_KEY = "abbreviationLocal";
    public static final String BIBLE_DESCRIPTION_KEY = "descriptionLocal";
    public static final String BIBLE_LANGUAGE_KEY = "language";

    //USER PREFERENCES
    public static final String CURRENT_SECTION_KEY = "section_key";
    public static final String APP_LANGUAGE_POSITION_KEY = "app_language_key";
    public static final String BIBLE_SELECTED_KEY = "bible_selected_key";
    public static final String LAST_USAGE_KEY = "last_usage_key";
    public static final String OPEN_TIME_USAGE_KEY = "open_time_usage_key";
    public static final String LAST_VERSE_READ_KEY = "last_read_key";
    public static final String LAST_VERSE_READ_DATE = "last_read_date_key";

    /*Rate keys*/
    public static final String PREF_LAST_LAUNCH_RATE_DIALOG_DATE_KEY = "rate_last_launch_date";
    public static final String PREF_RATE_LAUNCH_TIMES_KEY = "rate_launch_times";
    public static final String PREF_ALREADY_RATED_KEY = "already_rated_flag";
    public static final String PREF_SHOW_DIALOG_FLAG_KEY = "rate_show_dialog_flag";

    public static final String PREF_APP_FIRST_LAUNCH_KEY = "app_first_launch_date";

    /*Premium keys*/
    public static final String PREF_LAST_LAUNCH_PREMIUM_DIALOG_DATE_KEY = "premium_last_launch_date";
    public static final String PREF_PREMIUM_SHOW_DIALOG_FLAG_KEY = "premium_show_dialog_flag";

    /*Urls*/
    public static final String GOOGLE_APP_DETAILS_URL = "http://play.google.com/store/apps/details?id=";
    public static final String INSTAGRAM_APP_ACCOUNT_USER = "dememo_app";
    public static final String INSTAGRAM_APP_ACCOUNT_URL = "http://instagram.com/_u/"
            + INSTAGRAM_APP_ACCOUNT_USER;
    public static final String INSTAGRAM_PACKAGE = "com.instagram.android";
    public static final String LINKEDIN_PACKAGE = "com.linkedin.android";
    public static final String MARKET_APP_DETAILS_URL = "market://details?id=";

    public static final String URL_DEVELOPER_1 = "https://www.linkedin.com/in/elioenai-reyes-gonzález";
    public static final String URL_DEVELOPER_2 = "https://www.linkedin.com/in/josé-ernesto-cortes-7bb8671b6";

    public static final String SYNC_ALERT_FLAG_KEY = "sync_alert_flag";

    // DeMeMo adMob id
    public static final String interstitial_ad_unit_id = "ca-app-pub-5375894039512920/4596144610";

    // Interstitial Ad Unit Id
//    public static final String testing_interstitial_ad_unit_id =
//            "ca-app-pub-3940256099942544/1033173712";

    public static ArrayList<String> getBrands(Context context) {
        ArrayList<String> brands = new ArrayList<>();
        Collections.addAll(brands, context.getResources().getStringArray(R.array.brands));
        return brands;
    }

    public static ArrayList<String> getMarks(FragmentActivity context) {
        ArrayList<String> marks = new ArrayList<>();
        Collections.addAll(marks, context.getResources().getStringArray(R.array.marks));
        return marks;
    }

    /**
     * <item>English</item>
     * <item>Español</item>
     * <item>Português</item>
     * <item>繁體中文</item>
     * <item>Română</item>
     **/

    public static final int[] FlagResIds = new int[]{
            R.drawable.ic_flag_uk,
            R.drawable.ic_flag_spain,
            R.drawable.ic_flag_portugal
//            R.drawable.ic_flag_china,
//            R.drawable.ic_flag_romania
    };

    public static Bible King_James_Version() {
        Bible bible = new Bible();
        bible.setId("de4e12af7f28f599-02");
        bible.setNameLocal("King James Version");
        bible.setAbbreviationLocal("KJV");
        bible.setDescriptionLocal("Protestant");
        BibleLanguage language = new BibleLanguage();
        language.setNameLocal("English");
        bible.setLanguage(language);
        return bible;
    }

    public static Bible Reina_Valera_1909() {
        Bible bible = new Bible();
        bible.setId("592420522e16049f-01");
        bible.setNameLocal("Reina Valera Bible 1909");
        bible.setAbbreviationLocal("RVR09");
        bible.setDescriptionLocal("Protestant Bible");
        BibleLanguage language = new BibleLanguage();
        language.setNameLocal("Español");
        bible.setLanguage(language);
        return bible;
    }

    public static Bible Translation_Brasilian_Portuguese() {
        Bible bible = new Bible();
        bible.setId("90799bb5b996fddc-01");
        bible.setNameLocal("Translation for Translators in Brasilian Portuguese");
        bible.setAbbreviationLocal("TfTP");
        bible.setDescriptionLocal("comum");
        BibleLanguage language = new BibleLanguage();
        language.setNameLocal("Português");
        bible.setLanguage(language);
        return bible;
    }
}
