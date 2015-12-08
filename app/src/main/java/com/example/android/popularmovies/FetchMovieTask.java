package com.example.android.popularmovies;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Encapsulates fetching the forecast and displaying it as a {@link ListView} layout.
 */

// param1 passes into doInBackground()
// param3 declares return type for doInBackground()

// commented out --> now returning Array of Objects

// public class FetchMovieTask extends AsyncTask<String, Void, String>

public class FetchMovieTask extends AsyncTask<String, Void, ArrayList<MovieData>> {

    // remove '?' before 'api_key' and '=' after, the Uri class builds it for you
    public static final String kb = "api_key";
    public static final String kc = "API_KEY_GOES_HERE";
    public static final String sort = "sort_by";
    // added to assign User Setting to this String
    public static String sortBy = "";

    private final String LOG_TAG = FetchMovieTask.class.getSimpleName();
    private ArrayList<MovieData> movieDataObjects = new ArrayList<>();
    private GridViewAsyncAdapter gridViewAdapter;

    /**
     * Constructor for the FetchDoodleDataTask object.
     * @param gridViewAdapter An adapter to recycle items correctly in the grid view.
     * @param movieDataObjects A list of objects with information about Movies.
     */
    public FetchMovieTask(GridViewAsyncAdapter gridViewAdapter,
                               ArrayList<MovieData> movieDataObjects) {
        this.movieDataObjects = movieDataObjects;
        this.gridViewAdapter = gridViewAdapter;
    }

    @Override
    // change return type to String in order to return output String
    // changed from String to ArrayList<MovieData>
    // protected String doInBackground(String... params) {
    protected ArrayList<MovieData> doInBackground(String... params) {
        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String  doodleDataJsonResponse = null;

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
        }

        // returns output from API in a String variable
        // return movieJsonStr;

        // return ArrayList of MovieData Objects
        return parseJSONObject(doodleDataJsonResponse);

        // Any other case that gets here is an error that was not caught, so return null.
        //return null;
    }

    @Override
    /**
     * Override the onPostExecute method to notify the grid view adapter that new data was received
     * so that the items in the grid view can appropriately reflect the changes.
     * @param movieDataObjects A list of objects with information about the Movies.
     */
    public void onPostExecute(ArrayList<MovieData> movieDataObjects) {
        gridViewAdapter.notifyDataSetChanged();
    }

    // method that takes in String of output and returns Array of MovieData Objects
    private ArrayList<MovieData> parseJSONObject(String jsonString) {
        try {
            // converting output String to JSONObject
            JSONObject jsonObject = new JSONObject(jsonString);
            // parsing JSONObject into JSONArray
            JSONArray results = jsonObject.getJSONArray("results");
            // create For Loop to loop through each index in "results" ArrayList
            // and parse for movieJSONObject by ArrayList index
            for (int i = 0; i < results.length(); i++) {
                // parse out each movie in Array
                JSONObject movieJSONObject = results.getJSONObject(i);
                // parsing JSONObject Values into Strings based on the Key
                String title = movieJSONObject.getString("original_title");
                String image = movieJSONObject.getString("backdrop_path");
                String summary = movieJSONObject.getString("overview");
                String rating = movieJSONObject.getString("vote_average");
                String releaseDate = movieJSONObject.getString("release_date");
                MovieData movieDataItem = new MovieData(title, image, summary, rating, releaseDate);
                movieDataObjects.add(movieDataItem);
            }
            //
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

}