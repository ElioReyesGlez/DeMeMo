package com.erg.memorized.model.bible_api_models;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class Child {

    private String text;
    private ArrayList<Item> items;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public ArrayList<Item> getItems() {
        return items;
    }

    public void setItems(ArrayList<Item> items) {
        this.items = items;
    }

    @NotNull
    @Override
    public String toString() {
        return "Child{" +
                "text='" + text + '\'' +
                ", items=" + items +
                '}';
    }
}
