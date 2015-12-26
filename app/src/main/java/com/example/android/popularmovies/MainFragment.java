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
        Log.v("CursorAdapter_Called", "HERE");

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
                        .COLUMN_NAME_VOTEAVERAGE));
                String popularity = cursor.getString(cursor.getColumnIndex(CursorContract.MovieData
                        .COLUMN_NAME_POPULARITY));
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
            doodleTask.execute("vote_average.desc");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}




