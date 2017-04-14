package com.plumya.popularmovies.util;

import com.plumya.popularmovies.model.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by toml on 13.04.17.
 */
public final class MovieJsonUtils {

    public static final String RESULTS = "results";

    private MovieJsonUtils() {
    }

    public static List<Movie> parse(String movieListStr) throws JSONException {
        JSONObject moviesJsonObj = new JSONObject(movieListStr);
        JSONArray moviesJsonArray = moviesJsonObj.getJSONArray(RESULTS);
        List<Movie> movies = new ArrayList<>();
        for (int i = 0; i < moviesJsonArray.length(); i++) {
            JSONObject movieJson = moviesJsonArray.getJSONObject(i);
            String originalTitle = movieJson.getString("original_title");
            String posterPath = movieJson.getString("poster_path");
            String overview = movieJson.getString("overview");
            String voteAverage = movieJson.getString("vote_average");
            String releaseDate = movieJson.getString("release_date");

            Movie movie = new Movie(originalTitle, posterPath, overview, voteAverage, releaseDate);
            movies.add(movie);
        }
        return movies;
    }
}
