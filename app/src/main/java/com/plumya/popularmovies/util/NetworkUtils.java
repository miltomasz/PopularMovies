package com.plumya.popularmovies.util;

import android.net.Uri;
import android.util.Log;

import com.plumya.popularmovies.MainActivity;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by miltomasz on 18/04/17.
 */

public final class NetworkUtils {

    public static final String BASE_IMG_URL = "http://image.tmdb.org/t/p";
    public static final String BASE_MOVIE_URL = "http://api.themoviedb.org/3";

    private static final String TAG = NetworkUtils.class.getSimpleName();
    private static final String MOVIE_PATH = "movie";
    private static final String POPULAR_PATH = "popular";
    private static final String TOP_RATED_PATH = "top_rated";
    private static final String API_KEY = "api_key";
    private static final String API_KEY_VALUE = "69a24cb38d0560337bd90269945449db";

    public final static class EndpointsCreator {
        public static String movie(String selectedOption) {
            String selectedPath = path(selectedOption);
            return Uri.parse(MOVIE_PATH)
                    .buildUpon()
                    .appendPath(selectedPath)
                    .build()
                    .toString();
        }

        public static String movie(long id, String path) {
            return Uri.parse(MOVIE_PATH)
                    .buildUpon()
                    .appendPath(String.valueOf(id))
                    .appendPath(path)
                    .build()
                    .toString();
        }
    }

    public static URL buildUrl(String selectedPath) {
        Uri builtUri = Uri.parse(BASE_MOVIE_URL)
                .buildUpon()
                .appendEncodedPath(selectedPath)
                .appendQueryParameter(API_KEY, API_KEY_VALUE)
                .build();
        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            Log.e(TAG, "Exception occurred while parsing URI: " + e.getMessage());
        }
        Log.v(TAG, "Built URL " + url);
        return url;
    }

    public static Uri buildImageUri(String imageSize, String imagePath) {
        Uri baseUri = Uri.parse(BASE_IMG_URL)
                .buildUpon()
                .appendPath(imageSize)
                .build();
        Uri imageUri = Uri.parse(baseUri.toString() + imagePath);
        Log.v(TAG, "Built image URI " + imageUri.toString());
        return imageUri;
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    private static String path(String selectedOption) {
        switch (selectedOption) {
            case MainActivity.POPULAR_OPT : return POPULAR_PATH;
            case MainActivity.TOP_RATED_OPT : return TOP_RATED_PATH;
            default : return "";
        }
    }
}
