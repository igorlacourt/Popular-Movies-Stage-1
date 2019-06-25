package com.udacity.lacourt.popularmoviesstage1;

import com.udacity.lacourt.popularmoviesstage1.model.PostResponse;
import com.udacity.lacourt.popularmoviesstage1.model.ReviewsGetResponse;
import com.udacity.lacourt.popularmoviesstage1.model.TrailersPostResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MoviesApiService {
//    @GET("/movie/popular")
//    void getPopularMovies(Callback<Movie.MovieResult> cb);

    String API_KEY = "";//BuildConfig.API_KEY;

    @GET("movie/popular?api_key=" + API_KEY + "&language=en-US&page=1")
    Call<PostResponse> getResponse();

    @GET("movie/popular?api_key=" + API_KEY + "&language=en-US")
    Call<PostResponse> getMostPopular(@Query("page") String page);

    @GET("movie/top_rated?api_key=" + API_KEY + "&language=en-US")
    Call<PostResponse> getTopRated(@Query("page") String page);

    @GET("movie/{id}/videos?api_key=" + API_KEY)
    Call<TrailersPostResponse> getTrailer(@Path("id") String user);

    @GET("movie/{id}/reviews?api_key=" + API_KEY)
    Call<ReviewsGetResponse> getReview(@Path("id") String user);
}