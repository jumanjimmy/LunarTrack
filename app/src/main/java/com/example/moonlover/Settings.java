package com.example.moonlover;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Locale;

public class Settings extends AppCompatActivity {

    ImageButton muteButton;
    ImageButton btnEnglish, btnPolish, btnBack, infoButton;
    boolean isMusicPlaying = true;
    TextView txtMusic;
    ImageView settingsView;
    private static final String PREF_MUSIC_STATE = "pref_music_state";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        txtMusic = findViewById(R.id.txtMusic);

        btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMainPage();
            }
        });

        btnEnglish = findViewById(R.id.uk_flag);
        btnEnglish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setLanguage("en");
                saveLanguage("en");
                saveMusicState();
                recreate();
            }
        });

        btnPolish = findViewById(R.id.pl_flag);
        btnPolish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setLanguage("pl");
                saveLanguage("pl");
                saveMusicState();
                recreate();
            }
        });

        settingsView = findViewById(R.id.settingsView);
        String savedLanguage = getSavedLanguage();
        if (savedLanguage.equals("pl")) {
            settingsView.setImageResource(R.drawable.settings_text_pl);
        } else {
            settingsView.setImageResource(R.drawable.settings_text);
        }

        muteButton = findViewById(R.id.imgbSoundButton);
        isMusicPlaying = getMusicState(); // Odczytaj zapisany stan muzyki

        if (isMusicPlaying) {
            muteButton.setImageResource(android.R.drawable.ic_lock_silent_mode_off);
            txtMusic.setText(R.string.music_on);
        } else {
            muteButton.setImageResource(android.R.drawable.ic_lock_silent_mode);
            txtMusic.setText(R.string.music_off);
        }
        muteButton.setOnClickListener(v -> {
            Intent pauseIntent = new Intent(this, MusicService.class);
            if (isMusicPlaying) {
                pauseIntent.setAction("com.example.moonlover.ACTION_MUTE");
                muteButton.setImageResource(android.R.drawable.ic_lock_silent_mode);
                txtMusic.setText(R.string.music_off);

                isMusicPlaying = false;
            } else {
                pauseIntent.setAction("com.example.moonlover.ACTION_UNMUTE");
                muteButton.setImageResource(android.R.drawable.ic_lock_silent_mode_off);
                Intent resumeIntent = new Intent(this, MusicService.class);
                txtMusic.setText(R.string.music_on);
                startService(resumeIntent);
                isMusicPlaying = true;
            }
            startService(pauseIntent);
        });

        infoButton = findViewById(R.id.infoButton);
        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showInfo();
            }
        });

    }

    public void openMainPage() {
        Intent openMain = new Intent(this, MainActivity.class);
        startActivity(openMain);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
    public void setLanguage(String languageCode){
        Resources resources = this.getResources();
        Configuration configuration = resources.getConfiguration();
        Locale locale = new Locale(languageCode);
        locale.setDefault(locale);
        configuration.setLocale(locale);
        resources.updateConfiguration(configuration,
                resources.getDisplayMetrics());
    }

    private void showInfo(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Info");
        builder.setMessage(getString(R.string.info));
        builder.setPositiveButton("OK!", null); // Dodajemy przycisk OK

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    public static final String PREF_LANGUAGE = "pref_language";

    public void saveLanguage(String languageCode) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PREF_LANGUAGE, languageCode);
        editor.apply();
        setLanguage(languageCode);
    }

    public String getSavedLanguage() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        return sharedPreferences.getString(PREF_LANGUAGE, "");
    }

    private void saveMusicState() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(PREF_MUSIC_STATE, isMusicPlaying);
        editor.apply();
    }
    private boolean getMusicState() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        return sharedPreferences.getBoolean(PREF_MUSIC_STATE, true);
    }

    private void updateMuteButtonState() {
        if (isMusicPlaying) {
            muteButton.setImageResource(android.R.drawable.ic_lock_silent_mode_off);
            txtMusic.setText(R.string.music_on);
        } else {
            muteButton.setImageResource(android.R.drawable.ic_lock_silent_mode);
            txtMusic.setText(R.string.music_off);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent resumeIntent = new Intent(this, MusicService.class);
        resumeIntent.setAction("com.example.moonlover.ACTION_RESUME");
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
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
    }
}