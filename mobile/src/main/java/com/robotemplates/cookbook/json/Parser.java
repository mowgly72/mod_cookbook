package com.robotemplates.cookbook.json;

import android.provider.SyncStateContract;

import com.robotemplates.cookbook.pojo.SubReddit;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


import static com.robotemplates.cookbook.extras.Keys.EndpointSubReddits.KEY_SUB_REDDITS;
import static com.robotemplates.cookbook.extras.Keys.EndpointSubReddits.KEY_URL_IMAGE;
import static com.robotemplates.cookbook.extras.Keys.EndpointSubReddits.KEY_NAME;
import static com.robotemplates.cookbook.extras.Keys.EndpointSubReddits.KEY_SERVER_ID;

/**
 * Created by willy on 17/07/15.
 */
public class Parser {
    public static ArrayList<SubReddit> parseSubRedditsJSON(JSONObject response) {
        ArrayList<SubReddit> listSubReddit = new ArrayList<>();
        if (response != null && response.length() > 0) {
            try {
                JSONArray arraySubReddits = response.getJSONArray(KEY_SUB_REDDITS);
                for (int i = 0; i < arraySubReddits.length(); i++) {
                    long id = -1;
                    String name =  "" ;
                    String urlImage = "";

                    JSONObject currentSubReddit = arraySubReddits.getJSONObject(i);
                    //get the id of the current movie
                    if (Utils.contains(currentSubReddit, KEY_SERVER_ID)) {
                        id = currentSubReddit.getLong(KEY_SERVER_ID);
                    }
                    //get the title of the current movie
                    if (Utils.contains(currentSubReddit, KEY_NAME)) {
                        name = currentSubReddit.getString(KEY_NAME);
                    }

                    if (Utils.contains(currentSubReddit, KEY_URL_IMAGE)) {
                        urlImage = currentSubReddit.getString(KEY_URL_IMAGE);
                    }

                    SubReddit subReddit = new SubReddit();
                    subReddit.setId(id);
                    subReddit.setName(name);
                    subReddit.setUrlImage(urlImage);

                    if (id != -1 ) {
                        listSubReddit.add(subReddit);
                    }
                }

            } catch (JSONException e) {

            }
//                L.t(getActivity(), listMovies.size() + " rows fetched");
        }
        return listSubReddit;
    }


}
