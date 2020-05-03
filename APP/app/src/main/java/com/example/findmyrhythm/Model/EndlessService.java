package com.example.findmyrhythm.Model;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.findmyrhythm.R;
import com.google.firebase.auth.FirebaseAuth;

public class EndlessService extends Service {

    //End-less Service
    private PowerManager.WakeLock wakeLock = null;
    private boolean isServiceStarted = false;
    private static String NOTIFICATION_GROUP = "com.android.EVENT_NOTIFICATION";
    private static final String TAG = "EndlessService";



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent != null) {
            boolean state = intent.getExtras().getBoolean("SERVICE_STATE", false);

            if (state)
                startService();
            else
                stopService();
        }
        return START_STICKY;

    }

    @Override
    public void onCreate() {


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);
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

        NotificationManagerCompat sNotificationManager = NotificationManagerCompat.from(this);

        Notification notification = new NotificationCompat.Builder(this, "serviceChannel")
                .setContentTitle("Endless Service")
                .setContentText("Content Text")
                .setSmallIcon(R.drawable.status_bar_icon)
                .setPriority(Notification.PRIORITY_MIN)
                .build();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel sChannel = new NotificationChannel("serviceChannel", "serviceNotif", NotificationManager.IMPORTANCE_HIGH);
            sChannel.enableLights(true);
            sChannel.enableVibration(true);
            sNotificationManager.createNotificationChannel(sChannel);
        }

        startForeground(-2, notification);

        EventService service = new EventService();
        //NotificationManagerCompat sNotificationManager = NotificationManagerCompat.from(context);

        service.subscribeEventNotificationListener(this, FirebaseAuth.getInstance().getCurrentUser().getUid());
        sNotificationManager.deleteNotificationChannel("serviceChannel");
        sNotificationManager.cancel(-2);
    }

    private void stopService() {

        if (wakeLock.isHeld())
            wakeLock.release();

        stopForeground(true);
        stopSelf();
        isServiceStarted = false;
    }
}
