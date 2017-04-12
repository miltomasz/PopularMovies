package com.plumya.popularmovies.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.plumya.popularmovies.R;
import com.plumya.popularmovies.model.Movie;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by miltomasz on 11/04/17.
 */

public class PopularMoviesAdapter extends RecyclerView.Adapter<PopularMoviesAdapter.ViewHolder> {

    private List<Movie> mMovieList;
    private Context mContext;

    public PopularMoviesAdapter(List<Movie> mMovieList, Context mContext) {
        this.mMovieList = mMovieList;
        this.mContext = mContext;
    }

    public void setmMovieList(List<Movie> mMovieList) {
        this.mMovieList = mMovieList;
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
        Picasso.with(mContext).load(movie.getPosterPath()).into(holder.mMovieImg);
    }

    @Override
    public int getItemCount() {
        if (mMovieList == null) return 0;
        return mMovieList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView mMovieImg;

        public ViewHolder(View itemView) {
            super(itemView);
            mMovieImg = (ImageView) itemView.findViewById(R.id.movie_img);
        }
    }
}
