package com.cyber.www.zungvi;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import static com.cyber.www.zungvi.App.CHANNEL_1_ID;

public class NotificationJobService extends JobService {

    private static final String TAG = "NotificationJobService";
    private boolean jobCancelled = false;
    private NotificationManagerCompat notificationManager;

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d(TAG,"Job started");
        doBackgroundWork(params);
        return true;
    }

    private void doBackgroundWork(JobParameters params)
    {

        notificationManager = NotificationManagerCompat.from(this);
        Intent activityIntent = new Intent(this,Main2Activity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this,0,activityIntent,0);

        Intent broadcastIntent = new Intent(this,NotificationReceiver.class);
        broadcastIntent.putExtra("toastMessage","feeds");
        PendingIntent actionIntent = PendingIntent.getBroadcast(this,0,broadcastIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_1_ID)
            .setSmallIcon(R.drawable.ic_notifications)
            .setContentTitle("Zungvi")
            .setContentText("You may have new notifications")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setColor(Color.BLUE)
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .addAction(R.mipmap.ic_launcher,"Feeds",actionIntent)
            .build();

        notificationManager.notify(1,notification);

        if (Build.VERSION.SDK_INT > 21) {
            jobFinished(params, false);
        }

    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d(TAG,"Job cancelled before completion");
        jobCancelled = true;
        return true;
    }
}
