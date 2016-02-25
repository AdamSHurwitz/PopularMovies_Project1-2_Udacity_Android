/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.adamhurwitz.android.popularmovies.service;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import com.adamhurwitz.android.popularmovies.Constants;
import com.adamhurwitz.android.popularmovies.data.CursorContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class YouTubeService extends IntentService {
    private final String LOG_TAG = YouTubeService.class.getSimpleName();
    public String VIDEOS = "/videos";
    public static final String YOUTUBE_BASE_URL = "https://www.youtube.com/watch?v=";

    public static final String SORT_PARAMETER = "sort_by";

    public YouTubeService() {
        super("YouTubeService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String[] youTubeArray = intent.getStringArrayExtra("YOUTUBE_QUERY");

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String jsonResponse = null;

        try {
            // Construct the URL to fetch data from and make the connection.
            Uri builtUri = Uri.parse(Constants.BASE_URL + youTubeArray[0] + VIDEOS).buildUpon()
                    .appendQueryParameter(Constants.KEY_PARAMETER, Constants.KEY_CODE)
                    .build();
            URL url = new URL(builtUri.toString());
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // See if the input stream is not null and a connection could be made. If it is null, do
            // not process any further.
            InputStream inputStream = urlConnection.getInputStream();
            StringBuilder buffer = new StringBuilder();
            if (inputStream == null) {
                return;
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
                return;
            }

            jsonResponse = buffer.toString();

        } catch (IOException e) {
            // If there was no valid Google doodle data returned, there is no point in attempting to
            // parse it.
            Log.e(LOG_TAG, "Error, IOException.", e);
            return;
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
        parseJSONResponse(jsonResponse, youTubeArray[1]);

        // Any other case that gets here is an error that was not caught, so return null.
        return;
    }

    /**
     * Parses the JSON response for information about the Google doodles.
     *
     * @param jsonResponse A JSON string which needs to be parsed for data about the
     *                     Google doodles.
     */
    private void parseJSONResponse(String jsonResponse, String params1)
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
                putDataIntoDb(jObject.getString("key"), params1);
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "PARSING ERROR " + e.getMessage(), e);
            e.printStackTrace();
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void putDataIntoDb(String key, String title) {

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();

        // Build Movie YouTube URL
        // Construct the URL to fetch data from and make the connection.

        String youTubeUrl = YOUTUBE_BASE_URL + key;

        values.put(CursorContract.MovieData.COLUMN_NAME_YOUTUBEURL, youTubeUrl);

        Cursor c = this.getContentResolver().query(
                CursorContract.MovieData.CONTENT_URI,
                new String[]{CursorContract.MovieData.COLUMN_NAME_YOUTUBEURL},
                CursorContract.MovieData.COLUMN_NAME_TITLE + "= ?",
                new String[]{title}, // The values for the WHERE clause
                null,                                     // don't group the rows
                null                                     // don't filter by row groups
        );

        c.moveToFirst();

        int mRowsUpdated = this.getContentResolver().update(
                CursorContract.MovieData.CONTENT_URI,
                values,
                CursorContract.MovieData.COLUMN_NAME_TITLE + "= ?",
                new String[]{title});
    }
}

