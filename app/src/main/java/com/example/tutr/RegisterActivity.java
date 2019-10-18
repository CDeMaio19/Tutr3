package com.example.tutr;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
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

public class RegisterActivity extends AppCompatActivity {


    private FirebaseAuth UAuth;

    private EditText FN;
    private EditText LN;
    private EditText SO;
    private EditText UEmail;
    private EditText UPassword;

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

        ST = (RadioButton) findViewById(R.id.Student);
        TU = (RadioButton) findViewById(R.id.Educator);

        Reg = (Button) findViewById(R.id.Login);

        Reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RegisterUser();
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
                            String id = firebaseUser.getUid();


                            HashMap<String, String> userHash = new HashMap<>();
                            userHash.put("id", id);
                            userHash.put("username", FN.getText().toString() + "_" + LN.getText().toString());
                            userHash.put("School", SO.getText().toString());
                            userHash.put("Email", UEmail.getText().toString());
                            if (ST.isChecked()) {
                                final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child("Students");
                                reference.push().child(id).setValue(userHash);
                            }
                            else{
                                final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child("Tutor");
                                reference.push().child(id).setValue(userHash);
                            }

                            startActivity(new Intent(RegisterActivity.this,MainActivity.class));
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
}
