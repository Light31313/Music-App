package com.example.musicapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.musicapplication.api.API;
import com.example.musicapplication.databinding.ActivitySplashScreenBinding;
import com.example.musicapplication.entity.Music;
import java.util.ArrayList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressLint("CustomSplashScreen")
public class SplashScreen extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        ActivitySplashScreenBinding binding = ActivitySplashScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ImageView imgLogo = binding.imgLogo;
        imgLogo.startAnimation(AnimationUtils.loadAnimation(this, R.anim.anim_rotate_logo));


        API.getClient("https://614890bd035b3600175b9f07.mockapi.io/");

        API.getMusicRetrofit().getAllMusics().enqueue(new Callback<ArrayList<Music>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<Music>> call, @NonNull Response<ArrayList<Music>> response) {
                ArrayList<Music> musics = response.body();
                Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("musicList", musics);
                intent.putExtra("bundle", bundle);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<Music>> call, @NonNull Throwable t) {
                Toast.makeText(SplashScreen.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    public void finish() {
        super.finish();
        //overridePendingTransition(R.anim.anim_splash_zoom_out, R.anim.anim_splash_zoom_out);
    }
}