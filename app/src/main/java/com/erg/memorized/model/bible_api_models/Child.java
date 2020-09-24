package com.erg.memorized.model.bible_api_models;

public class Child {

    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "Child{" +
                "text='" + text + '\'' +
                '}';
    }
}
