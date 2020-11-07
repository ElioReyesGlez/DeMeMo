package com.erg.memorized.model.bible_api_models;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class Item {

    private String text;
    private ArrayList<Child> items;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public ArrayList<Child> getItems() {
        return items;
    }

    public void setItems(ArrayList<Child> items) {
        this.items = items;
    }

    @NotNull
    @Override
    public String toString() {
        return "Item{" +
                "text='" + text + '\'' +
                ", items=" + items +
                '}';
    }
}
