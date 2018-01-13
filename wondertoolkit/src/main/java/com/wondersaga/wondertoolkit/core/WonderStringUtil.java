package com.wondersaga.wondertoolkit.core;

/**
 * Created by ProphecyX on 10/1/2017 AD.
 */

public class WonderStringUtil {

    static public boolean isEmpty(String str) {

        if (str == null)
            return true;

        if (str.equals(""))
            return true;

        return false;
    }
}
