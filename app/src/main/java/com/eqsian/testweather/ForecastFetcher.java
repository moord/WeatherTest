package com.eqsian.testweather;


import android.content.Context;
import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ForecastFetcher {
    private static final String TAG = "ForecastFetcher";
    private static final String API_KEY =  WeatherFetcher.API_KEY;

    private Context mContext;
    private final String cityName;

    public ForecastFetcher(Context context, String city) {
        mContext = context;
        cityName = city;
    }

    public String getJSONString(String UrlSpec) throws IOException {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(UrlSpec)
                .build();
        Response responce = client.newCall(request).execute();
        String result = responce.body().string();
        return result;
    }

    public List<Weather> fetchItems() {
        List<Weather> weatherItems = new ArrayList<>();
        try {
            String url = Uri.parse("http://api.openweathermap.org/data/2.5/forecast/daily")
                    .buildUpon()
                    .appendQueryParameter("q", cityName)
                    .appendQueryParameter("APPID", API_KEY)
                    .appendQueryParameter("units", "metric")
                    .appendQueryParameter("lang", "ru")
                    .appendQueryParameter("cnt", "7")
                    .build().toString();
            String jsonString = getJSONString(url);
            JSONObject jsonBody = new JSONObject(jsonString);
            parseItems(weatherItems,jsonBody);
        } catch (IOException ioe) {
            Log.e(TAG, "Ошибка загрузки данных", ioe);
        } catch (JSONException joe) {
            Log.e(TAG, "Ошибка парсинга JSON", joe);
        }
        return weatherItems;
    }

    private void parseItems(List<Weather> items, JSONObject jsonBody) throws IOException, JSONException {

        if (jsonBody.getInt("cod") != 200) return;

        JSONArray listJsonArray = jsonBody.getJSONArray("list");

        for (int i = 0; i < listJsonArray.length(); i++) {
            JSONObject jsonItem = listJsonArray.getJSONObject(i);

            JSONObject details = jsonItem.getJSONArray("weather").getJSONObject(0);
            JSONObject temp = jsonItem.getJSONObject("temp");

            items.add(new Weather(mContext,
                    jsonItem.getLong("dt"),
                    temp.getDouble("day"),
                    temp.getDouble("min"),
                    temp.getDouble("max"),
                    jsonItem.getDouble("humidity"),
                    jsonItem.getDouble("pressure"),
                    details.getInt("id"),
                    jsonItem.getLong("dt"),
                    jsonItem.getLong("dt"),
                    details.getString("description")));
        }
    }

}
