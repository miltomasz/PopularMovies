package com.plumya.popularmovies;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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

import com.plumya.popularmovies.adapter.PopularMoviesAdapter;
import com.plumya.popularmovies.model.Movie;
import com.plumya.popularmovies.util.MovieJsonUtils;
import com.plumya.popularmovies.util.NetworkUtils;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements PopularMoviesAdapter.PopularMoviesAdapterOnClickHandler{

    public static final String POPULAR_OPT = "popular";
    public static final String TOP_RATED_OPT = "topRated";
    public static final String SORTING_OPTION = "sortingOption";

    private RecyclerView mMoviesRecyclerView;
    private PopularMoviesAdapter mMoviesAdapter;
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
        mMoviesRecyclerView.setAdapter(mMoviesAdapter);

        if (savedInstanceState != null) {
            mSortingOption = savedInstanceState.getString(SORTING_OPTION);
        }
        loadMovies(mSortingOption);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(SORTING_OPTION, mSortingOption);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.movies, menu);
        return true;
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

    /**
     * Gets movie list from back-end in background thread
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
            URL moviesUrl = NetworkUtils.buildUrl(NetworkUtils.path(selectedOption));
            try {
                String moviesJson = NetworkUtils.getResponseFromHttpUrl(moviesUrl);
                return MovieJsonUtils.parse(moviesJson);
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
                mMoviesAdapter.setMovieList(movieList);
            } else {
                showErrorMessage();
            }
        }
    }
}
