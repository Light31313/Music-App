package com.example.musicapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.musicapplication.Fragment.FavoriteFragment;
import com.example.musicapplication.Fragment.MusicFragment;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener {
    BottomNavigationView bottomNavigation;
    BadgeDrawable badgeDrawable;
    Bundle bundle;

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
        badgeDrawable = bottomNavigation.getOrCreateBadge(R.id.page_home);
        //badgeDrawable.setVisible(false);
        badgeDrawable.setNumber(99);

        Intent intent = getIntent();
        bundle = intent.getBundleExtra("bundle");
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, MusicFragment.class, bundle).commit();
    }

    private void initEvent() {
        bottomNavigation.setOnItemSelectedListener(this);
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
