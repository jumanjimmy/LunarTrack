package com.example.moonlover;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;

public class MusicPlayer extends Service implements MediaPlayer.OnCompletionListener {
    public MediaPlayer boris;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("MusicPlayer", "onStartCommand: called");
        boris = MediaPlayer.create(this, R.raw.borismoon);
        boris.setLooping(true);
        boris.start();
        Log.d("MusicPlayer", "onStartCommand: started playing music");
        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        boris.release(); //zwolnij zasoby odtwarzacza
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        //tu możesz zdefiniować, co ma się dziać, gdy muzyka dobiegnie końca
    }
}

