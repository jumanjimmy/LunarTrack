package com.example.moonlover;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.TimeZone;

public class MoonPhase {

    private double longitude;
    private double latitude;
    private TimeZone timeZone;
    public static final double SYNODIC_MONTH = 29.530588853; // Długość miesiąca synodycznego w dniach

    public static MoonPhase fromDate(LocalDate date) {
        return new MoonPhase(date);
    }

    private final LocalDate date;

    private MoonPhase(LocalDate date) {
        this.date = date;
    }

    /**
     * Oblicza fazę Księżyca na podstawie daty.
     *
     * @return Faza Księżyca jako wartość z zakresu [0, 1].
     */
    public double calculatePhase() {
        LocalDate baseDate = LocalDate.of(2000, 1, 6); // Bazowa data do obliczenia fazy Księżyca
        long daysSinceBase = ChronoUnit.DAYS.between(baseDate, date);
        double phase = (daysSinceBase % SYNODIC_MONTH) / SYNODIC_MONTH;
        return (phase < 0) ? (phase + 1) : phase; // Ustalenie przedziału [0, 1]
    }
    private LocalTime getMoonrise(LocalDate date, double latitude, double longitude) {
        TimeZone timeZone = TimeZone.getTimeZone("Europe/Warsaw"); // Strefa czasowa dla Słupska
        double jd = getJulianDate(date); // Julian Date dla wybranej daty
        double moonPhase = getMoonPhase(jd); // Faza Księżyca dla wybranej daty
        double riseTime = getMoonRiseTime(jd, moonPhase, latitude, longitude); // Czas wschodu Księżyca dla wybranej daty i miejsca
        LocalTime moonrise = LocalTime.ofSecondOfDay((long) (riseTime * 3600)); // Konwersja na LocalTime
        return moonrise;
    }

    public double getJulianDate(LocalDate date) {
        int year = date.getYear();
        int month = date.getMonthValue();
        int day = date.getDayOfMonth();
        int hour = 0;
        int minute = 0;
        int second = 0;
        return getJulianDate(year, month, day, hour, minute, second);
    }

    public double getJulianDate(int year, int month, int day, int hour, int minute, int second) {
        double y = year;
        double m = month;
        double d = day + (hour + minute / 60.0 + second / 3600.0) / 24.0;
        if (month < 3) {
            y -= 1;
            m += 12;
        }
        double a = Math.floor(y / 100);
        double b = 2 - a + Math.floor(a / 4);
        double jd = Math.floor(365.25 * (y + 4716)) + Math.floor(30.6001 * (m + 1)) + d + b - 1524.5;
        return jd;
    }

    private double getMoonPhase(double jd) {
        double c = jd - 2451545.0;
        double g = Math.toRadians(357.529 + 0.98560028 * c);
        double lambda = Math.toRadians(280.459 + 0.98564736 * c);
        double e = 0.016708617 * Math.sin(g) + 0.00000042 * Math.sin(2 * g);
        double sinLambda = Math.sin(lambda);
        double ec = Math.toRadians(23.439 - 0.00000036 * c);
        double sinEc = Math.sin(ec);
        double y = Math.pow(Math.tan(0.5 * (Math.PI / 2 - ec)), 2);
        double phase = 0.5 + 0.5 * (sinLambda * Math.cos(ec) - y * sinEc) / (1 - Math.pow(e, 2));
        return phase;
    }

    private double getMoonRiseTime(double jd, double moonPhase, double latitude, double longitude) {
        double lw = Math.toRadians(-longitude);
        double phi = Math.toRadians(latitude);
        double sinDec = Math.sin(Math.asin(0.39782 * Math.sin(Math.toRadians(360.0 * (moonPhase + 0.25)))));
        double cosDec = Math.sqrt(1 - Math.pow(sinDec, 2));
        double sinPhi = Math.sin(phi);
        double cosPhi = Math.sqrt(1 - Math.pow(sinPhi, 2));
        double cosH = (Math.sin(Math.toRadians(-0.8333)) - sinPhi * sinDec) / (cosPhi * cosDec);
        if (cosH < -1 || cosH > 1) {
            return Double.NaN;
        }
        double H = Math.acos(cosH);
        double UT = (jd - 2451545.0) / 365.25;
        double localOffset = (lw / Math.toRadians(15.0)) / 24.0;
        double moonrise = 2451545.0 + UT - localOffset - (H / Math.toRadians(15.0 * 24.0));
        return moonrise;
    }




    public static String getMoonPhaseName(double moonPhase) {
        if (moonPhase < 0.02) {
            return "NÓW";
        } else if (moonPhase < 0.24) {
            return "SIERP PRZYBYWAJĄCY";
        } else if (moonPhase >= 0.24 && moonPhase < 0.276) {
            return "PIERWSZA KWADRA";
        } else if (moonPhase < 0.5) {
            return "KSIĘŻYC GARBATY PRZYBYWAJĄCY";
        } else if (moonPhase >= 0.5 && moonPhase <= 0.54) {
            return "PEŁNIA";
        } else if (moonPhase < 0.7355) {
            return "KSIĘŻYC GARBATY UBYWAJĄCY";
        } else if (moonPhase >= 0.7355 && moonPhase < 0.769) {
            return "TRZECIA KWADRA";
        } else if (moonPhase < 0.98){
            return "SIERP UBYWAJĄCY";
        } else {
            return "NÓW";
        }
    }

}




