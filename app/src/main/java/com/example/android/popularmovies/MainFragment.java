package com.example.android.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainFragment extends Fragment {

    // commented out → using API data

  /*   static final variable to store dummmy data
    private final Integer[] dummyData = {R.drawable.ant, R.drawable.deadpool, R.drawable.fantastic,
            R.drawable.goodnight, R.drawable.mission, R.drawable.southpaw,
            R.drawable.star_wars, R.drawable.straight, R.drawable.terminator,
            R.drawable.the_gift, R.drawable.the_man, R.drawable.wet_hot_summer};*/

    // creating ArrayList of Movies
    private ArrayList<MovieData> movieDataObjects = new ArrayList<MovieData>();
    // creating GridViewAsyncAdapter
    private GridViewAsyncAdapter gridViewAsyncAdapter;
    // create EXTRA_MESSAGE
    public static final String EXTRA_MESSAGE = "com.example.android.popularmovies";

    /**
     * Empty constructor for the MainFragment class.
     */
    public MainFragment(){
    }

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
        movieDataObjects.clear();
        gridViewAsyncAdapter.notifyDataSetChanged();
        FetchMovieTask movieTask = new FetchMovieTask(gridViewAsyncAdapter, movieDataObjects);
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

        gridViewAsyncAdapter = new GridViewAsyncAdapter(
                // current context (this fragment's containing activity)
                getActivity(),
                // ID of view item layout, not needed since we get it in getView()
                R.layout.grid_item_layout,
                movieDataObjects);

        // Get a reference to GridView, and attach this adapter to it
        GridView gridView = (GridView) view.findViewById(R.id.grid_view_layout);
        gridView.setAdapter(gridViewAsyncAdapter);


        // Create Toast
        // gridView.setOnItemClickListener(new OnItem... [auto-completes])
        /*gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            // parent = parent view, view = grid_item view, position = grid_item position
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // removed and replaced w/ Explicit Intent
                Toast.makeText(getActivity(), movieDataObjects.get(position).getTitle(),
                        Toast.LENGTH_SHORT).show();
            }
        });*/

        // Click Listener
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            // parent = parent view, view = grid_item view, position = grid_item position
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // removed and replaced w/ Explicit Intent
                //Toast.makeText(getActivity(), movieDataObjects.get(position).getTitle() , Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(getActivity(), com.example.android.popularmovies.DetailActivity.class);
                String message = movieDataObjects.get(position).getTitle();
                 intent.putExtra(EXTRA_MESSAGE, message);

                intent.putExtra(EXTRA_MESSAGE, movieDataObjects.get(position));
                startActivity(intent);
            }
        });

        // doInBackground -> commented out to make doInBackground that checks for Settings
        /**FetchMovieTask movieTask = new FetchMovieTask();
         movieTask.execute("popularity.desc");*/


        if (savedInstanceState == null || !savedInstanceState.containsKey("key")) {
            // looks for doInBackground
            FetchMovieTask movieTask = new FetchMovieTask(gridViewAsyncAdapter, movieDataObjects);;
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String sortBy = prefs.getString("sortBy_key", "0");
            movieTask.execute(sortBy);
        } else {
            movieDataObjects = savedInstanceState.getParcelableArrayList("key");
        }


        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("key", movieDataObjects);
        super.onSaveInstanceState(outState);
    }

}
