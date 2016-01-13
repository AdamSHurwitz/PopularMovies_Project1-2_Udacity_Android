package com.adamhurwitz.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.adamhurwitz.android.popularmovies.data.CursorContract;
import com.adamhurwitz.android.popularmovies.data.CursorDbHelper;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainFragment extends Fragment {
    private final String LOG_TAG = MainFragment.class.getSimpleName();
    private com.adamhurwitz.android.popularmovies.AsyncCursorAdapter asyncCursorAdapter;

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

        asyncCursorAdapter = new com.adamhurwitz.android.popularmovies.AsyncCursorAdapter(
                getActivity(), null, 0);
        Log.v("CursorAdapter_Called", "HERE");

        // Get a reference to the grid view layout and attach the adapter to it.
        GridView gridView = (GridView) view.findViewById(R.id.grid_view_layout);
        gridView.setAdapter(asyncCursorAdapter);

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

                String[] doodleDataItems = {movie_id, title, image_url, summary, rating,
                        release_date, favorite};

                Intent intent = new Intent(getActivity(),
                        com.adamhurwitz.android.popularmovies.DetailActivity.class);

                intent.putExtra("Cursor Movie Attributes", doodleDataItems);

                startActivity(intent);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                startActivity(new Intent(getActivity(), SettingsActivity.class));
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void getMovieData() {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                this.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();

        if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
            com.adamhurwitz.android.popularmovies.FetchMovieTask MovieTask =
                    new FragmentFetchMovieTask(getContext());
            MovieTask.execute("popularity.desc");
        }
    }

    private class FragmentFetchMovieTask extends com.adamhurwitz.android.popularmovies
            .FetchMovieTask {
        public FragmentFetchMovieTask(Context context) {
            super(context);
        }

        @Override

        /** Override the onPostExecute method to notify the grid view adapter that new data was
         * received so that the items in the grid view can appropriately reflect the changes.
         * @param movieDataObjects A list of objects with information about the Movies.
         */

        public void onPostExecute(Void param) {
            SharedPreferences sql_pref = PreferenceManager.getDefaultSharedPreferences(
                    getContext());
            String sort_value = sql_pref.getString("sort_key", "popularity.desc");

            switch (sort_value) {
                case "popularity.desc":
                    CursorDbHelper cursorDbHelper1 = new CursorDbHelper(getContext());
                    SQLiteDatabase db1 = cursorDbHelper1.getWritableDatabase();
                    Cursor cursor1 = db1.query(
                            CursorContract.MovieData.TABLE_NAME,  // The table to query
                            null,                               // The columns to return
                            null,  // The columns for the WHERE clause
                            null,                            // The values for the WHERE clause
                            null,                                     // don't group the rows
                            null,                                     // don't filter by row groups
                            CursorContract.MovieData.COLUMN_NAME_POPULARITY + " DESC"
                            // The sort order
                    );
                    asyncCursorAdapter.changeCursor(cursor1);
                    asyncCursorAdapter.notifyDataSetChanged();
                    Toast.makeText(getContext(), "Sorting by Popularity...", Toast.LENGTH_SHORT)
                            .show();
                    break;
                case "vote_average.desc":
                    CursorDbHelper cursorDbHelper2 = new CursorDbHelper(getContext());
                    SQLiteDatabase db2 = cursorDbHelper2.getWritableDatabase();
                    Cursor cursor2 = db2.query(
                            CursorContract.MovieData.TABLE_NAME,  // The table to query
                            null,                               // The columns to return
                            null,  // The columns for the WHERE clause
                            null,                            // The values for the WHERE clause
                            null,                                     // don't group the rows
                            null,                                     // don't filter by row groups
                            CursorContract.MovieData.COLUMN_NAME_VOTEAVERAGE + " DESC"
                            // The sort order
                    );
                    asyncCursorAdapter.changeCursor(cursor2);
                    asyncCursorAdapter.notifyDataSetChanged();
                    Toast.makeText(getContext(), "Sorting by Ratings...", Toast.LENGTH_SHORT)
                            .show();
                    break;
                case "favorites":
                    String[] whereValues = {"2"};
                    CursorDbHelper cursorDbHelper3 = new CursorDbHelper(getContext());
                    SQLiteDatabase db3 = cursorDbHelper3.getWritableDatabase();
                    Cursor cursor3 = db3.query(
                            CursorContract.MovieData.TABLE_NAME,  // The table to query
                            null,                               // The columns to return
                            CursorContract.MovieData.COLUMN_NAME_FAVORITE + "= ?",
                            // The columns for the WHERE clause
                            whereValues,
                            // The values for the WHERE clause
                            null,                                     // don't group the rows
                            null,                                     // don't filter by row groups
                            ""                                 // The sort order
                    );
                    asyncCursorAdapter.changeCursor(cursor3);
                    asyncCursorAdapter.notifyDataSetChanged();
                    Toast.makeText(getContext(), "Sorting by Favorites...", Toast.LENGTH_SHORT)
                            .show();
                    break;
                default:
                    CursorDbHelper cursorDbHelper4 = new CursorDbHelper(getContext());
                    SQLiteDatabase db4 = cursorDbHelper4.getWritableDatabase();
                    Cursor cursor4 = db4.query(
                            CursorContract.MovieData.TABLE_NAME,  // The table to query
                            null,                               // The columns to return
                            null,  // The columns for the WHERE clause
                            null,                            // The values for the WHERE clause
                            null,                                     // don't group the rows
                            null,                                     // don't filter by row groups
                            CursorContract.MovieData.COLUMN_NAME_POPULARITY + " DESC"
                            // The sort order
                    );
                    asyncCursorAdapter.changeCursor(cursor4);
                    asyncCursorAdapter.notifyDataSetChanged();
                    Toast.makeText(getContext(), "Sorting by Popularity...", Toast.LENGTH_SHORT)
                            .show();
                    break;
            }

            /*switch (sort_value) {
                case "popularity.desc":
                    whereColumns = null;
                    whereValues[0] = null;
                    sortOrder = CursorContract.MovieData.COLUMN_NAME_POPULARITY + " DESC";
                    Toast.makeText(getContext(), "Sorting by Popularity...", Toast.LENGTH_SHORT)
                            .show();
                    break;
                case "vote_average.desc":
                    whereColumns = null;
                    whereValues[0] = null;
                    sortOrder = CursorContract.MovieData.COLUMN_NAME_VOTEAVERAGE + " DESC";
                    Toast.makeText(getContext(), "Sorting by Ratings...", Toast.LENGTH_SHORT)
                    .show();
                    break;
                case "favorites":
                    whereColumns = CursorContract.MovieData.COLUMN_NAME_FAVORITE + "= ?";
                    whereValues[0] = null;
                    sortOrder = CursorContract.MovieData._ID + " DESC";
                    break;
                default:
                    whereColumns = null;
                    whereValues[0] = null;
                    sortOrder = CursorContract.MovieData.COLUMN_NAME_POPULARITY + " DESC";
                    Toast.makeText(getContext(), "Sorting by Popularity...", Toast.LENGTH_SHORT)
                            .show();
                    break;
            }*/

          /*  CursorDbHelper cursorDbHelper = new CursorDbHelper(getContext());
            SQLiteDatabase db = cursorDbHelper.getWritableDatabase();
            Cursor cursor = db.query(
                    CursorContract.MovieData.TABLE_NAME,  // The table to query
                    null,                               // The columns to return
                    whereColumns,  // The columns for the WHERE clause
                    whereValues,                            // The values for the WHERE clause
                    null,                                     // don't group the rows
                    null,                                     // don't filter by row groups
                    sortOrder                                 // The sort order
            );
            asyncCursorAdapter.changeCursor(cursor);
            asyncCursorAdapter.notifyDataSetChanged();*/
        }
    }
}




