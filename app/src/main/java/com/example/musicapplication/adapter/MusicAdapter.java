package com.example.musicapplication.adapter;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.musicapplication.Fragment.MusicFragment;
import com.example.musicapplication.Fragment.PlayFragment;
import com.example.musicapplication.R;
import com.example.musicapplication.entity.Music;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;

public class MusicAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final MusicFragment musicFragment;
    private final List<Music> musicList;

    public MusicAdapter(MusicFragment musicFragment, List<Music> musicList) {
        this.musicFragment = musicFragment;
        this.musicList = musicList;
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
        Glide.with(musicFragment).load(music.getImageMusic()).into(musicViewHolder.imgMusic);
        musicViewHolder.clMusic.setOnClickListener(view -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable("song_info", music);
            musicFragment.getParentFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .setCustomAnimations(R.anim.anim_move_up, R.anim.anim_move_down, R.anim.anim_move_up, R.anim.anim_move_down)
                    .add(R.id.fv_play_music, PlayFragment.class, bundle)
                    .addToBackStack(null)
                    .commit();
        });

    }

    @Override
    public int getItemCount() {
        return musicList.size();
    }
}
