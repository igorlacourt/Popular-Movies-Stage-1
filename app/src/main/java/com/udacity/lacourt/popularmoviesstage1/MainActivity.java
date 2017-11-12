package com.udacity.lacourt.popularmoviesstage1;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.udacity.lacourt.popularmoviesstage1.data.Preferences;
import com.udacity.lacourt.popularmoviesstage1.databinding.ActivityMainBinding;
import com.udacity.lacourt.popularmoviesstage1.model.PostResponse;
import com.udacity.lacourt.popularmoviesstage1.model.Result;
import com.udacity.lacourt.popularmoviesstage1.utils.AppStatus;
import com.udacity.lacourt.popularmoviesstage1.utils.InfiniteScrollListener;

import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieOnClickHandler {

    private static final String BASE_URL = "https://api.themoviedb.org/3/";

    private Retrofit mRetrofit;

    private MovieAdapter mAdapter;
    private GridLayoutManager layoutManager;

    private int page = 1;
    private MoviesApiService client;
    private Call<PostResponse> call;

    private final int PAGE_ITEMS = 20;

    private Preferences mPreferences;
    private Toast mToast;

    private ActivityMainBinding bind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = DataBindingUtil.setContentView(this, R.layout.activity_main);

        mToast = new Toast(this);
        mPreferences = new Preferences(this);

        bind.newItemsProgressBar.setVisibility(View.INVISIBLE);
        bind.progressBar.setVisibility(View.VISIBLE);
        bind.errorLayout.setVisibility(View.INVISIBLE);

        mRetrofit = null;

        setGridLayoutManager();

        bind.recyclerView.setLayoutManager(layoutManager);
        bind.recyclerView.setHasFixedSize(true);

        mAdapter = new MovieAdapter(this, this);

        bind.recyclerView.setAdapter(mAdapter);

        loadDataOnScreen();

    }

    private void loadDataOnScreen() {

        if(AppStatus.getInstance(this).isOnline()) {

            setRetrofit();

            bind.recyclerView.addOnScrollListener(createInfiniteScrollListener());
            fetchDataFromAPI(page);

        } else {
            showNoConnectionMessage();
        }
    }

    private void setGridLayoutManager() {
        if(this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            layoutManager = new GridLayoutManager(this, 2);

        } else {
            layoutManager = new GridLayoutManager(this, 4);
        }
    }

    @NonNull
    private InfiniteScrollListener createInfiniteScrollListener() {


        return new InfiniteScrollListener(PAGE_ITEMS, layoutManager) {
            @Override public void onScrolledToEnd(final int firstVisibleItemPosition) {


                if(!bind.progressBar.isShown()) bind.newItemsProgressBar.setVisibility(View.VISIBLE);

                if(AppStatus.getInstance(getApplicationContext()).isOnline()) {
                    page++;
                    fetchDataFromAPI(page);

                } else {
                    bind.newItemsProgressBar.setVisibility(View.INVISIBLE);
                    if(mToast != null) mToast.cancel();
                    mToast = Toast.makeText(getApplicationContext(), getString(R.string.no_internet), Toast.LENGTH_SHORT);
                    mToast.show();
                }
            }
        };

    }

    public void setRetrofit() {
        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(1, TimeUnit.SECONDS)
                .writeTimeout(1, TimeUnit.SECONDS)
                .connectTimeout(1, TimeUnit.SECONDS)
                .build();

        mRetrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();

        //Retrofit Client
        client = mRetrofit.create(MoviesApiService.class);

    }

    public void fetchDataFromAPI(int page) {

        if(mPreferences.isMostPopular()){

            call = client.getMostPopular(String.valueOf(page));
            getResusltFromAPI();

        } else {

            call = client.getTopRated(String.valueOf(page));
            getResusltFromAPI();
        }
    }

    public void getResusltFromAPI() {
        //Display data
        call.enqueue(new Callback<PostResponse>() {

            @Override
            public void onResponse(Call<PostResponse> call, Response<PostResponse> response) {

                if (response.isSuccessful()) {

                    mAdapter.setData(response.body().getResults());
                    bind.progressBar.setVisibility(View.INVISIBLE);
                    bind.newItemsProgressBar.setVisibility(View.INVISIBLE);

                } else {
                    Log.d("TAAG", "response code: " + response.code());
                }
            }



            @Override
            public void onFailure(Call<PostResponse> call, Throwable t) {
                t.printStackTrace();
                Log.d("TAAG", "onFailure!" );

                t.printStackTrace();

                checkPoorConnection(t);


            }
        });
    }

    private void checkPoorConnection(Throwable t){
        //Checks poor connection
        if(t instanceof SocketTimeoutException){

            if(mAdapter.movies == null){
                showPoorConnectiorMessage();
            }
            else {
                if(mToast != null) mToast.cancel();
                mToast = Toast.makeText(getApplicationContext(), getString(R.string.poor_connection_toast), Toast.LENGTH_LONG);
                mToast.show();
            }

        }
    }

    private void showPoorConnectiorMessage() {
        bind.progressBar.setVisibility(View.INVISIBLE);
        bind.errorMessage.setText(getString(R.string.poor_connection));
        bind.errorLayout.setVisibility(View.VISIBLE);

    }

    private void showNoConnectionMessage() {
        bind.errorMessage.setText("Check your internet connection and try again.");
        bind.errorLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(Result result) {
        Intent intent = new Intent(this, MovieDetailsActivity.class);
        intent.putExtra("original_title", result.getOriginalTitle());
        intent.putExtra("poster", "http://image.tmdb.org/t/p/w780" + result.getPosterPath());
        intent.putExtra("overview", result.getOverview());
        intent.putExtra("vote_average", result.getVoteAverage());
        intent.putExtra("release_date", result.getReleaseDate());
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.options, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.sort_pop) {

            loadMostpopular(true);

        }

        if (id == R.id.sort_rated) {

            loadMostpopular(false);

        }

        return super.onOptionsItemSelected(item);
    }

    private void loadMostpopular(boolean isMostPopular){
        if(AppStatus.getInstance(getApplication()).isOnline()) {

            mPreferences.setMostPopular(isMostPopular);

            if(mAdapter.movies != null) {
                mAdapter.movies.clear();
                fetchDataFromAPI(1);

            } else {
                recreate();
            }

        } else {
            if(mToast != null) mToast.cancel();

            mToast = Toast.makeText(this, getString(R.string.no_internet), Toast.LENGTH_SHORT);
            mToast.show();
        }
    }

    public void onRetry(View view) {
        recreate();
    }

}
