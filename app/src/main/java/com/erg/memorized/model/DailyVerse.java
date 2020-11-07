package com.erg.memorized.model;

import com.erg.memorized.model.your_version_models.Verse;

import org.jetbrains.annotations.NotNull;

public class DailyVerse {

    private Verse verse;
    private String day;

    public Verse getVerse() {
        return verse;
    }

    public String getDay() {
        return day;
    }

    @NotNull
    @Override
    public String toString() {
        return "DailyVerse{" +
                "verse=" + verse +
                ", day='" + day + '\'' +
                '}';
    }
}
