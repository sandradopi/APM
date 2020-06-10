package com.apmuei.findmyrhythm.Model;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;

public class NotifJobService extends JobService {

    private static final String TAG = "NotifJobService";
    private boolean jobCancelled = false;

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d(TAG, "Job Started");
        listenToEvents(params);

        // Reschedule the job
        //ListenerJob.scheduleJob(getApplicationContext());
        return true;
    }

    private void listenToEvents(final JobParameters params) {

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                EventService service = new EventService();
                service.subscribeEventNotificationListener(NotifJobService.this, FirebaseAuth.getInstance().getCurrentUser().getUid());

                //Antes estaba a 90
                for (int i = 0; i < 180; i++) {
                    Log.d(TAG, "Run: " + i * 5);

                    if (jobCancelled) {
                        return;
                    }

                    try {
                        //Thread.sleep(10000);
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                Log.d(TAG, "Job finished");
                jobFinished(params, true);
            }
        });

        t.start();
    }
    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d(TAG, "Job cancelled before completion");
        jobCancelled = true;
        return true;
    }

    //jobFinished();
}
