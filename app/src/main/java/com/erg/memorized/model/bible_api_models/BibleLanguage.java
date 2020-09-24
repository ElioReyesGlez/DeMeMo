package com.erg.memorized.model.bible_api_models;

public class BibleLanguage {
    private String id;
    private String name;
    private String nameLocal;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getNameLocal() {
        return nameLocal;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNameLocal(String nameLocal) {
        this.nameLocal = nameLocal;
    }

    @Override
    public String toString() {
        return "BibleLanguage{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", nameLocal='" + nameLocal + '\'' +
                '}';
    }
}
