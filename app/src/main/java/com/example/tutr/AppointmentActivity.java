package com.example.tutr;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class AppointmentActivity extends AppCompatActivity {
    private TextView UserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointments);

    String ID = getIntent().getStringExtra("EXTRA_ID");
    String Username = getIntent().getStringExtra("Extra_Username");

    UserName = findViewById(R.id.Username);
    UserName.setText(Username);


}
}
