package com.gmail.btheo95.musicflashlight.util;

/**
 * Created by btheo on 9/13/2017.
 */


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import com.gmail.btheo95.musicflashlight.R;
import com.gmail.btheo95.musicflashlight.activity.MainActivity;

public class NotificationHelper extends ContextWrapper {

    private NotificationManager manager;
    public static final String RUNNING_IN_BACKGROUND_CHANNEL = "run_in_background_channel";

    public NotificationHelper(Context ctx) {
        super(ctx);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel chan = new NotificationChannel(RUNNING_IN_BACKGROUND_CHANNEL,
                    getString(R.string.notification_channel_flash_on_text),
                    NotificationManager.IMPORTANCE_LOW);
            chan.setLightColor(Color.GREEN);
            chan.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            getManager().createNotificationChannel(chan);
        }

    }


    public NotificationCompat.Builder getNotification() {

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder nb = new NotificationCompat.Builder(this, RUNNING_IN_BACKGROUND_CHANNEL)
                .setContentTitle(getString(R.string.notification_flash_title))
                .setContentText(getString(R.string.notification_flash_content))
                .setSmallIcon(R.drawable.ic_filled_light_bulb_white_24dp)
                .setOngoing(true)
                .setColor(ContextCompat.getColor(this, R.color.primary))
                .setContentIntent(pendingIntent);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN
                && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            nb.setPriority(Notification.PRIORITY_MAX);
        }

        return nb;
    }


    public void notify(int id, NotificationCompat.Builder notification) {
        getManager().notify(id, notification.build());
    }

    private NotificationManager getManager() {
        if (manager == null) {
            manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return manager;
    }
}