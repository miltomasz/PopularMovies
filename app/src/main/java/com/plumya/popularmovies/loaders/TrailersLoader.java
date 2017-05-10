package com.plumya.popularmovies.loaders;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.util.Log;

import com.plumya.popularmovies.adapter.TrailersAdapter;
import com.plumya.popularmovies.model.Trailer;
import com.plumya.popularmovies.util.JsonUtils;
import com.plumya.popularmovies.util.NetworkUtils;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.List;

/**
 * Created by miltomasz on 10/05/17.
 */

public class TrailersLoader implements LoaderManager.LoaderCallbacks<List<Trailer>> {

    private static final String TAG = TrailersLoader.class.getSimpleName();
    private static final String VIDEOS_PATH = "videos";
    private static final String MOVIE_ID_EXTRA = "movieId";

    private Context mContext;
    private TrailersAdapter mTrailersAdapter;
    private List<Trailer> mTrailers;

    public TrailersLoader(Context mContext, TrailersAdapter trailersAdapter)  {
        this.mContext = mContext;
        this.mTrailersAdapter = trailersAdapter;
    }

    @Override
    public Loader<List<Trailer>> onCreateLoader(int id, final Bundle bundle) {
        return new AsyncTaskLoader<List<Trailer>>(mContext) {
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
}