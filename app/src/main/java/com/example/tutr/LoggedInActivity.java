package com.example.tutr;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.WindowManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class LoggedInActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged_in);
        BottomNavigationView bottomNavigation = findViewById(R.id.bottom_nav);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_ui_container,new ChatFragment()).commit();
        Toolbar toolbar = findViewById(R.id.chat_toolbar);
        setSupportActionBar(toolbar);

        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Fragment selectedFragment = null;
                switch (menuItem.getItemId()){
                    case R.id.nav_chat:
                        selectedFragment = new ChatFragment();
                    break;
                    case R.id.nav_questions:
                        selectedFragment = new QuestionFragment();
                        break;
                    case R.id.nav_profile:
                        selectedFragment = new ProfileFragment();

                }

                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_ui_container,selectedFragment).commit();


                return true;
            }
        });
    }



}
