package com.plumya.popularmovies;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

public class MainActivity extends AppCompatActivity {

    private RecyclerView mMoviesRecyclerView;
    private PopularMoviesAdapter mMoviesAdapter;

    private TextView mErrorMessageDisplay;

    private ProgressBar mLoadingIndicator;

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

        mMoviesAdapter = new PopularMoviesAdapter(this);
        mMoviesRecyclerView.setAdapter(mMoviesAdapter);

        loadMovies();
    }

    private void showWeatherDataView() {
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mMoviesRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        mMoviesRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }
    
    private void loadMovies() {
        showWeatherDataView();
        new PopularMoviesTask().execute("popular");
    }

    public class PopularMoviesTask extends AsyncTask<String, Void, List<Movie>> {

        private final String TAG = PopularMoviesTask.class.getSimpleName();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

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
                showWeatherDataView();
                mMoviesAdapter.setMovieList(movieList);
            } else {
                showErrorMessage();
            }
        }
    }

}
