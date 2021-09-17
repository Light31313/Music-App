package com.example.musicapplication.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.musicapplication.R;
import com.example.musicapplication.adapter.MusicAdapter;
import com.example.musicapplication.entity.Music;

import java.util.ArrayList;
import java.util.List;


public class MusicFragment extends Fragment {
    private List<Music> musicList;
    private MusicAdapter adapter;
    private RecyclerView rvListSong;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_music, container, false);
        rvListSong = view.findViewById(R.id.rv_list_song);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initComponent();
        initEvent();
    }

    private void initComponent() {
        musicList = new ArrayList<>();
        addMusic();
        adapter = new MusicAdapter(getContext() , musicList);
        rvListSong.setAdapter(adapter);
        rvListSong.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void addMusic() {
        musicList.add(new Music("Japanese song", "Belle", R.raw.belle_mv));
        musicList.add(new Music("Elesa pokemon theme", "Elesa", R.raw.elesa_pokemon_theme));
        musicList.add(new Music("Outer wild theme", "traveler", R.raw.outer_wild_ost));
        musicList.add(new Music("Some thing just like this", "Chainsmokers", R.raw.something_just_like_this));
        musicList.add(new Music("Vietnamese song", "Vietnamese singer", R.raw.vietnamese_song));
    }

    private void initEvent() {
    }

}