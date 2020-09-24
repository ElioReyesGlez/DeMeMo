package com.erg.memorized.model;

import com.erg.memorized.model.your_version_models.Verse;

public class DailyVerse {

    private Verse verse;
    private String day;

    public Verse getVerse() {
        return verse;
    }

    public String getDay() {
        return day;
    }

    @Override
    public String toString() {
        return "DailyVerse{" +
                "verse=" + verse +
                ", day='" + day + '\'' +
                '}';
    }
}
