package com.example.musicapplication.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.musicapplication.entity.Music;

public class MusicViewModel extends ViewModel {
    private final MutableLiveData<Music> music = new MutableLiveData<>();

    public void setMusic(Music music) {
        this.music.setValue(music);
    }

    public LiveData<Music> getMusic() {
        return music;
    }

    private final MutableLiveData<Boolean> isNext = new MutableLiveData<>();

    public void setNext(boolean isNext) {
        this.isNext.setValue(isNext);
    }

    public LiveData<Boolean> getNext() {
        return isNext;
    }

    private final MutableLiveData<Boolean> isPrevious = new MutableLiveData<>();

    public void setPrevious(boolean isPrevious) {
        this.isPrevious.setValue(isPrevious);
    }

    public LiveData<Boolean> getPrevious() {
        return isPrevious;
    }

    private final MutableLiveData<Boolean> isRandom = new MutableLiveData<>();

    public void setRandom(boolean isRandom) {
        this.isRandom.setValue(isRandom);
    }

    public LiveData<Boolean> getRandom() {
        return isRandom;
    }

    private final MutableLiveData<Boolean> isClickable = new MutableLiveData<>();

    public void setClickable(boolean isClickable) {
        this.isClickable.setValue(isClickable);
    }

    public LiveData<Boolean> getClickable() {
        return isClickable;
    }

}
