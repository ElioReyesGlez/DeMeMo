package com.erg.memorized.model.bible_api_models;

import java.util.ArrayList;

public class BibleRoot {
    private ArrayList<Bible> data;

    public ArrayList<Bible> getData() {
        return data;
    }

    public void setData(ArrayList<Bible> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Bible{" +
                "data=" + data +
                '}';
    }
}
