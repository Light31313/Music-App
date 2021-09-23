package com.example.musicapplication.service;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.media.session.MediaSessionCompat;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.musicapplication.R;
import com.example.musicapplication.entity.Music;

public class CreateNotification {
    public static final String CHANNEL_ID = "channel1";

    public static final String ACTION_PREVIOUS = "actionprevious";
    public static final String ACTION_PLAY = "actionplay";
    public static final String ACTION_NEXT = "actionnext";

    private static NotificationCompat.Builder notification;
    private static NotificationManagerCompat notificationManagerCompat;
    private static PendingIntent pendingIntentPlay, pendingIntentPrevious, pendingIntentNext;

    public static void createNotification(Context context, Music music, int playButton) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManagerCompat = NotificationManagerCompat.from(context);
            MediaSessionCompat mediaSessionCompat = new MediaSessionCompat(context, "tag");

            Bitmap icon = BitmapFactory.decodeResource(context.getResources(), R.drawable.play_music_background);


            Intent intentPrevious = new Intent(context, MediaActionReceiver.class)
                    .setAction(ACTION_PREVIOUS);
            pendingIntentPrevious = PendingIntent.getBroadcast(context, 0,
                    intentPrevious, PendingIntent.FLAG_UPDATE_CURRENT);


            Intent intentPlay = new Intent(context, MediaActionReceiver.class)
                    .setAction(ACTION_PLAY);
            pendingIntentPlay = PendingIntent.getBroadcast(context, 0,
                    intentPlay, PendingIntent.FLAG_UPDATE_CURRENT);


            Intent intentNext = new Intent(context, MediaActionReceiver.class)
                    .setAction(ACTION_NEXT);
            pendingIntentNext = PendingIntent.getBroadcast(context, 0,
                    intentNext, PendingIntent.FLAG_UPDATE_CURRENT);


            //create notification
            notification = new NotificationCompat.Builder(context, CHANNEL_ID);
            notification.setSmallIcon(R.drawable.logo_app)
                    .setContentTitle(music.getSongName())
                    .setContentText(music.getSinger())
                    .setLargeIcon(icon)
                    .setOnlyAlertOnce(true)//show notification for only first time
                    .setShowWhen(false)
                    .addAction(R.drawable.ic_baseline_skip_previous_24, "Previous", pendingIntentPrevious)
                    .addAction(playButton, "Play", pendingIntentPlay)
                    .addAction(R.drawable.ic_baseline_skip_next_24, "Next", pendingIntentNext)
                    .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                            .setShowActionsInCompactView(0, 1, 2)
                            .setMediaSession(mediaSessionCompat.getSessionToken()))
                    .setPriority(NotificationCompat.PRIORITY_LOW)
                    .build();

            notificationManagerCompat.notify(1, notification.build());

        }
    }

    public static void updateNotification(Music music, int playButton) {
        notification.clearActions()
                .addAction(R.drawable.ic_baseline_skip_previous_24, "Previous", pendingIntentPrevious)
                .addAction(playButton, "Play", pendingIntentPlay)
                .addAction(R.drawable.ic_baseline_skip_next_24, "Next", pendingIntentNext)
                .setContentTitle(music.getSongName())
                .setContentText(music.getSinger());
        notificationManagerCompat.notify(1, notification.build());
    }
}
