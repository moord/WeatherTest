package com.eqsian.testweather;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class WeatherDBHelper extends SQLiteOpenHelper {
    private static final String TAG = "WeatherDBHelper";

    private static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "weather_new_db";

    public WeatherDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(WeatherContract.LocationEntry.CREATE_TABLE);
        ContentValues cv = new ContentValues();

        cv.put(WeatherContract.LocationEntry.COLUMN_CITY_NAME, "Saint Petersburg");
        cv.put(WeatherContract.LocationEntry.COLUMN_STATUS, "init");
        db.insert(WeatherContract.LocationEntry.TABLE_NAME, null, cv);

        cv.put(WeatherContract.LocationEntry.COLUMN_CITY_NAME, "Moscow");
        cv.put(WeatherContract.LocationEntry.COLUMN_STATUS, "refresh");
        db.insert(WeatherContract.LocationEntry.TABLE_NAME, null, cv);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + WeatherContract.LocationEntry.CREATE_TABLE );
        onCreate(db);
    }
}
