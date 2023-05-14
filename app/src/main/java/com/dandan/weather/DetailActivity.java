package com.dandan.weather;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import androidx.fragment.app.FragmentManager;

import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;




import com.google.android.material.tabs.TabLayout;


import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity {
    private ArrayList<Forecast> forecasts;
    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private FragmentAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        forecasts = getIntent().getParcelableArrayListExtra("forecast");

        viewPager = findViewById(R.id.tabPager);
        tabLayout = findViewById(R.id.detailTabLayout);
        FragmentManager manager = getSupportFragmentManager();

        pagerAdapter = new FragmentAdapter(manager, getLifecycle(), forecasts);
        viewPager.setAdapter(pagerAdapter);
        TabLayout.Tab t0 = tabLayout.newTab();
        TabLayout.Tab t1 = tabLayout.newTab();
        TabLayout.Tab t2 = tabLayout.newTab();
        t0.setText("TODAY");
        t0.setIcon(R.drawable.ic_calendar_today);
        t1.setText("WEEKLY");
        t1.setIcon(R.drawable.ic_trending_up);
        t2.setText("WEATHER DATA");
        t2.setIcon(R.drawable.ic_thermometer);
        tabLayout.addTab(t0);
        tabLayout.addTab(t1);
        tabLayout.addTab(t2);


        setTitle(forecasts.get(0).location);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);



        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                Log.i("Tab Selected","A tab has selected");
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                tabLayout.selectTab(tabLayout.getTabAt(position));
            }
        });
//        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//        GaugeFragment favorite = new GaugeFragment();
//        Bundle bundle = new Bundle();
//        bundle.putParcelableArrayList("forecasts",forecasts);
//        favorite.setArguments(bundle);
//        ft.replace(R.id.chart, favorite);
//        ft.commit();
}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inf = getMenuInflater();
        inf.inflate(R.menu.share_menu, menu);//the xml file
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.tweet:
                StringBuilder builder = new StringBuilder();
                String url = "https://twitter.com/intent/tweet?text=";
                builder.append("Check out " + forecasts.get(0).location + "'s weather! It is "
                        + forecasts.get(0).temperature + "ºF!&hashtags=CSCI571WeatherSearch");
                url += builder.toString();
                Intent tweet = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(tweet);
                return true;
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}




















//
//        TextView humidity = findViewById(R.id.detailHumidityVal);
//        TextView cloudCover = findViewById(R.id.detailCloudCoverVal);
//        TextView status = findViewById(R.id.detailStatusVal);
//        TextView precipitation = findViewById(R.id.detailPrecipitationVal);
//        TextView windSpeed = findViewById(R.id.detailWindSpeedVal);
//        TextView visibility = findViewById(R.id.detailVisibilityVal);
//        TextView temperature = findViewById(R.id.detailTemperatureVal);
//        TextView ozone = findViewById(R.id.detailOzoneVal);
//        TextView pressure = findViewById(R.id.detailPressureVal);
//        ImageView statusImg = findViewById(R.id.detailStatus);
//
//        String humidityStr = df.format(forecast.humidity) + "%";
//        humidity.setText(humidityStr);
//        String cloudCoverStr = df.format(forecast.cloudCover) + "%";
//        cloudCover.setText(cloudCoverStr);
//        status.setText(forecast.status);
//        int id;
//        if (Integer.parseInt(forecast.weatherCode) <= 3002 && Integer.parseInt(forecast.weatherCode) >= 3000) {
//            id = getResources().getIdentifier("com.dandan.weather:mipmap/" + forecast.path, null, null);
//        } else {
//            id = getResources().getIdentifier("com.dandan.weather:drawable/" + forecast.path, null, null);
//        }
//        statusImg.setImageResource(id);
//        String precipitaionStr = df.format(forecast.precipitationProbability) + "%";
//        precipitation.setText(precipitaionStr);
//        String windSpeedStr = df.format(forecast.windSpeed) + "mph";
//        windSpeed.setText(windSpeedStr);
//        String visibilityStr = df.format(forecast.visibility) + "mi";
//        visibility.setText(visibilityStr);
//        String tempStr = Math.round(forecast.temperature) + "ºF";
//        temperature.setText(tempStr);
//        ozone.setText(df.format(forecast.uvIndex));
//        String pressureStr = df.format(forecast.pressureSeaLevel) + "inHg";
//        pressure.setText(pressureStr);
