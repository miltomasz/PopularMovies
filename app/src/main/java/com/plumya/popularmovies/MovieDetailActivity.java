package com.plumya.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.plumya.popularmovies.model.Movie;
import com.plumya.popularmovies.util.NetworkUtils;
import com.squareup.picasso.Picasso;

/**
 * Created by miltomasz on 15/04/17.
 */

public class MovieDetailActivity extends AppCompatActivity {

    public static final String EXTRA_MOVIE = "movie";
    private TextView mTitleTv;
    private ImageView mMovieImg;
    private TextView mReleaseDateTv;
    private TextView mUserRatingTv;
    private TextView mOverviewTv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        mTitleTv = (TextView) findViewById(R.id.title_tv);
        mMovieImg = (ImageView) findViewById(R.id.movie_image_img) ;
        mReleaseDateTv = (TextView) findViewById(R.id.release_date_tv);
        mUserRatingTv = (TextView) findViewById(R.id.user_rating_tv);
        mOverviewTv = (TextView) findViewById(R.id.overview_tv);

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
        }
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
}
