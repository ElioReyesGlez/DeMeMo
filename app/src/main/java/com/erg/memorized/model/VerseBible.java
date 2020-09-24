package com.erg.memorized.model;

import com.erg.memorized.model.bible_api_models.Data;

public class VerseBible {
   private Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "VerseBible{" +
                "data=" + data +
                '}';
    }
}
