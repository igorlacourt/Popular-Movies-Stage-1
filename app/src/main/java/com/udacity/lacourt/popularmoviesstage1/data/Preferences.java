package com.udacity.lacourt.popularmoviesstage1.data;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.udacity.lacourt.popularmoviesstage1.R;

/**
 * Created by igor on 08/11/2017.
 */

public class Preferences {

    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    Activity mActivity;

    public Preferences(Activity activity) {
        this.mActivity = activity;
        sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        editor = sharedPref.edit();
    }

    public boolean isTopRated() {
        return sharedPref.getBoolean(mActivity.getString(R.string.is_top_rated), false);
    }

    public void setTopRated(boolean topRated) {
        editor.putBoolean(mActivity.getString(R.string.is_top_rated), topRated);
        editor.commit();
    }

    public boolean isMostPopular() {
        return sharedPref.getBoolean(mActivity.getString(R.string.is_most_popular), true);
    }

    public void setMostPopular(boolean mostPopular) {
        editor.putBoolean(mActivity.getString(R.string.is_most_popular), mostPopular);
        editor.commit();
    }
}
