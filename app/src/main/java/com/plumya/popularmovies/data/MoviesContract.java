package com.plumya.popularmovies.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by miltomasz on 05/05/17.
 */

public class MoviesContract {

    public static final String AUTHORITY = "com.plumya.popularmovies";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final String PATH_MOVIES = "movies";

    public static final class MoviesEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();
        // table
        public static final String TABLE_NAME = "movies";
        // column names
        public static final String COLUMN_WEB_ID = "webId";
        public static final String COLUMN_ORIGINAL_TITLE = "originalTitle";
        public static final String COLUMN_POSTER_PATH = "posterPath";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_VOTE_AVERAGE = "voteAverage";
        public static final String COLUMN_RELEASE_DATE = "releaseDate";
        public static final String COLUMN_FAVORITE = "favorite";
    }
}
