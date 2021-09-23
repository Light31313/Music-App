package com.example.musicapplication.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.musicapplication.Fragment.MusicFragment;
import com.example.musicapplication.R;


public class ForegroundService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (null == intent) return START_STICKY;
        Log.i("ForegroundService", "onStartCommand(): Action: " + intent.getAction());
        startForeground(1, createNotification());
        // background task running here...
        return START_STICKY;
    }

    private Notification createNotification() {
        RemoteViews collapsedLayout = new RemoteViews(getPackageName(), R.layout.layout_notification_custom_collapsed);
        RemoteViews expandedLayout = new RemoteViews(getPackageName(), R.layout.layout_notification_custom_expanded);

        PendingIntent prevIntent = createIntentAction("Prev");
        PendingIntent playIntent = createIntentAction("Play");
        PendingIntent nextIntent = createIntentAction("Next");

        collapsedLayout.setOnClickPendingIntent(R.id.im_next_notification_music, nextIntent);
        collapsedLayout.setOnClickPendingIntent(R.id.im_play_notification_music, playIntent);

        expandedLayout.setOnClickPendingIntent(R.id.im_prev_notification_music, prevIntent);
        expandedLayout.setOnClickPendingIntent(R.id.im_next_notification_music, nextIntent);
        expandedLayout.setOnClickPendingIntent(R.id.im_play_notification_music, playIntent);

        return new NotificationCompat.Builder(this, MusicFragment.CHANNEL_ID)
                .setSmallIcon(R.drawable.notification_icon)
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .setContent(collapsedLayout)
                .setCustomContentView(collapsedLayout)
                .setCustomBigContentView(expandedLayout)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build();
    }

    private PendingIntent createIntentAction(@NonNull String nameAction) {
        Intent actionIntent = new Intent(this, MediaActionReceiver.class).setAction(nameAction);
        return PendingIntent.getBroadcast(this, 0, actionIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
