package com.erg.memorized.model.bible_api_models;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class BibleRoot {
    private ArrayList<Bible> data;

    public ArrayList<Bible> getData() {
        return data;
    }

    public void setData(ArrayList<Bible> data) {
        this.data = data;
    }

    @NotNull
    @Override
    public String toString() {
        return "Bible{" +
                "data=" + data +
                '}';
    }
}
