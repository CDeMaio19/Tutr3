package com.example.tutr;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class LoggedInActivity extends AppCompatActivity {

    private String majorSubjectSelected;
    private Spinner majorSubjectSpinner;
    private Spinner minorSubjectSpinner;
    private MatchingFragment matchingFragment = new MatchingFragment();
    private ChatFragment chatFragment = new ChatFragment();
    private ProfileFragment profileFragment = new ProfileFragment();
    private TutorRatingsFragment tutorRatingsFragment = new TutorRatingsFragment();
    private DatabaseReference userReference;
    private EditText questionData;
    private EditText descriptionData;
    private Bundle userStatusBundle;
    public DataSnapshot myDataSnapshot;
    public static boolean isTutor;
    private AlertDialog alert;
    private View popupView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged_in);

        Intent intent = getIntent();
        isTutor = intent.getBooleanExtra("User Status",false);
        String userPath;
        if(isTutor)
        {
            userPath = "Tutors";
        }
        else
        {
            userPath = "Students";
        }
        userStatusBundle = new Bundle();
        userStatusBundle.putBoolean("User Status",isTutor);

        DatabaseReference subjectsReference = FirebaseDatabase.getInstance().getReference("Subjects");
        userReference = FirebaseDatabase.getInstance().getReference("Users").child(userPath);
        subjectsReference.addValueEventListener(subjectsValueEventListener);
        userReference.addListenerForSingleValueEvent(userValueEventListener);
        BottomNavigationView bottomNavigation = findViewById(R.id.bottom_nav);
        LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
        if(inflater!=null) {
            popupView = inflater.inflate(R.layout.ask_a_question_popup, null);
        }

        questionData = popupView.findViewById(R.id.question_editText);
        descriptionData = popupView.findViewById(R.id.question_description_editText);

        bottomNavigation.getMenu().add(Menu.NONE,R.id.nav_ratings,0,R.string.Ratings).setIcon(R.drawable.ic_ratings_black_24dp);
        bottomNavigation.getMenu().add(Menu.NONE,R.id.nav_matching,1,R.string.Matching).setIcon(R.drawable.ic_match_black_24dp);
        bottomNavigation.getMenu().add(Menu.NONE,R.id.nav_chat,2,R.string.chats).setIcon(R.drawable.ic_question_answer_black_24dp);
        bottomNavigation.getMenu().add(Menu.NONE,R.id.nav_profile,3,R.string.profile).setIcon((R.drawable.ic_person_black_24dp));

        FragmentManager fm = getSupportFragmentManager();
        final FragmentTransaction ft = fm.beginTransaction();
        //starts the Matching or tutor ratings fragment as the initial fragment for the user to see depending on
        //what type of user is logged in
        if(!isTutor) {
            bottomNavigation.getMenu().findItem(R.id.nav_ratings).setVisible(false);
            bottomNavigation.getMenu().findItem(R.id.nav_matching).setVisible(true);
            ft.add(R.id.fragment_ui_container, new MatchingFragment());
            ft.addToBackStack("Matching Fragment");
            ft.commit();
        }
        else
        {
            bottomNavigation.getMenu().findItem(R.id.nav_ratings).setVisible(true);
            bottomNavigation.getMenu().findItem(R.id.nav_matching).setVisible(false);
            ft.add(R.id.fragment_ui_container, new TutorRatingsFragment());
            ft.addToBackStack("Tutor Ratings Fragment");
            ft.commit();
        }


        //stops keyboard from automatically popping up on the start of the activity
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);



        Toolbar toolbar = findViewById(R.id.chat_toolbar);
        setSupportActionBar(toolbar);

        //sets the cases for the bottom navigation - allows the switching between fragments on click of the bottom navigation
        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    switch (menuItem.getItemId()) {
                        case R.id.nav_matching:
                            fragmentTransaction.replace(R.id.fragment_ui_container, matchingFragment);
                            break;
                        case R.id.nav_ratings:
                            fragmentTransaction.replace(R.id.fragment_ui_container, tutorRatingsFragment);
                            break;
                        case R.id.nav_chat:
                            fragmentTransaction.replace(R.id.fragment_ui_container, chatFragment);
                            break;
                        case R.id.nav_profile:
                            profileFragment.setArguments(userStatusBundle);
                            fragmentTransaction.replace(R.id.fragment_ui_container, profileFragment);
                    }

                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

                return true;
            }
        });

    }

    //creates the initial popup screen for asking a question that the student will see when they log into the application
    public void Popup(final DataSnapshot dataSnapshot)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        Button submit = popupView.findViewById(R.id.submit_button);
        submit.setOnClickListener(submitOnClickListener);
        //spinners to display the major and minor subjects for the question being asked
        majorSubjectSpinner = popupView.findViewById(R.id.major_subject_spinner);
        minorSubjectSpinner = popupView.findViewById(R.id.minor_subject_spinner);
        final ArrayList<String> majorSubjectsList = new ArrayList<>();
        final ArrayList<String> minorSubjectsList = new ArrayList<>();
        final ArrayAdapter<String> majorSubjectsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, majorSubjectsList);
        final ArrayAdapter<String> minorSubjectsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, minorSubjectsList);
        //loops through all of the major subjects in the database and adds it to the list that will be used in the spinner
        for (DataSnapshot majorSubject: dataSnapshot.getChildren())
        {
            majorSubjectsList.add(majorSubject.getKey());
            majorSubjectsAdapter.notifyDataSetChanged();

        }
        majorSubjectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                majorSubjectSelected = (String)majorSubjectSpinner.getSelectedItem();
                minorSubjectsAdapter.clear();
                //depending on the major subject selected get all children under that node in the database and put into a list
                for (DataSnapshot minorSubject: dataSnapshot.child(majorSubjectSelected).getChildren())
                {
                    minorSubjectsList.add(minorSubject.getKey());
                    minorSubjectsAdapter.notifyDataSetChanged();
                }

            }
            //sets the default value of the spinners if nothing is selected
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                majorSubjectSelected = "English";
                for (DataSnapshot minorSubject: dataSnapshot.child(majorSubjectSelected).getChildren())
                {
                    minorSubjectsList.add(minorSubject.getKey());
                    minorSubjectsAdapter.notifyDataSetChanged();
                }

            }
        });
        //sets the adapters for both spinners so the data can be viewed
        majorSubjectsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        majorSubjectSpinner.setAdapter(majorSubjectsAdapter);
        minorSubjectsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        minorSubjectSpinner.setAdapter(minorSubjectsAdapter);
        builder.setView(popupView);
        alert = builder.create();
        if(popupView.getParent()!=null)
        {
            ((ViewGroup)popupView.getParent()).removeView(popupView);
        }
        //show popup window
        alert.show();

    }
    private ValueEventListener subjectsValueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            myDataSnapshot = dataSnapshot;
            if(!isTutor) {
                Popup(myDataSnapshot);
            }

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };
    private ValueEventListener userValueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            if(firebaseUser!=null) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot.child("id").getValue().toString().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                        //marks that the user is online
                        userReference.child(firebaseUser.getUid()).child("isOnline").setValue(true);
                        break;
                    }
                }
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    private View.OnClickListener submitOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(!questionData.getText().toString().equals("")&& !descriptionData.getText().toString().equals("")) {
                alert.dismiss();
                SetMatchingData();
            }

        }
    };
    //sets the data that the matching fragment will use to find the correct tutors for the given question
    private void SetMatchingData()
    {
        String majorSubject;
        String minorSubject;
        String question;
        String description;

        questionData = popupView.findViewById(R.id.question_editText);
        descriptionData = popupView.findViewById(R.id.question_description_editText);

        majorSubject = (String)majorSubjectSpinner.getSelectedItem();
        minorSubject = (String)minorSubjectSpinner.getSelectedItem();

        question = questionData.getText().toString();
        description = descriptionData.getText().toString();

        Bundle bundle = new Bundle();
        //data the matching fragment will receive
        bundle.putString("Major Subject", majorSubject);
        bundle.putString("Minor Subject", minorSubject);
        bundle.putString("Question", question);
        bundle.putString("Description", description);
        MatchingFragment matchingFragment = new MatchingFragment();
        matchingFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_ui_container, matchingFragment).commit();
    }

}
