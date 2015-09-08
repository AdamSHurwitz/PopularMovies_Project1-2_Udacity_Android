package com.example.android.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

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
 * A placeholder fragment containing a simple view.
 */
public class MainFragment extends Fragment {

    // commented out → using API data

    // static final variable to store dummmy data
//    private final Integer[] dummyData = {R.drawable.ant, R.drawable.deadpool, R.drawable.fantastic,
//            R.drawable.goodnight, R.drawable.mission, R.drawable.southpaw,
//            R.drawable.star_wars, R.drawable.straight, R.drawable.terminator,
//            R.drawable.the_gift, R.drawable.the_man, R.drawable.wet_hot_summer};

    // creating ArrayList of Movies
    ArrayList<Movie> movieObjects = new ArrayList<Movie>();

    // create EXTRA_MESSAGE
    public static final String EXTRA_MESSAGE = "com.example.android.popularmovies";


    // inflating menu
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);

    }

    // loads GridView with current Setting selected
    @Override
    public void onStart() {
        super.onStart();  // Always call the superclass method first
        movieObjects.clear();
        arrayAdapter.notifyDataSetChanged();
        FetchMovieTask movieTask = new FetchMovieTask();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortBy = prefs.getString("sortBy_key", "0");
        movieTask.execute(sortBy);
    }

//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        inflater.inflate(R.menu.menu_main, menu);
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (item.getItemId()) {
            /*case R.id.action_refresh:
                 FetchMovieTask movieTask = new FetchMovieTask();
                 movieTask.execute("popularity.desc");
                return true;*/
            case R.id.action_settings:
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);

            /** if (id == R.id.action_refresh) {
             // looks for doInBackground --> commented out to move into onCreateView()
             /**FetchMovieTask movieTask = new FetchMovieTask();
             movieTask.execute("popularity.desc");
             return true;
             }
             return super.onOptionsItemSelected(item); */

        }
    }

    // declaring ArrayAdapter

    // used dummyDataAdapter before
    // private ArrayAdapter dummyDataAdapter;

    private ArrayAdapter arrayAdapter;

    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.grid_view_layout, container, false);

        // commented out in order to use arrayAdapter
        /*dummyDataAdapter = new GridViewAdapter(
                // current context (this fragment's containing activity)
                getActivity(),
                // ID of view item layout, not needed since we get it in getView()
                R.layout.grid_item_layout);*/

        arrayAdapter = new GridViewAdapter(
                // current context (this fragment's containing activity)
                getActivity(),
                // ID of view item layout, not needed since we get it in getView()
                R.layout.grid_item_layout);

        // Get a reference to GridView, and attach this adapter to it
        GridView gridView = (GridView) view.findViewById(R.id.grid_view_layout);
        gridView.setAdapter(arrayAdapter);


        // create Toast
        // gridView.setOnItemClickListener(new OnItem... [auto-completes])
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            // parent = parent view, view = grid_item view, position = grid_item position
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // removed and replaced w/ Explicit Intent
                //Toast.makeText(getActivity(), movieObjects.get(position).getTitle() , Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(getActivity(), com.example.android.popularmovies.DetailActivity.class);
                /**String message = movieObjects.get(position).getTitle();
                 intent.putExtra(EXTRA_MESSAGE, message);*/

                intent.putExtra(EXTRA_MESSAGE, movieObjects.get(position));
                startActivity(intent);
            }
        });

        // doInBackground -> commented out to make doInBackground that checks for Settings
        /**FetchMovieTask movieTask = new FetchMovieTask();
         movieTask.execute("popularity.desc");*/


        if (savedInstanceState == null || !savedInstanceState.containsKey("key")) {
            // looks for doInBackground
            FetchMovieTask movieTask = new FetchMovieTask();
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String sortBy = prefs.getString("sortBy_key", "0");
            movieTask.execute(sortBy);
        } else {
            movieObjects = savedInstanceState.getParcelableArrayList("key");
        }


        return view;
    }

    // used for Array of Integers when using dummy data
    // private class GridViewAdapter extends ArrayAdapter<Integer>

    private class GridViewAdapter extends ArrayAdapter<Movie> {
        private final String LOG_TAG = GridViewAdapter.class.getSimpleName();
        // declare Context variable
        Context context;

        /**
         * @param context  is the Context
         * @param resource is the grid_view_layout
         */
        // creates contructor to create GridViewAdapter object
        public GridViewAdapter(Context context, int resource) {

            // commented out → no longer used since passing in Movie Objects

            //super(context, resource, dummyData);

            super(context, resource, movieObjects);
            this.context = context;
        }

        // get view to create view, telling Adapter what's included in the grid_item_layout
        @Override
        public View getView(int position, View view, ViewGroup parent) {
            // Construct the URL to query images in Picasso
            final String PICASSO_BASE_URL = "http://image.tmdb.org/t/p/";
            final String imageUrl = movieObjects.get(position).getImage();

            Uri builtUri = Uri.parse(PICASSO_BASE_URL).buildUpon()
                    // appending size and image source
                    .appendPath("w500")
                    .appendPath(imageUrl.substring(1))
                    .build();
            Log.v(LOG_TAG, "Built URI " + builtUri.toString());
            // generate images with Picasso


            // new method to only use memory when view is being used
            // layout inflater
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();

            // holder will hold the references to your views
            ViewHolder holder;

            // first clutter of views
            if (view == null) {
                // need inflater to inflate the grid_item_layout
                view = inflater.inflate(R.layout.grid_item_layout, parent, false);
                holder = new ViewHolder();
                // once view is inflated we can grab elements, getting and saving grid_item_imageview
                // as ImageView
                holder.gridItem = (ImageView) view.findViewById(R.id.grid_item_imageview);
                holder.titleItem = (TextView) view.findViewById(R.id.grid_item_textview);
                view.setTag(holder);
                // if view is not empty, re-use view to repopulate w/ data
            } else {
                holder = (ViewHolder) view.getTag();
            }

            // commented out --> linking adapter to old dummyData Array index

            // use setter method setImageResource() to set ImageView image from dummyData Array
            //gridItem.setImageResource(dummyData[position]);

            Picasso.with(context).
                    load(builtUri)
                    .resize(500, 500)
                    .centerCrop()
                    .placeholder(R.drawable.user_placeholder)
                    .error(R.drawable.user_placeholder_error)
                    .into(holder.gridItem);
            holder.titleItem.setText(movieObjects.get(position).getTitle());


            return view;
        }

        class ViewHolder {
            // declare your views here
            TextView titleItem;
            ImageView gridItem;

        /*// commented out in order to implement test to only use memory for View showing

            // once view is inflated we can grab elements, getting and saving grid_item_imageview
            // as ImageView
              // layout inflator
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            View view = inflater.inflate(R.layout.grid_item_layout, parent, false);
            ImageView gridItem = (ImageView) view.findViewById(R.id.grid_item_imageview);
            TextView titleItem = (TextView) view.findViewById(R.id.grid_item_textview);


        // use setter method setImageResource() to set ImageView image from dummyData Array
        // commented out because linking adapter to old dummyData Array index
        //gridItem.setImageResource(dummyData[position]);

        // Construct the URL to query images in Picasso
        final String PICASSO_BASE_URL = "http://image.tmdb.org/t/p/";
        final String imageUrl = movieObjects.get(position).getImage();

        Uri builtUri = Uri.parse(PICASSO_BASE_URL).buildUpon()
                // appending size and image source
                .appendPath("w500")
                .appendPath(imageUrl.substring(1))
                .build();
        Log.v(LOG_TAG,"Built URI "+builtUri.toString());
            // generate images with Picasso

            Picasso.with(context).load(builtUri).resize(500, 500).centerCrop().placeholder(R.drawable.user_placeholder).error(R.drawable.user_placeholder_error).into(gridItem);
            titleItem.setText(movieObjects.get(position).getTitle()
        );

        return view;*/
        }
    }


    /**
     * Encapsulates fetching the forecast and displaying it as a {@link ListView} layout.
     */

// param1 passes into doInBackground()
// param3 declares return type for doInBackground()

// commented out --> now returning Array of Objects

// public class FetchMovieTask extends AsyncTask<String, Void, String>

    public class FetchMovieTask extends AsyncTask<String, Void, ArrayList<Movie>> {

        private final String LOG_TAG = FetchMovieTask.class.getSimpleName();

        @Override
        // change return type to String in order to return output String

        // changed from String to ArrayList<Movie>
        // protected String doInBackground(String... params) {

        protected ArrayList<Movie> doInBackground(String... params) {
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String movieJsonStr = null;


            // remove '?' before 'api_key' and '=' after, the Uri class builds it for you
            String kb = "api_key";
            String kc = "81696f0358507756b5119609b0fae31e";
            String sort = "sort_by";
            // added to assign User Setting to this String
            String sortBy = "";

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
                movieJsonStr = buffer.toString();

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

            // return ArrayList of Movie Objects
            return parseJSONObject(movieJsonStr);
        }

        // method that takes in String of output and returns Array of Movie Objects
        private ArrayList<Movie> parseJSONObject(String jsonString) {
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
                    Movie movieItem = new Movie(title, image, summary, rating, releaseDate);
                    movieObjects.add(movieItem);
                }
                //
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public void onPostExecute(ArrayList<Movie> movies) {
            arrayAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("key", movieObjects);
        super.onSaveInstanceState(outState);
    }

}
