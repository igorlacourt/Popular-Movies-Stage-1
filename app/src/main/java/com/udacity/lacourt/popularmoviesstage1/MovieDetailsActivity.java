package com.udacity.lacourt.popularmoviesstage1;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.udacity.lacourt.popularmoviesstage1.data.FavoritesContract;
//import com.udacity.lacourt.popularmoviesstage1.data.FavoritesDbHelper;
import com.udacity.lacourt.popularmoviesstage1.data.FavoritesDbHelper;
import com.udacity.lacourt.popularmoviesstage1.databinding.ActivityMovieDetailsBinding;
import com.udacity.lacourt.popularmoviesstage1.model.Result;
import com.udacity.lacourt.popularmoviesstage1.model.TrailersPostResponse;
import com.udacity.lacourt.popularmoviesstage1.utils.RetrofitClass;

import retrofit2.Call;
import retrofit2.Response;

import com.udacity.lacourt.popularmoviesstage1.data.FavoritesContract.FavoritesEntry;

import java.util.ArrayList;
import java.util.List;

public class MovieDetailsActivity extends AppCompatActivity {

    String sPosterPath;
    String movieId;
    String trailerKey = null;

    private ActivityMovieDetailsBinding bind;
    private Result movie;


    //TODO (1) Create List of favorites.
    //TODO (2) Make a list of the trailers and, right below, a list of the reviews.
    //TODO (3) Check the rotation issues (save state in Bundle).

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        bind = DataBindingUtil.setContentView(this, R.layout.activity_movie_details);

        bind.posterLoading.setVisibility(View.VISIBLE);

        movie = (Result) getIntent().getParcelableExtra("movie");

        //Initialize for in case of error
        sPosterPath = "http://www.techdisko.com/wp-content/uploads/2015/10/featured.png";
        movieId = String.valueOf(0000);
        String sOriginalTitle = "Nothing to show";
        String sOverview = "ERROR";
        Double dVoteAverage = 0.0;
        String sReleaseDate = "00-00-0000";

        if(movie.getOverview() != null) {
            sPosterPath = "http://image.tmdb.org/t/p/w780" + movie.getPosterPath();
            movieId = String.valueOf(movie.getId());
            sOriginalTitle = movie.getOriginalTitle();
            sOverview = movie.getOverview();
            dVoteAverage = movie.getVoteAverage();
            sReleaseDate = movie.getReleaseDate();
        }

        setFavoritesButtonColor();

        loadPosters();

        bind.originalTitle.setText(sOriginalTitle);
        bind.overview.setText("   " + sOverview);
        setVoteAvarage(dVoteAverage);
        bind.releaseDate.setText("Release date: " + sReleaseDate);

        RetrofitClass.setRetrofit();
        if(movieId != "0") {
            RetrofitClass.requestTrailer(movieId);
            getResultFromAPI();
        }

    }

//    private void onAddToFavoritesSelected(View view) {
//        FavoritesDbHelper.addToFavorites(movie);
//    }



    public void getResultFromAPI() {
        //Display data
        RetrofitClass.trailerCall.enqueue(new retrofit2.Callback<TrailersPostResponse>() {

            @Override
            public void onResponse(Call<TrailersPostResponse> call, Response<TrailersPostResponse> response) {

                if (response.isSuccessful()) {
                    trailerKey = response.body().getTrailers().get(0).getKey();

                } else {
                    Log.d("TAAG", "response code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<TrailersPostResponse> call, Throwable t) {
                t.printStackTrace();
                Log.d("TAAG", "onFailure!" );

                t.printStackTrace();

//                checkPoorConnection(t);


            }
        });
    }

    private void loadPosters() {

        Picasso.with(this).load(sPosterPath)
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

    public void setVoteAvarage(Double voteAvarage) {

        if(voteAvarage == 0.0001) {
            bind.voteAverage.setTextColor(getResources().getColor(R.color.releaseDateColor));
            bind.voteAverage.setText("Missing vote avarage.");
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

        bind.voteAverage.setTextColor(getResources().getColor(color));
        bind.voteAverage.setText("Vote avarage: " + String.valueOf(voteAvarage));
    }

    public void onShowFullScreenPoster (View view) {
        Intent intent = new Intent(this, FullScreenPosterActivity.class);
        intent.putExtra("poster_path", sPosterPath);
        startActivity(intent);
    }
    
    public void onTrailerSelected(View view) {
        if(trailerKey != null) {
//            Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + trailerKey));
            Intent webIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://www.youtube.com/watch?v=" + trailerKey));
            this.startActivity(webIntent);
//            try {
//                this.startActivity(appIntent);
//            } catch (ActivityNotFoundException ex) {
//                this.startActivity(webIntent);
//            }
        }
    }

    public void onAddToFavoritesSelected(View view) {

        if(FavoritesDbHelper.isMovieInFavorites(movieId)) {

            FavoritesDbHelper.removeFromFavorites(movieId);
            bind.addFavoriteButton.setBackgroundColor(Color.GRAY);
        } else {

            FavoritesDbHelper.addToFavorites(movie);
            bind.addFavoriteButton.setBackgroundColor(Color.YELLOW);

        }


    }

    public void setFavoritesButtonColor() {
        if(FavoritesDbHelper.isMovieInFavorites(movieId)) {

            bind.addFavoriteButton.setBackgroundColor(Color.YELLOW);
        } else {

            bind.addFavoriteButton.setBackgroundColor(Color.GRAY);

        }
    }
}
