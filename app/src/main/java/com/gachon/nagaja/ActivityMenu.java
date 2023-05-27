package com.gachon.nagaja;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ActivityMenu extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    private BottomNavigationView mBottomNavigationView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);


        mBottomNavigationView = findViewById(R.id.menu_bottom_navigation);
        mBottomNavigationView.setOnNavigationItemSelectedListener(this);

        //처으에 앱을 실행했을떄 Bookmark창이 디폴트로 보여지기 위함
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.menu_frame_layout, new BookmarkFragment())
                .commit();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_bookmark:
                // MENU1 클릭시 실행될 코드
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.menu_frame_layout, new BookmarkFragment())
                        .commit();
                return true;
            case R.id.menu_home:
                // MENU2 클릭시 실행될 코드
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.menu_frame_layout, new UploadMapFragment())
                        .commit();
                return true;
            case R.id.menu_store:
                // MENU3 클릭시 실행될 코드
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.menu_frame_layout, new ImageFragment())
                        .commit();
                return true;
            default:
                return false;
        }
    }
}
