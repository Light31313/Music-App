package com.example.musicapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.musicapplication.databinding.ActivitySplashScreenBinding;
import com.example.musicapplication.entity.Music;
import com.example.musicapplication.entity.MusicRetrofit;
import java.util.ArrayList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SplashScreen extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        ActivitySplashScreenBinding binding = ActivitySplashScreenBinding.inflate(getLayoutInflater());
        getSupportActionBar().hide();
        setContentView(binding.getRoot());

        ImageView imgLogo = binding.imgLogo;
        imgLogo.startAnimation(AnimationUtils.loadAnimation(this, R.anim.anim_rotate_logo));

        SharedPreferences sharedPreferences = this.getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("music storage url", "https://614890bd035b3600175b9f07.mockapi.io/");
        editor.apply();

        String url = sharedPreferences.getString("music storage url", "invalid url");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        MusicRetrofit musicRetrofit = retrofit.create(MusicRetrofit.class);

        Call<ArrayList<Music>> call = musicRetrofit.getAllMusics();
        call.enqueue(new Callback<ArrayList<Music>>() {
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
                Toast.makeText(SplashScreen.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void finish() {
        super.finish();
        //overridePendingTransition(R.anim.anim_splash_zoom_out, R.anim.anim_splash_zoom_out);
    }
}