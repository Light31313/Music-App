package com.example.musicapplication.entity;

import androidx.databinding.BaseObservable;


import java.io.Serializable;

public class Music extends BaseObservable implements Serializable {
    private int id;
    private String imageMusic;
    private String songName;
    private String singer;
    private String source;

    public Music(int id, String imageMusic, String songName, String singer, String source) {
        this.id = id;
        this.imageMusic = imageMusic;
        this.songName = songName;
        this.singer = singer;
        this.source = source;
    }

    public Music(String imageMusic, String songName, String singer, String source) {
        this.imageMusic = imageMusic;
        this.songName = songName;
        this.singer = singer;
        this.source = source;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImageMusic() {
        return imageMusic;
    }

    public void setImageMusic(String imageMusic) {
        this.imageMusic = imageMusic;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
