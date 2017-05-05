package com.plumya.popularmovies.util;

import com.plumya.popularmovies.model.Movie;
import com.plumya.popularmovies.model.Review;
import com.plumya.popularmovies.model.Trailer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by toml on 13.04.17.
 */
public final class JsonUtils {

    public static final String RESULTS = "results";

    private JsonUtils() {
    }

    public static class Movies {
        public static List<Movie> parse(String movieListStr) throws JSONException {
            JSONObject moviesJsonObj = new JSONObject(movieListStr);
            JSONArray moviesJsonArray = moviesJsonObj.getJSONArray(RESULTS);
            List<Movie> movies = new ArrayList<>();
            for (int i = 0; i < moviesJsonArray.length(); i++) {
                JSONObject movieJson = moviesJsonArray.getJSONObject(i);
                String id = movieJson.getString("id");
                String originalTitle = movieJson.getString("original_title");
                String posterPath = movieJson.getString("poster_path");
                String overview = movieJson.getString("overview");
                String voteAverage = movieJson.getString("vote_average");
                String releaseDate = movieJson.getString("release_date");
                Movie movie = new Movie(Long.parseLong(id), originalTitle,
                        posterPath, overview, voteAverage, releaseDate);
                movies.add(movie);
            }
            return movies;
        }
    }

    public static class Trailers {
        public static List<Trailer> parse(String trailerListStr) throws JSONException {
            JSONObject trailerJsonObj = new JSONObject(trailerListStr);
            long movieId = trailerJsonObj.getLong("id");
            JSONArray trailerJsonArray = trailerJsonObj.getJSONArray(RESULTS);
            List<Trailer> trailers = new ArrayList<>();
            for (int i = 0; i < trailerJsonArray.length(); i++) {
                JSONObject trailerJson = trailerJsonArray.getJSONObject(i);
                String id = trailerJson.getString("id");
                String key = trailerJson.getString("key");
                String name = trailerJson.getString("name");
                Trailer movie = new Trailer(id, movieId, key, name);
                trailers.add(movie);
            }
            return trailers;
        }
    }

    public static class Reviews {
        public static List<Review> parse(String reviewListStr) throws JSONException {
            JSONObject reviewJsonObj = new JSONObject(reviewListStr);
            long movieId = reviewJsonObj.getLong("id");
            JSONArray reviewJsonArray = reviewJsonObj.getJSONArray(RESULTS);
            List<Review> reviews = new ArrayList<>();
            for (int i = 0; i < reviewJsonArray.length(); i++) {
                JSONObject reviewJson = reviewJsonArray.getJSONObject(i);
                String id = reviewJson.getString("id");
                String author = reviewJson.getString("author");
                String content = reviewJson.getString("content");
                String url = reviewJson.getString("url");
                Review review = new Review(id, movieId, author, content, url);
                reviews.add(review);
            }
            return reviews;
        }
    }
}
