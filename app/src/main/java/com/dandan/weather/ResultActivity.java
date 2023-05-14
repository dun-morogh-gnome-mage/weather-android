package com.dandan.weather;


import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.MenuItem;


import java.util.ArrayList;
import java.util.HashSet;

public class ResultActivity extends AppCompatActivity {

    ArrayList<Forecast> forecasts;
    ArrayList<String> favoritesLocation;
    int current_added;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        forecasts = getIntent().getParcelableArrayListExtra("forecasts");
        favoritesLocation = getIntent().getStringArrayListExtra("favoritesLocation");
        setTitle(forecasts.get(0).location);
        ActionBar actionBar = getSupportActionBar();

        // showing the back button in action bar
        actionBar.setDisplayHomeAsUpEnabled(true);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Favorite favorite = new Favorite();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("forecasts",forecasts);
        bundle.putInt("position",favoritesLocation.size()-1);
        Log.i("Result", "received size " + favoritesLocation.size());
        bundle.putBoolean("flag",true);
        bundle.putInt("add",0);
        favorite.setArguments(bundle);
        ft.replace(R.id.resultFragment, favorite);
        ft.commit();



    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(ResultActivity.this, MainActivity.class);
                intent.putExtra("added",current_added);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void updateLocation(int add) {
        current_added = add;

    }
}