package com.udacity.lacourt.popularmoviesstage1.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;

import static com.udacity.lacourt.popularmoviesstage1.data.FavoritesContract.FavoritesEntry.TABLE_NAME;

public class FavoritesContentProvider extends ContentProvider {

    private static final int FAVORITES = 100;
    private static final int FAVORITE_WITH_ID = 101;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private static UriMatcher buildUriMatcher() {

        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(FavoritesContract.AUTHORITY, FavoritesContract.PATH_FAVORITES, FAVORITES);
        uriMatcher.addURI(FavoritesContract.AUTHORITY, FavoritesContract.PATH_FAVORITES + "/#", FAVORITE_WITH_ID);

        return uriMatcher;
    }

    private FavoritesDbHelper mFavoritesDbHelper;

    @Override
    public boolean onCreate() {

        Context context = getContext();
        mFavoritesDbHelper = new FavoritesDbHelper(context);
        return true;
    }


    // Implement insert to handle requests to insert a single new row of data
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {

        final SQLiteDatabase db = mFavoritesDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        Uri returnUri; // URI to be returned

        switch (match) {
            case FAVORITES:
                // Insert new values into the database
                long id = db.insert(TABLE_NAME, null, values);
                if ( id > 0 ) {
                    returnUri = ContentUris.withAppendedId(FavoritesContract.FavoritesEntry.CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Notify the resolver if the uri has been changed, and return the newly inserted URI
        getContext().getContentResolver().notifyChange(uri, null);

        // Return constructed uri (this points to the newly inserted row of data)
        return returnUri;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        // Get access to underlying database (read-only for query)
        final SQLiteDatabase db = mFavoritesDbHelper.getReadableDatabase();

        int match = sUriMatcher.match(uri);
        Cursor retCursor;

        switch (match) {
            // Query for the tasks directory
            case FAVORITES:
                retCursor =  db.query(TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            case FAVORITE_WITH_ID:
                String id = uri.getPathSegments().get(1);

                String mSelection = FavoritesContract.FavoritesEntry.COLUMN_MOVIE_ID + "=?";
                String[] mSelectionArgs = new String[]{id};

                retCursor =  db.query(TABLE_NAME,
                        projection,
                        mSelection,
                        mSelectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            // Default exception
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Set a notification URI on the Cursor and return that Cursor
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);

        // Return the desired Cursor
        return retCursor;
    }

    // Implement delete to delete a single row of data
    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {

        final SQLiteDatabase db = mFavoritesDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        // Keep track of the number of deleted favorites
        int favoriteDeleted; // starts as 0

        switch (match) {
            // Handle the single item case, recognized by the ID included in the URI path
            case FAVORITE_WITH_ID:
                // Get the movie ID from the URI path
                String id = uri.getPathSegments().get(1);
                // Use selections/selectionArgs to filter for this ID
                favoriteDeleted = db.delete(TABLE_NAME, FavoritesContract.FavoritesEntry.COLUMN_MOVIE_ID + "=?", new String[]{id});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (favoriteDeleted != 0) {

            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of favorites deleted
        return favoriteDeleted;
    }


    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {

        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public String getType(@NonNull Uri uri) {

        throw new UnsupportedOperationException("Not yet implemented");
    }

}
