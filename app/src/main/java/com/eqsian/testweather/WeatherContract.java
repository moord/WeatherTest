package com.eqsian.testweather;

import android.provider.BaseColumns;


public class WeatherContract {

    private WeatherContract() {
    }

    public static final class LocationEntry implements BaseColumns {
        public static final String TABLE_NAME = "location";
        public static final String COLUMN_CITY_NAME = "city_name";
        public static final String COLUMN_DATE = "dt";
        public static final String COLUMN_TEMP = "temp";
        public static final String COLUMN_MIN_TEMP = "min_temp";
        public static final String COLUMN_MAX_TEMP = "max_temp";
        public static final String COLUMN_HUMIDITY = "humidity";
        public static final String COLUMN_PRESSURE = "pressure";
        public static final String COLUMN_ICON = "icon";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_STATUS = "status";

        public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " +
                TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_CITY_NAME + " TEXT, " +
                COLUMN_DATE + " TEXT, " +
                COLUMN_TEMP + " TEXT, " +
                COLUMN_MIN_TEMP + " TEXT, " +
                COLUMN_MAX_TEMP + " TEXT, " +
                COLUMN_HUMIDITY + " TEXT, " +
                COLUMN_PRESSURE + " TEXT, " +
                COLUMN_ICON + " TEXT, " +
                COLUMN_DESCRIPTION + " TEXT, " +
                COLUMN_STATUS + " TEXT " + ")";
    }
}
