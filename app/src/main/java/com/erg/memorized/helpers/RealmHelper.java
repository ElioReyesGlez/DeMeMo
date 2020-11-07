package com.erg.memorized.helpers;

import android.content.Context;
import android.util.Log;

import com.erg.memorized.model.ItemUser;
import com.erg.memorized.model.ItemVerse;
import com.erg.memorized.util.Constants;

import java.util.ArrayList;
import java.util.Objects;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.Sort;

import static com.erg.memorized.util.Constants.VERSE_COLUMN_ID;

public class RealmHelper {

    private static final String TAG = "RealmHelper";

    private final RealmConfiguration verseRealmConfig;
    private final RealmConfiguration userRealmConfig;
    private RealmResults<ItemVerse> verseRealmResults;

    public RealmHelper() {
        verseRealmConfig = new RealmConfiguration.Builder()
                .name(Constants.VERSES_REALM_DB_NAME)
                .modules(new ItemVerse())
                .deleteRealmIfMigrationNeeded()
                .build();

        userRealmConfig = new RealmConfiguration.Builder()
                .name(Constants.USERS_REALM_DB_NAME)
                .modules(new ItemUser())
                .deleteRealmIfMigrationNeeded()
                .build();
    }

    public Realm getVerseRealmInstance() {
        return Realm.getInstance(verseRealmConfig);
    }

    public Realm getUserRealmInstance() {
        return Realm.getInstance(userRealmConfig);
    }

    public static void startRealm(Context context) {
        Realm.init(context);
    }

    public void addVerseToDB(ItemVerse itemVerse) {
        Realm mRealm = getVerseRealmInstance();
        mRealm.executeTransaction(r -> mRealm.insertOrUpdate(itemVerse));
    }

    public void addVerseListToDB(ArrayList<ItemVerse> verses){
        Realm mRealm = getVerseRealmInstance();
        for (ItemVerse verse : verses) {
            if (findItemVerseById(verse.getId()) == null) {
                mRealm.executeTransaction(r -> mRealm.insertOrUpdate(verse));
            }
        }
    }

    public void addUserToDB(ItemUser user) {
        Realm realm = getUserRealmInstance();
        realm.executeTransaction(r -> realm.deleteAll());
        realm.executeTransaction(r -> realm.insertOrUpdate(user));
    }

    public ItemUser getUser() {
        Realm realm = getUserRealmInstance();
        ItemUser currentUser;
        try {
            currentUser = realm.copyFromRealm(
                    Objects.requireNonNull(realm.where(ItemUser.class).findFirst()));
            Log.d(TAG, "getUser: Current User: " + currentUser.toString());
        } catch (NullPointerException e){
            Log.e(TAG, "Error: " + e.getMessage());
            return null;
        }

        if (currentUser.getEmail() == null) {
            return null;
        }

        return currentUser;
    }

    public ArrayList<ItemVerse> getSavedVerses() {
        Realm mRealm = getVerseRealmInstance();
        mRealm.executeTransaction(r -> verseRealmResults = mRealm.where(ItemVerse.class)
                .findAll()
                .sort(VERSE_COLUMN_ID, Sort.DESCENDING));
        return new ArrayList<>(mRealm.copyFromRealm(verseRealmResults));
    }

    public void deleteVerseFromRealmDataBase(ItemVerse itemVerse) {
        Realm mRealm = getVerseRealmInstance();
        mRealm.executeTransaction(realm -> {
            RealmResults<ItemVerse> result = realm.where(ItemVerse.class)
                    .equalTo(VERSE_COLUMN_ID, itemVerse.getId())
                    .findAll();
            result.deleteFirstFromRealm();
        });
    }

    public void deleteAllVerseFromRealmDataBase() {
        Realm mRealm = getVerseRealmInstance();
        mRealm.executeTransaction(realm -> realm.delete(ItemVerse.class));
    }

    public void deleteUserFromRealmDataBase(ItemUser itemUser) {
        Realm mRealm = getUserRealmInstance();
        mRealm.executeTransaction(realm -> {
            RealmResults<ItemUser> result = realm.where(ItemUser.class)
                    .equalTo(Constants.USER_COLUMN_ID, itemUser.getId())
                    .findAll();
            result.deleteFirstFromRealm();
        });
    }

/*
    public void deleteAllUserFromRealmDataBase() {
        Realm mRealm = getUserRealmInstance();
        mRealm.executeTransaction(realm -> realm.delete(ItemUser.class));
    }
*/

    public  ItemVerse findItemVerseById(long idItem) {
        Realm mRealm = getVerseRealmInstance();
        try {
            return mRealm.copyFromRealm(Objects.requireNonNull(mRealm.where(ItemVerse.class)
                    .equalTo(VERSE_COLUMN_ID, idItem)
                    .findFirst()));
        } catch  (Exception e) {
            if (e instanceof IllegalArgumentException || e instanceof NullPointerException)
                return null;
        }

        return null;
    }

    public ItemVerse findItemVerseByTitle(String title) {
        Realm mRealm = getVerseRealmInstance();
        return mRealm.where(ItemVerse.class)
                .equalTo(Constants.VERSE_COLUMN_TITLE, title)
                .findFirst();
    }

    public ItemVerse findItemVerseByText(String verseText) {
        Realm mRealm = getVerseRealmInstance();
        return mRealm.where(ItemVerse.class)
                .equalTo(Constants.VERSE_COLUMN_VERSE, verseText)
                .findFirst();
    }

/*    private static class Migration implements RealmMigration {

        @Override
        public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {

            RealmSchema schema = realm.getSchema();

            if (oldVersion == 0) {
                schema.create(VERSES_REALM_CLASS)
                        .addField("untilAlarm", long.class)
                        .addField("isRepeatingAlarm", boolean.class)
                        .removeField("isRepeatingAlarm")
                        .removeField("isChecked");
                oldVersion++;
            }
        }
    }*/
}
