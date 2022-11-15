package com.datpv134.closeeyesandlisten.service;

import static com.datpv134.closeeyesandlisten.service.MyApplication.CHANNEL_ID;
import static com.datpv134.closeeyesandlisten.service.MyApplication.isPushNotifi;
import static com.datpv134.closeeyesandlisten.service.MyApplication.isRunning;
import static com.datpv134.closeeyesandlisten.service.MyApplication.mediaPlayer;

import android.app.Application;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;

import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.bumptech.glide.Glide;
import com.datpv134.closeeyesandlisten.R;
import com.datpv134.closeeyesandlisten.model.Song;
import com.datpv134.closeeyesandlisten.ui.MainActivity;

public class MyService extends Service {
    private static final int ACTION_PAUSE = 1;
    private static final int ACTION_RESUME = 2;
    private static final int ACTION_NEXT = 3;
    private static final int ACTION_BACK = 4;

    private Song song;
    Bitmap bitmap = null;
    boolean isPlaying, isNewSong;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("My Service", "onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("myService", "onStartCommand");
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            Song temp = (Song) bundle.get("song1");
            isPlaying = bundle.getBoolean("isPlaying", false);
            isNewSong = bundle.getBoolean("isNewSong", false);
            if (isNewSong) {
                sendMessage(5);
            }
            if (temp != null) {
                song = ((MyApplication) this.getApplication()).getCurrentSong();;

                sendNotificationMedia();
                sendNotificationMedia();
            }
        }

        int actionMusic = intent.getIntExtra("action_music_service", -1);
        handleActionMusic(actionMusic);

        return START_NOT_STICKY;
    }

    private void handleActionMusic(int action) {
        switch (action) {
            case ACTION_PAUSE:
                pauseMusic();
                break;
            case ACTION_RESUME:
                resumeMusic();
                break;
            case ACTION_NEXT:
                sendMessage(3);
                sendNotificationMedia();
                break;
            case ACTION_BACK:
                sendMessage(4);
                sendNotificationMedia();
                break;
            default:
                break;
        }
    }

    private void pauseMusic() {
        if (isRunning) {
            sendMessage(1);
        }
        isPlaying = false;
        sendNotificationMedia();
    }

    private void resumeMusic() {
        if (isRunning) {
            sendMessage(2);
        }
        isPlaying = true;
        sendNotificationMedia();
    }

    private void sendMessage(int action) {
        Intent intent = new Intent("my_message");
        intent.putExtra("my_action", action);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    @Override
    public void onDestroy() {
        mediaPlayer.stop();
        mediaPlayer.reset();
        isPushNotifi = false;
        super.onDestroy();
    }

//    private void sendNotification() {
//        Intent intent = new Intent(getBaseContext(), MainActivity.class);
//        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//
//        remoteViews = new RemoteViews(getPackageName(), R.layout.custom_notification);
//        remoteViews.setImageViewResource(R.id.imgSongNotifi, R.drawable.icon_music);
//
//        try {
//            while (bitmap == null) {
//                bitmap = Glide.with(getBaseContext())
//                        .asBitmap()
//                        .load(song.getImage())
//                        .submit(512, 512)
//                        .get();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        remoteViews.setImageViewBitmap(R.id.imgSongNotifi, bitmap);
//        remoteViews.setTextViewText(R.id.tvTitle, "Hello");
//        remoteViews.setTextViewText(R.id.tvSongNotifi, song.getName());
//        remoteViews.setImageViewResource(R.id.imgPlayOrPauseNotifi, R.drawable.icon_pause_2);
//
//        changeButton();
//
////        remoteViews.setOnClickPendingIntent(R.id.imgClear, getPendingIntent(this, ACTION_CLEAR));
//        remoteViews.setOnClickPendingIntent(R.id.imgBackNotifi, getPendingIntent(this, ACTION_BACK));
//        remoteViews.setOnClickPendingIntent(R.id.imgNextNotifi, getPendingIntent(this, ACTION_NEXT));
//
//        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
//                .setSmallIcon(R.drawable.icon_music)
//                .setContentIntent(pendingIntent)
//                .setCustomContentView(remoteViews)
//                .setSound(null)
//                .build();
//
//        startForeground(1, notification);
//
//        isPushNotifi = true;
//    }

    private void sendNotificationMedia() {
        try {
            bitmap = Glide.with(getBaseContext())
                        .asBitmap()
                        .load(song.getImage())
                        .submit(512, 512)
                        .get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Intent intent = new Intent(getBaseContext(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//
        NotificationCompat.Builder notification = new NotificationCompat.Builder(getBaseContext(), CHANNEL_ID)
                // Show controls on lock screen even when user hides sensitive content.
                .setContentTitle("Hello")
                .setContentText(song.getName())
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setSmallIcon(R.drawable.icon_music)
                .setOngoing(false)
                .setContentIntent(pendingIntent)
                // Add media control buttons that invoke intents in your media service
//                .addAction(R.drawable.icon_back, "Previous", getPendingIntent(this, ACTION_BACK)) // #0
//                .addAction(R.drawable.icon_pause, "Pause", getPendingIntent(this, ACTION_PAUSE))  // #1
//                .addAction(R.drawable.icon_next, "Next", getPendingIntent(this, ACTION_NEXT))     // #2
                // Apply the media style template
                .setLargeIcon(bitmap)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setShowActionsInCompactView(0, 1, 2 /* #1: pause button */));

        if (isPlaying) {
            notification.addAction(R.drawable.icon_back, "Previous", getPendingIntent(this, ACTION_BACK)) // #0
                    .addAction(R.drawable.icon_pause_2, "Pause", getPendingIntent(this, ACTION_PAUSE))  // #1
                    .addAction(R.drawable.icon_next, "Next", getPendingIntent(this, ACTION_NEXT));
        } else {
            notification.addAction(R.drawable.icon_back, "Previous", getPendingIntent(this, ACTION_BACK)) // #0
                    .addAction(R.drawable.ic_play_2, "Pause", getPendingIntent(this, ACTION_RESUME))  // #1
                    .addAction(R.drawable.icon_next, "Next", getPendingIntent(this, ACTION_NEXT));
        }

        Notification notification1 = notification.build();

        startForeground(1, notification1);

        isPushNotifi = true;
    }

//    private void changeButton() {
//        if (isPlaying) {
//            remoteViews.setOnClickPendingIntent(R.id.imgPlayOrPauseNotifi, getPendingIntent(this, ACTION_PAUSE));
//            remoteViews.setImageViewResource(R.id.imgPlayOrPauseNotifi, R.drawable.icon_pause_2);
//        } else {
//            remoteViews.setOnClickPendingIntent(R.id.imgPlayOrPauseNotifi, getPendingIntent(this, ACTION_RESUME));
//            remoteViews.setImageViewResource(R.id.imgPlayOrPauseNotifi, R.drawable.ic_play_2);
//        }
//    }

    private PendingIntent getPendingIntent(Context context, int action) {
        Intent intent = new Intent(this, MyReceiver.class);
        intent.putExtra("action_music", action);

        return PendingIntent.getBroadcast(context.getApplicationContext(), action, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
