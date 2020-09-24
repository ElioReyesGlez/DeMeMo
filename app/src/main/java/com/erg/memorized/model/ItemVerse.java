package com.erg.memorized.model;

import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.Objects;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmModule;

import static com.erg.memorized.util.Constants.VERSE_ALARM_STATUS;
import static com.erg.memorized.util.Constants.VERSE_COLUMN_DATE_ALARM;
import static com.erg.memorized.util.Constants.VERSE_COLUMN_ID;
import static com.erg.memorized.util.Constants.VERSE_COLUMN_MEMORIZING_STATUS;
import static com.erg.memorized.util.Constants.VERSE_COLUMN_REPEATING_STATUS;
import static com.erg.memorized.util.Constants.VERSE_COLUMN_SCORE;
import static com.erg.memorized.util.Constants.VERSE_COLUMN_TITLE;
import static com.erg.memorized.util.Constants.VERSE_COLUMN_UNTIL_DATE_ALARM;
import static com.erg.memorized.util.Constants.VERSE_COLUMN_VERSE;

@RealmModule(classes = {ItemVerse.class})
public class ItemVerse extends RealmObject {


    @PrimaryKey
    private long id;

    private int pos;
    private String title;
    private String verseText;
    private boolean isOnAlarm;
    private boolean repeatingAlarmStatus;
    private long dateAlarm = -1;
    private long untilAlarm = -1;
    private boolean isMemorized;
    private float verseScore = 0;

    public ItemVerse() {
    }

    public ItemVerse(String title, String verse) {
        this.title = title;
        this.verseText = verse;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public String getVerseText() {
        return verseText;
    }

    public void setVerseText(String verseText) {
        this.verseText = verseText;
    }

    public long getId() {
        return id;
    }

    public void setId(long date) {
        this.id = date;
    }

    public long getDateAlarm() {
        return dateAlarm;
    }

    public boolean isOnAlarm() {
        return isOnAlarm;
    }

    public void setOnAlarm(boolean onAlarm) {
        isOnAlarm = onAlarm;
    }

    public void setDateAlarm(long dateAlarm) {
        this.dateAlarm = dateAlarm;
    }

    public long getUntilAlarm() {
        return untilAlarm;
    }

    public void setUntilAlarm(long untilAlarm) {
        this.untilAlarm = untilAlarm;
    }

    public boolean getRepeatingAlarmStatus() {
        return repeatingAlarmStatus;
    }

    public void setRepeatingAlarmStatus(boolean repeatingAlarm) {
        repeatingAlarmStatus = repeatingAlarm;
    }

    public boolean isMemorized() {
        return isMemorized;
    }

    public void setMemorized(boolean memorized) {
        isMemorized = memorized;
    }

    public float getVerseScore() {
        return verseScore;
    }

    public void setVerseScore(float verseScore) {
        this.verseScore = verseScore;
    }

    public HashMap<String, String> getVerseIntoHasMap() {
        HashMap<String, String> verseHasMap = new HashMap<>();
        verseHasMap.put(VERSE_COLUMN_ID, String.valueOf(getId()));
        verseHasMap.put(VERSE_COLUMN_TITLE, getTitle());
        verseHasMap.put(VERSE_COLUMN_VERSE, getVerseText());
        verseHasMap.put(VERSE_COLUMN_MEMORIZING_STATUS, String.valueOf(isMemorized()));
        verseHasMap.put(VERSE_ALARM_STATUS, String.valueOf(isOnAlarm()));
        verseHasMap.put(VERSE_COLUMN_DATE_ALARM, String.valueOf(getDateAlarm()));
        verseHasMap.put(VERSE_COLUMN_UNTIL_DATE_ALARM, String.valueOf(getUntilAlarm()));
        verseHasMap.put(VERSE_COLUMN_REPEATING_STATUS, String.valueOf(getRepeatingAlarmStatus()));
        verseHasMap.put(VERSE_COLUMN_SCORE, String.valueOf(getVerseScore()));

        return verseHasMap;
    }

    public static ItemVerse getVerseFromHasMap(HashMap<String, String> verseHasMap) {
        ItemVerse itemVerse = new ItemVerse();
        itemVerse.setId(Long.parseLong(Objects.requireNonNull(verseHasMap.get(VERSE_COLUMN_ID))));
        itemVerse.setTitle(verseHasMap.get(VERSE_COLUMN_TITLE));
        itemVerse.setVerseText(verseHasMap.get(VERSE_COLUMN_VERSE));
        itemVerse.setMemorized(Boolean.parseBoolean(verseHasMap.get(VERSE_COLUMN_MEMORIZING_STATUS)));
        itemVerse.setOnAlarm(Boolean.parseBoolean(verseHasMap.get(VERSE_ALARM_STATUS)));
        itemVerse.setDateAlarm(Long.parseLong(Objects.requireNonNull(verseHasMap.get(VERSE_COLUMN_DATE_ALARM))));
        itemVerse.setUntilAlarm(Long.parseLong(Objects.requireNonNull(verseHasMap.get(VERSE_COLUMN_DATE_ALARM))));
        itemVerse.setRepeatingAlarmStatus(Boolean.parseBoolean(verseHasMap.get(VERSE_COLUMN_DATE_ALARM)));
        itemVerse.setVerseScore(Float.parseFloat(Objects.requireNonNull(verseHasMap.get(VERSE_COLUMN_SCORE))));
        return itemVerse;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof ItemVerse) {
            ItemVerse comparable = (ItemVerse) obj;
            return getId() == comparable.getId()
                    && getTitle().equals(comparable.getTitle())
                    && getVerseText().equals(comparable.getVerseText())
                    && isMemorized() == comparable.isMemorized()
                    && isOnAlarm() == comparable.isOnAlarm()
                    && getDateAlarm() == comparable.getDateAlarm()
                    && getUntilAlarm() == comparable.getUntilAlarm()
                    && getRepeatingAlarmStatus() == comparable.getRepeatingAlarmStatus()
                    && getVerseScore() == comparable.getVerseScore();
        } else {
            return false;
        }
    }

    /*
    *  private boolean repeatingAlarmStatus;
    private long dateAlarm = -1;
    private long untilAlarm = -1;
    * */
}
