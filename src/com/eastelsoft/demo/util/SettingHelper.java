package com.eastelsoft.demo.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SettingHelper {
    private static SharedPreferences.Editor editor = null;
    private static SharedPreferences sharedPreferences = null;
    
    private static final String MMS_FILTER_STATUS = "mms_filter_status";
    
    public static String getMmsStatus(Context context) {
        return SettingHelper.getSharedPreferences(context, MMS_FILTER_STATUS, "open");
    }
    
    public static void setMmsStatus(Context context,String status) {
        SettingHelper.setEditor(context, MMS_FILTER_STATUS, status);
    }

    private SettingHelper() {

    }

    private static SharedPreferences.Editor getEditorObject(Context paramContext) {
        if (editor == null)
            editor = PreferenceManager.getDefaultSharedPreferences(paramContext).edit();
        return editor;
    }

    public static int getSharedPreferences(Context paramContext, String paramString, int paramInt) {
        return getSharedPreferencesObject(paramContext).getInt(paramString, paramInt);
    }

    public static long getSharedPreferences(Context paramContext, String paramString, long paramLong) {
        return getSharedPreferencesObject(paramContext).getLong(paramString, paramLong);
    }

    public static Boolean getSharedPreferences(Context paramContext, String paramString, Boolean paramBoolean) {
        return getSharedPreferencesObject(paramContext).getBoolean(paramString, paramBoolean);
    }

    public static String getSharedPreferences(Context paramContext, String paramString1, String paramString2) {
        return getSharedPreferencesObject(paramContext).getString(paramString1, paramString2);
    }

    private static SharedPreferences getSharedPreferencesObject(Context paramContext) {
        if (sharedPreferences == null)
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(paramContext);
        return sharedPreferences;
    }

    public static void setEditor(Context paramContext, String paramString, int paramInt) {
        getEditorObject(paramContext).putInt(paramString, paramInt).commit();
    }

    public static void setEditor(Context paramContext, String paramString, long paramLong) {
        getEditorObject(paramContext).putLong(paramString, paramLong).commit();
    }

    public static void setEditor(Context paramContext, String paramString, Boolean paramBoolean) {
        getEditorObject(paramContext).putBoolean(paramString, paramBoolean).commit();
    }

    public static void setEditor(Context paramContext, String paramString1, String paramString2) {
        getEditorObject(paramContext).putString(paramString1, paramString2).commit();
    }
}
