package com.pcs.ztqtj.view.appwidget.job;

import android.content.Context;
import android.support.annotation.NonNull;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobManager;
import com.evernote.android.job.JobRequest;
import com.pcs.ztqtj.control.tool.ZtqAppWidget;

import java.util.concurrent.TimeUnit;

/**
 * Created by tyaathome on 2019/05/16.
 */
public class UpdateWidgetJob extends Job {

    public static final String TAG = "UpdateWidgetJob_TJ_TAG";
    private Context context;
    private static int mJobId;

    public UpdateWidgetJob(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    protected Result onRunJob(@NonNull Params params) {
        ZtqAppWidget.getInstance().request(context);
//        SimpleDateFormat format = new SimpleDateFormat("HH-mm-ss", Locale.getDefault());
//        Date date = Calendar.getInstance().getTime();
//        String time = format.format(date);
//        Log.e(TAG, "onRunJob: " + time);
        return Result.SUCCESS;
    }

    @Override
    protected void onReschedule(int newJobId) {
        super.onReschedule(newJobId);
        mJobId = newJobId;
    }

    public static void scheduleJob() {
        mJobId = new JobRequest.Builder(TAG)
                .setPeriodic(TimeUnit.MINUTES.toMillis(15), TimeUnit.MINUTES.toMillis(5))
                //.setPeriodic(TimeUnit.SECONDS.toMillis(10))
                //.setExecutionWindow(TimeUnit.SECONDS.toMillis(1),
                //        TimeUnit.SECONDS.toMillis(1))
                .setUpdateCurrent(true)
                .build()
                .schedule();
    }

    public static void cancelJob() {
        JobManager.instance().cancel(mJobId);
    }
}
