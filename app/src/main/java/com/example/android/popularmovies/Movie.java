package com.example.android.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by adamhurwitz on 8/25/15.
 */
public class Movie implements Parcelable{
    private String title;
    private String image;
    private String summary;
    private String rating;
    private String releaseDate;


    public Movie(String title, String image, String summary,
                 String rating, String releaseDate) {
        this.title = title;
        this.image = image;
        this.summary = summary;
        this.rating = rating;
        this.releaseDate = releaseDate;
    }

    private Movie(Parcel in) {
        title = in.readString();
        image = in.readString();
        summary = in.readString();
        rating = in.readString();
        releaseDate = in.readString();
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public String getImage() {
        return image;
    }

    public String getSummary() {
        return summary;
    }

    public String getRating() {
        return rating;
    }

    public String getReleaseDate() {
        return releaseDate;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(title);
        dest.writeString(image);
        dest.writeString(summary);
        dest.writeString(rating);
        dest.writeString(releaseDate);
    }
}
