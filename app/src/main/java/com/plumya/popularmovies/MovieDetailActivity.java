package com.plumya.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.plumya.popularmovies.adapter.ReviewsAdapter;
import com.plumya.popularmovies.adapter.TrailersAdapter;
import com.plumya.popularmovies.model.Movie;
import com.plumya.popularmovies.model.Review;
import com.plumya.popularmovies.model.Trailer;
import com.plumya.popularmovies.util.JsonUtils;
import com.plumya.popularmovies.util.NetworkUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.List;

/**
 * Created by miltomasz on 15/04/17.
 */

public class MovieDetailActivity extends AppCompatActivity
        implements TrailersAdapter.TrailersAdapterOnClickHandler {

    public static final String EXTRA_MOVIE = "movie";
    private static final String TAG = MovieDetailActivity.class.getSimpleName();
    private static final String MOVIE_ID_EXTRA = "movieId";
    private static final String VIDEOS_PATH = "videos";
    private static final String REVIEWS_PATH = "reviews";
    private static final int TRAILERS_LOADER = 1234;
    private static final int REVIEWS_LOADER = 1235;

    private TextView mTitleTv;
    private ImageView mMovieImg;
    private TextView mReleaseDateTv;
    private TextView mUserRatingTv;
    private TextView mOverviewTv;
    private RecyclerView mTrailersRecyclerView;
    private RecyclerView mReviewsRecyclerView;
    private TrailersAdapter mTrailersAdapter;
    private ReviewsAdapter mReviewsAdapter;
    private List<Trailer> mTrailers;
    private List<Review> mReviews;

    private LoaderManager.LoaderCallbacks<List<Trailer>> trailersCallback = new LoaderManager.LoaderCallbacks<List<Trailer>>() {
        @Override
        public Loader<List<Trailer>> onCreateLoader(int id, final Bundle bundle) {
            return new AsyncTaskLoader<List<Trailer>>(MovieDetailActivity.this) {
                @Override
                protected void onStartLoading() {
                    super.onStartLoading();
                    if (bundle == null) {
                        return;
                    }
                    if (mTrailers != null) {
                        deliverResult(mTrailers);
                    } else {
                        forceLoad();
                    }
                }

                @Override
                public List<Trailer> loadInBackground() {
                    long movieId = bundle.getLong(MOVIE_ID_EXTRA);
                    URL trailersUrl = NetworkUtils.buildUrl(
                            NetworkUtils.EndpointsCreator.movie(movieId, VIDEOS_PATH));
                    try {
                        String trailersJson = NetworkUtils.getResponseFromHttpUrl(trailersUrl);
                        return JsonUtils.Trailers.parse(trailersJson);
                    } catch (IOException e) {
                        Log.d(TAG, "Exception occurred while requesting trailers: " + e.getMessage());
                    } catch (JSONException e) {
                        Log.d(TAG, "Exception occurred while parsing trailers json: " + e.getMessage());
                    }
                    return null;
                }

                @Override
                public void deliverResult(List<Trailer> data) {
                    mTrailers = data;
                    super.deliverResult(data);
                }
            };
        }

        @Override
        public void onLoadFinished(Loader<List<Trailer>> loader, List<Trailer> data) {
            if (data != null) {
                mTrailersAdapter.setTrailerList(data);
            }
        }

        @Override
        public void onLoaderReset(Loader<List<Trailer>> loader) {
        }
    };

    private LoaderManager.LoaderCallbacks<List<Review>> reviewsCallback = new LoaderManager.LoaderCallbacks<List<Review>>() {
        @Override
        public Loader<List<Review>> onCreateLoader(int id, final Bundle bundle) {
            return new AsyncTaskLoader<List<Review>>(MovieDetailActivity.this) {
                @Override
                protected void onStartLoading() {
                    super.onStartLoading();
                    if (bundle == null) {
                        return;
                    }
                    if (mReviews != null) {
                        deliverResult(mReviews);
                    } else {
                        forceLoad();
                    }
                }

                @Override
                public List<Review> loadInBackground() {
                    long movieId = bundle.getLong(MOVIE_ID_EXTRA);
                    URL reviewsUrl = NetworkUtils.buildUrl(
                            NetworkUtils.EndpointsCreator.movie(movieId, REVIEWS_PATH));
                    try {
                        String trailersJson = NetworkUtils.getResponseFromHttpUrl(reviewsUrl);
                        return JsonUtils.Reviews.parse(trailersJson);
                    } catch (IOException e) {
                        Log.d(TAG, "Exception occurred while requesting reviews: " + e.getMessage());
                    } catch (JSONException e) {
                        Log.d(TAG, "Exception occurred while parsing reviews json: " + e.getMessage());
                    }
                    return null;
                }

                @Override
                public void deliverResult(List<Review> data) {
                    mReviews = data;
                    super.deliverResult(data);
                }
            };
        }

        @Override
        public void onLoadFinished(Loader<List<Review>> loader, List<Review> data) {
            if (data != null) {
                mReviewsAdapter.setReviewList(data);
            }
        }

        @Override
        public void onLoaderReset(Loader<List<Review>> loader) {
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        mTitleTv = (TextView) findViewById(R.id.title_tv);
        mMovieImg = (ImageView) findViewById(R.id.movie_image_img) ;
        mReleaseDateTv = (TextView) findViewById(R.id.release_date_tv);
        mUserRatingTv = (TextView) findViewById(R.id.user_rating_tv);
        mOverviewTv = (TextView) findViewById(R.id.overview_tv);
        mTrailersRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_trailers);
        mReviewsRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_reviews);

        TabHost host = (TabHost) findViewById(R.id.activityDetailTabHost);
        setUpTabHost(host);

        Intent intent = getIntent();

        if (intent != null && intent.hasExtra(EXTRA_MOVIE)) {
            Movie movie = intent.getParcelableExtra(EXTRA_MOVIE);
            Uri imageUri = NetworkUtils.buildImageUri(
                    Movie.DETAIL_IMG_SIZE, movie.getPosterPath());
            mTitleTv.setText(movie.getOriginalTitle());
            Picasso.with(getApplicationContext())
                    .load(imageUri)
                    .into(mMovieImg);
            mReleaseDateTv.setText(movie.getReleaseDate());
            mUserRatingTv.setText(movie.getVoteAverage());
            mOverviewTv.setText(movie.getOverview());

            setUpTrailersRecyclerView();
            setUpReviewsRecyclerView();

            Bundle bundle = new Bundle();
            bundle.putLong(MOVIE_ID_EXTRA, movie.getId());

            getSupportLoaderManager().initLoader(TRAILERS_LOADER, bundle, trailersCallback);
            getSupportLoaderManager().initLoader(REVIEWS_LOADER, bundle, reviewsCallback);
        }
    }

    private void setUpTrailersRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mTrailersRecyclerView.setLayoutManager(layoutManager);
        mTrailersRecyclerView.setHasFixedSize(true);
        mTrailersRecyclerView.setItemViewCacheSize(20);
        mTrailersRecyclerView.setDrawingCacheEnabled(true);

        mTrailersAdapter = new TrailersAdapter(this);
        mTrailersRecyclerView.setNestedScrollingEnabled(false);
        mTrailersRecyclerView.setAdapter(mTrailersAdapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mTrailersRecyclerView.getContext(),
                layoutManager.getOrientation());
        mTrailersRecyclerView.addItemDecoration(dividerItemDecoration);
    }

    private void setUpReviewsRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mReviewsRecyclerView.setLayoutManager(layoutManager);
        mReviewsRecyclerView.setHasFixedSize(true);
        mReviewsRecyclerView.setItemViewCacheSize(20);
        mReviewsRecyclerView.setDrawingCacheEnabled(true);

        mReviewsAdapter = new ReviewsAdapter();
        mReviewsRecyclerView.setNestedScrollingEnabled(false);
        mReviewsRecyclerView.setAdapter(mReviewsAdapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mReviewsRecyclerView.getContext(),
                layoutManager.getOrientation());
        mReviewsRecyclerView.addItemDecoration(dividerItemDecoration);
    }

    private void setUpTabHost(TabHost host) {
        host.setup();

        TabHost.TabSpec specTrailers = host.newTabSpec("Trailers");
        specTrailers.setContent(R.id.tab_trailers);
        specTrailers.setIndicator("Trailers");
        host.addTab(specTrailers);

        TabHost.TabSpec specReviews = host.newTabSpec("Reviews");
        specReviews.setContent(R.id.tab_reviews);
        specReviews.setIndicator("Reviews");
        host.addTab(specReviews);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(Trailer trailer) {
        String videoId = trailer.getKey();
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + videoId));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(this, "Please install YouTube application", Toast.LENGTH_LONG).show();
        }
    }
}
