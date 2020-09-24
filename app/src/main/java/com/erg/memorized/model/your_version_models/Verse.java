package com.erg.memorized.model.your_version_models;

import java.util.ArrayList;

public class Verse {

    private String human_reference;
    private ArrayList<String> usfms;
    private String url;
    private String text;

    public String getHuman_reference() {
        return human_reference;
    }

    public ArrayList<String> getUsfms() {
        return usfms;
    }

    public String getUrl() {
        return url;
    }

    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return "Verse{" +
                "human_reference='" + human_reference + '\'' +
                ", usfms=" + usfms +
                ", url='" + url + '\'' +
                ", text='" + text + '\'' +
                '}';
    }
}
