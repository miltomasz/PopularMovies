package com.plumya.popularmovies.loaders;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.util.Log;

import com.plumya.popularmovies.adapter.ReviewsAdapter;
import com.plumya.popularmovies.model.Review;
import com.plumya.popularmovies.util.JsonUtils;
import com.plumya.popularmovies.util.NetworkUtils;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.List;

/**
 * Created by miltomasz on 10/05/17.
 */

public class ReviewsLoader implements LoaderManager.LoaderCallbacks<List<Review>> {

    private static final String TAG = ReviewsLoader.class.getSimpleName() ;
    private static final String MOVIE_ID_EXTRA = "movieId";
    private static final String REVIEWS_PATH = "reviews";

    private Context mContext;
    private ReviewsAdapter mReviewsAdapter;
    private List<Review> mReviews;

    public ReviewsLoader(Context context, ReviewsAdapter reviewsAdapter) {
        this.mContext = context;
        this.mReviewsAdapter = reviewsAdapter;
    }

    @Override
    public Loader<List<Review>> onCreateLoader(int id, final Bundle bundle) {
        return new AsyncTaskLoader<List<Review>>(mContext) {
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
}
