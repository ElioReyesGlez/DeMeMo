package com.erg.memorized.interfaces;

import com.erg.memorized.model.DailyVerse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Query;

import static com.erg.memorized.util.Constants.BAR;
import static com.erg.memorized.util.Constants.DAY_OF_THE_YEAR;
import static com.erg.memorized.util.Constants.GET_VERSE_OF_THE_DAY;
import static com.erg.memorized.util.Constants.YOUR_VERSION_TOKEN;

public interface YourVersionApi {
    /**
     * https://developers.youversionapi.com/1.0/verse_of_the_day/{day_of_year}?version_id=1
     */
    @Headers({
            "accept: application/json",
            "x-youversion-developer-token:" + YOUR_VERSION_TOKEN,
            "accept-language: en",
    })
    @GET(GET_VERSE_OF_THE_DAY + BAR + DAY_OF_THE_YEAR)
    Call<DailyVerse> getDailyVerse(@Path("day_of_year") int day_of_year,
                                   @Query("version_id") int version_id );
}
