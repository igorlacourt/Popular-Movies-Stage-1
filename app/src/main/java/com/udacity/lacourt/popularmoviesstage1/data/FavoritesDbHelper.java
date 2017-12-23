package com.udacity.lacourt.popularmoviesstage1.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.udacity.lacourt.popularmoviesstage1.data.FavoritesContract.FavoritesEntry;
import com.udacity.lacourt.popularmoviesstage1.model.Result;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by igor on 20/12/2017.
 */

public class FavoritesDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "favorites.db";
    private static final int DATABASE_VERSION = 1;
    private static SQLiteDatabase mDb;

    public FavoritesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_FAVORITES_TABLE = "CREATE TABLE " + FavoritesContract.FavoritesEntry.TABLE_NAME + " (" +
                FavoritesContract.FavoritesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                FavoritesContract.FavoritesEntry.COLUMN_MOVIE_ID + " INT, " +
                FavoritesContract.FavoritesEntry.COLUMN_TITLE + " TEXT, " +
                FavoritesContract.FavoritesEntry.COLUMN_OVERVIEW + " TEXT, " +
                FavoritesContract.FavoritesEntry.COLUMN_POSTER_PATH + " TEXT, " +
                FavoritesContract.FavoritesEntry.COLUMN_RELEASE_DATE + " TEXT, " +
                FavoritesContract.FavoritesEntry.COLUMN_VOTE_AVARAGE + " REAL, " +
                FavoritesContract.FavoritesEntry.COLUMN_VOTE_COUNT + " INT, " +
                FavoritesContract.FavoritesEntry.COLUMN_POPULARITY + " REAL, " +
                FavoritesContract.FavoritesEntry.COLUMN_ORIGINAL_LANGUAGE + " TEXT, " +
                FavoritesContract.FavoritesEntry.COLUMN_ORIGINAL_TITLE + " TEXT, " +
                FavoritesContract.FavoritesEntry.COLUMN_BACKDROP_PATH + " TEXT" +
                ");";

        db.execSQL(SQL_CREATE_FAVORITES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + FavoritesEntry.TABLE_NAME);
        onCreate(db);
    }

    public static void onInicializeDB(Context context) {
        FavoritesDbHelper dbHelper = new FavoritesDbHelper(context);
        mDb = dbHelper.getWritableDatabase();
    }

    public static List<Result> populateFavoritesList() {

        Cursor c = getAllFavorites();
        List<Result> favoritesList = new ArrayList<Result>();

        if (c != null && c.getCount() > 0) {
            c.moveToFirst();
            for (int count = 0; count < c.getCount(); count++) {
                Result movie = new Result();

                int movieId = c.getInt(c.getColumnIndex(FavoritesContract.FavoritesEntry.COLUMN_MOVIE_ID));
                String title = c.getString(c.getColumnIndex(FavoritesContract.FavoritesEntry.COLUMN_TITLE));
                String overview = c.getString(c.getColumnIndex(FavoritesContract.FavoritesEntry.COLUMN_OVERVIEW));
                String posterPath = c.getString(c.getColumnIndex(FavoritesContract.FavoritesEntry.COLUMN_POSTER_PATH));
                String releaseDate = c.getString(c.getColumnIndex(FavoritesContract.FavoritesEntry.COLUMN_RELEASE_DATE));
                double voteAverage = c.getDouble(c.getColumnIndex(FavoritesContract.FavoritesEntry.COLUMN_VOTE_AVARAGE));
                int voteCount = c.getInt(c.getColumnIndex(FavoritesContract.FavoritesEntry.COLUMN_VOTE_COUNT));
                double popularity = c.getDouble(c.getColumnIndex(FavoritesContract.FavoritesEntry.COLUMN_POPULARITY));
                String originalLanguage = c.getString(c.getColumnIndex(FavoritesContract.FavoritesEntry.COLUMN_ORIGINAL_LANGUAGE));
                String originalTitle = c.getString(c.getColumnIndex(FavoritesContract.FavoritesEntry.COLUMN_ORIGINAL_TITLE));
                String backdropPath = c.getString(c.getColumnIndex(FavoritesContract.FavoritesEntry.COLUMN_BACKDROP_PATH));

                movie.setId(movieId);
                movie.setTitle(title);
                movie.setOverview(overview);
                movie.setPosterPath(posterPath);
                movie.setReleaseDate(releaseDate);
                movie.setVoteAverage(voteAverage);
                movie.setVoteCount(voteCount);
                movie.setPopularity(popularity);
                movie.setOriginalLanguage(originalLanguage);
                movie.setOriginalTitle(originalTitle);
                movie.setBackdropPath(backdropPath);

                favoritesList.add(movie);
                c.moveToNext();

            }
        }

        for(Result favorite : favoritesList) {
            Log.d("FAVORITES_TAG", "" + favorite.getTitle() + "\n\n\n\n");
        }

        return favoritesList;
    }

    public static Cursor getAllFavorites() {
        return mDb.query(
                FavoritesContract.FavoritesEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );
    }

    //TODO make a button to add favorites
    public static long addToFavorites(Result movie) {
        ContentValues cv = new ContentValues();

        cv.put(FavoritesEntry.COLUMN_MOVIE_ID, movie.getId());
        cv.put(FavoritesEntry.COLUMN_TITLE, movie.getTitle());
        cv.put(FavoritesEntry.COLUMN_RELEASE_DATE, movie.getReleaseDate());
        cv.put(FavoritesEntry.COLUMN_OVERVIEW, movie.getOverview());
        cv.put(FavoritesEntry.COLUMN_POSTER_PATH, movie.getPosterPath());
        cv.put(FavoritesEntry.COLUMN_ORIGINAL_TITLE, movie.getOriginalTitle());
        cv.put(FavoritesEntry.COLUMN_VOTE_AVARAGE, movie.getVoteAverage());
        cv.put(FavoritesEntry.COLUMN_ORIGINAL_LANGUAGE, movie.getOriginalLanguage());
        cv.put(FavoritesEntry.COLUMN_BACKDROP_PATH, movie.getBackdropPath());
        cv.put(FavoritesEntry.COLUMN_VOTE_COUNT, movie.getVoteCount());
        cv.put(FavoritesEntry.COLUMN_POPULARITY, movie.getPopularity());

        return mDb.insert(FavoritesEntry.TABLE_NAME, null, cv);
    }

    public static boolean removeFromFavorites(String movieId) {
        // COMPLETED (2) Inside, call mDb.delete to pass in the TABLE_NAME and the condition that WaitlistEntry._ID equals id
        return mDb.delete(FavoritesEntry.TABLE_NAME, FavoritesEntry.COLUMN_MOVIE_ID + "=" + movieId, null) > 0;
    }

    public static boolean isMovieInFavorites(String movieId) {
        String Query = "Select * from " + FavoritesEntry.TABLE_NAME + " where " + FavoritesEntry.COLUMN_MOVIE_ID + " = " + movieId;
        Cursor cursor = mDb.rawQuery(Query, null);
        if(cursor.getCount() <= 0){
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }
}
