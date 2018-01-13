package com.wondersaga.wondertoolkit.core;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by Prophecy (Adawat Chanchua) on 10/5/2017 AD.
 */

public class WonderModel {

    static private WonderModel _s_wonderModel = null;

    static public WonderModel getInstance() {

        if (_s_wonderModel == null)
            _s_wonderModel = new WonderModel();

        return _s_wonderModel;
    }

    Map<String, JSONObject> database;
    private Map<String, Set<WonderViewModel>> viewModelEventMap;

    public WonderModel() {

        database = new HashMap<>();
        viewModelEventMap = new HashMap<>();
    }

    public Map<String, Set<WonderViewModel>> getViewModelEventMap() {
        return viewModelEventMap;
    }

    // View model - event mapper

    // CRUD is here

    public <T> void upsert(String key, T data) throws JSONException {

        if (data.getClass() != JSONObject.class) {

            Gson gson = new Gson();
            JSONObject jsonData = new JSONObject(gson.toJson(data));
            database.put(key, jsonData);
        }
        else {

            database.put(key, (JSONObject) data);
        }

        dispatchOperationEvent(key);
    }

    public JSONObject find(String key) {

        JSONObject data = database.get(key);
        dispatchOperationEvent(key);
        return data;
    }

    public void delete(String key) {

        database.remove(key);
        dispatchOperationEvent(key);
    }

    private void dispatchOperationEvent(String key) {

        if (!viewModelEventMap.containsKey(key))
            return;
        if (!database.containsKey(key))
            return;

        // Get data
        JSONObject data = database.get(key);

        // Dispatch data
        Set<WonderViewModel> destSet = viewModelEventMap.get(key);
        for (WonderViewModel vm : destSet) {

            try {
                vm.onDataUpdate(key, data);
            } catch (JSONException e) {
                WonderLog.logError(e);
            }
        }
    }
}
