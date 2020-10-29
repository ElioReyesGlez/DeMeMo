package com.erg.memorized.model;

import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.HashMap;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmModule;

import static com.erg.memorized.util.Constants.USER_COLUMN_EMAIL;
import static com.erg.memorized.util.Constants.USER_COLUMN_ID;
import static com.erg.memorized.util.Constants.USER_COLUMN_IMG;
import static com.erg.memorized.util.Constants.USER_COLUMN_MOBILE;
import static com.erg.memorized.util.Constants.USER_COLUMN_NAME;
import static com.erg.memorized.util.Constants.USER_COLUMN_PASS;
import static com.erg.memorized.util.Constants.USER_COLUMN_PREMIUM_STATUS;
import static com.erg.memorized.util.Constants.USER_COLUMN_VERSES_SCORE;

@RealmModule(classes = {ItemUser.class})
public class ItemUser extends RealmObject implements Serializable {

    @PrimaryKey
    private String id;

    private String email;
    private String img;
    private String mobile;
    private String name;
    private String pass;


    private RealmList<String> verses;
    private float score;
    private boolean isPremium = false;

    public ItemUser() {
    }

    public ItemUser(RealmList<String> verses) {
        this.verses = verses;
    }

    public ItemUser(String email, String id, String img,
                    String mobile, String name, String pass) {
        this.email = email;
        this.id = id;
        this.img = img;
        this.mobile = mobile;
        this.name = name;
        this.pass = pass;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public RealmList<String> getVerses() {
        return verses;
    }

    public void setVerses(RealmList<String> verses) {
        this.verses = verses;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public void addScore(float score) {
        this.score = +score;
    }

    public boolean isPremium() {
        return isPremium;
    }

    public void setPremium(boolean premium) {
        isPremium = premium;
    }

    public HashMap<String, String> getUserIntoHasMap() {
        HashMap<String, String> userHasMap = new HashMap<>();
        userHasMap.put(USER_COLUMN_EMAIL, email);
        userHasMap.put(USER_COLUMN_ID, id);
        userHasMap.put(USER_COLUMN_IMG, img);
        userHasMap.put(USER_COLUMN_MOBILE, mobile);
        userHasMap.put(USER_COLUMN_NAME, name);
        userHasMap.put(USER_COLUMN_PASS, pass);
        userHasMap.put(USER_COLUMN_VERSES_SCORE, String.valueOf(score));
        userHasMap.put(USER_COLUMN_PREMIUM_STATUS, String.valueOf(isPremium));
        return userHasMap;
    }

    public HashMap<String, String> getScoreInfoIntoHasMap() {
        HashMap<String, String> scoreHasMap = new HashMap<>();
        scoreHasMap.put(USER_COLUMN_NAME, name);
        scoreHasMap.put(USER_COLUMN_IMG, img);
        scoreHasMap.put(USER_COLUMN_VERSES_SCORE, String.valueOf(score));
        scoreHasMap.put(USER_COLUMN_PREMIUM_STATUS, String.valueOf(isPremium));
        return scoreHasMap;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof ItemUser) {
            ItemUser comparable = (ItemUser) obj;
            return getId().equals(comparable.getId());
        } else {
            return false;
        }
    }
}
