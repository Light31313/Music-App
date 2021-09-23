package com.example.musicapplication.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.lifecycle.ViewModelProvider;

import com.example.musicapplication.viewmodel.MusicViewModel;

public class MediaActionReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals("Play")){

        }
        else if(intent.getAction().equals("Next")){

        }
        else if(intent.getAction().equals("Prev")){

        }
    }
}
