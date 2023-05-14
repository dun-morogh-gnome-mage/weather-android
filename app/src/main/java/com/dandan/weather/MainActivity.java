package com.dandan.weather;



import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;



import androidx.fragment.app.FragmentManager;


import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.BuildConfig;
import androidx.viewpager2.widget.ViewPager2;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;

import android.view.MenuItem;
import android.view.View;


import android.widget.AdapterView;
import android.widget.ArrayAdapter;


import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;




import com.android.volley.Request;
import com.android.volley.RequestQueue;


import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;


import java.lang.reflect.Type;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    private ArrayList<Forecast> forecasts;
    private ArrayList<String> favoritesLocation;
    private ArrayList<ArrayList<Forecast>> allForecasts;
    private ProgressBar w;
    private TextView prompt;

    public static final String WEATHER_APP = "weatherAppData";
    private static final String IP_URL = "https://ipinfo.io/json?";
    private static final String WEATHER_URL = "https://precise-datum-330001.wl.r.appspot.com/tomorrow";
    private static final String AUTO_URL = "https://maps.googleapis.com/maps/api/place/autocomplete/json?";
    private static double lat;
    private static double lng;
    private static String autoCity;
    private static String autoState;
    private static String currentLoc;
    private FavoriteFragmentAdapter favoriteFragmentAdapter;
    ArrayAdapter<String> adapter;
    RequestQueue queue;


    private ViewPager2 viewPager;
    private TabLayout tabLayout;

    public MainActivity() {
        if(BuildConfig.DEBUG)
            StrictMode.enableDefaults();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        queue = Volley.newRequestQueue(this); //requests FIFO
        forecasts = new ArrayList<>();
        favoritesLocation = new ArrayList<>(); //should have corresponding index
        allForecasts = new ArrayList<>();

        w = findViewById(R.id.progressBar);
        prompt = findViewById(R.id.fetchingText);

        viewPager = findViewById(R.id.mainViewPager);
        tabLayout = findViewById(R.id.mainTablayout);

        int icon_default = getResources().getIdentifier("com.dandan.weather:drawable/ic_unselected",null,null);
        int icon_selected = getResources().getIdentifier("com.dandan.weather:drawable/ic_selected",null,null);

        FragmentManager manager = getSupportFragmentManager();
        favoriteFragmentAdapter = new FavoriteFragmentAdapter(
                manager,
                getLifecycle(),
                new ArrayList<>());



        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                int pos = tab.getPosition();
                for (int i = 0; i < tabLayout.getTabCount(); i++) {
                    if (i == pos) continue;
                    tabLayout.getTabAt(i).setIcon(icon_default);
                }
                tab.setIcon(icon_selected);
                Log.i("Main", "Tab " + tab.getPosition() + " selected");
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

        SharedPreferences sharedPreferences = getSharedPreferences(WEATHER_APP,MODE_PRIVATE);

        if (sharedPreferences.getBoolean("secondRun",false)) {
            w.setVisibility(View.VISIBLE);
            prompt.setVisibility(View.VISIBLE);

            Gson gson = new Gson();
            String savedLocations = sharedPreferences.getString("locations","");
            String savedForecasts = sharedPreferences.getString("allForecasts","");
            favoritesLocation = gson.fromJson(savedLocations,ArrayList.class);
            Type type = new TypeToken<ArrayList<ArrayList<Forecast>>>(){}.getType();
            allForecasts = gson.fromJson(savedForecasts,type);

            restoreData(favoriteFragmentAdapter,allForecasts);
            viewPager.setAdapter(favoriteFragmentAdapter);
            w.setVisibility(View.GONE);
            prompt.setVisibility(View.GONE);

            for (int i = 0; i < favoriteFragmentAdapter.favorites.size(); i++) {
                if (i == 0) {
                    tabLayout.addTab(tabLayout.newTab().setIcon(icon_selected));

                } else {
                    tabLayout.addTab(tabLayout.newTab().setIcon(icon_default));
                }
            }
            Intent intent = getIntent();
            int flag = intent.getIntExtra("added", 2);
            // means the data in the result activity is removed so we need update
            if (flag == 0) {
                int last_pos = favoriteFragmentAdapter.favorites.size()-1;
                favoriteFragmentAdapter.getFavorites().remove(last_pos);
                favoritesLocation.remove(last_pos);
                allForecasts.remove(last_pos);
                Log.i("Main","reupdate the list");
            }

        }  else {
            w.setVisibility(View.VISIBLE);
            prompt.setVisibility(View.VISIBLE);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, IP_URL+TOKEN, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        String[] loc = response.get("loc").toString().split(",");
                        lat = Double.parseDouble(loc[0]);
                        lng = Double.parseDouble(loc[1]);
                        autoCity = response.getString("city");
                        autoState = response.getString("region");
                        currentLoc = autoCity + ", " + autoState;

                        JsonObjectRequest default_weather = new JsonObjectRequest(Request.Method.GET,
                                WEATHER_URL + "?checked=true&" + "lat=" + lat + "&lng=" + lng,
                                null,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        try {
                                            JSONObject fifteen = response.getJSONObject("fifteen");
                                            JSONObject five = response.getJSONObject("five");
                                            String cur = five.getString("startTime");
                                            JSONArray forecast_arr = fifteen.getJSONArray("intervals");

                                            populateFifteenDaysForecast(forecast_arr,cur,forecasts);

                                            Favorite favorite = new Favorite();
                                            Bundle bundle = new Bundle();
                                            bundle.putParcelableArrayList("forecasts",forecasts);

                                            bundle.putStringArrayList("favoritesLocation",favoritesLocation);
                                            bundle.putBoolean("flag",false);
                                            bundle.putInt("position",favoritesLocation.size());
                                            bundle.putInt("add", 0);
                                            favorite.setArguments(bundle);

                                            favoriteFragmentAdapter.updateFavoritesList(favorite);
                                            favoritesLocation.add(currentLoc);
                                            allForecasts.add(forecasts);
                                            viewPager.setAdapter(favoriteFragmentAdapter);
                                            for (int i = 0; i < favoritesLocation.size(); i++) {
                                                if (i == 0) {
                                                    tabLayout.addTab(tabLayout.newTab().setIcon(icon_selected));

                                                } else {
                                                    tabLayout.addTab(tabLayout.newTab().setIcon(icon_default));
                                                }
                                            }


                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        } finally {
                                            w.setVisibility(View.GONE);
                                            prompt.setVisibility(View.GONE);
                                        }

                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                error.printStackTrace();
                            }
                        });
                        queue.add(default_weather);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                }
            });
            queue.add(jsonObjectRequest);
        }

        viewPager.setUserInputEnabled(true);


    }



    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        MenuInflater inf = getMenuInflater();
        inf.inflate(R.menu.search_menu,menu);//the xml file


        MenuItem searchMenu = menu.findItem(R.id.search_btn);

        // Get SearchView object.
        androidx.appcompat.widget.SearchView searchView = (androidx.appcompat.widget.SearchView) searchMenu.getActionView();

        androidx.appcompat.widget.SearchView.SearchAutoComplete searchAutoComplete = searchView.findViewById(androidx.appcompat.R.id.search_src_text);

        searchAutoComplete.setTextColor(Color.WHITE);
        searchAutoComplete.setThreshold(3);

        searchView.setIconified(false);
        searchView.setQueryHint("Search...");

        Context context = this;
        ArrayList<String> suggest = new ArrayList<>();
        searchAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                searchAutoComplete.setText((String)parent.getItemAtPosition(position));
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                w.setVisibility(View.VISIBLE);
                prompt.setVisibility(View.VISIBLE);
                viewPager.setVisibility(View.GONE);
                tabLayout.setVisibility(View.GONE);
                String[] queryToken = query.split(", ");
                JsonObjectRequest weatherReq = new JsonObjectRequest(Request.Method.GET,
                        WEATHER_URL + "?checked=false&city=" + queryToken[0] + "&state=" + queryToken[1],
                        null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    currentLoc = queryToken[0] + ", " + queryToken[1];
                                    JSONObject fifteen = response.getJSONObject("fifteen");
                                    JSONObject five = response.getJSONObject("five");
                                    String cur = five.getString("startTime");
                                    JSONArray forecast_arr = fifteen.getJSONArray("intervals");
                                    ArrayList<Forecast> newWeather = new ArrayList<>();
                                    populateFifteenDaysForecast(forecast_arr,cur,newWeather);
                                    favoritesLocation.add(currentLoc);
                                    allForecasts.add(newWeather);
                                    Intent intent = new Intent(MainActivity.this, ResultActivity.class);
                                    intent.putParcelableArrayListExtra("forecasts",newWeather);
                                    intent.putStringArrayListExtra("favoritesLocation",favoritesLocation);
                                    Log.i("Main","location " + favoritesLocation.size());
                                    favoriteFragmentAdapter.updateFavoritesList(Favorite.newInstance(forecasts,
                                            true,
                                            favoritesLocation.size(),0));
                                    startActivity(intent);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });
                queue.add(weatherReq);

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText)
            {
                JsonObjectRequest suggestions = new JsonObjectRequest(Request.Method.GET,
                         AUTO_URL + "input=" + newText + "&types=(cities)&key=" + GOOGLE_TOKEN,
                        null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                suggest.clear();
                                try {
                                    JSONArray suggestions = response.getJSONArray("predictions");
                                    for (int i = 0; i < suggestions.length(); i++) {
                                        String[] text = suggestions.getJSONObject(i).getString("description").split(", ");

                                        suggest.add(text[0] + ", " + text[1]);
                                        adapter = new ArrayAdapter<>(
                                                context,
                                                android.R.layout.simple_spinner_dropdown_item,
                                                suggest);


                                        searchAutoComplete.setAdapter(adapter);
                                        adapter.getFilter().filter(newText);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }

                });
                queue.add(suggestions);
                return false;
            }
        });

        return true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences sharedPreferences = getSharedPreferences(WEATHER_APP,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String locationInfo = gson.toJson(favoritesLocation);
        String allForecastInfo = gson.toJson(allForecasts);
        editor.putString("locations", locationInfo);
        editor.putBoolean("secondRun",true);
        editor.putString("allForecasts",allForecastInfo);
        editor.apply();
    }

    private void populateFifteenDaysForecast(JSONArray arr, String cur, ArrayList<Forecast> result) throws Exception {
        Forecast.curTime = cur;
        for (int i = 0; i < arr.length(); i++) {
            JSONObject o = arr.getJSONObject(i);
            JSONObject values = o.getJSONObject("values");
            int uv = 0;
            try {
                values.getString("uvIndex");
            } catch (Exception e) {
                uv = -1;
            }
            uv = uv == -1  ? -1 : Integer.parseInt(values.getString("uvIndex"));
            result.add(new Forecast(o.getString("startTime"),
                                     Double.parseDouble(values.getString("cloudCover")),
                                     Double.parseDouble(values.getString("humidity")),
                                     Integer.parseInt(values.getString("moonPhase")),
                                     Double.parseDouble(values.getString("precipitationProbability")),
                                     Integer.parseInt(values.getString("precipitationType")),
                                     Double.parseDouble(values.getString("pressureSeaLevel")),
                                    values.getString("sunriseTime"),
                                    values.getString("sunsetTime"),
                                    Double.parseDouble(values.getString("temperature")),
                                    Double.parseDouble(values.getString("temperatureApparent")),
                                    Double.parseDouble(values.getString("temperatureMax")),
                                    Double.parseDouble(values.getString("temperatureMin")),
                                    uv,
                                    Double.parseDouble(values.getString("visibility")),
                                    values.getString("weatherCode"),
                                    Double.parseDouble(values.getString("windDirection")),
                                    Double.parseDouble(values.getString("windSpeed")),
                                    currentLoc));
        }
    }



    public void updateFavorites(int removed, int position) {
        if (removed == 1) {
            tabLayout.removeTabAt(position);
            favoriteFragmentAdapter.removeFromFavoritesList(position);
            favoritesLocation.remove(position);
            allForecasts.remove(position);
            tabLayout.selectTab(tabLayout.getTabAt(position-1));
            Log.i("Main", "remove city at " + position);
            arrangeLocation();
        }
    }

    public void arrangeLocation() {
        for (int i = 0; i < favoriteFragmentAdapter.getFavorites().size(); i++) {
            favoriteFragmentAdapter.getFavorites().get(i).position = i;
        }
        Log.i("Main","Rearrange " + favoriteFragmentAdapter.getItemCount() + " items");
    }

    public void restoreData(FavoriteFragmentAdapter adapter,
                            ArrayList<ArrayList<Forecast>> allForecasts) {
        ArrayList<Favorite> favorites = new ArrayList<>();
        Log.i("Main allForcasts size",String.valueOf(allForecasts.size()));
        for (int i = 0; i < allForecasts.size(); i++) {
            ArrayList<Forecast> current = allForecasts.get(i);
            boolean add = i == 0? false : true;
            Favorite favorite = Favorite.newInstance(current,add,i,1);
            favorites.add(favorite);
        }
        adapter.setFavorites(favorites);

    }



}


// use "added" to get new list from resultactivity