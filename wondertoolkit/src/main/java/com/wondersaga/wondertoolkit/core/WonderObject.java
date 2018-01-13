package com.wondersaga.wondertoolkit.core;

import com.google.gson.Gson;

import org.json.JSONObject;

/**
 * Created by Prophecy (Adawat Chanchua) on 10/4/2017 AD.
 */

public class WonderObject {

    // ---------------------------------------------------------------------------------------------
    // Main parser
    // ---------------------------------------------------------------------------------------------

    static public WonderObject parse(String evt) {

        try {

            Gson gson = new Gson();
            WonderObject wonderObject = gson.fromJson(evt, WonderObject.class);

            JSONObject evtJson = new JSONObject(evt);

            if (evtJson.has("data"))
                wonderObject.data = evtJson.getJSONObject("data");

            return wonderObject;

        } catch (Exception e) {
            WonderLog.logError(e);
        }

        return null;
    }

    static public String stringify(WonderObject evt) {

        Gson gson = new Gson();
        return gson.toJson(evt);
    }

    // ---------------------------------------------------------------------------------------------
    // Wonder data

    private String code;
    private String dest;
    private String message;
    private JSONObject data;
    private Error error;

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public JSONObject getData() {
        return data;
    }

    public Error getError() {
        return error;
    }

    public String getDest() {
        return dest;
    }

    public void setDest(String dest) {
        this.dest = dest;
    }

    public static class Error {

        private String code;
        private String message;

        public String getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }
    }
}
