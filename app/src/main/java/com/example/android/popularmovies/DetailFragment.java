package com.example.android.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailFragment extends Fragment {

    public DetailFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_detail, container, false);

        //receive the intent
        //Activity has intent, must get intent from Activity
        Intent intent = getActivity().getIntent();
        if (intent != null) {
            /**String message = intent.getStringExtra(com.example.android.popularmovies.MainFragment.EXTRA_MESSAGE);
             // display the message
             TextView textView = new TextView(this);
             textView.setTextSize(40);
             textView.setText(message);*/

            //Create Movie Poster Within 'fragment_detail.xml'
            ImageView detail_movie_image = (ImageView) view.findViewById(R.id.detail_movie_image);
            Movie movie = intent.getParcelableExtra(com.example.android.popularmovies.MainFragment.EXTRA_MESSAGE);
            // Construct the URL to query images in Picasso
            final String PICASSO_BASE_URL = "http://image.tmdb.org/t/p/";
            final String imageUrl = movie.getImage();
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
                    .placeholder(R.drawable.user_placeholder)
                    .error(R.drawable.user_placeholder_error)
                    .into(detail_movie_image);

            //Create Movie Title within 'fragment_detail.xml'
            TextView title = (TextView) view.findViewById(R.id.detail_title);
            title.setText(movie.getTitle());

            //Create Movie User Rating Within 'fragment_detail.xml'
            TextView rating = (TextView) view.findViewById(R.id.detail_rating);
            rating.setText(movie.getRating() + " out of 10");

            //Create Movie User Release Date Within 'fragment_detail.xml'
            TextView releaseDate = (TextView) view.findViewById(R.id.detail_releasedate);
            releaseDate.setText("released: " + movie.getReleaseDate());

            //Create Movie Synopsis Within 'fragment_detail.xml'
            TextView synopsis = (TextView) view.findViewById(R.id.detail_synopsis);
            synopsis.setText(movie.getSummary());


            //setContentView(textView);

            //setContentView(R.layout.activity_detail);

        }

        return view;
    }
}
