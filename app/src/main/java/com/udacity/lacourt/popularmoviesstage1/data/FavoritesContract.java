package com.udacity.lacourt.popularmoviesstage1.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class FavoritesContract {

    // The authority, which is how your code knows which Content Provider to access
    public static final String AUTHORITY = "com.udacity.lacourt.popularmoviesstage1";

    // The base content URI = "content://" + <authority>
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final String PATH_FAVORITES = "favorites";

    public static final class FavoritesEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVORITES).build();

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
