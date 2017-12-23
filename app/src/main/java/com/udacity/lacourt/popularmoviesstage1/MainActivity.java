package com.udacity.lacourt.popularmoviesstage1;

import android.content.Intent;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.udacity.lacourt.popularmoviesstage1.data.FavoritesContract;
import com.udacity.lacourt.popularmoviesstage1.data.FavoritesDbHelper;
import com.udacity.lacourt.popularmoviesstage1.data.Preferences;
import com.udacity.lacourt.popularmoviesstage1.databinding.ActivityMainBinding;
import com.udacity.lacourt.popularmoviesstage1.model.PostResponse;
import com.udacity.lacourt.popularmoviesstage1.model.Result;
import com.udacity.lacourt.popularmoviesstage1.utils.AppStatus;
import com.udacity.lacourt.popularmoviesstage1.utils.InfiniteScrollListener;
import com.udacity.lacourt.popularmoviesstage1.utils.RetrofitClass;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieOnClickHandler {

    private MovieAdapter mAdapter;
    private GridLayoutManager layoutManager;

    private int page = 1;

    private final int PAGE_ITEMS = 20;

    private Preferences mPreferences;
    private Toast mToast;

    private ActivityMainBinding bind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = DataBindingUtil.setContentView(this, R.layout.activity_main);

        //Database
        FavoritesDbHelper.onInicializeDB(this);

        mToast = new Toast(this);
        mPreferences = new Preferences(this);

        bind.newItemsProgressBar.setVisibility(View.INVISIBLE);
        bind.progressBar.setVisibility(View.VISIBLE);
        bind.errorLayout.setVisibility(View.INVISIBLE);

        setGridLayoutManager();

        bind.recyclerView.setLayoutManager(layoutManager);
        bind.recyclerView.setHasFixedSize(true);

        mAdapter = new MovieAdapter(this, this);

        bind.recyclerView.setAdapter(mAdapter);

        loadDataOnScreen();

    }

    @Override
    protected void onStart() {
        super.onStart();
        //TODO make reclyview return from where it stoped after coming back from MovieDetailsActivity
        page = 1;

        mAdapter = new MovieAdapter(this, this);
        bind.recyclerView.setAdapter(mAdapter);

        loadDataOnScreen();
    }


    private void loadDataOnScreen() {

        if(AppStatus.getInstance(this).isOnline()) {

            RetrofitClass.setRetrofit();

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
            layoutManager = new GridLayoutManager(this, numberOfColumns(185));
        }
    }

    private int numberOfColumns(int imageWidth) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        // You can change this divider to adjust the size of the poster
        int screenWidth = displayMetrics.widthPixels;
        double screenHeight = displayMetrics.heightPixels;

        double imageHeight = (2 * imageWidth);

        double percentage = screenHeight / imageHeight;

        double width = percentage * imageWidth;

        double nColumns = screenWidth / (int)width;
        BigDecimal bdColums = new BigDecimal(nColumns).setScale(0, RoundingMode.HALF_EVEN);

        if (bdColums.doubleValue() < 2) return 2;
        return (int)bdColums.doubleValue();
    }

    @NonNull
    private InfiniteScrollListener createInfiniteScrollListener() {

        return new InfiniteScrollListener(PAGE_ITEMS, layoutManager) {
            @Override public void onScrolledToEnd(final int firstVisibleItemPosition) {



                if(AppStatus.getInstance(getApplicationContext()).isOnline()) {
                    page++;
                    if(mPreferences.isMostPopular() || mPreferences.isTopRated()){
                        if(!bind.progressBar.isShown()) bind.newItemsProgressBar.setVisibility(View.VISIBLE);
                        fetchDataFromAPI(page);
                    }

                } else {
                    bind.newItemsProgressBar.setVisibility(View.INVISIBLE);
                    if(mToast != null) mToast.cancel();
                    mToast = Toast.makeText(getApplicationContext(), getString(R.string.no_internet), Toast.LENGTH_SHORT);
                    mToast.show();
                }
            }
        };

    }

    public void fetchDataFromAPI(int page) {

        if(mPreferences.isMostPopular()){

            RetrofitClass.requestMostPopular(page);
            getResultFromAPI();

        } else if(mPreferences.isTopRated()) {

            RetrofitClass.requestTopRated(page);
            getResultFromAPI();

        } else {

            mAdapter.setData(FavoritesDbHelper.populateFavoritesList());
            bind.progressBar.setVisibility(View.INVISIBLE);
            bind.newItemsProgressBar.setVisibility(View.INVISIBLE);

        }
    }

    public void getResultFromAPI() {
        //Display data
        RetrofitClass.call.enqueue(new Callback<PostResponse>() {

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
        Intent movieIntent = new Intent(this, MovieDetailsActivity.class);
        movieIntent.putExtra("movie", result);
        startActivity(movieIntent);
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

            loadMostpopular(true, false);

        }

        if (id == R.id.sort_rated) {

            loadMostpopular(false, true);

        }

        if (id == R.id.favorites) {

            loadMostpopular(false, false);

        }

        return super.onOptionsItemSelected(item);
    }

    private void loadMostpopular(boolean isMostPopular, boolean isTopRated){
        if(AppStatus.getInstance(getApplication()).isOnline()) {

            mPreferences.setMostPopular(isMostPopular);
            mPreferences.setTopRated(isTopRated);

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
