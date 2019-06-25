package com.udacity.lacourt.popularmoviesstage1;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.udacity.lacourt.popularmoviesstage1.adapters.ReviewAdapter;
import com.udacity.lacourt.popularmoviesstage1.adapters.TrailerAdapter;
import com.udacity.lacourt.popularmoviesstage1.data.FavoritesContract.*;
import com.udacity.lacourt.popularmoviesstage1.databinding.ActivityMovieDetailsBinding;
import com.udacity.lacourt.popularmoviesstage1.model.Result;
import com.udacity.lacourt.popularmoviesstage1.model.Review;
import com.udacity.lacourt.popularmoviesstage1.model.ReviewsGetResponse;
import com.udacity.lacourt.popularmoviesstage1.model.Trailer;
import com.udacity.lacourt.popularmoviesstage1.model.TrailersPostResponse;
import com.udacity.lacourt.popularmoviesstage1.utils.RetrofitClass;

import retrofit2.Call;
import retrofit2.Response;

import com.udacity.lacourt.popularmoviesstage1.utils.UIUtils;

import java.util.ArrayList;

public class MovieDetailsActivity extends AppCompatActivity {

    private Toast mToast;

    private String mPosterPath;
    private String movieId;
    private String mOriginalTitle;
    private String mOverview;
    private Double mVoteAverage;
    private String mReleaseDate;
    private ArrayList<Trailer> trailers;
    private ArrayList<Review> reviews;
    private int numOfTrailers = 0;
    private int numOfReviews = 0;

    private String MOVIE_ID = "movieId";
    private String POSTER_PATH = "posterPath";
    private String ORIGINAL_TITLE = "originalTitle";
    private String OVERVIEW = "overview";
    private String VOTE_AVARAGE = "voteAverage";
    private String RELEASE_DATE = "releaseDate";
    private String TRAILERS = "trailers";
    private String REVIEWS = "reviews";
    private String NUM_OF_TRAILERS = "numOfTrailers";
    private String NUM_OF_REVIEWS = "numOfReviews";

    private ActivityMovieDetailsBinding bind;
    private Result movie;

    private TrailerAdapter trailerAdapter;
    private ReviewAdapter reviewAdapter;

    private TextView actionBarMovieTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        bind = DataBindingUtil.setContentView(this, R.layout.activity_movie_details);

        bind.posterLoading.setVisibility(View.VISIBLE);

        movie = getIntent().getParcelableExtra("movie");

        initializeUI(movie);

        setActionBarMovieTitle(movie.getTitle());

        setFavoritesButtonColor();

        loadPoster();

        bind.overview.setText("   " + mOverview);
        setVoteAvarage(mVoteAverage);
        bind.releaseDate.setText(mReleaseDate);

        trailers = new ArrayList<>();
        reviews = new ArrayList<>();

        trailerAdapter = new TrailerAdapter(this, 0, trailers);
        reviewAdapter = new ReviewAdapter(this, 0, reviews);

        loadDataFromSavedInstanceState(savedInstanceState);

    }

    private void loadDataFromSavedInstanceState(Bundle savedInstanceState){
        if (savedInstanceState != null) {
            //Get values from savedInstanceState
            movieId = savedInstanceState.getString(MOVIE_ID);
            mPosterPath = savedInstanceState.getString(POSTER_PATH);
            mOriginalTitle = savedInstanceState.getString(ORIGINAL_TITLE);
            mOverview = savedInstanceState.getString(OVERVIEW);
            mVoteAverage = savedInstanceState.getDouble(VOTE_AVARAGE);
            mReleaseDate = savedInstanceState.getString(RELEASE_DATE);
            trailers = savedInstanceState.getParcelableArrayList(TRAILERS);
            reviews = savedInstanceState.getParcelableArrayList(REVIEWS);
            numOfTrailers = savedInstanceState.getInt(NUM_OF_REVIEWS);

            //Set trailers to UI
            bind.videosHead.setText(
                    String.valueOf(numOfTrailers)
                            + " " + getResources().getText(R.string.videos_head));
            trailerAdapter.clear();
            trailerAdapter.addAll(trailers);
            bind.trailersList.setAdapter(trailerAdapter);
            UIUtils.setListViewHeightBasedOnItems(bind.trailersList);
            bind.trailersProgressBar.setVisibility(View.INVISIBLE);

            //Set reviews to UI
            reviewAdapter.clear();
            reviewAdapter.addAll(reviews);
            bind.reviewsHead.setText(numOfReviews + " " + getResources().getText(R.string.reviews_head));
            bind.reviewsList.setAdapter(reviewAdapter);
            UIUtils.setListViewHeightBasedOnItems(bind.reviewsList);
            bind.reviewsProgressBar.setVisibility(View.INVISIBLE);


        } else {

            RetrofitClass.setRetrofit();
            if(movieId != "0") {
                RetrofitClass.requestTrailer(movieId);
                getTrailersFromAPI();
                RetrofitClass.requestReview(movieId);
                getReviewsFromAPI();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(MOVIE_ID, movieId);
        outState.putString(POSTER_PATH, mPosterPath);
        outState.putString(ORIGINAL_TITLE, mOriginalTitle);
        outState.putString(OVERVIEW, mOverview);
        outState.putDouble(VOTE_AVARAGE, mVoteAverage);
        outState.putString(RELEASE_DATE, mReleaseDate);
        outState.putParcelableArrayList(TRAILERS, trailers);
        outState.putParcelableArrayList(REVIEWS, reviews);
        outState.putInt(NUM_OF_TRAILERS, numOfTrailers);
        outState.putInt(NUM_OF_REVIEWS, numOfReviews);

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);


    }

    private void initializeUI(Result movie) {
        //In case of error:
        mPosterPath = "http://www.techdisko.com/wp-content/uploads/2015/10/featured.png";
        movieId = String.valueOf(0000);
        mOriginalTitle = "Nothing to show";
        mOverview = "ERROR";
        mVoteAverage = 0.0;
        mReleaseDate = "0000";

        if(movie.getOverview() != null) {
            mPosterPath= "http://image.tmdb.org/t/p/w780" + movie.getPosterPath();
            movieId = String.valueOf(movie.getId());
            mOriginalTitle = movie.getOriginalTitle();
            mOverview = movie.getOverview();
            mVoteAverage = movie.getVoteAverage();
            mReleaseDate = movie.getReleaseDate().substring(0, 4);
        }
    }

    private void setActionBarMovieTitle(String movieTitle) {

        ActionBar actionbar = getSupportActionBar();

        actionBarMovieTitle = new TextView(MovieDetailsActivity.this);

        ViewGroup.LayoutParams layoutparams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        actionBarMovieTitle.setLayoutParams(layoutparams);
        actionBarMovieTitle.setText(movieTitle);
        actionBarMovieTitle.setTextColor(getResources().getColor(R.color.defaultFontColor));
        actionBarMovieTitle.setGravity(Gravity.CENTER);
        actionBarMovieTitle.setTextSize(getResources().getDimension(R.dimen.title));

        actionbar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionbar.setCustomView(actionBarMovieTitle);
    }

    private void getTrailersFromAPI() {
        //Display data
        RetrofitClass.trailerCall.enqueue(new retrofit2.Callback<TrailersPostResponse>() {

            @Override
            public void onResponse(Call<TrailersPostResponse> call, Response<TrailersPostResponse> response) {

                if (response.isSuccessful()) {

                    trailers.addAll(response.body().getTrailers());
                    numOfTrailers = response.body().getTrailers().size();

                    trailerAdapter.addAll(trailers);
                    bind.videosHead.setText(
                            String.valueOf(numOfTrailers)
                                    + " " + getResources().getText(R.string.videos_head));
                    bind.trailersList.setAdapter(trailerAdapter);
                    UIUtils.setListViewHeightBasedOnItems(bind.trailersList);
                    bind.trailersProgressBar.setVisibility(View.INVISIBLE);

                } else {
                    Log.d("TAAG", "response code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<TrailersPostResponse> call, Throwable t) {
                t.printStackTrace();
                Log.d("TAAG", "onFailure!" );

                t.printStackTrace();

            }
        });
    }

    private void getReviewsFromAPI() {
        //Display data
        RetrofitClass.reviewCall.enqueue(new retrofit2.Callback<ReviewsGetResponse>() {

            @Override
            public void onResponse(Call<ReviewsGetResponse> call, Response<ReviewsGetResponse> response) {

                if (response.isSuccessful()) {

                    reviews.addAll(response.body().getReviews());
                    numOfReviews = response.body().getTotalReviews();

                    reviewAdapter.addAll(reviews);
                    bind.reviewsHead.setText(numOfReviews + " " + getResources().getText(R.string.reviews_head));
                    bind.reviewsList.setAdapter(reviewAdapter);
                    UIUtils.setListViewHeightBasedOnItems(bind.reviewsList);
                    bind.reviewsProgressBar.setVisibility(View.INVISIBLE);

                } else {
                    Log.d("TAAG", "response code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ReviewsGetResponse> call, Throwable t) {
                t.printStackTrace();
                Log.d("TAAG", "onFailure!" );

                t.printStackTrace();

//                checkPoorConnection(t);


            }
        });
    }

    private void loadPoster() {

        Picasso.with(this).load(mPosterPath)
                .into(bind.detailPoster, new Callback() {
                    @Override
                    public void onSuccess() {
                        bind.posterLoading.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onError() {
                        Log.e("TAG", "Error loading image with Picasso!");
                    }
                });
    }

    private void setVoteAvarage(Double voteAvarage) {

        if(voteAvarage == 0.0001) {
            bind.voteAverage.setTextColor(getResources().getColor(R.color.releaseDateColor));
            bind.voteAverage.setText(getResources().getText(R.string.missing_vote_avarage));
            return;
        }

        int vote = (int) Math.floor(voteAvarage);
        int color = R.color.avg0_1;
        switch (vote) {
            case 9: color = R.color.avg8_10;
                break;
            case 8: color = R.color.avg8_10;
                break;
            case 7: color = R.color.avg7;
                break;
            case 6: color = R.color.avg4_6;
                break;
            case 5: color = R.color.avg4_6;
                break;
            case 4: color = R.color.avg4_6;
                break;
            case 3: color = R.color.avg2_3;
                break;
            case 2: color = R.color.avg2_3;
                break;
        }


        bind.voteAverageHead.setTextColor(getResources().getColor(color));
        bind.voteAverageHead.setText(getResources().getText(R.string.vote_avarage_head));
        bind.voteAverage.setTextColor(getResources().getColor(color));
        bind.voteAverage.setText(String.valueOf(voteAvarage));
    }

    public void onShowFullScreenPoster (View view) {
        Intent intent = new Intent(this, FullScreenPosterActivity.class);
        intent.putExtra("poster_path", mPosterPath);
        startActivity(intent);
    }

    public void onAddToFavoritesSelected(View view) {

        if(!isContentInFavorites()) {

            addFavorite();

        } else {

            deleteFavorite();

        }

    }

    private void deleteFavorite() {

        Uri uri = FavoritesEntry.CONTENT_URI;
        uri = uri.buildUpon().appendPath(movieId).build();

        int favoriteDeleted = getContentResolver().delete(uri, null, null);

        if(favoriteDeleted != 0){

            bind.addFavoriteButton.setImageResource(R.mipmap.favorite_unselected);

            if(mToast != null) {
                mToast.cancel();
            }

            Toast.makeText(this, getResources().getText(R.string.toast_favorite_removed), Toast.LENGTH_SHORT).show();
        }
    }

    private void addFavorite() {

        ContentValues contentValues = new ContentValues();
        // Put movie values into the ContentValues
        contentValues.put(FavoritesEntry.COLUMN_MOVIE_ID, movie.getId());
        contentValues.put(FavoritesEntry.COLUMN_TITLE, movie.getTitle());
        contentValues.put(FavoritesEntry.COLUMN_RELEASE_DATE, movie.getReleaseDate());
        contentValues.put(FavoritesEntry.COLUMN_OVERVIEW, movie.getOverview());
        contentValues.put(FavoritesEntry.COLUMN_POSTER_PATH, movie.getPosterPath());
        contentValues.put(FavoritesEntry.COLUMN_ORIGINAL_TITLE, movie.getOriginalTitle());
        contentValues.put(FavoritesEntry.COLUMN_VOTE_AVARAGE, movie.getVoteAverage());
        contentValues.put(FavoritesEntry.COLUMN_ORIGINAL_LANGUAGE, movie.getOriginalLanguage());
        contentValues.put(FavoritesEntry.COLUMN_BACKDROP_PATH, movie.getBackdropPath());
        contentValues.put(FavoritesEntry.COLUMN_VOTE_COUNT, movie.getVoteCount());

        // Insert the content values via a ContentResolver
        Uri uri = getContentResolver().insert(FavoritesEntry.CONTENT_URI, contentValues);

        bind.addFavoriteButton.setImageResource(R.mipmap.favorite_selected);

        if(mToast != null) {
            mToast.cancel();
        }

        Toast.makeText(this, getResources().getText(R.string.toast_favorite_added), Toast.LENGTH_SHORT).show();
    }

    private void setFavoritesButtonColor() {

        if(isContentInFavorites()) {
            bind.addFavoriteButton.setImageResource(R.mipmap.favorite_selected);
        } else {
            bind.addFavoriteButton.setImageResource(R.mipmap.favorite_unselected);
        }
    }

    private boolean isContentInFavorites() {
        int count = queryCursor().getCount();

        return count > 0;

    }

    private Cursor queryCursor(){

        try {
            Uri uri = FavoritesEntry.CONTENT_URI;
            uri = uri.buildUpon().appendPath(movieId).build();

            return getContentResolver().query(uri,
                    null,
                    null,
                    null,
                    null);

        } catch (Exception e) {
            Log.e("MovieDetailsActivity", "Failed to asynchronously load data.");
            e.printStackTrace();
            return null;
        }
    }

}
