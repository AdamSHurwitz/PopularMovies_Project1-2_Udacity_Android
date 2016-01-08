package com.adamhurwitz.android.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.adamhurwitz.android.popularmovies.data.CursorContract.MovieData;

/**
 * Manages a local database.
 */
public class CursorDbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "movie_data.db";

    public CursorDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_MOVIETABLE = "CREATE TABLE " + MovieData.TABLE_NAME + "(" +
                // AutoIncrement
                MovieData._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                MovieData.COLUMN_NAME_MOVIEID + " INTEGER NOT NULL, " +
                MovieData.COLUMN_NAME_TITLE + " TEXT NOT NULL, " +
                MovieData.COLUMN_NAME_IMAGEURL + " TEXT NOT NULL, " +
                MovieData.COLUMN_NAME_SUMMARY + " TEXT NOT NULL, " +
                MovieData.COLUMN_NAME_VOTEAVERAGE + " REAL NOT NULL, " +
                MovieData.COLUMN_NAME_POPULARITY + " REAL NOT NULL, " +
                MovieData.COLUMN_NAME_RELEASEDATE + " STRING NOT NULL, " +
                MovieData.COLUMN_NAME_FAVORITE + " INTEGER " +
                " );";

        sqLiteDatabase.execSQL(SQL_CREATE_MOVIETABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieData.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
