package com.plumya.popularmovies.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.plumya.popularmovies.R;
import com.plumya.popularmovies.listeners.PopularMoviesAdapterOnClickHandler;
import com.plumya.popularmovies.model.Movie;
import com.plumya.popularmovies.util.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by miltomasz on 11/04/17.
 */

public class PopularMoviesAdapter extends RecyclerView.Adapter<PopularMoviesAdapter.ViewHolder> {

    private List<Movie> mMovieList;
    private Context mContext;
    private PopularMoviesAdapterOnClickHandler mClickHandler;

    public PopularMoviesAdapter(Context context, PopularMoviesAdapterOnClickHandler clickHandler) {
        this.mContext = context;
        this.mClickHandler = clickHandler;
    }

    public void setMovieList(List<Movie> movieList) {
        this.mMovieList = movieList;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.movies_list_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Movie movie = mMovieList.get(position);
        Uri imageUri = NetworkUtils.buildImageUri(
                Movie.DEFAULT_IMG_SIZE, movie.getPosterPath());
        Picasso.with(mContext)
                .load(imageUri)
                .placeholder(R.mipmap.ic_launcher)
                .into(holder.mMovieImg);
    }

    @Override
    public int getItemCount() {
        if (mMovieList == null) return 0;
        return mMovieList.size();
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
            Movie movie = mMovieList.get(position);
            mClickHandler.onClick(movie);
        }
    }
}
