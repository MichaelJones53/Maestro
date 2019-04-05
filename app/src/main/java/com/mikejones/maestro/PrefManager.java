package com.mikejones.maestro;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefManager {
    public static final String PREF_NAME = "user_data";
    public static final String EMAIL = "email";
    public static final String ROLE = "role";
    public static final String USERNAME = "username";

    private Context mContext;
    private SharedPreferences.Editor editor;
    private SharedPreferences prefs;


    public PrefManager(Context c){
        mContext = c;
    }

    public String getEmail(){
        SharedPreferences prefs = getPrefs();
        return prefs.getString(EMAIL, null);
    }

    public String getRole(){
        SharedPreferences prefs = getPrefs();
        return prefs.getString(ROLE, null);
    }

    public String getUsername(){
        SharedPreferences prefs = getPrefs();
        return prefs.getString(USERNAME, null);
    }

    public void setEmail(String e){
        getEditor().putString(EMAIL, e);
        editor.apply();
    }

    public void setUsername(String e){
        getEditor().putString(USERNAME, e);
        editor.apply();
    }

    public void setRole(String e){
        getEditor().putString(ROLE, e);
        editor.apply();
    }

    public void setAllPrefs(String e, String r, String u){
        SharedPreferences.Editor editor= getEditor();
        editor.putString(EMAIL, e);
        editor.putString(USERNAME, u);
        editor.putString(ROLE, r);
        editor.apply();
    }

    public void clearALlPrefs(){
        SharedPreferences.Editor editor= getEditor();
        editor.putString(EMAIL, null);
        editor.putString(USERNAME, null);
        editor.putString(ROLE, null);
        editor.apply();
    }


    private SharedPreferences getPrefs(){
        return mContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }
    private SharedPreferences.Editor getEditor(){
        return mContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit();
    }
}
