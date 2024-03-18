package com.example.moonlover;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private ImageButton settingsButton, btnLets;
    private Intent music;
    private boolean isMusicPlaying = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        String savedLanguage = getSavedLanguage();
        setLanguage(savedLanguage);

        ImageView backgroundImageView = findViewById(R.id.imgBackground);
        Glide.with(this).asGif().load(R.drawable.andromeda).into(backgroundImageView);


        music = new Intent(this, MusicService.class);
        startService(music);


        btnLets = findViewById(R.id.btnLetsStart);
        btnLets.setOnClickListener(v -> {
            openMainPage();
        });

        settingsButton = findViewById(R.id.imgSettingsButton);
        settingsButton.setOnClickListener(v -> {
            openSettings();
        });




    }

    public static final String PREF_LANGUAGE = "pref_language";


    public void setLanguage(String languageCode){
        Resources resources = this.getResources();
        Configuration configuration = resources.getConfiguration();
        Locale locale = new Locale(languageCode);
        locale.setDefault(locale);
        configuration.setLocale(locale);
        resources.updateConfiguration(configuration,
                resources.getDisplayMetrics());
    }


    public String getSavedLanguage() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        return sharedPreferences.getString(PREF_LANGUAGE, "");
    }


    @Override
    protected void onResume() {
        super.onResume();
        Intent resumeIntent = new Intent(this, MusicService.class);
        resumeIntent.setAction("com.example.moonlover.ACTION_PLAY");
        startService(resumeIntent);
        String savedLanguage = getSavedLanguage();
        setLanguage(savedLanguage);
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
    public void openSettings() {
        Intent openSettings = new Intent(this, Settings.class);
        startActivity(openSettings);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }


}