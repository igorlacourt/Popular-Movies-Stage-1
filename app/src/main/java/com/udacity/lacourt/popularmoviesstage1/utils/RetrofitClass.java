package com.udacity.lacourt.popularmoviesstage1.utils;

import com.udacity.lacourt.popularmoviesstage1.MoviesApiService;
import com.udacity.lacourt.popularmoviesstage1.model.PostResponse;
import com.udacity.lacourt.popularmoviesstage1.model.TrailersPostResponse;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by igor on 15/12/2017.
 */



public class RetrofitClass {

    public static final String BASE_URL = "https://api.themoviedb.org/3/";
    public static Retrofit retrofit;

    public static MoviesApiService client;
    public static Call<PostResponse> call;
    public static Call<TrailersPostResponse> trailerCall;

    public static void setRetrofit() {
        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(2, TimeUnit.SECONDS)
                .writeTimeout(2, TimeUnit.SECONDS)
                .connectTimeout(2, TimeUnit.SECONDS)
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();

        //Retrofit Client
        client = retrofit.create(MoviesApiService.class);
    }

    public static void requestMostPopular(int page) {
        call = client.getMostPopular(String.valueOf(page));

    }

    public static void requestTopRated(int page) {
        call = client.getTopRated(String.valueOf(page));
    }

    public static void requestTrailer(String movie_id) {
        trailerCall = client.getTrailer(movie_id);
    }
}
