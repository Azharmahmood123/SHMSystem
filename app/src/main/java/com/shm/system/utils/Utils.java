package com.shm.system.utils;

import android.widget.Toast;

import com.shm.system.AppController;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.shm.system.contants.Globals.PrefConstants.APP_PREFS;

public class Utils {
    public static void showToast(String msg) {
        Toast.makeText(AppController.getInstance().getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    public static void setPrefs(String key, String value) {
        PreferenceUtility.setStringPreference(AppController.getInstance().getContext(), APP_PREFS, key, value);
    }

    public static void setPrefs(String key, int value) {
        PreferenceUtility.setIntPreference(AppController.getInstance().getContext(), APP_PREFS, key, value);
    }

    public static void setPrefs(String key, boolean value) {
        PreferenceUtility.setBoolPreference(AppController.getInstance().getContext(), APP_PREFS, key, value);
    }

    public static String getStringPrefs(String key) {
        return PreferenceUtility.getStringPreference(AppController.getInstance().getContext(), APP_PREFS, key, "");
    }

    public static int getIntPrefs(String key) {
        return PreferenceUtility.getIntPreference(AppController.getInstance().getContext(), APP_PREFS, key, -1);
    }

    public static boolean getBoolPrefs(String key) {
        return PreferenceUtility.getBoolPreference(AppController.getInstance().getContext(), APP_PREFS, key, false);
    }

    public void removeAllPrefs() {
        PreferenceUtility.removeAllPreferences(AppController.getInstance().getContext(), APP_PREFS);
    }

    public static String getDateTime(String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }
}
