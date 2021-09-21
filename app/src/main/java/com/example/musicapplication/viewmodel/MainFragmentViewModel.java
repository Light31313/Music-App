package com.example.musicapplication.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MainFragmentViewModel extends ViewModel {
    private final MutableLiveData<Boolean> isVisible = new MutableLiveData<>();

    public void setVisible(boolean isVisible) {
        this.isVisible.setValue(isVisible);
    }

    public LiveData<Boolean> getVisible() {
        return isVisible;
    }
}
