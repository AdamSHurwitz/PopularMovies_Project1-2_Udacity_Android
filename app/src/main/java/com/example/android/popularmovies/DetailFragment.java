package com.example.android.popularmovies;

import android.content.Intent;
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

import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailFragment extends Fragment {

    public DetailFragment() {
    }

    String movieTitle = "";
    String toggle = "off";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_detail, container, false);

        // get id for favorite_btn
        final ImageButton favoriteButton = (ImageButton) view.findViewById(R.id.favorite_btn);

        //receive the intent
        //Activity has intent, must get intent from Activity
        Intent intent = getActivity().getIntent();
        if (intent != null) {

            final String[] movie_data = intent.getStringArrayExtra("Cursor Doodle Attributes");
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

            //Create MovieData User Rating Within 'fragment_detail.xml'
            TextView rating = (TextView) view.findViewById(R.id.detail_rating);
            rating.setText(movie_data[3] + " out of 10");

            //Create MovieData User Release Date Within 'fragment_detail.xml'
            TextView releaseDate = (TextView) view.findViewById(R.id.detail_releasedate);
            releaseDate.setText("released: " + movie_data[4]);

            //Create MovieData Synopsis Within 'fragment_detail.xml'
            TextView synopsis = (TextView) view.findViewById(R.id.detail_synopsis);
            synopsis.setText(movie_data[2]);

            // Click listener for favorite button
            favoriteButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    // Perform action on click

                    // Turn button on
                    if (toggle == "off") {
                        toggle = "on";
                        favoriteButton.setImageResource(R.drawable.star_pressed_18dp);

                    }
                    // Turn button off
                    else if (toggle == "on") {
                        toggle = "off";
                        favoriteButton.setImageResource(R.drawable.star_default_18dp);
                        //TODO: update favorite to 0 for title

                    }

                    String favorite = movie_data[5];

                    Toast.makeText(getContext(), toggle + " " + movieTitle + " " + favorite, Toast.LENGTH_SHORT).show();
                }
            });

        }
        return view;
    }
}
