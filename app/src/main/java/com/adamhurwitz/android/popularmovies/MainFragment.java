package com.adamhurwitz.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.adamhurwitz.android.popularmovies.data.CursorContract;
import com.adamhurwitz.android.popularmovies.service.MovieDataService;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private final String LOG_TAG = MainFragment.class.getSimpleName();
    private CursorAdapter mCursorAdapter;
    private static final int LOADER_FRAGMENT = 0;

    String whereColumns = "";
    String[] whereValue = {"0"};
    String sortOrder = "";

    String initialPref;
    private GridView mGridView;
    private int mPosition = GridView.INVALID_POSITION;
    private static final String SELECTED_KEY = "selected_position";

    /**
     * Empty constructor for the AsyncParcelableFragment1() class.
     */
    public MainFragment() {
    }

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        public void onItemSelected(Uri uri, String title);
    }

    @Override
    public void onStart() {
        super.onStart();
        getMovieData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Initialize Adapter
        mCursorAdapter = new CursorAdapter(
                getActivity(), null, 0);

        View view = inflater.inflate(R.layout.grid_view_layout, container, false);

        // Create menu
        setHasOptionsMenu(true);

        // Get a reference to the grid view layout and attach the adapter to it.
        mGridView = (GridView) view.findViewById(R.id.grid_view_layout);
        mGridView.setAdapter(mCursorAdapter);

        // Click listener when grid item is selected
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // CursorAdapter returns a cursor at the correct position for getItem(), or null
                // if it cannot seek to that position.
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if (cursor != null) {
                    String mTitle = cursor.getString(cursor.getColumnIndex(CursorContract.MovieData
                            .COLUMN_NAME_TITLE));
                    String movie_id = cursor.getString(cursor.getColumnIndex(CursorContract.MovieData
                            .COLUMN_NAME_MOVIEID));
                    ((Callback) getActivity()).onItemSelected(
                            CursorContract.MovieData.buildMovieIdUri(), mTitle);
                }
                mPosition = position;
            }
        });

        // If there's instance state, mine it for useful information.
        // The end-goal here is that the user never knows that turning their device sideways
        // does crazy lifecycle related things.  It should feel like some stuff stretched out,
        // or magically appeared to take advantage of room, but data or place in the app was never
        // actually *lost*.
        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            // The listview probably hasn't even been populated yet.  Actually perform the
            // swapout in onLoadFinished.
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // When tablets rotate, the currently selected list item needs to be saved.
        // When no item is selected, mPosition will be set to Listview.INVALID_POSITION,
        // so check for that before storing.
        if (mPosition != GridView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();

        // Show message if no item is selected
        if (mPosition < 0 && null != getActivity().findViewById(R.id.no_detail_layout)) {
            LinearLayout noContentDetailLayout = (LinearLayout) getActivity().findViewById(
                    R.id.no_detail_layout);
            noContentDetailLayout.setVisibility(View.VISIBLE);
        }

        // Check initial status of SharedPreference value against current
        SharedPreferences sql_pref = PreferenceManager.getDefaultSharedPreferences(getContext());
        String sort_value = sql_pref.getString("sort_key", "popularity.desc");
        Log.v(LOG_TAG, "CALLED_ON_ONRESUME | " + sort_value);
        if (initialPref == null) {
            initialPref = sort_value;
            Log.v(LOG_TAG, "EMPTY TRUE");
            Log.v(LOG_TAG, "INITIALPREF INITIALIZED TO " + initialPref);
        }
        if (!sort_value.equals(initialPref)) {
            Log.v(LOG_TAG, "CALLED_ON_INITIALPREF | " + initialPref + " CALLED_ON_ONRESUME | " + sort_value);
            initialPref = sort_value;
            Log.v(LOG_TAG, "CALLED_ON_REQUERY!");

            String whereColumns2 = "";
            String[] whereValue2;
            String sortOrder2 = "";

            switch (sort_value) {
                case "popularity.desc":
                    sortOrder2 = CursorContract.MovieData.COLUMN_NAME_POPULARITY + " DESC";
                    whereColumns2 = null;
                    whereValue2 = null;
                    Toast.makeText(getContext(), "Sorting by Popularity...", Toast.LENGTH_SHORT)
                            .show();
                    break;
                case "vote_average.desc":
                    sortOrder2 = CursorContract.MovieData.COLUMN_NAME_VOTEAVERAGE + " DESC";
                    whereColumns2 = null;
                    whereValue2 = null;
                    Toast.makeText(getContext(), "Sorting by Ratings...", Toast.LENGTH_SHORT)
                            .show();
                    break;
                case "favorites":
                    sortOrder2 = null;
                    whereColumns2 = CursorContract.MovieData.COLUMN_NAME_FAVORITE + "= ?";
                    whereValue2 = new String[]{"2"};
                    Toast.makeText(getContext(), "Sorting by Favorites...", Toast.LENGTH_SHORT)
                            .show();
                    break;
                default:
                    sortOrder2 = CursorContract.MovieData.COLUMN_NAME_POPULARITY + " DESC";
                    whereColumns2 = null;
                    whereValue2 = null;
                    Toast.makeText(getContext(), "Sorting by Popularity...", Toast.LENGTH_SHORT)
                            .show();
                    break;
            }

            Cursor onResumeCursor = getContext().getContentResolver().query(
                    // The table to query
                    CursorContract.MovieData.CONTENT_URI,
                    // The columns to return
                    null,
                    // The columns for the WHERE clause
                    whereColumns2,
                    // The values for the WHERE clause
                    whereValue2,
                    // The sort order
                    sortOrder2);
            mCursorAdapter.changeCursor(onResumeCursor);
        }
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
            getActivity().startService(new Intent(getActivity(), MovieDataService.class)
                    .putExtra("MOVIE_QUERY", "popularity.desc"));
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(LOADER_FRAGMENT, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        SharedPreferences sql_pref = PreferenceManager.getDefaultSharedPreferences(getContext());
        String sort_value = sql_pref.getString("sort_key", "popularity.desc");
        switch (sort_value) {
            case "popularity.desc":
                sortOrder = CursorContract.MovieData.COLUMN_NAME_POPULARITY + " DESC";
                whereColumns = null;
                whereValue = null;
                Toast.makeText(getContext(), "Sorting by Popularity...", Toast.LENGTH_SHORT)
                        .show();
                break;
            case "vote_average.desc":
                sortOrder = CursorContract.MovieData.COLUMN_NAME_VOTEAVERAGE + " DESC";
                whereColumns = null;
                whereValue = null;
                Toast.makeText(getContext(), "Sorting by Ratings...", Toast.LENGTH_SHORT)
                        .show();
                break;
            case "favorites":
                sortOrder = null;
                whereColumns = CursorContract.MovieData.COLUMN_NAME_FAVORITE + "= ?";
                whereValue[0] = "2";
                Toast.makeText(getContext(), "Sorting by Favorites...", Toast.LENGTH_SHORT)
                        .show();
                break;
            default:
                sortOrder = CursorContract.MovieData.COLUMN_NAME_POPULARITY + " DESC";
                whereColumns = null;
                whereValue = null;
                Toast.makeText(getContext(), "Sorting by Popularity...", Toast.LENGTH_SHORT)
                        .show();
                break;
        }
        return new CursorLoader(getActivity(),
                // The table to query
                CursorContract.MovieData.CONTENT_URI,
                // The columns to return
                null,
                // The columns for the WHERE clause
                whereColumns,
                // The values for the WHERE clause
                whereValue,
                // The sort order
                sortOrder
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
        if (mPosition != ListView.INVALID_POSITION) {
            // If we don't need to restart the loader, and there's a desired position to restore
            // to, do so now.
            mGridView.smoothScrollToPosition(mPosition);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }
}




