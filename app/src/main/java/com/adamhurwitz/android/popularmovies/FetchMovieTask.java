package com.adamhurwitz.android.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.adamhurwitz.android.popularmovies.data.CursorContract;
import com.adamhurwitz.android.popularmovies.data.CursorDbHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

// param1 passes into doInBackground()
// param3 declares return type for doInBackground()


// public class FetchMovieTask extends AsyncTask<String, Void, String>

public abstract class FetchMovieTask extends AsyncTask<String, Void, Void> {

    private final String LOG_TAG = FetchMovieTask.class.getSimpleName();
    public static final String BASE_URL = "https://api.themoviedb.org/3/discover/movie";
    public static final String SORT_PARAMETER = "sort_by";
    public static final String KEY_PARAMETER = "api_key";
    public static final String KEY_CODE = "81696f0358507756b5119609b0fae31e";

    private final Context context;

    /**
     * Constructor for the FetchDoodleDataTask object.
     *
     * @param context            Provides context.
     */
    public FetchMovieTask(Context context) {
        this.context = context;
    }


    @Override
    protected Void doInBackground(String... params) {
        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String jsonResponse = null;

        try {
            // Construct the URL to fetch data from and make the connection.
            Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                    .appendQueryParameter(SORT_PARAMETER, params[0])
                    .appendQueryParameter(KEY_PARAMETER, KEY_CODE)
                    .build();
            URL url = new URL(builtUri.toString());
            Log.v(LOG_TAG, "Built URL " + builtUri.toString());
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // See if the input stream is not null and a connection could be made. If it is null, do
            // not process any further.
            InputStream inputStream = urlConnection.getInputStream();
            StringBuilder buffer = new StringBuilder();
            if (inputStream == null) {
                return null;
            }

            // Read the input stream to see if any valid response was give.
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                // Add new to make debugging easier.
                buffer.append(line).append("\n");
            }
            if (buffer.length() == 0) {
                // If the stream is empty, do not process any further.
                return null;
            }

            jsonResponse = buffer.toString();

        } catch (IOException e) {
            // If there was no valid Google doodle data returned, there is no point in attempting to
            // parse it.
            Log.e(LOG_TAG, "Error, IOException.", e);
            return null;
        } finally {
            // Make sure to close the connection and the reader no matter what.
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream ", e);
                }
            }
        }

        // return ArrayList of MovieData Objects
        parseJSONResponse(jsonResponse);

        // Any other case that gets here is an error that was not caught, so return null.
        return null;
    }

    /**
     * Parses the JSON response for information about the Google doodles.
     *
     * @param jsonResponse A JSON string which needs to be parsed for data about the
     *                           Google doodles.
     */
    private void parseJSONResponse(String jsonResponse)
    //throws JSONException
    {
        try {
            // convert String output into JSONObject
            JSONObject jsonObject = new JSONObject(jsonResponse);
            // parse JSONObject into JSONArray
            JSONArray jsonArray = jsonObject.getJSONArray("results");
            // create ForLoop to loop through each index in "results" ArrayList
            // and parse for JSONObject by ArrayList index
            for (int i = 0; i < jsonArray.length(); i++) {
                // parse out each movie in Array
                JSONObject jObject = jsonArray.getJSONObject(i);
                putDataIntoDb(
                        jObject.getInt("id"),
                        jObject.getString("original_title"),
                        jObject.getString("backdrop_path"),
                        jObject.getString("overview"),
                        jObject.getDouble("vote_average"),
                        jObject.getDouble("popularity"),
                        jObject.getString("release_date"));
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "PARSING ERROR " + e.getMessage(), e);
            e.printStackTrace();
        }
    }

    public void putDataIntoDb(
            //String id,
            Integer movie_id, String title, String image_url, String summary, Double
            vote_average, Double popularity, String release_date) {

        // Access database
        CursorDbHelper mDbHelper = new CursorDbHelper(context);

        // Put Info into Database

        // Gets the data repository in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(CursorContract.MovieData.COLUMN_NAME_MOVIEID, movie_id);
        values.put(CursorContract.MovieData.COLUMN_NAME_TITLE, title);
        values.put(CursorContract.MovieData.COLUMN_NAME_IMAGEURL, image_url);
        values.put(CursorContract.MovieData.COLUMN_NAME_SUMMARY, summary);
        values.put(CursorContract.MovieData.COLUMN_NAME_VOTEAVERAGE, vote_average);
        values.put(CursorContract.MovieData.COLUMN_NAME_POPULARITY, popularity);
        values.put(CursorContract.MovieData.COLUMN_NAME_RELEASEDATE, release_date);
        values.put(CursorContract.MovieData.COLUMN_NAME_FAVORITE, "1");

        //Log.v(LOG_TAG, "Content Values " + values.toString());

        //String whereValueId[] = {CursorContract.MovieData._ID};

        // Insert the new row, returning the primary key value of the new row
        long thisRowID;

        // If you are querying entire table, can leave everything as Null
        // Querying when Item ID Exists
        Cursor cursor = db.query(
                CursorContract.MovieData.TABLE_NAME,  // The table to query
                null,                                // The columns to return
                CursorContract.MovieData.COLUMN_NAME_TITLE + "= ?", // The columns for the WHERE clause
                new String[] {title}, // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                CursorContract.MovieData.COLUMN_NAME_POPULARITY + " DESC"  // The sort order
        );

        // If the Title Does Not Exist, Insert All Values
        if (cursor.getCount() == 0) {
            thisRowID = db.insert(
                    CursorContract.MovieData.TABLE_NAME,
                    null,
                    values);
            Log.v("VALUES_INSERTED: ", values.toString());
        }

        // No updates for now, don't want to wipe out Favorites selection
        // If the Item ID Does Exist, Update All Values
      /*  else {
            thisRowID = db.update(
                    CursorContract.MovieData.TABLE_NAME,
                    values,
                    CursorContract.MovieData._ID + "= ?",
                    whereValueTitle);
        }*/
    }
}