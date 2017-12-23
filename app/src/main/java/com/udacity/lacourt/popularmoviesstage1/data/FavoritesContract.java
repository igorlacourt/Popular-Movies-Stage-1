package com.udacity.lacourt.popularmoviesstage1.data;

import android.provider.BaseColumns;

/**
 * Created by igor on 20/12/2017.
 */

public class FavoritesContract {

    public static final class FavoritesEntry implements BaseColumns {

        public static final String TABLE_NAME = "favorites";

        public static final String COLUMN_MOVIE_ID = "movieId";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_POSTER_PATH = "posterPath";
        public static final String COLUMN_RELEASE_DATE = "releaseDate";
        public static final String COLUMN_VOTE_AVARAGE = "voteAverage";
        public static final String COLUMN_VOTE_COUNT = "voteCount";
        public static final String COLUMN_POPULARITY = "popularity";
        public static final String COLUMN_ORIGINAL_LANGUAGE = "originalLanguage";
        public static final String COLUMN_ORIGINAL_TITLE = "originalTitle";
        public static final String COLUMN_BACKDROP_PATH = "backdropPath";
    }
}
