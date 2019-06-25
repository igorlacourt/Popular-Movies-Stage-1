package com.udacity.lacourt.popularmoviesstage1.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.udacity.lacourt.popularmoviesstage1.data.FavoritesContract.FavoritesEntry;

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

}
