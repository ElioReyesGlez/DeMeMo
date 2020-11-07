package com.erg.memorized.model.bible_api_models;

import org.jetbrains.annotations.NotNull;

public class Bible {

    private String id;
    private String nameLocal;
    private String abbreviationLocal;
    private String descriptionLocal;
    private BibleLanguage language;

    public String getId() {
        return id;
    }

    public String getNameLocal() {
        return nameLocal;
    }

    public String getAbbreviationLocal() {
        return abbreviationLocal;
    }

    public BibleLanguage getLanguage() {
        return language;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setNameLocal(String nameLocal) {
        this.nameLocal = nameLocal;
    }

    public void setAbbreviationLocal(String abbreviationLocal) {
        this.abbreviationLocal = abbreviationLocal;
    }

    public void setLanguage(BibleLanguage language) {
        this.language = language;
    }

    public String getDescriptionLocal() {
        return descriptionLocal;
    }

    public void setDescriptionLocal(String descriptionLocal) {
        this.descriptionLocal = descriptionLocal;
    }

    @NotNull
    @Override
    public String toString() {
        return "Bible{" +
                "id='" + id + '\'' +
                ", nameLocal='" + nameLocal + '\'' +
                ", abbreviationLocal='" + abbreviationLocal + '\'' +
                ", descriptionLocal='" + descriptionLocal + '\'' +
                ", language=" + language +
                '}';
    }
}
