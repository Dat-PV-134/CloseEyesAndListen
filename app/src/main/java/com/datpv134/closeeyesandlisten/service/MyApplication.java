package com.datpv134.closeeyesandlisten.service;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;

import com.datpv134.closeeyesandlisten.model.Song;

public class MyApplication extends Application {

    public static final String CHANNEL_ID = "CHANNEL_ID";
    public static MediaPlayer mediaPlayer = new MediaPlayer();
    public static Song song;
    public static Boolean isRunning = false;
    public static int playNumber = 0;

    @Override
    public void onCreate() {
        super.onCreate();

        song = new Song("-1", "-1", "-1", "-1");

        createNotificationChannel();
    }

    public Song getCurrentSong() {
        return song;
    }

    public void setCurrentSong(Song song) {
        this.song = song;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "channel", NotificationManager.IMPORTANCE_DEFAULT);

            channel.setSound(null, null);

            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }
}
