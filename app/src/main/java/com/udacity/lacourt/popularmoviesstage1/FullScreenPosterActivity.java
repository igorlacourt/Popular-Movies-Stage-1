package com.udacity.lacourt.popularmoviesstage1;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class FullScreenPosterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_poster);

        loadFullScreenPoster();

    }

    private void loadFullScreenPoster() {

        ImageView fullScreenPoster = findViewById(R.id.full_screen_poster);

        final ProgressBar posterLoading = findViewById(R.id.full_poster_progress_bar);
        posterLoading.setVisibility(View.VISIBLE);

        String sPosterPath = getIntent().getStringExtra("poster_path");

        Picasso.with(this).load(sPosterPath)
                .into(fullScreenPoster, new Callback() {
                    @Override
                    public void onSuccess() {
                        posterLoading.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onError() {
                        Log.e("TAG", "Error loading image with Picasso!");
                    }
                });
    }
}
