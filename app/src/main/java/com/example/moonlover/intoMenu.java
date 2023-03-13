package com.example.moonlover;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import java.util.Calendar;

public class intoMenu extends AppCompatActivity {

    public MusicPlayer Music;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_into_menu);

        Music = new MusicPlayer();

        TextView txtDay = findViewById(R.id.txtDay);
        txtDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog(txtDay);

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
      //  Music.stopMusic();
    }

    @Override
    protected void onResume() {
        super.onResume();
       // Music.playMusic();
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