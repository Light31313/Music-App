package com.example.musicapplication.adapter;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicapplication.R;
import com.example.musicapplication.entity.Music;

import java.util.List;

public class MusicAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<Music> musicList;

    public MusicAdapter(Context context, List<Music> musicList) {
        this.context = context;
        this.musicList = musicList;
    }

    private static class MusicViewHolder extends RecyclerView.ViewHolder{
        private final ConstraintLayout clMusic;
        private final TextView songName;
        private final TextView singer;
        public MusicViewHolder(@NonNull View itemView) {
            super(itemView);
            clMusic = itemView.findViewById(R.id.cl_music);
            songName = itemView.findViewById(R.id.txt_song_name);
            singer = itemView.findViewById(R.id.txt_singer);
        }
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater =LayoutInflater.from(parent.getContext());
        return new MusicViewHolder(inflater.inflate(R.layout.item_music, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Music music = musicList.get(position);
        MusicViewHolder musicViewHolder = (MusicViewHolder) holder;
        musicViewHolder.songName.setText(music.getSongName());
        musicViewHolder.singer.setText(music.getSinger());
        musicViewHolder.clMusic.setOnClickListener(view -> {

        });

    }

    @Override
    public int getItemCount() {
        return musicList.size();
    }
}
