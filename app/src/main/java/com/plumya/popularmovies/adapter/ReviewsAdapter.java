package com.plumya.popularmovies.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.plumya.popularmovies.R;
import com.plumya.popularmovies.model.Review;

import java.util.List;

/**
 * Created by miltomasz on 05/05/17.
 */

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ViewHolder> {

    private List<Review> mReviewList;

    public void setReviewList(List<Review> reviewList) {
        mReviewList = reviewList;
        notifyDataSetChanged();
    }

    @Override
    public ReviewsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.reviews_list_item, parent, false);
        return new ReviewsAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ReviewsAdapter.ViewHolder holder, int position) {
        Review review = mReviewList.get(position);
        holder.mReviewNameTxv.setText(review.getAuthor());
        holder.mReviewContentTxv.setText(review.getContent());
    }

    @Override
    public int getItemCount() {
        if (mReviewList == null) return 0;
        return mReviewList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mReviewNameTxv;
        private TextView mReviewContentTxv;

        public ViewHolder(View itemView) {
            super(itemView);
            mReviewNameTxv = (TextView) itemView.findViewById(R.id.reviewer_name);
            mReviewContentTxv = (TextView) itemView.findViewById(R.id.review_content);
        }
    }
}
