package com.wondersaga.wondertoolkit.core;

/**
 * Created by ProphecyX on 10/1/2017 AD.
 */

public class WonderDef {

    public static class WonderError {

        public String code;
        public String message;

        public WonderError() {
            code = "undefined";
            message = "Undefined error";
        }

        public void logDebug() {
            WonderLog.logDebug(WonderError.class.getSimpleName(), "WonderError: code: " + code + " message: " + message);
        }
    }
}
