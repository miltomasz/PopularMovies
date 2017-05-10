package com.plumya.popularmovies;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
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
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.plumya.popularmovies.adapter.ReviewsAdapter;
import com.plumya.popularmovies.adapter.TrailersAdapter;
import com.plumya.popularmovies.data.MoviesContract.MoviesEntry;
import com.plumya.popularmovies.loaders.ReviewsLoader;
import com.plumya.popularmovies.loaders.TrailersLoader;
import com.plumya.popularmovies.model.Movie;
import com.plumya.popularmovies.model.Review;
import com.plumya.popularmovies.model.Trailer;
import com.plumya.popularmovies.util.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by miltomasz on 15/04/17.
 */

public class MovieDetailActivity extends AppCompatActivity
        implements TrailersAdapter.TrailersAdapterOnClickHandler, LoaderManager.LoaderCallbacks<Cursor> {

    public static final String EXTRA_MOVIE = "movie";
    private static final String TAG = MovieDetailActivity.class.getSimpleName();
    private static final String MOVIE_ID_EXTRA = "movieId";

    private static final int TRAILERS_LOADER = 1234;
    private static final int REVIEWS_LOADER = 1235;
    private static final int MOVIES_LOADER = 1236;

    private Movie mMovie;
    private TextView mTitleTv;
    private ImageView mMovieImg;
    private ImageView mFavoriteImg;
    private TextView mReleaseDateTv;
    private TextView mUserRatingTv;
    private TextView mOverviewTv;
    private RecyclerView mTrailersRecyclerView;
    private RecyclerView mReviewsRecyclerView;
    private TrailersAdapter mTrailersAdapter;
    private ReviewsAdapter mReviewsAdapter;
    private LoaderManager.LoaderCallbacks<List<Trailer>> mTrailersCallback;
    private LoaderManager.LoaderCallbacks<List<Review>> mReviewsCallback;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        mTitleTv = (TextView) findViewById(R.id.title_tv);
        mMovieImg = (ImageView) findViewById(R.id.movie_image_img);
        mFavoriteImg = (ImageView) findViewById(R.id.favorite_image_img);
        mReleaseDateTv = (TextView) findViewById(R.id.release_date_tv);
        mUserRatingTv = (TextView) findViewById(R.id.user_rating_tv);
        mOverviewTv = (TextView) findViewById(R.id.overview_tv);
        mTrailersRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_trailers);
        mReviewsRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_reviews);

        TabHost host = (TabHost) findViewById(R.id.activityDetailTabHost);
        setUpTabHost(host);

        Intent intent = getIntent();

        if (intent != null && intent.hasExtra(EXTRA_MOVIE)) {
            final Movie movie = intent.getParcelableExtra(EXTRA_MOVIE);
            mMovie = movie;
            Uri imageUri = NetworkUtils.buildImageUri(
                    Movie.DETAIL_IMG_SIZE, movie.getPosterPath());
            mTitleTv.setText(movie.getOriginalTitle());
            Picasso.with(getApplicationContext())
                    .load(imageUri)
                    .placeholder(R.mipmap.ic_launcher)
                    .into(mMovieImg);
            mReleaseDateTv.setText(movie.getReleaseDate());
            mUserRatingTv.setText(movie.getVoteAverage());
            mOverviewTv.setText(movie.getOverview());

            setUpTrailersRecyclerView();
            setUpReviewsRecyclerView();

            Bundle bundle = new Bundle();
            bundle.putLong(MOVIE_ID_EXTRA, movie.getId());

            getSupportLoaderManager().initLoader(TRAILERS_LOADER, bundle, mTrailersCallback);
            getSupportLoaderManager().initLoader(REVIEWS_LOADER, bundle, mReviewsCallback);
            getSupportLoaderManager().initLoader(MOVIES_LOADER, bundle, this);
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
        mTrailersCallback = new TrailersLoader(this, mTrailersAdapter);
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
        mReviewsCallback = new ReviewsLoader(this, mReviewsAdapter);
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

    public void onFavoriteClick(View v) {
        if (v.getTag() != null && !"".equals(v.getTag())) {
            mFavoriteImg.setImageResource(R.drawable.ic_favorite_border_black_24dp);
            Uri uri = (Uri) v.getTag();
            getContentResolver().delete(uri, null, null);
            v.setTag(null);
        } else {
            mFavoriteImg.setImageResource(R.drawable.ic_favorite_black_24dp);
            ContentValues contentValues = new ContentValues();
            contentValues.put(MoviesEntry.COLUMN_WEB_ID, mMovie.getId());
            contentValues.put(MoviesEntry.COLUMN_OVERVIEW, mMovie.getOverview());
            contentValues.put(MoviesEntry.COLUMN_ORIGINAL_TITLE,mMovie.getOriginalTitle());
            contentValues.put(MoviesEntry.COLUMN_POSTER_PATH, mMovie.getPosterPath());
            contentValues.put(MoviesEntry.COLUMN_RELEASE_DATE, mMovie.getReleaseDate());
            contentValues.put(MoviesEntry.COLUMN_VOTE_AVERAGE, mMovie.getVoteAverage());
            Uri uri = getContentResolver().insert(MoviesEntry.CONTENT_URI, contentValues);
            if (uri == null) {
                Toast.makeText(getBaseContext(), "Saving movie as favorite failed",
                        Toast.LENGTH_LONG).show();
            } else {
                v.setTag(uri);
            }
        }
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

    @Override
    public Loader<Cursor> onCreateLoader(int id, final Bundle queryBundle) {
        return new AsyncTaskLoader<Cursor>(this) {
            Cursor cursor = null;

            @Override
            protected void onStartLoading() {
                if (cursor != null) {
                    deliverResult(cursor);
                } else {
                    forceLoad();
                }
            }

            @Override
            public Cursor loadInBackground() {
                try {
                    String movieId =String.valueOf(queryBundle.getLong(MOVIE_ID_EXTRA));
                    return getContentResolver().query(MoviesEntry.CONTENT_URI,
                            new String[] { MoviesEntry._ID, MoviesEntry.COLUMN_WEB_ID },
                            MoviesEntry.COLUMN_WEB_ID + "=?",
                            new String[] { movieId },
                            null);
                } catch (Exception e) {
                    Log.e(TAG, "Failed to asynchronously load data: " + e.getMessage());
                    return null;
                }
            }

            public void deliverResult(Cursor data) {
                cursor = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor != null && cursor.moveToFirst()) {
            mFavoriteImg.setImageResource(R.drawable.ic_favorite_black_24dp);
            int idIndex = cursor.getColumnIndex(MoviesEntry._ID);
            String id = String.valueOf(cursor.getInt(idIndex));
            Uri uri = MoviesEntry.CONTENT_URI.buildUpon().appendPath(id).build();
            mFavoriteImg.setTag(uri);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
}
