package com.example.moonlover;



import static com.example.moonlover.MoonPhases.getMoonPhaseName;
import static com.example.moonlover.Settings.PREF_LANGUAGE;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import org.shredzone.commons.suncalc.MoonTimes;

public class Calculated extends AppCompatActivity {


    private TextView txtDate, txtMoonPhase, txtMoonRise, txtMoonSet;
    private ImageView moonPhoto;
    ImageButton leftButton, rightButton, btnBack, factButton;
    private Calendar calendar;
    private GestureDetector mGestureDetector;
    private LocalDate date;

    private final int[] ciekawostki = {
            R.string.fact1,
            R.string.fact2,
            R.string.fact3,
            R.string.fact4,
            R.string.fact5,
            R.string.fact6,
            R.string.fact8,
            R.string.fact9,
            R.string.fact10,
            // Dodaj więcej identyfikatorów stringów tutaj
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculated);

        String savedLanguage = getSavedLanguage();
        setLanguage(savedLanguage);

        ImageView arrow = findViewById(R.id.arrow);
        arrow.setVisibility(View.GONE);
        Animation arrowAppear = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
        Animation arrowSlide = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.arrow_vertical);
        arrowAppear.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                arrow.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                arrow.startAnimation(arrowSlide);
                arrow.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        arrow.startAnimation(arrowAppear);

        ImageView finger = findViewById(R.id.fingerSwipe);
        finger.setVisibility(View.GONE);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);


        factButton = findViewById(R.id.factButton);
        factButton.setOnClickListener(v -> showRandomFact());



        mGestureDetector = new GestureDetector(this, new MyGestureListener());
        calendar = Calendar.getInstance();
        txtMoonPhase = findViewById(R.id.txtMoonPhase);
        moonPhoto = findViewById(R.id.moonPhoto);

        txtDate = findViewById(R.id.Data);
        txtDate.setOnClickListener(view -> showDatePickerDialog(txtDate));

        txtMoonRise = findViewById(R.id.txtRise);
        txtMoonSet = findViewById(R.id.txtSet);


        leftButton = findViewById(R.id.btnLeft);
        leftButton.setOnClickListener(view -> {
            moveToLeftDay();
        });


        rightButton = findViewById(R.id.btnRight);
        rightButton.setOnClickListener(v -> {
            moveToRightDay();
        });

        btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMainPage();
            }
        });
    }


    private void showDatePickerDialog(final TextView txtDate) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String selectedDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                        txtDate.setText(selectedDate);
                        date = LocalDate.of(year, monthOfYear + 1, dayOfMonth);
                        MoonPhases moonPhases = MoonPhases.fromDate(date);
                        double phase = moonPhases.calculatePhase();
                        String phaseName = getMoonPhaseName(getApplicationContext(), phase);
                        txtMoonPhase.setText(phaseName);
                        moonPhoto.setImageResource(getResources().getIdentifier(getMoonImage(phase), "drawable", getPackageName()));
                        updateMoonTimes(date);

                        ImageView finger = findViewById(R.id.fingerSwipe);
                        Animation fingerAppear = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
                        Animation fingerSlide = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.finger_swipe);

                        fingerAppear.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {
                                finger.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                finger.startAnimation(fingerSlide);
                                finger.setVisibility(View.GONE);
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {
                            }
                        });


                        finger.startAnimation(fingerAppear);
                        Toast.makeText(Calculated.this, "Swipe to switch days!", Toast.LENGTH_LONG).show();


                        int daysToNextFullMoon = findNextFullMoonDays(date);
                        TextView txtNextMoon = findViewById(R.id.nextMoon);
                        String daysToFullMoonString = getString(R.string.days_to_full) + " " + daysToNextFullMoon;
                        txtNextMoon.setText(daysToFullMoonString);
                    }
                },
                year, month, dayOfMonth);
        datePickerDialog.show();
    }

    private int findNextFullMoonDays(LocalDate currentDate) {
        int daysToNextFullMoon = 0;
        double phase;
        LocalDate nextDate = currentDate;

        do {
            nextDate = nextDate.plusDays(1);
            MoonPhases moonPhases = MoonPhases.fromDate(nextDate);
            phase = moonPhases.calculatePhase();
            daysToNextFullMoon++;
        } while (phase < 0.5 || phase > 0.54);

        return daysToNextFullMoon;
    }

private void updateDaysToNextFullMoon(LocalDate date) {
    int daysToNextFullMoon = findNextFullMoonDays(date);
    TextView txtNextMoon = findViewById(R.id.nextMoon);
    String daysToFullMoonString = getString(R.string.days_to_full) + " " + daysToNextFullMoon;
    txtNextMoon.setText(daysToFullMoonString);

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
    public String getSavedLanguage() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        return sharedPreferences.getString(PREF_LANGUAGE, "");
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mGestureDetector.onTouchEvent(event)) {
            return true;
        }
        return super.onTouchEvent(event);
    }

    private void moveToLeftDay() {
        date = date.minusDays(1);
        updateSelectedDate(date);
        updateMoonImage(date);
        updateMoonTimes(date);
        updateDaysToNextFullMoon(date);
    }

    private void moveToRightDay() {
        date = date.plusDays(1);
        updateSelectedDate(date);
        updateMoonImage(date);
        updateMoonTimes(date);
        updateDaysToNextFullMoon(date);
    }



    private void updateSelectedDate(LocalDate date) {
        int year = date.getYear();
        int month = date.getMonthValue();
        int day = date.getDayOfMonth();
        String selectedDateStr = day + "/" + (month) + "/" + year;
        txtDate.setText(selectedDateStr);
    }

    private void updateMoonImage(LocalDate date) {
        MoonPhases moonPhases = MoonPhases.fromDate(date);
        double phase = moonPhases.calculatePhase();
        String phaseName = getMoonPhaseName(getApplicationContext(),phase);
        txtMoonPhase.setText(phaseName);
        moonPhoto.setImageResource(getResources().getIdentifier(getMoonImage(phase), "drawable", getPackageName()));
    }

    private void updateMoonTimes(LocalDate date) {


        // Sprawdzenie uprawnień do lokalizacji
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            // Pobranie lokalizacji użytkownika
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if (lastKnownLocation != null) {
                double latitude = lastKnownLocation.getLatitude();
                double longitude = lastKnownLocation.getLongitude();

                MoonTimes moonTimes = MoonTimes.compute()
                        .on(date)
                        .at(latitude, longitude)
                        .execute();

                // Pobieramy czasy wschodu i zachodu Księżyca
                ZonedDateTime moonRise = moonTimes.getRise();
                ZonedDateTime moonSet = moonTimes.getSet();

                // Wyświetlamy czasy wschodu i zachodu Księżyca w odpowiednich TextView
                if (moonRise != null) {
                    String riseTime = moonRise.format(DateTimeFormatter.ofPattern("HH:mm"));
                    String moonriseText = getString(R.string.moonrise) +"\n"+  riseTime;
                    txtMoonRise.setText(moonriseText);

                } else {
                    txtMoonRise.setText("Księżyc nie wschodzi tego dnia");
                }

                if (moonSet != null) {
                    String setTime = moonSet.format(DateTimeFormatter.ofPattern("HH:mm"));
                    String moonsetText = getString(R.string.moonset)+ "\n" + setTime;
                    txtMoonSet.setText(moonsetText);
                } else {
                    txtMoonSet.setText("Księżyc nie zachodzi tego dnia");
                }
            } else {
                // Jeżeli nie udało się pobrać lokalizacji użytkownika
                Toast.makeText(this, "Nie można pobrać lokalizacji użytkownika", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Jeżeli brak uprawnień do lokalizacji, poproś o nie
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }

    private void showRandomFact() {
        Random random = new Random();
        int index = random.nextInt(ciekawostki.length);
        String randomFact = getString(ciekawostki[index]);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.fact));
        builder.setMessage(randomFact);
        builder.setPositiveButton("OK!", null); // Dodajemy przycisk OK

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    @Override
    protected void onResume() {
        super.onResume();
        Intent resumeIntent = new Intent(this, MusicService.class);
        resumeIntent.setAction("com.example.moonlover.ACTION_RESUME");
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
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
    }

    public void openMainPage() {
        Intent openMain = new Intent(this, MainActivity.class);
        startActivity(openMain);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }


    public String getMoonImage(double phase) {
        if (phase <= 0.02) {
            return "moon_0"; //         Nów
        }else if (phase <= 0.03){
            return "moon_1";
        }else if (phase <= 0.059) {
            return "moon_2";
        } else if (phase <= 0.095) {
            return "moon_3";
        } else if (phase <= 0.128) {
            return "moon_4";
        } else if (phase <= 0.16) {
            return "moon_5";
        } else if (phase <= 0.195) {
            return "moon_6";
        } else if (phase <= 0.228) {
            return "moon_7";
        } else if (phase <= 0.265) {
            return "moon_8"; //         I kwadra
        } else if (phase <= 0.298) {
            return "moon_9";
        } else if (phase <= 0.334) {
            return "moon_10";
        } else if (phase <= 0.366) {
            return "moon_11";
        } else if (phase <= 0.399) {
            return "moon_12";
        } else if (phase <= 0.433) {
            return "moon_13";
        } else if (phase <= 0.468) {
            return "moon_14";
        } else if (phase <= 0.499) {
            return "moon_15";
        } else if (phase <= 0.54) {
            return "moon_16"; //        Pełnia
        } else if (phase <= 0.562) {
            return "moon_17";
        } else if (phase <= 0.595) {
            return "moon_18";
        } else if (phase <= 0.628) {
            return "moon_19";
        } else if (phase <= 0.661) {
            return "moon_20";
        } else if (phase <= 0.694) {
            return "moon_21";
        } else if (phase <= 0.727) {
            return "moon_22";
        } else if (phase <= 0.76) {
            return "moon_23"; //        III Kwadra
        } else if (phase <= 0.793) {
            return "moon_24";
        } else if (phase <= 0.826) {
            return "moon_25";
        } else if (phase <= 0.859) {
            return "moon_26";
        } else if (phase <= 0.892) {
            return "moon_27";
        } else if (phase <= 0.925) {
            return "moon_28";
        } else if (phase <= 0.958) {
            return "moon_29";
        } else if (phase <= 0.99) {
            return "moon_30"; //        Cienki sierp
        } else {
            return "moon_0";
        }
    }

    private class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            boolean result = false;
            try {
                float diffX = e2.getX() - e1.getX();
                if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffX > 0) {
                        // Swipe left
                        moveToLeftDay();
                        result = true;
                    } else {
                        // Swipe right
                        moveToRightDay();
                        result = true;
                    }
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            return result;
        }
    }
}

