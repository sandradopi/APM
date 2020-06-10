package com.apmuei.findmyrhythm.Model;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.util.Log;

public class ListenerJob {
    private static final String TAG = "ListenerJob";

    public static void scheduleJob(Context context) {
        ComponentName componentName = new ComponentName(context, NotifJobService.class);
        JobInfo jobInfo = new JobInfo.Builder(123, componentName)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setPersisted(true)
                //.setPeriodic(15*60*1000)
                .setMinimumLatency(1000)
                .setOverrideDeadline(1500)
                .build();

        JobScheduler scheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        int resultCode = scheduler.schedule(jobInfo);
        if (resultCode == JobScheduler.RESULT_SUCCESS)
            Log.d(TAG, "Job scheduled");

        else
            Log.d(TAG, "Job scheduling failed");
    }

    public void cancelJob(Context context) {
        JobScheduler scheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        scheduler.cancel(123);

    }
}
