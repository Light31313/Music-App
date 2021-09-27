package com.example.musicapplication.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.musicapplication.Fragment.MusicFragment;
import com.example.musicapplication.R;
import com.example.musicapplication.databinding.ItemMusicBinding;
import com.example.musicapplication.entity.Music;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;

public class MusicAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context context;
    private final List<Music> musicList;
    private final IMusicAdapter iMusicAdapter;

    public MusicAdapter(Context context, IMusicAdapter iMusicAdapter, List<Music> musicList) {
        this.context = context;
        this.musicList = musicList;
        this.iMusicAdapter = iMusicAdapter;
    }

    private static class MusicViewHolder extends RecyclerView.ViewHolder{
        private final ConstraintLayout clMusic;
        private final TextView songName;
        private final TextView singer;
        private final ShapeableImageView imgMusic;
        public MusicViewHolder(@NonNull View itemView) {
            super(itemView);
            clMusic = itemView.findViewById(R.id.cl_music);
            songName = itemView.findViewById(R.id.txt_song_name);
            singer = itemView.findViewById(R.id.txt_singer);
            imgMusic = itemView.findViewById(R.id.img_music);
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
        Glide.with(context).load(music.getImageMusic()).into(musicViewHolder.imgMusic);
        musicViewHolder.clMusic.setOnClickListener(view -> iMusicAdapter.chooseMusic(position));

    }

    @Override
    public int getItemCount() {
        return musicList.size();
    }

}
