package com.dandan.weather;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;

public class FragmentAdapter extends FragmentStateAdapter {
    public ArrayList<Forecast> forecasts;
    public Bundle bundle;
    public static final int PAGE_COUNT = 3;
    public FragmentAdapter(@NonNull FragmentManager fragmentManager,
                           @NonNull Lifecycle lifecycle,
                           @NonNull ArrayList<Forecast> forecasts) {
        super(fragmentManager, lifecycle);
        this.forecasts = forecasts;
        this.bundle = new Bundle();
//        Log.i("FragmentAdapter","adapter being created");
        bundle.putParcelableArrayList("forecasts",forecasts);
    }

    @NonNull
    @Override

    public Fragment createFragment(int position) {
        switch (position) {
            case 1 :
//                ChartFragment chartFragment = new ChartFragment();
//                chartFragment.setArguments(bundle);
//                return chartFragment;
                return ChartFragment.newInstance(this.forecasts);

            case 2:
//                GaugeFragment gaugeFragment = new GaugeFragment();
//                gaugeFragment.setArguments(bundle);
//                return gaugeFragment;
                return GaugeFragment.newInstance(this.forecasts);
        }
//        DetailFragment detailFragment = new DetailFragment();
//        detailFragment.setArguments(bundle);
        Log.i("FragmentAdapter","Fragment created");
//        return detailFragment;
        return DetailFragment.newInstance(this.forecasts);
    }

    @Override
    public int getItemCount() {
        return PAGE_COUNT;
    }
}
