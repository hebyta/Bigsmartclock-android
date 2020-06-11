package com.example.bigsmartclockapp.core.config;

import android.content.Context;
import android.content.SharedPreferences;

public class AppConfig {

    private static final String PREF_FILE = "Settings";
    public static final String LAST_CONNECTED_DEVICE = "lastConnectedDevice";

    public static String getLastConnectedDevice(Context ctx){
        SharedPreferences sharedPref = ctx.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        return sharedPref.getString(LAST_CONNECTED_DEVICE, null);
    }

    public static void setLastConnectedDevice(Context ctx, String address){
        SharedPreferences sharedPref  = ctx.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(LAST_CONNECTED_DEVICE, address);
        editor.commit();
    }


}
