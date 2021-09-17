package com.example.musicapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
    }

    private void initEvent() {
        bottomNavigation.setOnItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;
        int id = item.getItemId();
        if (id == R.id.page_home) {
            fragment = new MusicFragment();
        } else if (id == R.id.page_favorite) {
            fragment = new FavoriteFragment();
        }
        return loadFragment(fragment);
    }

    private boolean loadFragment(Fragment fragment) {
        //switching fragment
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }
}