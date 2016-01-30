package com.adamhurwitz.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.adamhurwitz.android.popularmovies.data.CursorContract;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainFragment extends Fragment {
    private final String LOG_TAG = MainFragment.class.getSimpleName();
    private AsyncCursorAdapter mAsyncCursorAdapter;

    /**
     * Empty constructor for the AsyncParcelableFragment1() class.
     */
    public MainFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.grid_view_layout, container, false);

        // Create menu
        setHasOptionsMenu(true);

        // Initialize Adapter
        mAsyncCursorAdapter = new com.adamhurwitz.android.popularmovies.AsyncCursorAdapter(
                getActivity(), null, 0);
        Log.v("CursorAdapter_Called", "HERE");

        // Get a reference to the grid view layout and attach the adapter to it.
        GridView gridView = (GridView) view.findViewById(R.id.grid_view_layout);
        gridView.setAdapter(mAsyncCursorAdapter);

        // Click listener when grid item is selected
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                String movie_id = cursor.getString(cursor.getColumnIndex(CursorContract.MovieData
                        .COLUMN_NAME_MOVIEID));
                String title = cursor.getString(cursor.getColumnIndex(CursorContract.MovieData
                        .COLUMN_NAME_TITLE));
                String image_url = cursor.getString(cursor.getColumnIndex(CursorContract.MovieData
                        .COLUMN_NAME_IMAGEURL));
                String summary = cursor.getString(cursor.getColumnIndex(CursorContract.MovieData
                        .COLUMN_NAME_SUMMARY));
                String rating = cursor.getString(cursor.getColumnIndex(CursorContract.MovieData
                        .COLUMN_NAME_VOTEAVERAGE));
                String popularity = cursor.getString(cursor.getColumnIndex(CursorContract.MovieData
                        .COLUMN_NAME_POPULARITY));
                String release_date = cursor.getString(cursor.getColumnIndex(CursorContract
                        .MovieData
                        .COLUMN_NAME_RELEASEDATE));
                String favorite = cursor.getString(cursor.getColumnIndex(CursorContract.MovieData
                        .COLUMN_NAME_FAVORITE));

                // store items to pass into intent
                String[] doodleDataItems = {movie_id, title, image_url, summary, rating,
                        release_date, favorite};
                Intent intent = new Intent(getActivity(),
                        com.adamhurwitz.android.popularmovies.DetailActivity.class);
                intent.putExtra("Cursor Movie Attributes", doodleDataItems);
                startActivity(intent);

                // launch method that executes AsyncTask to build YouTube URL and update database
                getYouTubeKey(movie_id, title);
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        // Call AsyncTask to get Movie Data
        getMovieData();
    }


    // Create menu items and actions
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                startActivity(new Intent(getActivity(), SettingsActivity.class));
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Method for executing movie data AsyncTask
    private void getMovieData() {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                this.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();

        if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
            com.adamhurwitz.android.popularmovies.FetchMovieTask MovieTask =
                    new FetchMovieTask(getContext(), mAsyncCursorAdapter) {
                    };
            MovieTask.execute("popularity.desc");
        }
    }

    // Method to execute AsyncTask for YouTube URLs
    private void getYouTubeKey(String movie_id, String title) {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                this.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();

        if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
            com.adamhurwitz.android.popularmovies.FetchYouTubeUrlTask YouTubeKeyTask =
                    new FetchYouTubeUrlTask(getContext());
            YouTubeKeyTask.execute(movie_id, title);
        }
    }

    // AsyncTask class for YouTube URLs
    private class FetchYouTubeUrlTask extends com.adamhurwitz.android.popularmovies
            .FetchYouTubeUrlTask {
        public FetchYouTubeUrlTask(Context context) {
            super(context);
        }
    }
}




