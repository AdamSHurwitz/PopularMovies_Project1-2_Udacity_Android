package com.example.android.popularmovies;

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

        asyncCursorAdapter = new AsyncCursorAdapter(getActivity(), null, 0);
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
        getDoodleData();
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


    private void getDoodleData() {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                this.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();

        // Get saved settings value
        String executeValue = "";
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        String pref_result = prefs.getString("sort_key", "popularity");


        if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
            FetchMovieTask task = new FragmentFetchMovieTask(getContext());
            /*doodleTask.execute(pref_result);
            Toast.makeText(getContext(), pref_result, Toast.LENGTH_SHORT).show();*/
            task.execute("popularity.desc");
        }
    }

    private class FragmentFetchMovieTask extends FetchMovieTask {
        public FragmentFetchMovieTask(Context context) {
            super(context);
        }

        Integer favResponse;

        public void favoritesSelector(Integer pressed) {
            switch (pressed) {
                case 1:
                    favResponse = 1;
                    break;
                case 0:
                    favResponse = 0;
                    break;
                default:
                    favResponse = 0;
                    break;
            }
        }

        @Override

        /** Override the onPostExecute method to notify the grid view adapter that new data was received
         * so that the items in the grid view can appropriately reflect the changes.
         * @param movieDataObjects A list of objects with information about the Movies.
         */

        public void onPostExecute(Void param) {
            SharedPreferences sql_pref = PreferenceManager.getDefaultSharedPreferences(getContext());
            String sort_value = sql_pref.getString("sort_key", "popularity.desc");

            //TODO: Add If/Else to sort by favorites when favResponse = 1

            String sortOrder = "";
            Boolean sort_favorites = false;

            switch (sort_value) {
                case "popularity.desc":
                    sortOrder = CursorContract.MovieData.COLUMN_NAME_POPULARITY + " DESC";
                    Toast.makeText(getContext(), "Sorting by Popularity...", Toast.LENGTH_SHORT)
                            .show();
                    break;
                case "vote_average.desc":
                    sortOrder = CursorContract.MovieData.COLUMN_NAME_VOTEAVERAGE + " DESC";
                    Toast.makeText(getContext(), "Sorting by Ratings...", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    sortOrder = CursorContract.MovieData.COLUMN_NAME_POPULARITY + " DESC";
                    Toast.makeText(getContext(), "Sorting by Popularity...", Toast.LENGTH_SHORT)
                            .show();
                    break;
            }

        /*String sortOrder =
                CursorContract.MovieData.COLUMN_NAME_POPULARITY + " DESC";*/
            CursorDbHelper cursorDbHelper = new CursorDbHelper(getContext());
            SQLiteDatabase db = cursorDbHelper.getWritableDatabase();
            Cursor cursor = db.query(
                    CursorContract.MovieData.TABLE_NAME,  // The table to query
                    null,                               // The columns to return
                    null,  // The columns for the WHERE clause
                    null,                            // The values for the WHERE clause
                    null,                                     // don't group the rows
                    null,                                     // don't filter by row groups
                    sortOrder                                 // The sort order
            );
            asyncCursorAdapter.changeCursor(cursor);
            asyncCursorAdapter.notifyDataSetChanged();
        }
    }
}




