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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private EditText UEmail;
    private EditText UPassword;


    private FirebaseAuth UAuth;


    private boolean isTutor;

    private DatabaseReference userStatusReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        UAuth = FirebaseAuth.getInstance();
        UEmail = findViewById(R.id.E_Email);
        UPassword = findViewById(R.id.E_Password);
        Button Login = findViewById(R.id.Login);
        TextView Register = findViewById(R.id.Reg);
        userStatusReference = FirebaseDatabase.getInstance().getReference("Users").child("Tutors");

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
        UAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

            }
        });
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
                    else {
                            final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

                            if (firebaseUser != null) {
                                if(UEmail.getText().toString().equals("admin@admin.com") && UPassword.getText().toString().equals("adminPassword"))
                                {
                                    Intent intent = new Intent(MainActivity.this,AdminActivity.class);
                                    startActivity(intent);
                                }
                                else {
                                    userStatusReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            //checks whether the logged in user is a tutor or not
                                            isTutor = dataSnapshot.hasChild(firebaseUser.getUid());
                                            Intent intent = new Intent(MainActivity.this, LoggedInActivity.class);
                                            intent.putExtra("User Status", isTutor);
                                            //Change Activity
                                            startActivity(intent);

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                }


                            }
                    }

                }
            });
        }


    }
}
