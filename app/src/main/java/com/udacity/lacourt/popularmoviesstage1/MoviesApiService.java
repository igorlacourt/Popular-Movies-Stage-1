package com.udacity.lacourt.popularmoviesstage1;

import com.udacity.lacourt.popularmoviesstage1.model.PostResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MoviesApiService {
//    @GET("/movie/popular")
//    void getPopularMovies(Callback<Movie.MovieResult> cb);

    final String API_KEY = BuildConfig.API_KEY;

    @GET("movie/popular?api_key=" + API_KEY + "&language=en-US&page=1")
    Call<PostResponse> getResponse();

    @GET("movie/popular?api_key=" + API_KEY + "&language=en-US")
    Call<PostResponse> getMostPopular(@Query("page") String page);

    @GET("movie/top_rated?api_key=fef98cf6bd829f53836bb7d92b02d6ef&language=en-US")
    Call<PostResponse> getTopRated(@Query("page") String page);
}