package com.example.musicapplication.entity;

public class Music {
    private int imageMusic;
    private String songName;
    private String singer;
    private int source;



    public Music(String title, String singer, int source) {
        this.songName = title;
        this.singer = singer;
        this.source = source;
    }

    public int getSource() {
        return source;
    }

    public void setSource(int source) {
        this.source = source;
    }

    public int getImageMusic() {
        return imageMusic;
    }

    public void setImageMusic(int imageMusic) {
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
}
