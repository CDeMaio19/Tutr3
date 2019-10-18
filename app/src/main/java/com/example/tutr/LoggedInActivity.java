package com.example.tutr;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class LoggedInActivity extends AppCompatActivity {
    private final int REQUEST_CODE = 1;
    private ImageView profileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged_in);
        BottomNavigationView bottomNavigation = findViewById(R.id.bottom_nav);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        profileImage = findViewById(R.id.user_profile);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.fragment_ui_container, new ChatFragment());
        ft.addToBackStack("Chat Fragment");
        ft.commit();
        Toolbar toolbar = findViewById(R.id.chat_toolbar);
        setSupportActionBar(toolbar);

        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Fragment selectedFragment = null;
                switch (menuItem.getItemId()) {
                    case R.id.nav_chat:
                        selectedFragment = new ChatFragment();

                        break;
                    case R.id.nav_questions:
                        selectedFragment = new QuestionFragment();
                        break;
                    case R.id.nav_profile:
                        selectedFragment = new ProfileFragment();

                }

                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_ui_container, selectedFragment).commit();


                return true;
            }
        });

    }


}
