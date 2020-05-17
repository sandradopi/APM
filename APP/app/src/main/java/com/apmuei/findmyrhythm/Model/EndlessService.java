package com.apmuei.findmyrhythm.Model;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.apmuei.findmyrhythm.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Timer;
import java.util.TimerTask;

public class EndlessService extends Service {

    //End-less Service
    private PowerManager.WakeLock wakeLock = null;
    private boolean isServiceStarted = false;
    private static String NOTIFICATION_GROUP = "com.android.EVENT_NOTIFICATION";
    private static final String TAG = "EndlessService";


    public EndlessService() {
        super();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent != null) {
                startService();
                startTimer();
        }
        return START_STICKY;

    }

    @Override
    public void onCreate() {


    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Intent broadcastIntent = new Intent(this, SensorRestarterBroadcastReceiver.class);
        sendBroadcast(broadcastIntent);

        stoptimertask();
    }

    private void startService() {

        if (isServiceStarted)
            return;

        Log.e(TAG, "Starting the foreground service task");
        //Toast.makeText(this, "Service starting its task", Toast.LENGTH_SHORT).show();
        isServiceStarted = true;

        //we need this lock so our service gets not affected by Doze Mode
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "EndlessService::NotificationLock");
        wakeLock.acquire(10000);

        EventService service = new EventService();
        service.subscribeEventNotificationListener(this, FirebaseAuth.getInstance().getCurrentUser().getUid());

    }

    private void stopService() {

        if (wakeLock.isHeld())
            wakeLock.release();

        stopSelf();
        isServiceStarted = false;
    }

    private Timer timer;
    private TimerTask timerTask;
    long oldTime=0;
    public int counter=0;

    public void startTimer() {
        //set a new Timer
        timer = new Timer();

        //initialize the TimerTask's job
        initializeTimerTask();

        //schedule the timer, to wake up every 1 second
        timer.schedule(timerTask, 1000, 1000); //
    }

    /**
     * it sets the timer to print the counter every x seconds
     */
    public void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {
                Log.i("in timer", "in timer ++++  "+ (counter++));
            }
        };
    }

    public void stoptimertask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

}
