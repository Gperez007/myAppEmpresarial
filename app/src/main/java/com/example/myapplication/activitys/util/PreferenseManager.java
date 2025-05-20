package com.example.myapplication.activitys.util;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenseManager {

    private final SharedPreferences sharedPreferences;

    public PreferenseManager (Context context){

        sharedPreferences = context.getSharedPreferences(Constant.KEY_PREFERENCE_NAME, context.MODE_PRIVATE);



    }

    public void putBoolean(String key, Boolean value){

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key,value);
        editor.apply();
    }

    public Boolean getBoolean(String key){

        return sharedPreferences.getBoolean(key,false);
    }

    public String getString(String key){

        return sharedPreferences.getString(key,null);
    }


    public void putString( String key, String value ){

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public void clear(){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}

