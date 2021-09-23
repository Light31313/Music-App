package com.example.musicapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;

import com.example.musicapplication.Fragment.FavoriteFragment;
import com.example.musicapplication.Fragment.MusicFragment;
import com.example.musicapplication.viewmodel.MainFragmentViewModel;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener{
    private BottomNavigationView bottomNavigation;
    private Bundle bundle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        initView();
        initComponent();
        initEvent();

    }

    private void initView() {
        bottomNavigation = findViewById(R.id.bottom_navigation);
    }

    private void initComponent() {
        BadgeDrawable badgeDrawable = bottomNavigation.getOrCreateBadge(R.id.page_home);
        //badgeDrawable.setVisible(false);
        badgeDrawable.setNumber(99);

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

}
