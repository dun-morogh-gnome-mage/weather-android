package com.dandan.weather;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetailFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    // TODO: Rename and change types of parameters
    private ArrayList<Forecast> forecasts;
    private Forecast forecast;
    private DecimalFormat df;
    public DetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment DetailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DetailFragment newInstance(ArrayList<Forecast> forecasts) {
        DetailFragment fragment = new DetailFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList("forecasts", forecasts);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            forecasts = getArguments().getParcelableArrayList("forecasts");
            forecast = forecasts.get(0);
            Log.i("Data Received in Detail Fragment","Received Data");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment



        return inflater.inflate(R.layout.fragment_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        df = new DecimalFormat("0.00");

        TextView humidity = getView().findViewById(R.id.detailHumidityVal);
        TextView cloudCover = getView().findViewById(R.id.detailCloudCoverVal);
        TextView status = getView().findViewById(R.id.detailStatusVal);
        TextView precipitation = getView().findViewById(R.id.detailPrecipitationVal);
        TextView windSpeed = getView().findViewById(R.id.detailWindSpeedVal);
        TextView visibility = getView().findViewById(R.id.detailVisibilityVal);
        TextView temperature = getView().findViewById(R.id.detailTemperatureVal);
        TextView ozone = getView().findViewById(R.id.detailOzoneVal);
        TextView pressure = getView().findViewById(R.id.detailPressureVal);
        ImageView statusImg = getView().findViewById(R.id.detailStatus);

        String humidityStr = df.format(forecast.humidity) + "%";
        humidity.setText(humidityStr);
        String cloudCoverStr = df.format(forecast.cloudCover) + "%";
        cloudCover.setText(cloudCoverStr);
        status.setText(forecast.status);
        int id;
        if (Integer.parseInt(forecast.weatherCode) <= 3002 && Integer.parseInt(forecast.weatherCode) >= 3000) {
            id = getResources().getIdentifier("com.dandan.weather:mipmap/" + forecast.path, null, null);
        } else {
            id = getResources().getIdentifier("com.dandan.weather:drawable/" + forecast.path, null, null);
        }
        statusImg.setImageResource(id);
        String precipitaionStr = df.format(forecast.precipitationProbability) + "%";
        precipitation.setText(precipitaionStr);
        String windSpeedStr = df.format(forecast.windSpeed) + "mph";
        windSpeed.setText(windSpeedStr);
        String visibilityStr = df.format(forecast.visibility) + "mi";
        visibility.setText(visibilityStr);
        String tempStr = Math.round(forecast.temperature) + "ºF";
        temperature.setText(tempStr);
        ozone.setText(df.format(forecast.uvIndex));
        String pressureStr = df.format(forecast.pressureSeaLevel) + "inHg";
        pressure.setText(pressureStr);
    }
}