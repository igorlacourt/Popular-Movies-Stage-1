package com.udacity.lacourt.popularmoviesstage1;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by igor on 08/11/2017.
 */

public class Preferences {

    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    Activity mActivity;

    private boolean isMostPopular;

    public Preferences(Activity activity) {
        this.mActivity = activity;
        sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        editor = sharedPref.edit();
    }


    public boolean isMostPopular() {
        return sharedPref.getBoolean(mActivity.getString(R.string.is_most_popular_key), true);
    }

    public void setMostPopular(boolean mostPopular) {

        editor.putBoolean(mActivity.getString(R.string.is_most_popular_key), mostPopular);
        editor.commit();
    }
}
