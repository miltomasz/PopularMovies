package com.plumya.popularmovies;

import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.plumya.popularmovies.adapter.MoviesCursorAdapter;
import com.plumya.popularmovies.adapter.PopularMoviesAdapter;
import com.plumya.popularmovies.data.MoviesContract;
import com.plumya.popularmovies.listeners.PopularMoviesAdapterOnClickHandler;
import com.plumya.popularmovies.model.Movie;
import com.plumya.popularmovies.util.JsonUtils;
import com.plumya.popularmovies.util.NetworkUtils;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements PopularMoviesAdapterOnClickHandler, LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = MainActivity.class.getSimpleName();
    public static final String POPULAR_OPT = "popular";
    public static final String TOP_RATED_OPT = "topRated";
    public static final String FAVORITE_OPT = "favorite";
    public static final String SORTING_OPTION = "sortingOption";
    public static final int MOVIES_LOADER_ID = 0;
    public static final String MOVIES_LIST_POSITION = "moviesListPosition";

    private RecyclerView mMoviesRecyclerView;
    private PopularMoviesAdapter mMoviesAdapter;
    private MoviesCursorAdapter mMoviesCursorAdapter;
    private Parcelable mLayoutManagerSavedState;
    private TextView mErrorMessageDisplay;
    private ProgressBar mLoadingIndicator;
    private String mSortingOption = POPULAR_OPT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMoviesRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_movies);
        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message_display);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);

        mMoviesRecyclerView.setLayoutManager(layoutManager);
        mMoviesRecyclerView.setHasFixedSize(true);
        mMoviesRecyclerView.setItemViewCacheSize(20);
        mMoviesRecyclerView.setDrawingCacheEnabled(true);

        mMoviesAdapter = new PopularMoviesAdapter(getApplicationContext(), this);
        mMoviesCursorAdapter = new MoviesCursorAdapter(getApplicationContext(), this);
        // initialize activity with moviesAdapter
        mMoviesRecyclerView.setAdapter(mMoviesAdapter);
        mMoviesRecyclerView.setSaveEnabled(true);

        if (savedInstanceState != null) {
            mSortingOption = savedInstanceState.getString(SORTING_OPTION);
        }
        if (mSortingOption.equals(FAVORITE_OPT)) {
            // Load movies from remote url
            loadFavoriteMovies();
        } else {
            // load movies from local database
            loadMovies(mSortingOption);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(SORTING_OPTION, mSortingOption);
        outState.putParcelable(MOVIES_LIST_POSITION,
                mMoviesRecyclerView.getLayoutManager().onSaveInstanceState());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            mLayoutManagerSavedState = savedInstanceState.getParcelable(MOVIES_LIST_POSITION);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    private void showMoviesView() {
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mMoviesRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        mMoviesRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }
    
    private void loadMovies(String sortOption) {
        setSortingOption(sortOption);
        showMoviesView();
        new PopularMoviesTask().execute(sortOption);
    }

    private void loadFavoriteMovies() {
        setSortingOption(FAVORITE_OPT);
        mMoviesRecyclerView.setAdapter(mMoviesCursorAdapter);
        getSupportLoaderManager().restartLoader(MOVIES_LOADER_ID, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.movies, menu);
        return true;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        getSupportLoaderManager().restartLoader(MOVIES_LOADER_ID, null, this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_popular : {
                loadMovies(POPULAR_OPT);
                break;
            }
            case R.id.action_top_rated : {
                loadMovies(TOP_RATED_OPT);
                break;
            }
            case R.id.action_favorite : {
                loadFavoriteMovies();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(Movie movie) {
        Intent intentToStartDetailActivity = new Intent(this, MovieDetailActivity.class);
        intentToStartDetailActivity.putExtra(MovieDetailActivity.EXTRA_MOVIE, movie);
        startActivity(intentToStartDetailActivity);
    }

    public void setSortingOption(String sortingOption) {
        mSortingOption = sortingOption;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<Cursor>(this) {
            Cursor mTaskData = null;

            @Override
            protected void onStartLoading() {
                if (mTaskData != null) {
                    deliverResult(mTaskData);
                } else {
                    forceLoad();
                }
            }

            @Override
            public Cursor loadInBackground() {
                try {
                    return getContentResolver().query(MoviesContract.MoviesEntry.CONTENT_URI,
                            null,
                            null,
                            null,
                            MoviesContract.MoviesEntry.COLUMN_ORIGINAL_TITLE);
                } catch (Exception e) {
                    Log.e(TAG, "Failed to asynchronously load data: " + e.getMessage());
                    return null;
                }
            }

            public void deliverResult(Cursor data) {
                mTaskData = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mMoviesCursorAdapter.swapCursor(data);
        mMoviesRecyclerView.getLayoutManager()
                .onRestoreInstanceState(mLayoutManagerSavedState);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMoviesCursorAdapter.swapCursor(null);
    }

    /**
     * Gets movies from network using background thread
     */
    public class PopularMoviesTask extends AsyncTask<String, Void, List<Movie>> {

        private final String TAG = PopularMoviesTask.class.getSimpleName();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        /**
         * Process task in background
         * @param params the first item is a sort option selected by user
         * @return fetched movies as a {@link List}
         */
        @Override
        protected List<Movie> doInBackground(String... params) {
            String selectedOption = params[0];
            URL moviesUrl = NetworkUtils.buildUrl(
                    NetworkUtils.EndpointsCreator.movie(selectedOption)
            );
            try {
                String moviesJson = NetworkUtils.getResponseFromHttpUrl(moviesUrl);
                return JsonUtils.Movies.parse(moviesJson);
            } catch (IOException e) {
                Log.d(TAG, "Exception occurred while requesting movies: " + e.getMessage());
            } catch (JSONException e) {
                Log.d(TAG, "Exception occurred while parsing movies json: " + e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<Movie> movieList) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (movieList != null) {
                showMoviesView();
                mMoviesRecyclerView.setAdapter(mMoviesAdapter);
                mMoviesAdapter.setMovieList(movieList);
                mMoviesRecyclerView.getLayoutManager()
                        .onRestoreInstanceState(mLayoutManagerSavedState);
            } else {
                showErrorMessage();
            }
        }
    }
}
