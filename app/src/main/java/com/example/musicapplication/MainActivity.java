package com.example.musicapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;

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
        return false;
    }
}