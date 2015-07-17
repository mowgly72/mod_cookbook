package com.robotemplates.cookbook.callbacks;

import com.robotemplates.cookbook.pojo.SubReddit;

import java.util.ArrayList;

/**
 * Created by willy on 17/07/15.
 */
public interface SubRedditsLoadedListener {
    public void onSubRedditsLoaded(ArrayList<SubReddit> listSubReddits);
}
