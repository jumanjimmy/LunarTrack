package com.example.moonlover;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class MainActivity extends AppCompatActivity {

    private ImageButton pauseButton;
    private Intent music;
    private boolean isMusicPlaying = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView backgroundImageView = findViewById(R.id.imageBackground);
        Glide.with(this).asGif().load(R.drawable.andromeda_animated).into(backgroundImageView);


        music = new Intent(this, MusicService.class);
        startService(music);


        Button btnLets = findViewById(R.id.btnLetsStart);
        btnLets.setOnClickListener(v -> {
            openMainPage();
        });

        pauseButton = findViewById(R.id.imgbSoundButton);
        pauseButton.setOnClickListener(v -> {
            Intent pauseIntent = new Intent(this, MusicService.class);
            if (isMusicPlaying) {
                pauseIntent.setAction("com.example.moonlover.ACTION_MUTE");
//                pauseButton.setImageResource(R.drawable.muted);
                pauseButton.setImageResource(android.R.drawable.ic_lock_silent_mode);
                isMusicPlaying = false;
            } else {
                pauseIntent.setAction("com.example.moonlover.ACTION_UNMUTE");
                pauseButton.setImageResource(android.R.drawable.ic_lock_silent_mode_off);
                Intent resumeIntent = new Intent(this, MusicService.class);
                startService(resumeIntent);
                isMusicPlaying = true;
            }
            startService(pauseIntent);
        });


    }


    @Override
    protected void onResume() {
        super.onResume();
        Intent resumeIntent = new Intent(this, MusicService.class);
        resumeIntent.setAction("com.example.moonlover.ACTION_PLAY");
        startService(resumeIntent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Intent intent = new Intent(this, MusicService.class);
        intent.setAction("com.example.moonlover.ACTION_PAUSE");
        startService(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(music);
    }

    public void openMainPage() {
        Intent openMain = new Intent(this, Calculated.class);
        startActivity(openMain);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }


}