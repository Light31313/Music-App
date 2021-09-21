package com.example.musicapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.musicapplication.entity.Music;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class SplashScreen extends AppCompatActivity {
    private int id;
    private String imageMusic;
    private String songName;
    private String singer;
    private String source;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = this.getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("music storage url", "https://614890bd035b3600175b9f07.mockapi.io/songInfo");
        editor.apply();

        String url = sharedPreferences.getString("music storage url", "invalid url");

        RequestQueue requestQueue = MySingleton.getInstance(this.getApplicationContext()).getRequestQueue();

        ArrayList<Music> musics = new ArrayList<>();
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                jsonArray -> {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        try {
                            id = jsonArray.getJSONObject(i).getInt("id");
                            imageMusic = jsonArray.getJSONObject(i).getString("singerImage");
                            songName = jsonArray.getJSONObject(i).getString("songName");
                            singer = jsonArray.getJSONObject(i).getString("singer");
                            source = jsonArray.getJSONObject(i).getString("source");
                            finish();
                        } catch (JSONException e) {
                            e.printStackTrace();

                        }
                        musics.add(new Music(id, imageMusic, songName, singer, source));
                    }
                    Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("musicList", musics);
                    intent.putExtra("bundle", bundle);
                    startActivity(intent);
                },
                error -> Toast.makeText(getApplication(), "No Internet connection", Toast.LENGTH_SHORT).show());

        MySingleton.getInstance(this).addToRequestQueue(jsonArrayRequest);

    }
}