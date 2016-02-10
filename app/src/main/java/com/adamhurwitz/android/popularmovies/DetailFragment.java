package com.adamhurwitz.android.popularmovies;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.adamhurwitz.android.popularmovies.data.CursorContract;
import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private final String LOG_TAG = DetailFragment.class.getSimpleName();
    private AsyncCursorAdapter asyncCursorAdapter;
    String movieId = "";
    String mTitle = "";
    View detailView;
    static final String DETAIL_URI = "URI";
    private Uri mUri;
    private static final int DETAIL_LOADER = 0;
    Cursor mData;
    String mMovieTitle;

    //Initialize views
    ImageButton favoriteButton;
    ImageButton playButton;
    ImageView movieImage;
    TextView movieTitleView;
    TextView ratingView;
    TextView releaseDateView;
    String mYouTubeUrl;
    TextView summaryView;
    TextView review1View;
    CardView review1Card;
    TextView review2View;
    CardView review2Card;
    TextView review3View;
    CardView review3Card;
    Cursor youTubeCursor;
    String review1;
    String review2;
    String review3;
    String favorite;
    CardView mainDetailCard;

    public DetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Read Arguments that Fragment was initialized with
        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(DetailFragment.DETAIL_URI);
            mTitle = arguments.getString("mTitle");
        }

        View view = inflater.inflate(R.layout.fragment_detail, container, false);
        detailView = view;
        asyncCursorAdapter = new com.adamhurwitz.android.popularmovies.AsyncCursorAdapter(
                getActivity(), null, 0);


        // Get Views
        favoriteButton = (ImageButton) view.findViewById(R.id.favorite_btn);
        playButton = (ImageButton) view.findViewById(R.id.play_btn);
        movieImage = (ImageView) view.findViewById(R.id.detail_movie_image);
        movieTitleView = (TextView) view.findViewById(R.id.detail_title);
        ratingView = (TextView) view.findViewById(R.id.detail_rating);
        releaseDateView = (TextView) view.findViewById(R.id.detail_releasedate);
        summaryView = (TextView) view.findViewById(R.id.detail_synopsis);
        review1View = (TextView) view.findViewById(R.id.review1_view);
        review1Card = (CardView) view.findViewById(R.id.review1_card);
        review2View = (TextView) view.findViewById(R.id.review2_view);
        review2Card = (CardView) view.findViewById(R.id.review2_card);
        review3View = (TextView) view.findViewById(R.id.review3_view);
        review3Card = (CardView) view.findViewById(R.id.review3_card);
        mainDetailCard = (CardView) view.findViewById(R.id.main_detail_card);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        // Call AsyncTask to get Review Data
        //getReview(movieId, movieTitle);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (null != mUri && null != mTitle) {
            // Now create and return a CursorLoader that will take care of
            // creating a Cursor for the data being displayed.
            return new CursorLoader(
                    getActivity(),
                    mUri,
                    null,
                    CursorContract.MovieData.COLUMN_NAME_TITLE + "= ?",
                    new String[]{mTitle},
                    null
            );
        }
        return null;
    }

    //TODO: Get values returned from Cursor and place into DetailFragment

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mData = data;
        if (data != null && data.moveToFirst()) {
            String imageUrl = data.getString(data.getColumnIndex(CursorContract.MovieData
                    .COLUMN_NAME_IMAGEURL));
            final String movieTitle = data.getString(data.getColumnIndex(CursorContract.MovieData
                    .COLUMN_NAME_TITLE));
            mMovieTitle = movieTitle;
            String rating = data.getString(data.getColumnIndex(CursorContract.MovieData
                    .COLUMN_NAME_VOTEAVERAGE));
            String releaseDate = data.getString(data.getColumnIndex(CursorContract.MovieData
                    .COLUMN_NAME_RELEASEDATE));
            String summary = data.getString(data.getColumnIndex(CursorContract.MovieData
                    .COLUMN_NAME_SUMMARY));
            String movie_id = data.getString(data.getColumnIndex(CursorContract.MovieData
                    .COLUMN_NAME_MOVIEID));
            favorite = data.getString(data.getColumnIndex(CursorContract.MovieData
                    .COLUMN_NAME_FAVORITE));
            Log.v(LOG_TAG, "onLoadFinished favorite: " + favorite);

            // Display correct status for favorite button

            if (favorite.equals("2")) {
                favoriteButton.setImageResource(R.drawable.star_pressed_18dp);
            } else {
                favoriteButton.setImageResource(R.drawable.star_default_18dp);
            }

            // Click listener for Favorite button

            favoriteButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                  /*  CursorDbHelper cursorDbHelper = new CursorDbHelper(getContext());
                    SQLiteDatabase db = cursorDbHelper.getReadableDatabase();
                    Cursor cursor = db.query(CursorContract.MovieData.TABLE_NAME, null,
                            CursorContract.MovieData.COLUMN_NAME_TITLE + "= ?",
                            new String[]{movieTitle},
                            null, null, CursorContract.MovieData._ID + " DESC");*/

                    ContentValues values = new ContentValues();

                    // Perform action on click
                    if (favorite.equals("1")) {
                        favoriteButton.setImageResource(R.drawable.star_pressed_18dp);
                        //cursor.moveToFirst();
                        values.put(CursorContract.MovieData.COLUMN_NAME_FAVORITE, 2);
                        favorite = "2";
                    } else {
                        favoriteButton.setImageResource(R.drawable.star_default_18dp);
                        //cursor.moveToFirst();
                        values.put(CursorContract.MovieData.COLUMN_NAME_FAVORITE, 1);
                        favorite = "1";
                    }

                    long rowsUpdated = getContext().getContentResolver().update(
                            CursorContract.MovieData.CONTENT_URI,
                            values,
                            CursorContract.MovieData.COLUMN_NAME_TITLE + "= ?",
                            new String[]{movieTitle});
                    /*long rowId = db.update(CursorContract.MovieData.TABLE_NAME, values,
                            CursorContract.MovieData.COLUMN_NAME_TITLE + "= ?",
                            new String[]{movieTitle});*/
                    //cursor.close();
                }
            });

            // Construct the URL to query images in Picasso
            final String PICASSO_BASE_URL = "http://image.tmdb.org/t/p/";
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
                    .into(movieImage);
            movieTitleView.setText(movieTitle);
            ratingView.setText(rating);
            review1Card.setVisibility(View.VISIBLE);
            releaseDateView.setText(releaseDate);
            summaryView.setText(summary);

            // Launch method that executes AsyncTask to build YouTube URL and Reviews and update DB
            if (mYouTubeUrl == null && review1 == null && review2 == null && review3 == null) {
                // Launch AsyncTask to get YouTube URL
                getYouTubeKey(movie_id, mTitle);
                // Launch AsyncTask to get Reviews
                getReview(movie_id, movieTitle);

                youTubeCursor = getContext().getContentResolver().query(
                        CursorContract.MovieData.CONTENT_URI,
                        null,
                        CursorContract.MovieData.COLUMN_NAME_TITLE + "= ?",
                        new String[]{movieTitle},
                        null,
                        null);

                youTubeCursor.moveToFirst();
            }

            mYouTubeUrl = youTubeCursor.getString(data.getColumnIndex(CursorContract.MovieData
                    .COLUMN_NAME_YOUTUBEURL));

            playButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    if (mYouTubeUrl == null) {
                    } else {
                        // Web Browser Intent
                        Uri webpage = Uri.parse(mYouTubeUrl);
                        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
                        startActivity(intent);
                    }
                }
            });

            // Get Reviews from Cursor
            review1 = data.getString(data.getColumnIndex(CursorContract.MovieData
                    .COLUMN_NAME_REVIEW_1));
            review2 = data.getString(data.getColumnIndex(CursorContract.MovieData
                    .COLUMN_NAME_REVIEW_2));
            review3 = data.getString(data.getColumnIndex(CursorContract.MovieData
                    .COLUMN_NAME_REVIEW_3));
            if (review1 != null && review1 != "") {
                review1View.setText(review1);
                review1Card.setVisibility(View.VISIBLE);
            } else {
                review1Card.setVisibility(View.INVISIBLE);
            }
            if (review2 != null && review2 != "") {
                review2View.setText(review2);
                review2Card.setVisibility(View.VISIBLE);
            } else {
                review2Card.setVisibility(View.INVISIBLE);
            }
            if (review3 != null && review3 != "") {
                review3View.setText(review3);
                review3Card.setVisibility(View.VISIBLE);
            } else {
                review3Card.setVisibility(View.INVISIBLE);
            }

            // Show detail view if when Item is clicked
            if (movieTitle != null) {
                mainDetailCard.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    // Method to execute AsyncTask for YouTube URLs
    private void getYouTubeKey(String movie_id, String title) {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                this.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();

        if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
            com.adamhurwitz.android.popularmovies.FetchYouTubeUrlTask YouTubeKeyTask =
                    new FetchYouTubeUrlTask(getContext());
            YouTubeKeyTask.execute(movie_id, title);
        }
    }

    private void getReview(String movie_id, String title) {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                this.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
            FetchReviewTask reviewTask = new FetchReviewTask(getContext(), detailView);
            reviewTask.execute(movie_id, title);
        }
    }

    // AsyncTask class for YouTube URLs
    private class FetchYouTubeUrlTask extends com.adamhurwitz.android.popularmovies
            .FetchYouTubeUrlTask {
        public FetchYouTubeUrlTask(Context context) {
            super(context);
        }
    }

    private class FetchReviewTask extends com.adamhurwitz.android.popularmovies.FetchReviewTask {
        public FetchReviewTask(Context context, View view) {
            super(context, view);
        }
    }
}

