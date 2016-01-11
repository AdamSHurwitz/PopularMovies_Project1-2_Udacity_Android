package com.adamhurwitz.android.popularmovies.data;

import android.provider.BaseColumns;

/**
 * Defines table and column names for the weather database.
 */
public class CursorContract {
    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public CursorContract(){}

    /*
        Inner class that defines the table contents of the table
     */
    public static abstract class MovieData implements BaseColumns {
        public static final String TABLE_NAME = "movie_data";
        public static final String COLUMN_NAME_MOVIEID = "movie_id";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_IMAGEURL = "image_url";
        public static final String COLUMN_NAME_SUMMARY = "summary";
        public static final String COLUMN_NAME_VOTEAVERAGE = "rating";
        public static final String COLUMN_NAME_POPULARITY = "popularity";
        public static final String COLUMN_NAME_RELEASEDATE = "release_date";
        public static final String COLUMN_NAME_FAVORITE = "favorite";
        public static final String COLUMN_NAME_YOUTUBEURL = "youtube_url";
        public static final String COLUMN_NAME_REVIEW_1 = "review_1";
        public static final String COLUMN_NAME_REVIEW_2 = "review_2";
        public static final String COLUMN_NAME_REVIEW_3 = "review_3";
    }

}