package com.erg.memorized.model;

import org.jetbrains.annotations.NotNull;

public class LeaderboardItem {

    private String id;
    private String img;
    private boolean isPremium;
    private String name;
    private float score;

    public LeaderboardItem() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public boolean isPremium() {
        return isPremium;
    }

    public void setPremium(boolean premium) {
        isPremium = premium;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    @NotNull
    @Override
    public String toString() {
        return "LeaderboardItem{" +
                "id='" + id + '\'' +
                ", img='" + img + '\'' +
                ", isPremium=" + isPremium +
                ", name='" + name + '\'' +
                ", score=" + score +
                '}';
    }
}
