package com.udacity.lacourt.popularmoviesstage1;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.udacity.lacourt.popularmoviesstage1.databinding.ActivityMovieDetailsBinding;

public class MovieDetailsActivity extends AppCompatActivity {

    String sPosterPath;

    private ActivityMovieDetailsBinding bind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        bind = DataBindingUtil.setContentView(this, R.layout.activity_movie_details);

        bind.posterLoading.setVisibility(View.VISIBLE);

        sPosterPath = getIntent().getStringExtra("poster");
        String sOriginalTitle = getIntent().getStringExtra("original_title");
        String sOverview = getIntent().getStringExtra("overview");
        Double dVoteAverage = getIntent().getDoubleExtra("vote_average", 0.0001);
        String sReleaseDate = getIntent().getStringExtra("release_date");


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


        bind.originalTitle.setText(sOriginalTitle);
        bind.overview.setText("   " + sOverview);
        setVoteAvarage(dVoteAverage);
        bind.releaseDate.setText("Release date: " + sReleaseDate);


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
}
