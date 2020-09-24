package com.erg.memorized.interfaces;

import com.erg.memorized.model.VerseBible;
import com.erg.memorized.model.bible_api_models.BibleRoot;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Query;

import static com.erg.memorized.util.Constants.BIBLE_ID;
import static com.erg.memorized.util.Constants.GET_BIBLES;
import static com.erg.memorized.util.Constants.USER_KEY_API_BIBLE;
import static com.erg.memorized.util.Constants.VERSE_ID;

public interface BibleApi {
    /**
     * https://api.scripture.api.bible/v1/bibles/592420522e16049f-01/verses/JER.29.11?
     * content-type=json&include-notes=false&include-titles=false
     * &include-chapter-numbers=false&include-verse-numbers=false
     * &include-verse-spans=false&use-org-id=false
     * <p>
     * /v1/bibles/{bibleId}/verses/{verseId}
     */
    @Headers({"api-key:" + USER_KEY_API_BIBLE})
    @GET(GET_BIBLES + "/" + BIBLE_ID + "/verses/" + VERSE_ID)
    Call<VerseBible> getVerse(
            @Path("bibleId") String bibleId,
            @Path("verseId") String verseId,
            @Query("content-type") String content_type,
            @Query("include-notes") boolean include_notes,
            @Query("include-titles") boolean include_titles,
            @Query("include-chapter-numbers") boolean include_chapter_numbers,
            @Query("include-verse-numbers") boolean include_verse_numbers,
            @Query("include-verse-spans") boolean include_verse_spans,
            @Query("use-org-id") boolean use_org_id
    );


    @Headers({"api-key:" + USER_KEY_API_BIBLE})
    @GET(GET_BIBLES)
    Call<BibleRoot> getBibles();
}
