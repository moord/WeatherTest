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

import com.eqsian.testweather.databinding.LocationListItemBinding;


public class WeatherRecyclerViewCursorAdapter extends RecyclerView.Adapter<WeatherRecyclerViewCursorAdapter.ViewHolder> {
    private static final String TAG = "WeatherRecyclerViewCursorAdapter";

    Cursor mCursor;
    static Context mContext;
    public static OnItemClickListener listener;
    public static UpdateItem updateItem;


    public WeatherRecyclerViewCursorAdapter(Context context, Cursor mCursor) {
        this.mCursor = mCursor;
        this.mContext = context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        LocationListItemBinding itemBinding;

        public ViewHolder(View itemView) {
            super(itemView);
            itemBinding = DataBindingUtil.bind(itemView);
        }

        public void bindCursor(Cursor cursor) {

            itemBinding.cityName.setText(cursor.getString(cursor.getColumnIndexOrThrow(WeatherContract.LocationEntry.COLUMN_CITY_NAME)));
            itemBinding.temp.setText(cursor.getString(cursor.getColumnIndexOrThrow(WeatherContract.LocationEntry.COLUMN_TEMP)));
            itemBinding.tvIcon.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "fonts/weather.ttf"));
            itemBinding.tvIcon.setText(cursor.getString(cursor.getColumnIndexOrThrow(WeatherContract.LocationEntry.COLUMN_ICON)));
            itemBinding.tvDate.setText(cursor.getString(cursor.getColumnIndexOrThrow(WeatherContract.LocationEntry.COLUMN_DATE)));

            if (!cursor.getString(cursor.getColumnIndexOrThrow(WeatherContract.LocationEntry.COLUMN_STATUS)).equals("ready")) {
                new WeatherFetchTask(cursor.getString(cursor.getColumnIndexOrThrow(WeatherContract.LocationEntry.COLUMN_CITY_NAME)), itemBinding).execute();
            }

            itemBinding.ibDelete.setOnClickListener(this);
            itemBinding.getRoot().setOnClickListener(this);

        }

        public void onClick(View v) {
            if (WeatherRecyclerViewCursorAdapter.listener != null) {
                WeatherRecyclerViewCursorAdapter.listener.onItemClick(itemView, getLayoutPosition(), v);
            }
        }

    }

    public interface OnItemClickListener {
        void onItemClick(View itemView, int position, View view);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface UpdateItem {
        void set(String cityName, Weather weather);
    }

    public void setUpdateItem(UpdateItem updateItem) {
        this.updateItem = updateItem;
    }

    @Override
    public WeatherRecyclerViewCursorAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.location_list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(WeatherRecyclerViewCursorAdapter.ViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        holder.bindCursor(mCursor);
    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }

    public void changeCursor(Cursor cursor) {
        mCursor = cursor;
        notifyDataSetChanged();
    }

    public int getId(int position) {
        mCursor.moveToPosition(position);
        return mCursor.getInt(mCursor.getColumnIndexOrThrow(WeatherContract.LocationEntry._ID));
    }


    private static class WeatherFetchTask extends AsyncTask<View, Void, Weather> {
        LocationListItemBinding itemBinding;
        private final String cityName;

        public WeatherFetchTask(String cityName, LocationListItemBinding itemBinding) {
            this.cityName = cityName;
            this.itemBinding = itemBinding;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            itemBinding.temp.setVisibility(View.GONE);
            itemBinding.tvIcon.setVisibility(View.GONE);
            itemBinding.progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Weather doInBackground(View... params) {
            return new WeatherFetcher(mContext, cityName).fetchItem();
        }

        @Override
        protected void onPostExecute(Weather weather) {
            itemBinding.progressBar.setVisibility(View.GONE);
            itemBinding.temp.setVisibility(View.VISIBLE);
            itemBinding.tvIcon.setVisibility(View.VISIBLE);
            if (weather != null) {
                itemBinding.temp.setText(weather.temp);
                itemBinding.tvIcon.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "fonts/weather.ttf"));
                itemBinding.tvIcon.setText(weather.icon);
                itemBinding.tvDate.setText(weather.dt);

                if (WeatherRecyclerViewCursorAdapter.updateItem!= null) {
                    WeatherRecyclerViewCursorAdapter.updateItem.set(cityName, weather);
                }
            } else {
                itemBinding.tvDate.setText(R.string.refresh_error);
            }

        }
    }

}
