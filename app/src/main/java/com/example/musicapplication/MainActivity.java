package com.example.musicapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.example.musicapplication.Fragment.FavoriteFragment;
import com.example.musicapplication.Fragment.MusicFragment;
import com.example.musicapplication.databinding.ActivityMainBinding;
import com.example.musicapplication.viewmodel.MainFragmentViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener{
    private BottomNavigationView bottomNavigation;
    private Bundle bundle;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initView();
        initComponent();
        initEvent();

    }

    private void initView() {
        setSupportActionBar(binding.tbMenu);
        bottomNavigation = binding.bottomNavigation;
    }

    private void initComponent() {


        Intent intent = getIntent();
        bundle = intent.getBundleExtra("bundle");
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, MusicFragment.class, bundle).commit();
    }

    private void initEvent() {
        bottomNavigation.setOnItemSelectedListener(this);
        MainFragmentViewModel viewModel = new ViewModelProvider(this).get(MainFragmentViewModel.class);
        viewModel.getVisible().observe(this, isVisible ->{
            if(isVisible){
                bottomNavigation.startAnimation(AnimationUtils.loadAnimation(this, R.anim.anim_navigation_move_up));
                bottomNavigation.setVisibility(View.VISIBLE);
            }
            else {
                bottomNavigation.setVisibility(View.GONE);
                bottomNavigation.startAnimation(AnimationUtils.loadAnimation(this, R.anim.anim_navigation_move_down));
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.page_home) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .setReorderingAllowed(true)
                    .setCustomAnimations(R.anim.anim_appear_and_zoom_in, R.anim.anim_disappear_and_zoom_out)
                    .replace(R.id.fragment_container, MusicFragment.class, bundle)
                    .commit();
        } else if (id == R.id.page_favorite) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .setReorderingAllowed(true)
                    .setCustomAnimations(R.anim.anim_appear_and_zoom_in, R.anim.anim_disappear_and_zoom_out)
                    .replace(R.id.fragment_container, FavoriteFragment.class, null)
                    .commit();
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.crud_menu, menu);
        return true;
    }


}
