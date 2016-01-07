package com.adamhurwitz.android.popularmovies;


// used for Array of Integers when using dummy data
// private class GridViewAdapter extends ArrayAdapter<Integer>

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class GridViewAsyncAdapter extends ArrayAdapter<MovieData> {
    private final String LOG_TAG = GridViewAsyncAdapter.class.getSimpleName();
    // declare Context variable
    Context context;
    ArrayList<MovieData> movieDataObjects;

    /**
     * @param context  is the Context
     * @param resource is the grid_view_layout
     */
    // creates constructor to create GridViewAdapter object
    public GridViewAsyncAdapter(Context context, int resource, ArrayList<MovieData> movieDataObjects) {

        // commented out → no longer used since passing in MovieData Objects

        //super(context, resource, dummyData);

        super(context, resource, movieDataObjects);
        this.context = context;
        this.movieDataObjects = movieDataObjects;
    }

    // get view to create view, telling Adapter what's included in the grid_item_layout
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        // Construct the URL to query images in Picasso
        final String PICASSO_BASE_URL = "http://image.tmdb.org/t/p/";
        final String imageUrl = movieDataObjects.get(position).getImage();

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
        holder.titleItem.setText(movieDataObjects.get(position).getTitle());


        return view;
    }

    class ViewHolder {
        // declare your views here
        TextView titleItem;
        ImageView gridItem;
    }
}