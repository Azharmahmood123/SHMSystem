package com.shm.system.utils;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class PreferenceUtility {

    public static void setStringPreference(Context context, String prefName, String key, String value) {
        SharedPreferences userPref = context.getSharedPreferences(prefName, MODE_PRIVATE);
        SharedPreferences.Editor editor = userPref.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String getStringPreference(Context context,String prefName, String key, String defaultValue) {
        SharedPreferences usePref = context.getSharedPreferences(prefName, MODE_PRIVATE);
        return usePref.getString(key, defaultValue);
    }

    public static void setBoolPreference(Context context,String prefName, String key, boolean value) {
        SharedPreferences userPref = context.getSharedPreferences(prefName, MODE_PRIVATE);
        SharedPreferences.Editor editor = userPref.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static boolean getBoolPreference(Context context,String prefName, String key, boolean defaultValue) {
        SharedPreferences usePref = context.getSharedPreferences(prefName, MODE_PRIVATE);
        return usePref.getBoolean(key, defaultValue);
    }

    public static void setIntPreference(Context context,String prefName, String key, int value) {
        SharedPreferences userPref = context.getSharedPreferences(prefName, MODE_PRIVATE);
        SharedPreferences.Editor editor = userPref.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public static int getIntPreference(Context context,String prefName, String key, int defaultValue) {
        SharedPreferences usePref = context.getSharedPreferences(prefName, MODE_PRIVATE);
        return usePref.getInt(key, defaultValue);
    }

    public static void removeAllPreferences(Context context,String prefName) {
        SharedPreferences userPref = context.getSharedPreferences(prefName, MODE_PRIVATE);
        SharedPreferences.Editor editor = userPref.edit();
        editor.clear();
        editor.apply();
    }
}
