package com.eqsian.testweather;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.eqsian.testweather.databinding.ActivityWeatherBinding;

import java.util.HashMap;


public class WeatherActivity extends AppCompatActivity {
    private static final String TAG = "WeatherActivity";

    private ActivityWeatherBinding binding;

    SQLiteDatabase db;

    WeatherRecyclerViewCursorAdapter weatherAdapter;

    Weather mWeather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_weather);

        binding.rvData.setLayoutManager(new LinearLayoutManager(this));

        db = new WeatherDBHelper(this).getWritableDatabase();

        weatherAdapter = new WeatherRecyclerViewCursorAdapter(this, getAllLocations());
        weatherAdapter.setOnItemClickListener(new WeatherRecyclerViewCursorAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View viewHolder, int position, View v) {
                switch (v.getId()) {
                    case R.id.ibDelete:
                        delLocation(weatherAdapter.getId(position));
                        weatherAdapter.changeCursor(getAllLocations());
                        break;
                    default:
                        // вызываем новую активити с прогнозом
                        Cursor mCursor = getLocation(weatherAdapter.getId(position));

                        mCursor.moveToFirst();

                        HashMap<String, String> location = new HashMap<>();

                        location.put(WeatherContract.LocationEntry.COLUMN_CITY_NAME, mCursor.getString(mCursor.getColumnIndexOrThrow(WeatherContract.LocationEntry.COLUMN_CITY_NAME)));
                        location.put(WeatherContract.LocationEntry.COLUMN_TEMP, mCursor.getString(mCursor.getColumnIndexOrThrow(WeatherContract.LocationEntry.COLUMN_TEMP)));
                        location.put(WeatherContract.LocationEntry.COLUMN_DATE, mCursor.getString(mCursor.getColumnIndexOrThrow(WeatherContract.LocationEntry.COLUMN_DATE)));
                        location.put(WeatherContract.LocationEntry.COLUMN_HUMIDITY, mCursor.getString(mCursor.getColumnIndexOrThrow(WeatherContract.LocationEntry.COLUMN_HUMIDITY)));
                        location.put(WeatherContract.LocationEntry.COLUMN_PRESSURE, mCursor.getString(mCursor.getColumnIndexOrThrow(WeatherContract.LocationEntry.COLUMN_PRESSURE)));
                        location.put(WeatherContract.LocationEntry.COLUMN_ICON, mCursor.getString(mCursor.getColumnIndexOrThrow(WeatherContract.LocationEntry.COLUMN_ICON)));
                        location.put(WeatherContract.LocationEntry.COLUMN_DESCRIPTION, mCursor.getString(mCursor.getColumnIndexOrThrow(WeatherContract.LocationEntry.COLUMN_DESCRIPTION)));

                        Intent intent = new Intent(WeatherActivity.this, ForecastActivity.class);
                        intent.putExtra("location", location);

                        startActivity(intent);

                        break;
                }
            }
        });

        weatherAdapter.setUpdateItem(new WeatherRecyclerViewCursorAdapter.UpdateItem() {
            @Override
            public void set(String cityName, Weather weather) {
                updateLocation(cityName, weather);
            }
        });

        binding.rvData.setAdapter(weatherAdapter);

        binding.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(binding.cityNameEditText.getText().toString().length()>0) {
                    dismissKeyboard(binding.cityNameEditText);
                    addLocation(binding.cityNameEditText.getText().toString());
                    weatherAdapter.changeCursor(getAllLocations());
                    binding.cityNameEditText.setText("");
                }
            }
        });
        binding.btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshLocations();
                weatherAdapter.changeCursor(getAllLocations());
            }
        });

    }

    private void dismissKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
    
    @Override
    protected void onDestroy() {
        db.close();
        super.onDestroy();
    }

    Cursor getAllLocations() {
        String[] queryCols = new String[]{WeatherContract.LocationEntry._ID,
                WeatherContract.LocationEntry.COLUMN_CITY_NAME,
                WeatherContract.LocationEntry.COLUMN_DATE,
                WeatherContract.LocationEntry.COLUMN_HUMIDITY,
                WeatherContract.LocationEntry.COLUMN_PRESSURE,
                WeatherContract.LocationEntry.COLUMN_ICON,
                WeatherContract.LocationEntry.COLUMN_DESCRIPTION,
                WeatherContract.LocationEntry.COLUMN_STATUS,
                WeatherContract.LocationEntry.COLUMN_TEMP};

        Cursor cursor = db.query(
                WeatherContract.LocationEntry.TABLE_NAME,     // The table to query
                queryCols,                                // The columns to return
                null,                                     // The columns for the WHERE clause
                null,                                     // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                      // don't sort
        );

        return cursor;
        //binding.rvData.setAdapter(new WeatherRecyclerViewCursorAdapter(cursor));
    }

    Cursor getLocation(long id) {
        String[] queryCols = new String[]{WeatherContract.LocationEntry._ID,
                WeatherContract.LocationEntry.COLUMN_CITY_NAME,
                WeatherContract.LocationEntry.COLUMN_DATE,
                WeatherContract.LocationEntry.COLUMN_HUMIDITY,
                WeatherContract.LocationEntry.COLUMN_PRESSURE,
                WeatherContract.LocationEntry.COLUMN_ICON,
                WeatherContract.LocationEntry.COLUMN_DESCRIPTION,
                WeatherContract.LocationEntry.COLUMN_STATUS,
                WeatherContract.LocationEntry.COLUMN_TEMP};

        String selection = WeatherContract.LocationEntry._ID + " = ?";

        String[] selectionArgs = new String[]{String.valueOf(id)};

        Cursor cursor = db.query(
                WeatherContract.LocationEntry.TABLE_NAME,     // The table to query
                queryCols,                                // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                                     // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                      // don't sort
        );

        return cursor;
        //binding.rvData.setAdapter(new WeatherRecyclerViewCursorAdapter(cursor));
    }

    private void addLocation(String cityName) {
        ContentValues cv = new ContentValues();

        cv.put(WeatherContract.LocationEntry.COLUMN_CITY_NAME, cityName);
        cv.put(WeatherContract.LocationEntry.COLUMN_STATUS, "init");
        db.insert(WeatherContract.LocationEntry.TABLE_NAME, null, cv);
    }

    private void updateLocation(String cityName, Weather weather) {
        ContentValues cv = new ContentValues();

        cv.put(WeatherContract.LocationEntry.COLUMN_STATUS, "ready");
        cv.put(WeatherContract.LocationEntry.COLUMN_DATE, weather.dt);
        cv.put(WeatherContract.LocationEntry.COLUMN_TEMP, weather.temp);
        cv.put(WeatherContract.LocationEntry.COLUMN_MIN_TEMP, weather.minTemp);
        cv.put(WeatherContract.LocationEntry.COLUMN_MAX_TEMP, weather.maxTemp);
        cv.put(WeatherContract.LocationEntry.COLUMN_HUMIDITY, weather.humidity);
        cv.put(WeatherContract.LocationEntry.COLUMN_PRESSURE, weather.pressure);
        cv.put(WeatherContract.LocationEntry.COLUMN_DESCRIPTION, weather.description);
        cv.put(WeatherContract.LocationEntry.COLUMN_ICON, weather.icon);

        db.update(WeatherContract.LocationEntry.TABLE_NAME, cv, WeatherContract.LocationEntry.COLUMN_CITY_NAME + " = '" + cityName + "'", null);
    }

    private void refreshLocations() {
        ContentValues cv = new ContentValues();

        cv.put(WeatherContract.LocationEntry.COLUMN_STATUS, "refresh");

        db.update(WeatherContract.LocationEntry.TABLE_NAME, cv, null, null);
    }

    private void delLocation(long id) {
        db.delete(WeatherContract.LocationEntry.TABLE_NAME, WeatherContract.LocationEntry._ID + " = " + id, null);
    }
}
