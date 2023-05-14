package com.dandan.weather;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.adapter.FragmentViewHolder;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class FavoriteFragmentAdapter extends FragmentStateAdapter {
    public ArrayList<Favorite> favorites;
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return favorites.get(position);
    }

    @Override
    public int getItemCount() {
        return favorites.size();
    }

    @Override
    public void onBindViewHolder(@NonNull FragmentViewHolder holder, int position, @NonNull List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);

    }

    public FavoriteFragmentAdapter(@NonNull FragmentManager fragmentManager,
                                   @NonNull Lifecycle lifecycle,
                                   ArrayList<Favorite> favorites) {
        super(fragmentManager, lifecycle);
        this.favorites = favorites;
    }

    public void updateFavoritesList(Favorite fragment) {
        this.favorites.add(fragment);
        notifyDataSetChanged();
    }

    public void removeFromFavoritesList(int position) {
        this.favorites.remove(position);
        notifyItemRemoved(position);
        notifyDataSetChanged();
    }
    public ArrayList<Favorite> getFavorites() {return this.favorites;}
    public void setFavorites(ArrayList<Favorite> favorites) {this.favorites = favorites;}
}
