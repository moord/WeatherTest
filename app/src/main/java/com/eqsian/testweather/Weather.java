package com.eqsian.testweather;

import android.content.Context;
import android.util.Log;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;


public class Weather {
    private static final String TAG = "Weather";
    public final String dt;
    public final String temp;
    public final String minTemp;
    public final String maxTemp;
    public final String humidity;
    public final String pressure;
    public final String description;
    public final String icon;


    public Weather(Context context, long timeStamp, double temp, double minTemp, double maxTemp, double humidity, double pressure, int iconId, long sunrise, long sunset, String description) {

        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setMaximumFractionDigits(1);

        this.dt = convertTimeStampToDateTime(timeStamp);
        this.temp = numberFormat.format(temp) + "\u00B0C";
        this.minTemp = numberFormat.format(minTemp) + "\u00B0C";
        this.maxTemp = numberFormat.format(maxTemp) + "\u00B0C";
        this.humidity = NumberFormat.getPercentInstance().format(humidity / 100.0);
        this.pressure = numberFormat.format(pressure) + "hPa";
        this.icon = setWeatherIcon(context, iconId, timeStamp, sunrise, sunset);
        this.description = description;
    }


    private static String convertTimeStampToDateTime(long timeStamp) {
        Calendar calendar = Calendar.getInstance(); // create Calendar
        calendar.setTimeInMillis(timeStamp * 1000); // set time
        //TimeZone tz = TimeZone.getDefault(); // get device's time zone

        //calendar.add(Calendar.MILLISECOND, tz.getOffset(calendar.getTimeInMillis()));

        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        return dateFormatter.format(calendar.getTime());
    }

    private String setWeatherIcon(Context context, int actualId, long currentTime, long sunrise, long sunset) {
        int id = actualId / 100;
        String icon = "";
        if (actualId == 800) {

            if (currentTime >= sunrise && currentTime <= sunset) {
                icon = context.getString(R.string.weather_sunny);
            } else {
                icon = context.getString(R.string.weather_clear_night);
            }
        } else {
            switch (id) {
                case 2:
                    icon = context.getString(R.string.weather_thunder);
                    break;
                case 3:
                    icon = context.getString(R.string.weather_drizzle);
                    break;
                case 5:
                    icon = context.getString(R.string.weather_rainy);
                    break;
                case 6:
                    icon = context.getString(R.string.weather_snowy);
                    break;
                case 7:
                    icon = context.getString(R.string.weather_foggy);
                    break;
                case 8:
                    icon = context.getString(R.string.weather_cloudy);
                    break;
            }
        }

        return icon;
    }
}
