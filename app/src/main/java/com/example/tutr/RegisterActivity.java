package com.example.tutr;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

import static androidx.constraintlayout.widget.Constraints.TAG;

public class RegisterActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {


    private FirebaseAuth UAuth;

    private EditText FN;
    private EditText LN;
    private EditText SO;
    private EditText UEmail;
    private EditText UPassword;
    private EditText Resume;
    private Spinner FOE;
    private Spinner Sub;
    private String SubjectText = "English";
    private String ExpertText;
    private String filePath;

    private RadioButton ST;
    private RadioButton TU;

    private ImageButton attachResume;
    private final int REQUEST_CODE = 1;

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
        Resume = findViewById(R.id.resume_attachment);
        FOE = (Spinner) findViewById(R.id.FOE);
        Sub = (Spinner) findViewById(R.id.Subject);


        ST = (RadioButton) findViewById(R.id.Student);
        TU = (RadioButton) findViewById(R.id.Educator);

        Button Reg = (Button) findViewById(R.id.Login);
        attachResume = findViewById(R.id.attach_resume_button);

        Reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RegisterUser();
            }
        });

        FOE.setVisibility(View.INVISIBLE);
        Sub.setVisibility(View.INVISIBLE);
        Resume.setVisibility(View.INVISIBLE);
        attachResume.setVisibility(View.INVISIBLE);

        final ArrayAdapter<CharSequence> S = ArrayAdapter.createFromResource(this, R.array.Subjects, android.R.layout.simple_spinner_item);
        S.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Sub.setAdapter(S);
        Sub.setOnItemSelectedListener(this);

        FOE.setAdapter(S);
        FOE.setOnItemSelectedListener(this);

        final ArrayAdapter<CharSequence> E = ArrayAdapter.createFromResource(this, R.array.English, android.R.layout.simple_spinner_item);
        E.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ArrayAdapter<CharSequence> F = ArrayAdapter.createFromResource(this, R.array.Foreign_Language, android.R.layout.simple_spinner_item);
        F.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ArrayAdapter<CharSequence> M = ArrayAdapter.createFromResource(this, R.array.Math, android.R.layout.simple_spinner_item);
        M.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ArrayAdapter<CharSequence> Sc = ArrayAdapter.createFromResource(this, R.array.Science, android.R.layout.simple_spinner_item);
        Sc.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ArrayAdapter<CharSequence> SS = ArrayAdapter.createFromResource(this, R.array.Social_Studies, android.R.layout.simple_spinner_item);
        SS.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        final ArrayAdapter<CharSequence> Subsc = ArrayAdapter.createFromResource(this, R.array.Subscription, android.R.layout.simple_spinner_item);
        Subsc.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        final ArrayAdapter<CharSequence> Pay = ArrayAdapter.createFromResource(this, R.array.Payment, android.R.layout.simple_spinner_item);
        Pay.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        attachResume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("application/pdf");
                //starts activity to select a photo from the gallery
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_CODE);

            }
        });

        TU.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FOE.setVisibility(View.VISIBLE);
                FOE.setAdapter(E);
                Sub.setVisibility(View.VISIBLE);
                Sub.setAdapter(S);
                Resume.setVisibility(View.VISIBLE);
                attachResume.setVisibility(View.VISIBLE);
            }
        });

        ST.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FOE.setVisibility(View.VISIBLE);
                FOE.setAdapter(Pay);
                Sub.setVisibility(View.VISIBLE);
                Sub.setAdapter(Subsc);
                Resume.setVisibility(View.INVISIBLE);
                attachResume.setVisibility(View.INVISIBLE);
            }
        });

    }

    private void RegisterUser() {
        String email = UEmail.getText().toString().trim();
        String password = UPassword.getText().toString().trim();
        String resumeText = Resume.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please enter email", Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter password", Toast.LENGTH_LONG).show();
            return;
        }
        if(TextUtils.isEmpty(resumeText))
        {
            Toast.makeText(this, "Please attach a resume", Toast.LENGTH_LONG).show();
            return;
        }

        Toast.makeText(this, "Registering Please Wait...", Toast.LENGTH_LONG).show();

        UAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, "Registered", Toast.LENGTH_LONG).show();
                            //add user to the database
                            FirebaseUser firebaseUser = UAuth.getCurrentUser();
                            if (firebaseUser != null) {
                                String id = firebaseUser.getUid();


                                HashMap<String, String> userHash = new HashMap<>();
                                userHash.put("id", id);
                                userHash.put("username", FN.getText().toString() + "_" + LN.getText().toString());
                                userHash.put("school", SO.getText().toString());
                                userHash.put("email", UEmail.getText().toString());
                                userHash.put("profilePhoto", "default");
                                if (TU.isChecked()) {
                                    userHash.put("subject", SubjectText);
                                    userHash.put("areaOfExpertise",ExpertText);
                                    userHash.put("resume", filePath);
                                }

                                userHash.put("description", "");


                                if (ST.isChecked()) {
                                    userHash.put("paymentMeathod", FOE.getSelectedItem().toString());
                                    userHash.put("payment", Sub.getSelectedItem().toString());
                                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child("Students").child(id);
                                    reference.setValue(userHash);
                                } else {
                                    userHash.put("subject", Sub.getSelectedItem().toString());
                                    userHash.put("areaOfExpertise", FOE.getSelectedItem().toString());
                                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child("Tutors").child(id);
                                    reference.setValue(userHash);
                                }

                                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                            }
                        } else {
                            Toast.makeText(RegisterActivity.this, "Registration Error", Toast.LENGTH_LONG).show();
                            FirebaseAuthException e = (FirebaseAuthException) task.getException();
                            Toast.makeText(RegisterActivity.this, "Failed Registration: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
                switch (SubjectText) {
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
            case R.id.FOE:
                ExpertText = adapterView.getSelectedItem().toString();

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {

            switch (requestCode)
            {
                case REQUEST_CODE:
                    if (resultCode == Activity.RESULT_OK) {
                        if(data!=null) {
                            filePath = data.getData().toString();
                            Resume.setText(filePath);
                        }
                        break;
                    }
                    else if (resultCode == Activity.RESULT_CANCELED) {
                        Log.e(TAG, "Selecting file cancelled");
                    }
                    break;
            }
        }
        catch (Exception e) {
            Log.e(TAG, "Exception in onActivityResult : " + e.getMessage());
        }
    }
}
