package com.wondersaga.wondertoolkit.core;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.OnLifecycleEvent;
import android.arch.lifecycle.ViewModel;
import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Prophecy (Adawat Chanchua) on 10/5/2017 AD.
 */

public abstract class WonderViewModel extends ViewModel implements LifecycleObserver {

    // region Lifecycle Owner

    protected LifecycleOwner lifecycleOwner;

    public void setLifecycleOwnder(LifecycleOwner lifecycleOwnder) {
        this.lifecycleOwner = lifecycleOwnder;

        lifecycleOwnder.getLifecycle().addObserver(this);

        bindData();
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    void addLocationListener() {

        unBindData();
    }

    // endregion

    // region Context

    private Context applicationContext;
    private Context context;

    public void setApplicationContext(Context context) {
        applicationContext = context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    protected Context getApplicationContext() {
        return applicationContext;
    }

    protected Context getContext() {
        return context;
    }

    // endregion

    // region Data binding

    public void bindData() {

        String[] keyList = getDataKeyList();

        for (String key : keyList) {

            // Lazy create set
            if (!WonderModel.getInstance().getViewModelEventMap().containsKey(key))
                WonderModel.getInstance().getViewModelEventMap().put(key, new HashSet<WonderViewModel>());

            // Get set
            Set eventSet = WonderModel.getInstance().getViewModelEventMap().get(key);

            if (eventSet != null) {

                eventSet.add(this);
            }
        }
    }

    public void unBindData() {

        String[] keyList = getDataKeyList();

        for (String key : keyList) {

            // Get set
            Set eventSet = WonderModel.getInstance().getViewModelEventMap().get(key);

            if (eventSet != null) {

                eventSet.remove(this);
            }
        }
    }

    // endregion

    // region Data interface

    public void acquireData() {

        String[] keyList = getDataKeyList();

        for (String key : keyList) {

            WonderModel.getInstance().find(key);
        }
    }

    public abstract void onDataUpdate(String key, JSONObject data) throws JSONException;

    public <T> T getPojo(JSONObject data, Class<T> type) throws JsonSyntaxException {

        Gson gson = new Gson();
        T pojo = gson.fromJson(data.toString(), type);

        return pojo;
    }

    protected abstract String[] getDataKeyList();

    // endregion

    List<WonderEvent> managedWonderEventList = new ArrayList<>();

    WonderEvent createManagedWonderEvent() {

        WonderEvent newWonderEvent = new WonderEvent();
        managedWonderEventList.add(newWonderEvent);
        return newWonderEvent;
    }

    @Override
    protected void onCleared() {

        for (WonderEvent we : managedWonderEventList)
            we.emptyEventListener();
    }
}
