package com.adamhurwitz.android.popularmovies.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Defines table and column names for the database.
 */
public class CursorContract {
    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public CursorContract(){}

    // The "Content authority" is a name for the entire content provider, similar to the
    // relationship between a domain name and its website.  A convenient string to use for the
    // content authority is the package name for the app, which is guaranteed to be unique on the
    // device.
    public static final String CONTENT_AUTHORITY = "com.adamhurwitz.android.popularmovies";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Possible paths (appended to base content URI for possible URI's)
    public static final String PATH_MOVIETABLE = "movie_table";

    /*
        Inner class that defines the table contents of the table
     */
    public static abstract class MovieData implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_MOVIETABLE).build();
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/"
                        + PATH_MOVIETABLE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/"
                        + PATH_MOVIETABLE;

        public static final String TABLE_NAME = "movie_table";
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

        public static Uri buildMovieIdUri() {
            return CONTENT_URI;
        }
    }

}