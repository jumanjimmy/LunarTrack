package com.example.moonlover;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

public class MusicService extends Service {
    public static final String ACTION_PLAY = "com.example.moonlover.ACTION_PLAY";
    public static final String ACTION_PAUSE = "com.example.moonlover.ACTION_PAUSE";
    public static final String ACTION_RESUME = "com.example.moonlover.ACTION_RESUME";
    public static final String ACTION_STOP = "com.example.moonlover.ACTION_STOP";
    public static final String ACTION_MUTE = "com.example.moonlover.ACTION_MUTE";
    public static final String ACTION_UNMUTE = "com.example.moonlover.ACTION_UNMUTE";


    private MediaPlayer mediaPlayer;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = MediaPlayer.create(this, R.raw.borismoon);
        mediaPlayer.setLooping(true);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        if (action != null) {
            switch (action) {
                case ACTION_PLAY:
                case ACTION_RESUME:
                    if (!mediaPlayer.isPlaying()) {
                        mediaPlayer.start();
                        mediaPlayer.setLooping(true);
                    }
                    break;
                case ACTION_PAUSE:
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                    }
                    break;
                case ACTION_STOP:
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                    break;
                case ACTION_MUTE:
                    mediaPlayer.setVolume(0f, 0f);
                    break;
                case ACTION_UNMUTE:
                    mediaPlayer.setVolume(1f, 1f);
                    break;
            }
        }
        return START_NOT_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
        mediaPlayer.release();
    }


}
