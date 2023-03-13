package com.example.moonlover;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    Intent musicIntent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        musicIntent = new Intent(this, MusicPlayer.class);
//        startService(musicIntent);
        MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.borismoon);
        MusicThread musicThread = new MusicThread(mediaPlayer);
        musicThread.start();

        Button btnLets = (Button) findViewById(R.id.btnLetsStart);
        btnLets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMainPage();
            }
        });
    }

    public void openMainPage(){
        Intent openMain = new Intent(this,intoMenu.class);
        startActivity(openMain);

    }
    private void showDatePickerDialog(final TextView dateTextView) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // Ustawienie wybranej daty w elemencie TextView
                        String selectedDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                        dateTextView.setText(selectedDate);
                    }
                },
                year, month, dayOfMonth);
        datePickerDialog.show();
    }


}