package com.wondersaga.wondertoolkit.core;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Prophecy (Adawat Chanchua) on 10/8/2017 AD.
 */

public class WonderDatabase {

    // Singleton

    static private WonderDatabase _s_instance = null;

    static public WonderDatabase getInstance(Context context) {

        if (_s_instance == null)
            _s_instance = new WonderDatabase(context);
        else
            _s_instance.context = context;

        return _s_instance;
    }

    // Vars
    Context context;
    String preferencesName = "WONDER_DB";

    public enum StorageMode {
        SHARED_PREFERENCES,
    }

    StorageMode storageMode = StorageMode.SHARED_PREFERENCES;

    // Constructor

    public WonderDatabase(Context context) {

        this.context = context;
    }

    // Settings

    public void SetPreferencesName(String name) {
        preferencesName = name;
    }

    public void SetMode(StorageMode mode) {
        storageMode = mode;
    }

    // region CRUD

    public void upsert(String key, String value) {

        if (storageMode == StorageMode.SHARED_PREFERENCES) {

            SharedPreferences.Editor editor = getSharedPreferences().edit();
            editor.putString(key, value);
            editor.commit();
        }
        else {
            WonderLog.logError("No implementation!");
        }
    }

    public String find(String key) {

        if (storageMode == StorageMode.SHARED_PREFERENCES) {

            return getSharedPreferences().getString(key, "");
        }
        else {
            WonderLog.logError("No implementation!");
            return null;
        }
    }

    public void delete(String key) {

        if (storageMode == StorageMode.SHARED_PREFERENCES) {

            SharedPreferences.Editor editor = getSharedPreferences().edit();
            editor.putString(key, "");
            editor.commit();
        }
        else {
            WonderLog.logError("No implementation!");
        }
    }

    // endregion

    // region Private

    private SharedPreferences getSharedPreferences() {

        return context.getSharedPreferences(preferencesName, MODE_PRIVATE);
    }

    // endregion
}
