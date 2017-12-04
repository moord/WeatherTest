package com.eqsian.testweather;

import android.content.Context;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.eqsian.testweather.databinding.ForecastListItemBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.eqsian.testweather.WeatherRecyclerViewCursorAdapter.mContext;


public class ForecastRecyclerAdapter extends RecyclerView.Adapter<ForecastRecyclerAdapter.ViewHolder> {
    private static final String TAG = "ForecastRecyclerAdapterr";
    List<Weather> mItems;

    public ForecastRecyclerAdapter(List<Weather> mItems) {
        this.mItems = mItems;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ForecastListItemBinding itemBinding;

        public ViewHolder(View itemView) {
            super(itemView);
            itemBinding = DataBindingUtil.bind(itemView);
        }

        public void bindCursor(Weather item) {

            Context context = itemBinding.getRoot().getContext();
            itemBinding.icon.setText(item.icon);
            itemBinding.icon.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/weather.ttf"));
            itemBinding.dtSky.setText(item.dt + " : " + item.description);
            itemBinding.minTemp.setText(context.getString(R.string.min_temp).toString() + ": " + item.minTemp);
            itemBinding.maxTemp.setText(context.getString(R.string.max_temp).toString() + ": " + item.maxTemp);
            itemBinding.humidity.setText(context.getString(R.string.humidity).toString() + ": " + item.humidity);
        }
    }

    @Override
    public ForecastRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.forecast_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ForecastRecyclerAdapter.ViewHolder holder, int position) {
        Weather item = mItems.get(position);
        holder.bindCursor(item);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }
}
