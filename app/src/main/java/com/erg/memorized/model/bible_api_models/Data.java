package com.erg.memorized.model.bible_api_models;

import java.util.ArrayList;

public class Data {
    private String id;
    private String bibleId;
    private String reference;
    private ArrayList<Children> content;

    public String getId() {
        return id;
    }

    public String getBibleId() {
        return bibleId;
    }

    public String getReference() {
        return reference;
    }

    public ArrayList<Children> getContent() {
        return content;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setBibleId(String bibleId) {
        this.bibleId = bibleId;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public void setContent(ArrayList<Children> content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "Data{" +
                "id='" + id + '\'' +
                ", bibleId='" + bibleId + '\'' +
                ", reference='" + reference + '\'' +
                ", content=" + content +
                '}';
    }
}
