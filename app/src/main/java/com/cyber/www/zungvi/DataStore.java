package com.cyber.www.zungvi;

import android.content.Context;
import android.content.SharedPreferences;


public class DataStore {
    private static DataStore mInstance;
    private static Context mCtx;
    private static final String SHARED_PREF_NAME = "mysharedpref7";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_USER_EMAIL = "useremail";
    private static final String KEY_USER_ID = "userid";
    private static final String KEY_TV_CHANNEL = "channel";
    private static final String KEY_ACTIVATED_ACCOUNT = "status";

    private DataStore(Context context) {
        mCtx = context;
    }

    public static synchronized DataStore getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new DataStore(context);
        }
        return mInstance;
    }

    public boolean userLogin(int id, String username, String email) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt(KEY_USER_ID, id);
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_USER_EMAIL, email);
        editor.apply();

        return true;

    }

    public boolean tv_channel(String channel_key) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(KEY_TV_CHANNEL, channel_key);
        editor.apply();

        return true;
    }

    public boolean setEmail(String email) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(KEY_USER_EMAIL, email);
        editor.apply();

        return true;
    }

    public boolean account_state(String status) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(KEY_ACTIVATED_ACCOUNT, status);
        editor.apply();

        return true;
    }

    public boolean isLoggedIn() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_USERNAME, null) != null;
    }

    public boolean channelExist() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_TV_CHANNEL, null) != null;
    }

    public boolean logout() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        return true;
    }

    public int getCurrentUserId() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(KEY_USER_ID, 0);
    }

    public String getCurrentUserEmail() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_USER_EMAIL, "");
    }

    public String getUsername() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_USERNAME, "");
    }

    public String getTvChannel() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_TV_CHANNEL, "");
    }

    public String getAccountStatus() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_ACTIVATED_ACCOUNT, "");
    }

}
