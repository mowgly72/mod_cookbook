package com.robotemplates.cookbook.extras;


import com.robotemplates.cookbook.CookbookApplication;
import com.robotemplates.cookbook.database.DBApp;
import com.robotemplates.cookbook.json.Endpoints;
import com.robotemplates.cookbook.json.Parser;
import com.robotemplates.cookbook.json.Requestor;
import com.robotemplates.cookbook.pojo.SubReddit;
import com.android.volley.RequestQueue;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by willy on 17/07/15.
 */
public class AppUtils {
    public static ArrayList<SubReddit> loadSubReddits(RequestQueue requestQueue) {
        JSONObject response = Requestor.requestSubRedditsJSON(requestQueue, Endpoints.getRequestUrlSubReddits(30));
        ArrayList<SubReddit> listSubReddits = Parser.parseSubRedditsJSON(response);
        CookbookApplication.getWritableDatabase().insertSubReddits(DBApp.BOX_OFFICE, listSubReddits, true);
        return listSubReddits;
    }

}
