package com.example.tutr;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private EditText UEmail;
    private EditText UPassword;
    private TextView Register;

    private Button Login;

    private FirebaseAuth UAuth;

    private FirebaseAuth.AuthStateListener UAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        UAuth = FirebaseAuth.getInstance();
        UEmail = findViewById(R.id.E_Email);
        UPassword = findViewById(R.id.E_Password);
        Login = findViewById(R.id.Login);
        Register = findViewById(R.id.Reg);

        UAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if (firebaseAuth.getCurrentUser() != null) {
                    //Change Activity
                }

            }
        };

        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SignIn();



            }
        });

        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);

            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();

        UAuth.addAuthStateListener(UAuthListener);
    }


    private void SignIn() {

        String Email = UEmail.getText().toString();
        String Password = UPassword.getText().toString();

        if (TextUtils.isEmpty(Email) || TextUtils.isEmpty(Password)) {

            Toast.makeText(MainActivity.this, "Please Enter Email & Password", Toast.LENGTH_LONG).show();

        } else {
            UAuth.signInWithEmailAndPassword(Email, Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (!task.isSuccessful()) {
                        Toast.makeText(MainActivity.this, "Invalid Email & Password", Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        startActivity(new Intent(MainActivity.this, ChatTranscriptActivity.class));

                    }

                }
            });
        }


    }
}
