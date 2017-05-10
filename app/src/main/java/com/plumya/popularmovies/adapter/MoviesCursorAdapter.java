package com.plumya.popularmovies.adapter;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.plumya.popularmovies.R;
import com.plumya.popularmovies.data.MoviesContract.MoviesEntry;
import com.plumya.popularmovies.listeners.PopularMoviesAdapterOnClickHandler;
import com.plumya.popularmovies.model.Movie;
import com.plumya.popularmovies.util.NetworkUtils;
import com.squareup.picasso.Picasso;

/**
 * Created by miltomasz on 05/05/17.
 */

public class MoviesCursorAdapter extends RecyclerView.Adapter<MoviesCursorAdapter.ViewHolder> {

    private Cursor mCursor;
    private Context mContext;
    private PopularMoviesAdapterOnClickHandler mClickHandler;

    public MoviesCursorAdapter(Context mContext, PopularMoviesAdapterOnClickHandler clickHandler) {
        this.mContext = mContext;
        this.mClickHandler = clickHandler;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.movies_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        int idIndex = mCursor.getColumnIndex(MoviesEntry._ID);
        int posterPathIndex = mCursor.getColumnIndex(MoviesEntry.COLUMN_POSTER_PATH);

        mCursor.moveToPosition(position);

        final int id = mCursor.getInt(idIndex);
        String posterPath = mCursor.getString(posterPathIndex);

        Uri imageUri = NetworkUtils.buildImageUri(Movie.DEFAULT_IMG_SIZE, posterPath);
        Picasso.with(mContext)
                .load(imageUri)
                .into(holder.mMovieImg);
    }

    @Override
    public int getItemCount() {
        if (mCursor == null) {
            return 0;
        }
        return mCursor.getCount();
    }

    public Cursor swapCursor(Cursor c) {
        if (mCursor == c) {
            return null;
        }
        Cursor temp = mCursor;
        this.mCursor = c;
        if (c != null) {
            this.notifyDataSetChanged();
        }
        return temp;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView mMovieImg;

        public ViewHolder(View itemView) {
            super(itemView);
            mMovieImg = (ImageView) itemView.findViewById(R.id.movie_img);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (mCursor == null) return;
            boolean positionOk = mCursor.moveToPosition(position);
            if (positionOk) {
                String id = mCursor.getString(
                        mCursor.getColumnIndex(MoviesEntry.COLUMN_WEB_ID));
                String originalTitle = mCursor.getString(
                        mCursor.getColumnIndex(MoviesEntry.COLUMN_ORIGINAL_TITLE));
                String posterPath = mCursor.getString(
                        mCursor.getColumnIndex(MoviesEntry.COLUMN_POSTER_PATH));
                String overview = mCursor.getString
                        (mCursor.getColumnIndex(MoviesEntry.COLUMN_OVERVIEW));
                String voteAverage = mCursor.getString(
                        mCursor.getColumnIndex(MoviesEntry.COLUMN_VOTE_AVERAGE));
                String releaseDate = mCursor.getString
                        (mCursor.getColumnIndex(MoviesEntry.COLUMN_RELEASE_DATE));
                Movie movie = new Movie(Long.parseLong(id), originalTitle, posterPath,
                        overview, voteAverage, releaseDate);
                mClickHandler.onClick(movie);
            }
        }
    }
}
