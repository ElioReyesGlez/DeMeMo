package com.erg.memorized.model.bible_api_models;

import java.util.ArrayList;

public class Children {
    private ArrayList<Item> items;

    public ArrayList<Item> getItems() {
        return items;
    }

    public void setItems(ArrayList<Item> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return "Children{" +
                "items=" + items +
                '}';
    }
}
