package com.example.tutr;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdminActivity extends AppCompatActivity {
    private String adminOptionSelected;
    private Spinner adminViewOptions;
    private String chatRoomIDReference;
    private FirebaseListAdapter<User> adapter;
    private Spinner chatsSpinner;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        chatsSpinner = findViewById(R.id.chats_spinner);
        chatsSpinner.setVisibility(View.INVISIBLE);
        chatsSpinner.setOnItemSelectedListener(chatsItemSelected);
        FirebaseDatabase.getInstance().getReference("Chats").child("ChatRooms").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                SetChatsSpinner(chatsSpinner,dataSnapshot);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        adminOptionSelected = "View all users";
        adminViewOptions = findViewById(R.id.adminViewOptions);
        listView = findViewById(R.id.listView);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.AdminOptions,android.R.layout.simple_spinner_item);
        adminViewOptions.setAdapter(adapter);
        adminViewOptions.setOnItemSelectedListener(adminViewOptionsItemSelected);
    }

    private AdapterView.OnItemSelectedListener adminViewOptionsItemSelected = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            adminOptionSelected = (String) adminViewOptions.getSelectedItem();
            chatsSpinner.setVisibility(View.INVISIBLE);
            switch (adminOptionSelected)
            {
                case "View tutors":
                    ViewTutors();
                    listView.setAdapter(adapter);
                    break;
                case "View students":
                    ViewStudents();
                    listView.setAdapter(adapter);
                    break;
                case "View chat logs":
                    ViewChatLogs();
                    break;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            adminOptionSelected = "View all users";

        }
    };
    private void ViewTutors() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child("Tutors");
        adapter = new FirebaseListAdapter<User>(this,
                User.class, R.layout.tutor_list_item, reference) {
            @Override
            protected void populateView(View v, final User model, int position) {
                CircleImageView profileImage = v.findViewById(R.id.profile_image);
                TextView userName = v.findViewById(R.id.tutor_name);
                TextView schoolOrOccupation = v.findViewById(R.id.tutor_school_occupation);
                TextView areaOfExpertise = v.findViewById(R.id.tutor_area_of_expertise);
                TextView totalSessionTime = v.findViewById(R.id.total_session_time_data);
                long time = model.getTotalSessionTimes();
                //creates string in the form of HH:mm:ss
                String timeString = String.format(Locale.getDefault(),"%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(time),
                        TimeUnit.MILLISECONDS.toMinutes(time) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(time)),
                        TimeUnit.MILLISECONDS.toSeconds(time) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time)));
                totalSessionTime.setText(timeString);

                Button deleteButton = v.findViewById(R.id.delete_button);
                //populates the listView item with the data of the given tutor that matched with the query
                userName.setText(model.getUsername());
                schoolOrOccupation.setText(model.getSchool());
                areaOfExpertise.setText(model.getAreaOfExpertise());
                if (!model.getProfilePhoto().equals("default")) {
                    String profileString = model.getProfilePhoto();
                    byte[] encodeByte = Base64.decode(profileString, Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
                    profileImage.setImageBitmap(bitmap);
                } else {
                    profileImage.setImageResource(R.drawable.graduation_2841875_640);
                }
                //deletes the user from the application permanently
                deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });

            }
        };
    }
    private void ViewStudents()
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child("Students");
        adapter = new FirebaseListAdapter<User>(this,
                User.class, R.layout.student_list_item, reference) {
            @Override
            protected void populateView(View v, final User model, int position) {
                CircleImageView profileImage = v.findViewById(R.id.profile_image);
                TextView userName = v.findViewById(R.id.tutor_name);
                TextView email = v.findViewById(R.id.email);
                email.setText(model.getEmail());
                Button deleteButton = v.findViewById(R.id.delete_button);
                //populates the listView item with the data of the given tutor that matched with the query
                userName.setText(model.getUsername());
                if (!model.getProfilePhoto().equals("default")) {
                    String profileString = model.getProfilePhoto();
                    byte[] encodeByte = Base64.decode(profileString, Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
                    profileImage.setImageBitmap(bitmap);
                } else {
                    profileImage.setImageResource(R.drawable.graduation_2841875_640);
                }
                //deletes the user from the application permanently
                deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });

            }
        };

    }
    private void ViewChatLogs()
    {
        DatabaseReference chatsReference = FirebaseDatabase.getInstance().getReference("Chats").child("ChatRooms");
        chatsSpinner.setVisibility(View.VISIBLE);
        FirebaseListAdapter <Message> chatsAdapter = new FirebaseListAdapter<Message>(this, Message.class,
                R.layout.final_message, chatsReference.child(chatRoomIDReference).child("Messages")) {
            @Override
            protected void populateView(View v, final Message model, int position) {
                TextView messageText = v.findViewById(R.id.message_text);
                final TextView messageUser = v.findViewById(R.id.sent_by);
                TextView messageTime = v.findViewById(R.id.time_sent);

                messageText.setText(model.getText());
                messageTime.setText(DateFormat.format("hh:mm - MM-dd-yyyy", model.getTimeSent()));
                FirebaseDatabase.getInstance().getReference().child("Users").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.child("Tutors").hasChild(model.getSender())){
                            messageUser.setText(dataSnapshot.child("Tutors").child(model.getSender()).child("username").getValue().toString());
                        }
                        else
                        {
                            messageUser.setText(dataSnapshot.child("Students").child(model.getSender()).child("username").getValue().toString());

                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        };
        listView.setAdapter(chatsAdapter);


    }
    private void SetChatsSpinner(final Spinner spinner, DataSnapshot dataSnapshot)
    {

        final ArrayList<String> chatsList = new ArrayList<>();
        final ArrayAdapter chatsAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,chatsList);
        for(DataSnapshot snapshot:dataSnapshot.getChildren())
        {
            String spinnerText = snapshot.child("id").getValue(String.class);
            chatsList.add(spinnerText);
            chatsAdapter.notifyDataSetChanged();
        }
        chatsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(chatsAdapter);


    }
    private AdapterView.OnItemSelectedListener chatsItemSelected = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            chatRoomIDReference = (String)chatsSpinner.getSelectedItem();
            if(adminOptionSelected.equals("View chat Logs")) {
                ViewChatLogs();
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };
}