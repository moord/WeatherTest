package com.eqsian.testweather;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class WeatherFetcher {
    private static final String TAG = "WeatherFetcher";
    public static final String API_KEY = "b849169e8518a852747d3feae7403e88";

    private Context mContext;
    private final String cityName;

    public WeatherFetcher(Context context, String city) {
        mContext = context;
        cityName = city;
    }

    public String getJSONString(String UrlSpec) throws IOException {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(UrlSpec)
                .build();
        Response responce = client.newCall(request).execute();

        return responce.body().string();
    }

    public Weather fetchItem() {
        Weather weather = null;
        try {
            String url = Uri.parse("http://api.openweathermap.org/data/2.5/weather")
                    .buildUpon()
                    .appendQueryParameter("q", cityName)
                    .appendQueryParameter("APPID", API_KEY)
                    .appendQueryParameter("units", "metric")
                    .appendQueryParameter("lang", "ru")
                    .build().toString();
            String jsonString = getJSONString(url);
            JSONObject jsonBody = new JSONObject(jsonString);
            weather = parseItem(jsonBody);
        } catch (IOException ioe) {
            Log.e(TAG, "Ошибка загрузки данных", ioe);
        } catch (JSONException joe) {
            Log.e(TAG, "Ошибка парсинга JSON", joe);
        }
        return weather;
    }

    private Weather parseItem(JSONObject jsonBody) throws IOException, JSONException {

        if (jsonBody.getInt("cod") != 200) return null;

        JSONObject details = jsonBody.getJSONArray("weather").getJSONObject(0);
        JSONObject main = jsonBody.getJSONObject("main");
        JSONObject sys = jsonBody.getJSONObject("sys");

        return new Weather(mContext,
                jsonBody.getLong("dt"),
                main.getDouble("temp"),
                main.getDouble("temp_min"),
                main.getDouble("temp_max"),
                main.getDouble("humidity"),
                main.getDouble("pressure"),
                details.getInt("id"),
                sys.getLong("sunrise"),
                sys.getLong("sunset"),
                details.getString("description"));
    }
}
