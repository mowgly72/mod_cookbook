package com.robotemplates.cookbook.json;

import org.json.JSONObject;

/**
 * Created by willy on 17/07/15.
 */
public class Utils {
    public static boolean contains(JSONObject jsonObject, String key) {
        return jsonObject != null && jsonObject.has(key) && !jsonObject.isNull(key) ? true : false;
    }

}
