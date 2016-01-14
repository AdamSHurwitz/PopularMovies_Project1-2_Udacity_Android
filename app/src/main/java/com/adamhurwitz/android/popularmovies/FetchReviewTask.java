package com.adamhurwitz.android.popularmovies;

// param1 passes into doInBackground()
// param3 declares return type for doInBackground()


// public class FetchMovieTask extends AsyncTask<String, Void, String>


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

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

public class FetchReviewTask extends AsyncTask<String, Void, String[]> {

    private final String LOG_TAG = FetchReviewTask.class.getSimpleName();
    public static final String BASE_URL = "http://api.themoviedb.org/3/movie/";
    public static final String KEY_PARAMETER = "api_key";
    public static final String KEY_CODE = "81696f0358507756b5119609b0fae31e";

    private final Context context;
    private View detailFragmentView;


    /**
     * Constructor for the FetchDoodleDataTask object.
     *
     * @param context Provides context.
     */

    public FetchReviewTask(Context context, View view) {
        this.context = context;
        detailFragmentView = view;
    }

    @Override
    protected String[] doInBackground(String... params) {
        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String jsonResponse = null;

        try {
            // Construct the URL to fetch data from and make the connection.
            Uri builtUri = Uri.parse(BASE_URL + params[0] + "/reviews").buildUpon()
                    .appendQueryParameter(KEY_PARAMETER, KEY_CODE)
                    .build();
            URL url = new URL(builtUri.toString());
            Log.v(LOG_TAG, "Built_Review_URL " + builtUri.toString());
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

        try {
            return parseJSONResponse(jsonResponse, params[1]);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        // Any other case that gets here is an error that was not caught, so return null.
        return null;
    }


    /**
     * Parses the JSON response for information about the Google doodles.
     *
     * @param jsonResponse A JSON string which needs to be parsed for data about the
     *                     Google doodles.
     */

    private String[] parseJSONResponse(String jsonResponse, String params1) throws JSONException {
        try {
            // convert String output into JSONObject
            JSONObject jsonObject = new JSONObject(jsonResponse);
            // parse JSONObject into JSONArray
            JSONArray jsonArray = jsonObject.getJSONArray("results");
            // create ForLoop to loop through each index in "results" ArrayList
            // and parse for JSONObject by ArrayList index
            String[] reviews = new String[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jObject = jsonArray.getJSONObject(i);
                // parse out each movie in Array
                reviews[i] = jObject.getString("content");
            }
            if (reviews.length != 0) {
                putDataIntoDb(reviews, params1);
            }
            Log.v(LOG_TAG, "Movie_Review_Length" + params1 + ": " + reviews.length);
            //Log.v(LOG_TAG, "Movie_Review_1"+params1+ ": "+ reviews[0]);
            //Log.v(LOG_TAG, "Movie_Review_2"+params1+ ": "+ reviews[2]);
            return reviews;
        } catch (JSONException e) {
            Log.e(LOG_TAG, "PARSING ERROR " + e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
    }

    public void putDataIntoDb(String[] reviews, String title) {

        // Access database
        CursorDbHelper mDbHelper = new CursorDbHelper(context);
        // Put Info into Database
        // Gets the data repository in write mode
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();

        if (reviews.length == 1) {
            values.put(CursorContract.MovieData.COLUMN_NAME_REVIEW_1, reviews[0]);
        } else if (reviews.length == 2) {
            values.put(CursorContract.MovieData.COLUMN_NAME_REVIEW_1, reviews[0]);
            values.put(CursorContract.MovieData.COLUMN_NAME_REVIEW_2, reviews[1]);
        } else if (reviews.length == 3) {
            values.put(CursorContract.MovieData.COLUMN_NAME_REVIEW_1, reviews[0]);
            values.put(CursorContract.MovieData.COLUMN_NAME_REVIEW_2, reviews[1]);
            values.put(CursorContract.MovieData.COLUMN_NAME_REVIEW_3, reviews[2]);
        } else if (reviews.length > 3) {
            values.put(CursorContract.MovieData.COLUMN_NAME_REVIEW_1, reviews[0]);
            values.put(CursorContract.MovieData.COLUMN_NAME_REVIEW_2, reviews[1]);
            values.put(CursorContract.MovieData.COLUMN_NAME_REVIEW_3, reviews[2]);
        }
        Log.v(LOG_TAG, title + " reviews_Array_Length: " + reviews.length);
        //Log.v(LOG_TAG, title + " reviews" + " " + values.toString());

        Cursor c = db.query(
                CursorContract.MovieData.TABLE_NAME,  // The table to query
                null, // The columns to return
                CursorContract.MovieData.COLUMN_NAME_TITLE + "= ?",
                // The columns for the WHERE clause
                new String[]{title}, // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                CursorContract.MovieData._ID + " DESC" // The sort order

        );

        c.moveToFirst();

        long thisRowID = db.update(
                CursorContract.MovieData.TABLE_NAME,
                values,
                CursorContract.MovieData.COLUMN_NAME_TITLE + "= ?",
                new String[]{title});
    }

    @Override
    /**
     * Override the onPostExecute method to notify the grid view adapter that new data was
     * received so that the items in the grid view can appropriately reflect the changes.
     * @param reviews A list of objects with information about the reviews.
     */
    public void onPostExecute(String[] reviews) {
        // get id for reviews
        TextView review1Interface = (TextView) detailFragmentView.findViewById(R.id.review1_view);
        CardView review1Card = (CardView) detailFragmentView.findViewById(R.id.review1_card);
        TextView review2Interface = (TextView) detailFragmentView.findViewById(R.id.review2_view);
        CardView review2Card = (CardView) detailFragmentView.findViewById(R.id.review2_card);
        TextView review3Interface = (TextView) detailFragmentView.findViewById(R.id.review3_view);
        CardView review3Card = (CardView) detailFragmentView.findViewById(R.id.review3_card);


        if (reviews.length == 0) {
            //do nothing
        }
        if (reviews.length == 1 && reviews[0] != null && !reviews[0].equals("")
                && !reviews[0].equals(" ")) {
            Log.v("review1", reviews[0]);
            review1Interface.setText(reviews[0]);
            review1Card.setVisibility(View.VISIBLE);
        }
        if (reviews.length == 2 && reviews[1] != null && !reviews[1].equals("")
                && !reviews[1].equals(" ")) {
            Log.v("review1", reviews[1]);
            review2Interface.setText(reviews[1]);
            review2Card.setVisibility(View.VISIBLE);
        }
        if (reviews.length == 3 && reviews[2] != null && !reviews[2].equals("")
                && !reviews[2].equals(" ")) {
            Log.v("review1", reviews[2]);
            review3Interface.setText(reviews[2]);
            review3Card.setVisibility(View.VISIBLE);
        }
    }
}
