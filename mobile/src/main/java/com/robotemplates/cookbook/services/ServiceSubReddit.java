package com.robotemplates.cookbook.services;

import com.robotemplates.cookbook.callbacks.SubRedditsLoadedListener;
import com.robotemplates.cookbook.logging.L;
import com.robotemplates.cookbook.pojo.SubReddit;
import com.robotemplates.cookbook.task.TaskLoadSubReddits;

import java.util.ArrayList;

import me.tatarka.support.job.JobParameters;
import me.tatarka.support.job.JobService;

/**
 * Created by willy on 17/07/15.
 */
public class ServiceSubReddit extends JobService implements SubRedditsLoadedListener {
    private JobParameters jobParameters;

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        L.t(this, "onStartJob");
        this.jobParameters = jobParameters;
        new TaskLoadSubReddits(this).execute();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        L.t(this, "onStopJob");
        return false;
    }

    @Override
    public void onSubRedditsLoaded(ArrayList<SubReddit> listSubReddits) {
        L.t(this, "onBoxOfficeMoviesLoaded");
        jobFinished(jobParameters, false);
    }
}
