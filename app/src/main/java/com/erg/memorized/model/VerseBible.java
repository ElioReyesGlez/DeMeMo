package com.erg.memorized.model;

import com.erg.memorized.model.bible_api_models.Data;

import org.jetbrains.annotations.NotNull;

public class VerseBible {
   private Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    @NotNull
    @Override
    public String toString() {
        return "VerseBible{" +
                "data=" + data +
                '}';
    }
}
