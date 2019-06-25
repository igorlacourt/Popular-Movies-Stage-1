package com.udacity.lacourt.popularmoviesstage1.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.udacity.lacourt.popularmoviesstage1.R;
import com.udacity.lacourt.popularmoviesstage1.model.Result;

import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private final Context mContext;

    public List<Result> movies;

    private final MovieOnClickHandler movieOnClickHandler;

    private LayoutInflater inflater;

    private final Activity activity;

    public MovieAdapter(MovieOnClickHandler movieOnClickHandler, Context context) {

        this.mContext = context;
        this.activity = (Activity) context;
        this.movieOnClickHandler = movieOnClickHandler;
    }

    public interface MovieOnClickHandler {
        void onClick(Result result);
    }

    public void setData(List<Result> movies) {
        if(this.movies == null) {
            this.movies = movies;

        } else {
            this.movies.addAll(movies);
        }
        notifyDataSetChanged();
    }

    @Override
    public MovieAdapter.MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.movie_layout, parent, false);

        return new MovieViewHolder(view);
    }



    @Override
    public void onBindViewHolder(final MovieAdapter.MovieViewHolder holder, final int position){

        Picasso.with(mContext).load("http://image.tmdb.org/t/p/w185" + movies.get(position).getPosterPath())
                /*Should use .placeholder() and .error() in case of picasso encounter a null or empty value!
                .placeholder(R.drawable.user_placeholder)
                .error(R.drawable.user_placeholder_error) */
                .into(holder.mImageView, new Callback() {
                     @Override
                    public void onSuccess() {
                     holder.mMovieLayout.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onError() {

                    }
                });

        holder.mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    movieOnClickHandler.onClick(movies.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        if(movies == null) return 0;
        return movies.size();
    }

    class MovieViewHolder extends RecyclerView.ViewHolder{

        final RelativeLayout mMovieLayout;
        final ImageView mImageView;

        public MovieViewHolder(View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.poster);
            mMovieLayout = itemView.findViewById(R.id.movie_layout);
            mMovieLayout.setVisibility(View.INVISIBLE);
        }
    }

}
