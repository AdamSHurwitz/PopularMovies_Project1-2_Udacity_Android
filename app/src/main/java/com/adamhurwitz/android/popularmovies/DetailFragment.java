package com.adamhurwitz.android.popularmovies;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.adamhurwitz.android.popularmovies.data.CursorContract;
import com.adamhurwitz.android.popularmovies.data.CursorDbHelper;
import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailFragment extends Fragment {

    public DetailFragment() {
    }

    String toggle = "off";
    String movieTitle = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_detail, container, false);

        // get id for favorite_btn
        final ImageButton favoriteButton = (ImageButton) view.findViewById(R.id.favorite_btn);
        final ImageButton playButton = (ImageButton) view.findViewById(R.id.play_btn);

        //receive the intent
        //Activity has intent, must get intent from Activity
        Intent intent = getActivity().getIntent();
        if (intent != null) {

            final String[] movie_data = intent.getStringArrayExtra("Cursor Movie Attributes");
            // movie_data[0] = title
            // movie_data[1] = image
            // movie_data[2] = summary
            // movie_data[3] = rating
            // movie_data[4] = release_date
            // movie_data[5] = favorite

            //Create MovieData Poster Within 'fragment_detail.xml'
            ImageView detail_movie_image = (ImageView) view.findViewById(R.id.detail_movie_image);
            // Construct the URL to query images in Picasso
            final String PICASSO_BASE_URL = "http://image.tmdb.org/t/p/";
            final String imageUrl = movie_data[1];
            Uri builtUri = Uri.parse(PICASSO_BASE_URL).buildUpon()
                    // appending size and image source
                    .appendPath("w780")
                    .appendPath(imageUrl.substring(1))
                    .build();
            // generate images with Picasso
            // switch this to getActivity() since must get Context from Activity
            Picasso.with(getActivity())
                    .load(builtUri)
                    .resize(780, 780)
                    .centerCrop()
                            // TODO: Add in Animated Placeholder
                    .placeholder(R.drawable.user_placeholder)
                    .error(R.drawable.user_placeholder_error)
                    .into(detail_movie_image);

            //Create MovieData Title within 'fragment_detail.xml'
            TextView title = (TextView) view.findViewById(R.id.detail_title);
            title.setText(movie_data[0]);
            movieTitle = movie_data[0];

            //Create MovieData User Rating Within 'fragment_detail.xml'
            TextView rating = (TextView) view.findViewById(R.id.detail_rating);
            rating.setText(movie_data[3] + " out of 10");

            //Create MovieData User Release Date Within 'fragment_detail.xml'
            TextView releaseDate = (TextView) view.findViewById(R.id.detail_releasedate);
            releaseDate.setText("released: " + movie_data[4]);

            //Create MovieData Synopsis Within 'fragment_detail.xml'
            TextView synopsis = (TextView) view.findViewById(R.id.detail_synopsis);
            synopsis.setText(movie_data[2]);

            // Display correct on/off status for favorite button
            if (movie_data[5].equals("2")) {
                toggle = "on";
                favoriteButton.setImageResource(R.drawable.star_pressed_18dp);
            } else if (movie_data[5].equals("1")) {
                toggle = "off";
                favoriteButton.setImageResource(R.drawable.star_default_18dp);
            }

            // Click listener for favorite button
            favoriteButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    // Perform action on click

                    // Turn button on
                    if (toggle.equals("off")) {
                        toggle = "on";
                        favoriteButton.setImageResource(R.drawable.star_pressed_18dp);

                        // Update Database to Set Favorites True For Given Title
                        CursorDbHelper dbHelper1 = new CursorDbHelper(getContext());
                        SQLiteDatabase db1 = dbHelper1.getReadableDatabase();

                        // Query Database
                        String[] whereValue1 = {movieTitle};
                        String sortOrder1 =
                                CursorContract.MovieData._ID + " DESC";

                        Cursor c1 = db1.query(
                                CursorContract.MovieData.TABLE_NAME,  // The table to query
                                null,                               // The columns to return
                                CursorContract.MovieData.COLUMN_NAME_TITLE + "= ?", // The columns for the WHERE clause
                                whereValue1,                            // The values for the WHERE clause
                                null,                                     // don't group the rows
                                null,                                     // don't filter by row groups
                                sortOrder1                                 // The sort order
                        );

                        // Update current movie favorites to be set as on
                        if (c1.moveToFirst()) {
                            String favoriteColumn = c1.getString(
                                    c1.getColumnIndexOrThrow(CursorContract.MovieData
                                            .COLUMN_NAME_FAVORITE));
                            // New value column
                            ContentValues values = new ContentValues();
                            values.put(CursorContract.MovieData.COLUMN_NAME_FAVORITE, 2);

                            // Which row to update, based on the ID
                            String whereColumn = CursorContract.MovieData.COLUMN_NAME_TITLE + "= ?";
                            String[] whereValue = {movieTitle};

                            // Update
                            int count = db1.update(
                                    CursorContract.MovieData.TABLE_NAME,
                                    values,
                                    whereColumn,
                                    whereValue);
                        }
                        c1.close();
                        Toast.makeText(getContext(), toggle + " " + movieTitle + " " +
                                movie_data[5], Toast.LENGTH_SHORT).show();
                    }
                    // Turn button off
                    else if (toggle.equals("on")) {
                        toggle = "off";
                        favoriteButton.setImageResource(R.drawable.star_default_18dp);
                        // Update current movie favorites to be set as off
                        // Update Database to Set Favorites True For Given Title
                        CursorDbHelper dbHelper2 = new CursorDbHelper(getContext());
                        SQLiteDatabase db2 = dbHelper2.getReadableDatabase();

                        // Query Database
                        String[] whereValue2 = {movieTitle};
                        String sortOrder2 =
                                CursorContract.MovieData._ID + " DESC";

                        Cursor c2 = db2.query(
                                CursorContract.MovieData.TABLE_NAME,  // The table to query
                                null,                               // The columns to return
                                CursorContract.MovieData.COLUMN_NAME_TITLE + "= ?", // The columns for the WHERE clause
                                whereValue2,                            // The values for the WHERE clause
                                null,                                     // don't group the rows
                                null,                                     // don't filter by row groups
                                sortOrder2                                 // The sort order
                        );
                        if (c2.moveToFirst()) {
                            String favoriteColumn = c2.getString(
                                    c2.getColumnIndexOrThrow(CursorContract.MovieData.COLUMN_NAME_FAVORITE));
                            // New value column
                            ContentValues values = new ContentValues();
                            values.put(CursorContract.MovieData.COLUMN_NAME_FAVORITE, 1);

                            // Which row to update, based on the ID
                            String selection = CursorContract.MovieData.COLUMN_NAME_TITLE + "= ?";
                            String[] selectionArgs = {movieTitle};

                            // Update
                            int count = db2.update(
                                    CursorContract.MovieData.TABLE_NAME,
                                    values,
                                    selection,
                                    selectionArgs);
                        }
                        c2.close();
                        Toast.makeText(getContext(), toggle + " " + movieTitle + " "
                                + movie_data[5], Toast.LENGTH_SHORT).show();
                    }
                }

            });

            playButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {

                    String url = "https://www.youtube.com/watch?v=sGbxmsDFVnE";


                    // Web Browser Intent
                    Uri webpage = Uri.parse(url);
                    Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
                        startActivity(intent);

                        Toast.makeText(getContext(), "Play Button Launched Here", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            return view;
        }
    }
