package com.robotemplates.cookbook.task;

import android.os.AsyncTask;

import com.android.volley.RequestQueue;
import com.robotemplates.cookbook.callbacks.SubRedditsLoadedListener;
import com.robotemplates.cookbook.extras.AppUtils;
import com.robotemplates.cookbook.network.VolleySingleton;
import com.robotemplates.cookbook.pojo.SubReddit;

import java.util.ArrayList;

/**
 * Created by willy on 17/07/15.
 */
public class TaskLoadSubReddits extends AsyncTask<Void, Void, ArrayList<SubReddit>> {
    private SubRedditsLoadedListener  myComponent;
    private VolleySingleton volleySingleton;
    private RequestQueue requestQueue;


    public TaskLoadSubReddits(SubRedditsLoadedListener myComponent) {

        this.myComponent = myComponent;
        volleySingleton = VolleySingleton.getInstance();
        requestQueue = volleySingleton.getRequestQueue();
    }

    @Override
    protected ArrayList<SubReddit> doInBackground(Void... params) {
        ArrayList<SubReddit> listMovies = AppUtils.loadSubReddits(requestQueue);
        return listMovies;
    }

    @Override
    protected void onPostExecute(ArrayList<SubReddit> listSubReddits) {
        if (myComponent != null) {
            myComponent.onSubRedditsLoaded(listSubReddits);
        }
    }
}
