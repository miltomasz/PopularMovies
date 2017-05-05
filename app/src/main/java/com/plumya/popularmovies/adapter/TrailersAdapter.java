package com.plumya.popularmovies.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.plumya.popularmovies.R;
import com.plumya.popularmovies.model.Trailer;

import java.util.List;

/**
 * Created by miltomasz on 04/05/17.
 */

public class TrailersAdapter extends RecyclerView.Adapter<TrailersAdapter.ViewHolder> {

    private List<Trailer> mTrailerList;
    private TrailersAdapterOnClickHandler mClickHandler;

    public TrailersAdapter(TrailersAdapterOnClickHandler handler) {
        mClickHandler = handler;
    }

    public void setTrailerList(List<Trailer> trailerList) {
        mTrailerList = trailerList;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.trailers_list_item, parent, false);
        return new TrailersAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Trailer trailer = mTrailerList.get(position);
        holder.mTrailerNameTxv.setText(trailer.getName());
        holder.mTrailermageView.setImageResource(R.drawable.ic_youtube_play);
    }

    @Override
    public int getItemCount() {
        if (mTrailerList == null) return 0;
        return mTrailerList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView mTrailermageView;
        private TextView mTrailerNameTxv;

        public ViewHolder(View itemView) {
            super(itemView);
            mTrailermageView = (ImageView) itemView.findViewById(R.id.trailer_img);
            mTrailerNameTxv = (TextView) itemView.findViewById(R.id.trailer_name);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Trailer trailer = mTrailerList.get(position);
            mClickHandler.onClick(trailer);
        }
    }

    public interface TrailersAdapterOnClickHandler {
        void onClick(Trailer trailer);
    }
}
