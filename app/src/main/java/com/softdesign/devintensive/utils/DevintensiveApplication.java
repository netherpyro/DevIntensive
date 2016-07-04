package com.softdesign.devintensive.utils;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by nether on 26.06.16.
 */
public class DevintensiveApplication extends Application {

    public static SharedPreferences sSharedPreferences;

    public static SharedPreferences getSharedPreferences() {
        return sSharedPreferences;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        sSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
    }
}
