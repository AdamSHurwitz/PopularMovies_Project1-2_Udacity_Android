package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.android.popularmovies.data.CursorContract;
import com.example.android.popularmovies.data.CursorDbHelper;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainFragment extends Fragment {

    private AsyncCursorAdapter asyncCursorAdapter;

    /**
     * Empty constructor for the AsyncParcelableFragment1() class.
     */
    public MainFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.grid_view_layout, container, false);

        setHasOptionsMenu(true);

        // Access database
        CursorDbHelper mDbHelper = new CursorDbHelper(getContext());
        // Gets the data repository in read mode
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        // How you want the results sorted in the resulting Cursor
        String sortOrder =
                CursorContract.MovieData._ID + " DESC";
        String[] wherevalues = {"1"};

        // If you are querying entire table, can leave everything as Null
        Cursor cursor = db.query(
                CursorContract.MovieData.TABLE_NAME,  // The table to query
                null,                               // The columns to return
                null,  // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );

        asyncCursorAdapter = new AsyncCursorAdapter(getActivity(), cursor, 0);
        Log.v("CursorAdapter Called", "HERE");

        // Get a reference to the grid view layout and attach the adapter to it.
        GridView gridView = (GridView) view.findViewById(R.id.grid_view_layout);
        gridView.setAdapter(asyncCursorAdapter);


        // Create Toast

        // gridView.setOnItemClickListener(new OnItem... [auto-completes])
        /*gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            // parent = parent view, view = grid_item view, position = grid_item position
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // removed and replaced w/ Explicit Intent
                Toast.makeText(getActivity(), doodleDataList.get(position).getTitle(),
                        Toast.LENGTH_SHORT).show();
            }
        });*/

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                String title = cursor.getString(cursor.getColumnIndex(CursorContract.MovieData
                        .COLUMN_NAME_TITLE));
                String image_url = cursor.getString(cursor.getColumnIndex(CursorContract.MovieData
                        .COLUMN_NAME_IMAGEURL));
                String summary = cursor.getString(cursor.getColumnIndex(CursorContract.MovieData
                        .COLUMN_NAME_SUMMARY));
                String rating = cursor.getString(cursor.getColumnIndex(CursorContract.MovieData
                        .COLUMN_NAME_RATING));
                String release_date = cursor.getString(cursor.getColumnIndex(CursorContract.MovieData
                        .COLUMN_NAME_RELEASEDATE));

                String[] doodleDataItems = {title, image_url, summary, rating, release_date};

                Intent intent = new Intent(getActivity(),
                        DetailActivity.class);

                intent.putExtra("Cursor Doodle Attributes", doodleDataItems);

                startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        asyncCursorAdapter.notifyDataSetChanged();
        getDoodleData();
    }

    private void getDoodleData() {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                this.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();

        // Make sure that the device is actually connected to the internet before trying to get data
        // about the Google doodles.
        if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
            FetchMovieTask doodleTask = new FetchMovieTask(asyncCursorAdapter,
                    getContext());
            doodleTask.execute("release_date.desc", "vintage");

        }
    }


}

/**
 * A placeholder fragment containing a simple view.
 */
/*
public class MainFragment extends Fragment {

    // creating Adapter
    private AsyncCursorAdapter asyncCursorAdapter;

    */
/**
     * Empty constructor for the MainFragment class.
     *//*

    public MainFragment() {
    }

    // inflating menu
    */
/*@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
    }*//*


    // loads GridView with current Setting selected
    @Override
    public void onStart() {
        super.onStart();  // Always call the superclass method first
        asyncCursorAdapter.notifyDataSetChanged();
        getDoodleData();
    }

    private void getDoodleData() {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                this.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();

        // Make sure that the device is actually connected to the internet before trying to get data
        // about the Google doodles.
        if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
            FetchMovieTask movieTask = new FetchMovieTask(asyncCursorAdapter, getContext());
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String sortBy = prefs.getString("sortBy_key", "0");
            movieTask.execute(sortBy);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
            */
/*case R.id.action_refresh:
                 FetchMovieTask movieTask = new FetchMovieTask();
                 movieTask.execute("popularity.desc");
                return true;*//*

            case R.id.action_settings:
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);

            */
/** if (id == R.id.action_refresh) {
             // looks for doInBackground --> commented out to move into onCreateView()
             */
/**FetchMovieTask movieTask = new FetchMovieTask();
             movieTask.execute("popularity.desc");
             return true;
             }
             return super.onOptionsItemSelected(item); *//*


        }
    }

    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.grid_view_layout, container, false);

        setHasOptionsMenu(true);

        // Access database
        CursorDbHelper mDbHelper = new CursorDbHelper(getContext());
        // Gets the data repository in read mode
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        // How you want the results sorted in the resulting Cursor
        String sortOrder =
                CursorContract.MovieData._ID + " DESC";
        String[] wherevalues = {"1"};

        // If you are querying entire table, can leave everything as Null
        Cursor cursor = db.query(
                CursorContract.MovieData.TABLE_NAME,  // The table to query
                null,                               // The columns to return
                // TODO: Implement Favorites - Where clause =
                // CursorContract.MovieData.COLUMN_NAME_FAVORITE + " = ?"
                null,  // The columns for the WHERE clause
                // TODO: Implement Favorites - Where value = 1
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );


        asyncCursorAdapter = new AsyncCursorAdapter(
                // current context (this fragment's containing activity)
                getActivity(), cursor, 0);

        // Get a reference to GridView, and attach this adapter to it
        GridView gridView = (GridView) view.findViewById(R.id.grid_view_layout);
        gridView.setAdapter(asyncCursorAdapter);


        // Create Toast
        // gridView.setOnItemClickListener(new OnItem... [auto-completes])
        */
/*gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            // parent = parent view, view = grid_item view, position = grid_item position
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // removed and replaced w/ Explicit Intent
                Toast.makeText(getActivity(), movieDataObjects.get(position).getTitle(),
                        Toast.LENGTH_SHORT).show();
            }
        });*//*


        // Click Listener
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                String title = cursor.getString(cursor.getColumnIndex(CursorContract.MovieData
                        .COLUMN_NAME_TITLE));
                String image = cursor.getString(cursor.getColumnIndex(CursorContract.MovieData
                        .COLUMN_NAME_IMAGEURL));
                String summary = cursor.getString(cursor.getColumnIndex(CursorContract.MovieData
                        .COLUMN_NAME_SUMMARY));
                String rating = cursor.getString(cursor.getColumnIndex(CursorContract.MovieData
                        .COLUMN_NAME_RATING));
                String release_date = cursor.getString(cursor.getColumnIndex(CursorContract.MovieData
                        .COLUMN_NAME_RELEASEDATE));

                String[] doodleDataItems = {title, image, summary, rating, release_date};

                Intent intent = new Intent(getActivity(),
                        DetailActivity.class);

                intent.putExtra("Cursor Doodle Attributes", doodleDataItems);

                startActivity(intent);
            }
        });

        if (savedInstanceState == null || !savedInstanceState.containsKey("key")) {
            // looks for doInBackground
            FetchMovieTask movieTask = new FetchMovieTask(asyncCursorAdapter, getContext());
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String sortBy = prefs.getString("sortBy_key", "0");
            movieTask.execute(sortBy);

            return view;
        }
        return view;
    }
}
*/




