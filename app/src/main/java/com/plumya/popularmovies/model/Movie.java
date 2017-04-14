package com.plumya.popularmovies.model;

import com.plumya.popularmovies.util.NetworkUtils;

/**
 * Created by miltomasz on 10/04/17.
 */

public class Movie {
    private String originalTitle;
    private String posterPath;
    private String overview;
    private String voteAverage;
    private String releaseDate;

    public Movie(String originalTitle, String posterPath, String overview,
                 String voteAverage, String releaseDate) {
        this.originalTitle = originalTitle;
        this.posterPath = posterPath;
        this.overview = overview;
        this.voteAverage = voteAverage;
        this.releaseDate = releaseDate;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public String getOverview() {
        return overview;
    }

    public String getVoteAverage() {
        return voteAverage;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getAbsolutePosterPath() {
        return NetworkUtils.buildImageUri().toString() + getPosterPath();
    }
}
