package com.dandan.weather;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Favorite#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Favorite extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    public ArrayList<Forecast> forecasts;
    public boolean flag; // true means display the floating btn or not
    public int position;
    public int add; // means already added or not?
    // TODO: Rename and change types of parameters


    public Favorite() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment Favorite.
     */
    // TODO: Rename and change types and number of parameters
    public static Favorite newInstance(ArrayList<Forecast> forecasts,boolean flag,int position , int add) {
        Favorite fragment = new Favorite();
        Bundle args = new Bundle();
        args.putParcelableArrayList("forecasts", forecasts);
        args.putBoolean("flag",flag);
        args.putInt("position",position);
        args.putInt("add",add);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

      forecasts = getArguments().getParcelableArrayList("forecasts");
      position = getArguments().getInt("position");
      flag = getArguments().getBoolean("flag");
      add = getArguments().getInt("add");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favorite, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ImageView imgCard = getView().findViewById(R.id.weatherImgCard);
        TextView locCard = getView().findViewById(R.id.locationCard);
        TextView tempCard = getView().findViewById(R.id.temperatureCard);
        TextView statusCard = getView().findViewById(R.id.statusCard);
        FloatingActionButton btn = getView().findViewById(R.id.favoriteBtn);
        if (flag) {
            btn.setVisibility(View.VISIBLE);
            if (add == 1) {
                int id = getActivity().getResources().getIdentifier("com.dandan.weather:drawable/ic_map_marker_minus",null,null);
                btn.setImageResource(id);
            } else {
                int id = getActivity().getResources().getIdentifier("com.dandan.weather:drawable/ic_map_marker_plus",null,null);
                btn.setImageResource(id);
            }
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int is_removed = 0;
                    if (flag && add != 1) {
                        Toast.makeText(getActivity(),forecasts.get(0).location + " was added to favorites!",Toast.LENGTH_SHORT).show();
                        int id = getActivity().getResources().getIdentifier("com.dandan.weather:drawable/ic_map_marker_minus",null,null);
                        btn.setImageResource(id);
                        is_removed = 0;
                        add = 1;
                    } else {
                        Toast.makeText(getActivity(),forecasts.get(0).location + " was removed from favorites!",Toast.LENGTH_SHORT).show();
                        int id = getActivity().getResources().getIdentifier("com.dandan.weather:drawable/ic_map_marker_plus",null,null);
                        btn.setImageResource(id);
                        add = 0;
                        is_removed = 1;
                    }


                    if (determineActivity()) { // only remove
                        MainActivity main = (MainActivity) getActivity();
                        main.updateFavorites(is_removed,position);
                        Log.i("Favorite","remove city at " + position);
                    } else { // only add
                        ResultActivity resultActivity = (ResultActivity) getActivity();
                        resultActivity.updateLocation(add);
                        Log.i("Favorite","add city at " + position);
                    }

                    flag = !flag;
                }
            });
        }

        RecyclerView forecastList = getView().findViewById(R.id.forecastList);
        forecastList.setLayoutManager(new LinearLayoutManager(getActivity()));



        TextView humidity = getView().findViewById(R.id.humidityVal);
        TextView windSpeed = getView().findViewById(R.id.speedVal);
        TextView visibility = getView().findViewById(R.id.visibilityVal);
        TextView pressure = getView().findViewById(R.id.pressureVal);

        CardView cardView = getView().findViewById(R.id.weatherCard);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawDetail(v);
            }
        });

        CardView genaralCardView = getView().findViewById(R.id.weatherGeneralCard);
        int id;
        int code = Integer.parseInt(forecasts.get(0).weatherCode);

        if (code <= 3002 && code >= 3000) {
            id = getActivity().getResources().getIdentifier("com.dandan.weather:mipmap/" + forecasts.get(0).path, null, null);
        } else {
            id = getActivity().getResources().getIdentifier("com.dandan.weather:drawable/" + forecasts.get(0).path, null, null);
        }


        imgCard.setImageResource(id);
        locCard.setText(forecasts.get(0).location);
        String temperature = forecasts.get(0).temperature + "ºF";
        tempCard.setText(temperature);
        statusCard.setText(forecasts.get(0).status);

        String humi = (int)forecasts.get(0).humidity + "%";
        humidity.setText(humi);
        String ws = forecasts.get(0).windSpeed + "mph";
        windSpeed.setText(ws);
        String visi = forecasts.get(0).visibility + "mi";
        visibility.setText(visi);
        String pre = forecasts.get(0).pressureSeaLevel + "inHg";
        pressure.setText(pre);
        cardView.setVisibility(View.VISIBLE);
        genaralCardView.setVisibility(View.VISIBLE);
        cardView.setClickable(true);

        WeatherItemAdapter adapter = new WeatherItemAdapter(getActivity());
        adapter.setForecast(forecasts);
        forecastList.setAdapter(adapter);
        forecastList.setNestedScrollingEnabled(true);

    }

    public void drawDetail(View view) {
        Intent intent = new Intent(getActivity(), DetailActivity.class);
        intent.putParcelableArrayListExtra("forecast",forecasts);
        startActivity(intent);
    }

    public boolean determineActivity() {
        if (getActivity() != null && getActivity() instanceof MainActivity) {
            return true;
        }
        return false;
    }


}