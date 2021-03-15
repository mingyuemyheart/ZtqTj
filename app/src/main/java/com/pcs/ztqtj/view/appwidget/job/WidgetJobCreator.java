package com.pcs.ztqtj.view.appwidget.job;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobCreator;

/**
 * Created by tyaathome on 2019/05/16.
 */
public class WidgetJobCreator implements JobCreator {

    private Context context;

    public WidgetJobCreator(Context context) {
        this.context = context;
    }

    @Nullable
    @Override
    public Job create(@NonNull String tag) {
        switch (tag) {
            case UpdateWidgetJob.TAG:
                return new UpdateWidgetJob(context);
            default:
                return null;
        }
    }
}
