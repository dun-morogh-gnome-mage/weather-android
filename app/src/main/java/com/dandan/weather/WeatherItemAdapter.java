package com.dandan.weather;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class WeatherItemAdapter extends RecyclerView.Adapter<WeatherItemAdapter.ViewHolder> {
    private ArrayList<Forecast> forecast = new ArrayList<>();
    private final Context context;
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.weather_item, parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int roundedLow = (int) Math.round(forecast.get(position).temperatureMin);
        holder.tempLow.setText(String.valueOf(roundedLow));
        int roundedHigh = (int) Math.round(forecast.get(position).temperatureMax);
        holder.tempHigh.setText(String.valueOf(roundedHigh));
        String date = forecast.get(position).startTime.substring(0,10);
        holder.date.setText(date);
        holder.statusImg.setImageResource(this.context.
                getResources().getIdentifier("com.dandan.weather:drawable/" + forecast.get(position).path, null, null));

    }

    @Override
    public int getItemCount() {
        return forecast.size();
    }

    public void setForecast(ArrayList<Forecast> forecast) {

        this.forecast = forecast;
        notifyDataSetChanged();
    }

    public WeatherItemAdapter(Context context) {
        this.context = context;
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tempHigh, tempLow, date;
        private ImageView statusImg;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tempHigh = itemView.findViewById(R.id.tableTempHigh);
            tempLow = itemView.findViewById(R.id.tableTempLow);
            date = itemView.findViewById(R.id.tableDate);
            statusImg = itemView.findViewById(R.id.tableStatusImg);
        }
    }
}
