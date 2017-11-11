package com.udacity.lacourt.popularmoviesstage1;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
    private ProgressBar mProgressBar;
    private ProgressBar mNewItemsProgressBar;

    private TextView mErrorMessage;
    private RelativeLayout mErrorLayout;
    private Button mRetryButton;

    private Retrofit mRetrofit;

    private RecyclerView mRecyclerView;
    private MovieAdapter mAdapter;
    private GridLayoutManager layoutManager;

    private int page = 1;
    private MoviesApiService client;
    private Call<PostResponse> call;

    private final int PAGE_ITEMS = 20;

    private Preferences mPreferences;
    private Toast mToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToast = new Toast(this);
        mPreferences = new Preferences(this);

        mNewItemsProgressBar = (ProgressBar)findViewById(R.id.new_items_progress_bar);
        mNewItemsProgressBar.setVisibility(View.INVISIBLE);

        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mProgressBar.setVisibility(View.VISIBLE);

        mErrorMessage = (TextView) findViewById(R.id.error_message);
//        mErrorMessage.setVisibility(View.INVISIBLE);

        mRetryButton = (Button) findViewById(R.id.btn_retry);
//        mRetryButton.setVisibility(View.INVISIBLE);

        mErrorLayout = (RelativeLayout)findViewById(R.id.error_layout);
        mErrorLayout.setVisibility(View.INVISIBLE);

        mRetrofit = null;

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);


        setGridLayoutManager();

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        mAdapter = new MovieAdapter(this, this);

        mRecyclerView.setAdapter(mAdapter);


        loadDataOnScreen();

    }

    private void loadDataOnScreen() {

        if(AppStatus.getInstance(this).isOnline()) {

            setRetrofit();

            mRecyclerView.addOnScrollListener(createInfiniteScrollListener());

            fetchDataFromAPI(page);

        } else {
            mErrorMessage.setText("Check your internet connection and try again.");
            mErrorMessage.setVisibility(View.VISIBLE);
            mRetryButton.setVisibility(View.VISIBLE);
            mErrorLayout.setVisibility(View.VISIBLE);
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


                if(!mProgressBar.isShown())mNewItemsProgressBar.setVisibility(View.VISIBLE);

                if(AppStatus.getInstance(getApplicationContext()).isOnline()) {
                    page++;
                    fetchDataFromAPI(page);

                } else {
                    mNewItemsProgressBar.setVisibility(View.INVISIBLE);
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
                    mProgressBar.setVisibility(View.INVISIBLE);
                    mNewItemsProgressBar.setVisibility(View.INVISIBLE);

                } else {
                    Log.d("TAAG", "response code: " + response.code());
                }
            }



            @Override
            public void onFailure(Call<PostResponse> call, Throwable t) {
                t.printStackTrace();
                Log.d("TAAG", "Fail!" );

                t.printStackTrace();

                checkPoorConnection(t);


            }
        });
    }

    private void checkPoorConnection(Throwable t){
        //Checks poor connection
        if(t instanceof SocketTimeoutException){

            mProgressBar.setVisibility(View.INVISIBLE);
            mErrorLayout.setVisibility(View.VISIBLE);
            mErrorMessage.setText(getString(R.string.poor_connection));
        }
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
