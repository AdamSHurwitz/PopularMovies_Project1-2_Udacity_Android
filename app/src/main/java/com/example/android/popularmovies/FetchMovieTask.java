package com.example.android.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ListView;

import com.example.android.popularmovies.data.CursorContract;
import com.example.android.popularmovies.data.CursorDbHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Encapsulates fetching the forecast and displaying it as a {@link ListView} layout.
 */

// param1 passes into doInBackground()
// param3 declares return type for doInBackground()

// commented out --> now returning Array of Objects

// public class FetchMovieTask extends AsyncTask<String, Void, String>

public class FetchMovieTask extends AsyncTask<String, Void, Void> {

    // remove '?' before 'api_key' and '=' after, the Uri class builds it for you
    public static final String kb = "api_key";
    public static final String kc = "81696f0358507756b5119609b0fae31e";
    public static final String sort = "sort_by";
    // added to assign User Setting to this String
    public static String sortBy = "";

    private final String LOG_TAG = FetchMovieTask.class.getSimpleName();
    private AsyncCursorAdapter asyncCursorAdapter;
    private final Context context;

    /**
     * Constructor for the FetchDoodleDataTask object.
     *
     * @param asyncCursorAdapter An adapter to recycle items correctly in the grid view.
     * @param context            Provides context.
     */
    public FetchMovieTask(AsyncCursorAdapter asyncCursorAdapter,
                          Context context) {
        this.asyncCursorAdapter = asyncCursorAdapter;
        this.context = context;
    }

    @Override
    // change return type to String in order to return output String
    // changed from String to ArrayList<MovieData>
    // protected String doInBackground(String... params) {
    protected Void doInBackground(String... params) {
        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String doodleDataJsonResponse = null;

        // builds correct HTTP request based on User Setting passed in onCreateView()
        // through Shared Preferences
        if (params[0].equals("popularity")) {
            sortBy = "popularity.desc";
        } else {
            sortBy = "vote_average.desc";
        }

        try {
            // Construct the URL for the moviedb query
            // Possible parameters are available at moviedb API page, at
            // http://docs.themoviedb.apiary.io/#
            final String MOVIE_BASE_URL = "https://api.themoviedb.org/3/discover/movie";

            Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                    // not needed
                    // .appendPath(params[0])

                    // looking at 0th param of what is passed from 'execute()' method into
                    // 'doInBackground()' param to append URL path to base
                    // --> commented out to pass in User Preference
                    // .appendQueryParameter(sort, params[0])
                    .appendQueryParameter(sort, sortBy)
                            // appending parameter
                    .appendQueryParameter(kb, kc)
                    .build();


            URL url = new URL(builtUri.toString());
            Log.v(LOG_TAG, "Built URI " + builtUri.toString());


            // Create the request to themoviedb, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String

            // .getInputStream() pings moviedb API
            InputStream inputStream = urlConnection.getInputStream();

            // grabs the output from movidedb API call and places output in buffer
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }

            // saving buffer which contains the output to a string variable
            doodleDataJsonResponse = buffer.toString();

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attemping
            // to parse it.
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }

            // If valid data was returned, return the parsed data.
            try {
                Log.i(LOG_TAG, "The Google doodle data that was returned is: " +
                        doodleDataJsonResponse);
                parseDoodleDataJsonResponse(doodleDataJsonResponse);
                return null;
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
        }

        return null;

        // Any other case that gets here is an error that was not caught, so return null.
        //return null;
    }

    @Override
    /**
     * Override the onPostExecute method to notify the grid view adapter that new data was received
     * so that the items in the grid view can appropriately reflect the changes.
     * @param movieDataObjects A list of objects with information about the Movies.
     */
    public void onPostExecute(Void param) {
        asyncCursorAdapter.notifyDataSetChanged();
    }

    /**
     * Parses the JSON response for information about the Google doodles.
     *
     * @param doodleDataJsonResponse A JSON string which needs to be parsed for data about the
     *                               Google doodles.
     */
    private void parseDoodleDataJsonResponse(String doodleDataJsonResponse)
            throws JSONException {
        try {
            Log.v("parseDoodleDataJson", "called here");
            JSONArray doodlesInfo = new JSONArray(doodleDataJsonResponse);
            for (int index = 0; index < doodlesInfo.length(); index++) {
                JSONObject doodleDataJson = doodlesInfo.getJSONObject(index);
                putDoodleDataIntoDb(
                        doodleDataJson.getString("original_title"),
                        doodleDataJson.getString("backdrop_path"),
                        doodleDataJson.getString("overview"),
                        doodleDataJson.getString("vote_average"),
                        doodleDataJson.getString("release_date"));
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }

    public void putDoodleDataIntoDb(String title, String image_url, String summary, String
            rating, String release_date) {
        Log.v("putInfoIntoDatabase", "called here");

        // Access database
        CursorDbHelper mDbHelper = new CursorDbHelper(context);

        // Put Info into Database

        // Gets the data repository in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(CursorContract.MovieData.COLUMN_NAME_TITLE, title);
        values.put(CursorContract.MovieData.COLUMN_NAME_IMAGEURL, image_url);
        values.put(CursorContract.MovieData.COLUMN_NAME_SUMMARY, summary);
        values.put(CursorContract.MovieData.COLUMN_NAME_RATING, rating);
        values.put(CursorContract.MovieData.COLUMN_NAME_RELEASEDATE, release_date);

        // How you want the results sorted in the resulting Cursor
        String sortOrder =
                CursorContract.MovieData._ID + " DESC";
        String whereValue[] = {title};

        // Insert the new row, returning the primary key value of the new row
        long thisRowID;

        // If you are querying entire table, can leave everything as Null
        // Querying when Item ID Exists
        Cursor cursor = db.query(
                CursorContract.MovieData.TABLE_NAME,  // The table to query
                null,                                // The columns to return
                CursorContract.MovieData._ID + "= ?", // The columns for the WHERE clause
                whereValue, // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );

        // If the Item ID Does Not Exist, Insert All Values
        if (cursor.getCount() == 0) {
            thisRowID = db.insert(
                    CursorContract.MovieData.TABLE_NAME,
                    null,
                    values);
        }

        // If the Item ID Does Exist, Update All Values
        else {
            thisRowID = db.update(
                    CursorContract.MovieData.TABLE_NAME,
                    values,
                    CursorContract.MovieData._ID + "= ?",
                    whereValue);
        }
    }
}