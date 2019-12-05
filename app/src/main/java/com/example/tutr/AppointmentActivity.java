package com.example.tutr;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AppointmentActivity extends AppCompatActivity {
    private TextView UserName;

    private TextView Mon;
    private TextView Tue;
    private TextView Wed;
    private TextView Thu;
    private TextView Fri;
    private TextView Sat;
    private TextView Sun;

    private TextView MonAv;
    private TextView TueAv;
    private TextView WedAv;
    private TextView ThuAv;
    private TextView FriAv;
    private TextView SatAv;
    private TextView SunAv;

    private Spinner MonAvSel;
    private Spinner TueAvSel;
    private Spinner WedAvSel;
    private Spinner ThuAvSel;
    private Spinner FriAvSel;
    private Spinner SatAvSel;
    private Spinner SunAvSel;

    private Button AppMon;
    private Button AppTue;
    private Button AppWed;
    private Button AppThu;
    private Button AppFri;
    private Button AppSat;
    private Button AppSun;

    private FirebaseUser firebaseUser;
    private DatabaseReference reference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointments);



    String ID = getIntent().getStringExtra("EXTRA_ID");
    String Username = getIntent().getStringExtra("Extra_Username");
    //String Mo = getIntent().getStringExtra("Extra_MonAv");
    String Tu = getIntent().getStringExtra("Extra_TueAv");
    String We = getIntent().getStringExtra("Extra_WedAv");
    String Th = getIntent().getStringExtra("Extra_ThuAv");
    String Fr = getIntent().getStringExtra("Extra_FriAv");
    String Sa = getIntent().getStringExtra("Extra_SatAv");
    String Su = getIntent().getStringExtra("Extra_SunAv");
        Toast.makeText(this, ID, Toast.LENGTH_SHORT).show();
        if(ID!=null) {
            reference = FirebaseDatabase.getInstance().getReference("Users").child("Tutors").child(ID);
        }


    DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
    DatabaseReference Tutor = ref.child("Users").child("Tutors").child(ID);
    //Toast.makeText(getApplicationContext(), Mo, Toast.LENGTH_SHORT).show();
    Tutor.addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
           String Mo = dataSnapshot.child("MondayAvailability").getValue(String.class);
           String Tu = dataSnapshot.child("TuesdayAvailability").getValue(String.class);
           String We = dataSnapshot.child("WednesdayAvailability").getValue(String.class);
           String Th = dataSnapshot.child("ThursdayAvailability").getValue(String.class);
           String Fr = dataSnapshot.child("FridayAvailability").getValue(String.class);
           String Sa = dataSnapshot.child("SaturdayAvailability").getValue(String.class);
           String Su = dataSnapshot.child("SundayAvailability").getValue(String.class);

          
           //Toast.makeText(getApplicationContext(), MondayEn, Toast.LENGTH_LONG).show();

            ArrayAdapter<CharSequence> App = ArrayAdapter.createFromResource(getApplicationContext(), R.array.App, android.R.layout.simple_spinner_item);
            App.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            if (Mo == null){
                Mon.setVisibility(View.GONE);
                MonAv.setVisibility(View.GONE);
                MonAvSel.setVisibility(View.GONE);
                AppMon.setVisibility(View.GONE);
                //Toast.makeText(getApplicationContext(),Mo, Toast.LENGTH_SHORT).show();
                //Toast.makeText(getApplicationContext(), Tutor.toString(), Toast.LENGTH_SHORT).show();
            }
            else {
                MonAv.setText(Mo);
                MonAvSel.setAdapter(App);
                // Toast.makeText(getApplicationContext(), Mo, Toast.LENGTH_SHORT).show();
            }
           //Toast.makeText(getApplicationContext(), dataSnapshot.getValue(String.class), Toast.LENGTH_SHORT).show();
            if (Tu == null){
                Tue.setVisibility(View.GONE);
                TueAv.setVisibility(View.GONE);
                TueAvSel.setVisibility(View.GONE);
                AppTue.setVisibility(View.GONE);
            }
            else{
                TueAv.setText(Tu);
                TueAvSel.setAdapter(App);
                //Toast.makeText(this, Tu, Toast.LENGTH_SHORT).show();
            }
            if (We == null){
                Wed.setVisibility(View.GONE);
                WedAv.setVisibility(View.GONE);
                WedAvSel.setVisibility(View.GONE);
                AppWed.setVisibility(View.GONE);
            }
            else{
                WedAv.setText(We);
                WedAvSel.setAdapter(App);
            }
            if (Th == null){
                Thu.setVisibility(View.GONE);
                ThuAv.setVisibility(View.GONE);
                ThuAvSel.setVisibility(View.GONE);
                AppThu.setVisibility(View.GONE);
            }
            else{
                ThuAv.setText(Th);
                ThuAvSel.setAdapter(App);
            }
            if (Fr == null){
                Fri.setVisibility(View.GONE);
                FriAv.setVisibility(View.GONE);
                FriAvSel.setVisibility(View.GONE);
                AppFri.setVisibility(View.GONE);
            }
            else {
                FriAv.setText(Fr);
                FriAvSel.setAdapter(App);
            }
            if (Sa == null){
                Sat.setVisibility(View.GONE);
                SatAv.setVisibility(View.GONE);
                SatAvSel.setVisibility(View.GONE);
                AppSat.setVisibility(View.GONE);
            }
            else{
                SatAv.setText(Sa);
                SatAvSel.setAdapter(App);
            }
            if (Su == null){
                Sun.setVisibility(View.GONE);
                SunAv.setVisibility(View.GONE);
                SunAvSel.setVisibility(View.GONE);
                AppSun.setVisibility(View.GONE);
            }
            else{
                SunAv.setText(Su);
                SunAvSel.setAdapter(App);
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    });

    Mon = findViewById(R.id.MonT);
    MonAv = findViewById(R.id.MonAvT);
    MonAvSel = findViewById(R.id.MonAvSel);
    AppMon = findViewById(R.id.AppMon);

        Tue = findViewById(R.id.TueT);
        TueAv = findViewById(R.id.TueAvT);
        TueAvSel = findViewById(R.id.TueAvSel);
        AppTue = findViewById(R.id.AppTue);

        Wed = findViewById(R.id.WedT);
        WedAv = findViewById(R.id.WedAvT);
        WedAvSel = findViewById(R.id.WedAvSel);
        AppWed = findViewById(R.id.AppWed);

        Thu = findViewById(R.id.ThuT);
        ThuAv = findViewById(R.id.ThuAvT);
        ThuAvSel = findViewById(R.id.ThuAvSel);
        AppThu = findViewById(R.id.AppThu);

        Fri = findViewById(R.id.FriT);
        FriAv = findViewById(R.id.FriAvT);
        FriAvSel = findViewById(R.id.FriAvSel);
        AppFri = findViewById(R.id.AppFri);

        Sat = findViewById(R.id.SatT);
        SatAv = findViewById(R.id.SatAvT);
        SatAvSel = findViewById(R.id.SatAvSel);
        AppSat = findViewById(R.id.AppSat);

        Sun = findViewById(R.id.SunT);
        SunAv = findViewById(R.id.SunAvT);
        SunAvSel = findViewById(R.id.SunAvSel);
        AppSun = findViewById(R.id.AppSun);

AppMon.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        Intent intent = new Intent(getApplicationContext(), LoggedInActivity.class);
        startActivity(intent);
    }
});
        AppTue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LoggedInActivity.class);
                startActivity(intent);
            }
        });
        AppWed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LoggedInActivity.class);
                startActivity(intent);
            }
        });
        AppThu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LoggedInActivity.class);
                startActivity(intent);
            }
        });
        AppFri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LoggedInActivity.class);
                startActivity(intent);
            }
        });
        AppSat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LoggedInActivity.class);
                startActivity(intent);
            }
        });
        AppSun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LoggedInActivity.class);
                startActivity(intent);
            }
        });



    UserName = findViewById(R.id.Username);
    String formattedText = String.format("for: %s", Username);
    UserName.setText(formattedText);


}

}
