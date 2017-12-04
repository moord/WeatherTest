package com.eqsian.testweather;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.eqsian.testweather.databinding.ActivityForecastBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class ForecastActivity extends AppCompatActivity {
    private static final String TAG = "ForecastActivity";

    private ActivityForecastBinding binding;
    List<Weather> mItems = new ArrayList<>();;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_forecast);

        // извлечь данные из bundle
        Intent intent = getIntent();

        HashMap<String, String> location = (HashMap<String, String>)intent.getSerializableExtra("location");

        String cityName = location.get(WeatherContract.LocationEntry.COLUMN_CITY_NAME);
        binding.data.setText(getResources().getString(R.string.last_update) + " " + location.get(WeatherContract.LocationEntry.COLUMN_DATE));
        binding.sky.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/weather.ttf"));
        binding.sky.setText(location.get(WeatherContract.LocationEntry.COLUMN_ICON));
        binding.temperature.setText(location.get(WeatherContract.LocationEntry.COLUMN_TEMP));
        binding.city.setText(cityName);
        binding.details.setText(location.get(WeatherContract.LocationEntry.COLUMN_DESCRIPTION) + "\n" + getResources().getString(R.string.humidity)
                + ": " + location.get(WeatherContract.LocationEntry.COLUMN_HUMIDITY)+ "\n" + getResources().getString(R.string.pressure)
                + ": " + location.get(WeatherContract.LocationEntry.COLUMN_PRESSURE));
        //binding.details.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        binding.details.setLineSpacing(0,1.4f);

        binding.rvForecast.setLayoutManager(new LinearLayoutManager(this));

        new FetchItemTask(cityName).execute();

        setupAdapter();
    }

    private  void setupAdapter(){
        binding.rvForecast.setAdapter(new ForecastRecyclerAdapter(mItems));
    }


    private class FetchItemTask extends AsyncTask<View, Void, List<Weather>> {
        private final String cityName;

        public FetchItemTask(String cityName) {
            this.cityName = cityName;
        }

        @Override
        protected List<Weather> doInBackground(View... params) {
            return new ForecastFetcher(ForecastActivity.this, cityName).fetchItems();
        }

        @Override
        protected void onPostExecute(List<Weather> items) {
            mItems = items;
            setupAdapter();
        }
    }

}
