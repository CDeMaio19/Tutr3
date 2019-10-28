package com.example.tutr;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {


    private FirebaseAuth UAuth;

    private EditText FN;
    private EditText LN;
    private EditText SO;
    private EditText UEmail;
    private EditText UPassword;
    private Spinner FOE;
    private Spinner Sub;
    private String SubjectText = "English";

    private RadioButton ST;
    private RadioButton TU;

    private Button Reg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        UAuth = FirebaseAuth.getInstance();

        UEmail = (EditText) findViewById(R.id.E_Email);
        UPassword = (EditText) findViewById(R.id.E_Password);

        FN = (EditText) findViewById(R.id.e_FName);
        LN = (EditText) findViewById(R.id.e_LName);
        SO = (EditText) findViewById(R.id.e_School);
        FOE = (Spinner) findViewById(R.id.FOE);
        Sub = (Spinner) findViewById(R.id.Subject);

        ST = (RadioButton) findViewById(R.id.Student);
        TU = (RadioButton) findViewById(R.id.Educator);

        Reg = (Button) findViewById(R.id.Login);

        Reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RegisterUser();
            }
        });

        FOE.setVisibility(View.INVISIBLE);
        Sub.setVisibility(View.INVISIBLE);

        ArrayAdapter<CharSequence> S = ArrayAdapter.createFromResource(this, R.array.Subjects, android.R.layout.simple_spinner_item);
        S.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Sub.setAdapter(S);
        Sub.setOnItemSelectedListener(this);

        FOE.setAdapter(S);
        FOE.setOnItemSelectedListener(this);

        ArrayAdapter<CharSequence> E = ArrayAdapter.createFromResource(this, R.array.English, android.R.layout.simple_spinner_item);
        E.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ArrayAdapter<CharSequence> F = ArrayAdapter.createFromResource(this, R.array.Foreign_Language, android.R.layout.simple_spinner_item);
        F.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ArrayAdapter<CharSequence> M = ArrayAdapter.createFromResource(this, R.array.Math, android.R.layout.simple_spinner_item);
        M.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ArrayAdapter<CharSequence> Sc = ArrayAdapter.createFromResource(this, R.array.Science, android.R.layout.simple_spinner_item);
        Sc.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ArrayAdapter<CharSequence> SS = ArrayAdapter.createFromResource(this, R.array.Social_Studies, android.R.layout.simple_spinner_item);
        SS.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);




        TU.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FOE.setVisibility(View.VISIBLE);
                Sub.setVisibility(View.VISIBLE);
            }
        });

        ST.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    FOE.setVisibility(View.INVISIBLE);
                    Sub.setVisibility(View.INVISIBLE);
            }
        });

    }
    private void RegisterUser(){
        String email = UEmail.getText().toString().trim();
        String password = UPassword.getText().toString().trim();

        if(TextUtils.isEmpty(email)){
            Toast.makeText(this,"Please enter email",Toast.LENGTH_LONG).show();
            return;
        }

        if(TextUtils.isEmpty(password)){
            Toast.makeText(this,"Please enter password",Toast.LENGTH_LONG).show();
            return;
        }

        Toast.makeText(this,"Registering Please Wait...", Toast.LENGTH_LONG).show();

        UAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, "Registered", Toast.LENGTH_LONG).show();
                            //add user to the database
                            FirebaseUser firebaseUser = UAuth.getCurrentUser();
                            if(firebaseUser != null) {
                                String id = firebaseUser.getUid();


                                HashMap<String, String> userHash = new HashMap<>();
                                userHash.put("id", id);
                                userHash.put("username", FN.getText().toString() + "_" + LN.getText().toString());
                                userHash.put("school", SO.getText().toString());
                                userHash.put("email", UEmail.getText().toString());
                                userHash.put("profilePhotoURL", "default");
                                if (TU.isChecked()) {
                                    userHash.put("areaOfExpertise","");
                                }
                                else {
                                userHash.put("areaOfExpertise", "");
                                }
                                userHash.put("description", "");


                                if (ST.isChecked()) {
                                    final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child("Students").child(id);
                                    reference.setValue(userHash);
                                } else {

                                    final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child("Tutors").child(id);
                                    reference.setValue(userHash);
                                }

                                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                            }
                        }
                        else {
                            Toast.makeText(RegisterActivity.this, "Registration Error", Toast.LENGTH_LONG).show();
                            FirebaseAuthException e = (FirebaseAuthException)task.getException();
                            Toast.makeText(RegisterActivity.this, "Failed Registration: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.e("LoginActivity", "Failed Registration", e);

                        }
                    }
                });
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        switch (adapterView.getId()) {
            case R.id.Subject:
                SubjectText = adapterView.getSelectedItem().toString();
                ArrayAdapter<CharSequence> E = ArrayAdapter.createFromResource(this, R.array.English, android.R.layout.simple_spinner_item);
                E.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                ArrayAdapter<CharSequence> F = ArrayAdapter.createFromResource(this, R.array.Foreign_Language, android.R.layout.simple_spinner_item);
                F.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                ArrayAdapter<CharSequence> M = ArrayAdapter.createFromResource(this, R.array.Math, android.R.layout.simple_spinner_item);
                M.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                ArrayAdapter<CharSequence> Sc = ArrayAdapter.createFromResource(this, R.array.Science, android.R.layout.simple_spinner_item);
                Sc.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                ArrayAdapter<CharSequence> SS = ArrayAdapter.createFromResource(this, R.array.Social_Studies, android.R.layout.simple_spinner_item);
                SS.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                switch (SubjectText){
                    case "English":
                        FOE.setAdapter(E);
                        //FOE.setOnItemSelectedListener(this);
                        break;
                    case "Foreign Language":
                        FOE.setAdapter(F);
                        //FOE.setOnItemSelectedListener(this);
                        break;
                    case "Math":
                        FOE.setAdapter(M);
                        // FOE.setOnItemSelectedListener(this);
                        break;
                    case "Science":
                        FOE.setAdapter(Sc);
                        //FOE.setOnItemSelectedListener(this);
                        break;
                    case "Social Studies":
                        FOE.setAdapter(SS);
                        //FOE.setOnItemSelectedListener(this);
                        break;
                    default:
                        break;
                }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
