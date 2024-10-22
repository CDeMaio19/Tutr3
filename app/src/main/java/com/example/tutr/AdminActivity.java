package com.example.tutr;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;

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
        adminOptionSelected = "View tutors";
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
            adminOptionSelected = "View tutors";

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
                TextView resume = v.findViewById(R.id.resume_data);
                if(model.getResume()!=null){
                    resume.setText(model.getResume());
                }
                TextView areaOfExpertise = v.findViewById(R.id.tutor_area_of_expertise);
                TextView totalSessionTime = v.findViewById(R.id.total_session_time_data);
                long time = model.getTotalSessionTimes();
                //creates string in the form of HH:mm:ss
                String timeString = String.format(Locale.getDefault(),"%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(time),
                        TimeUnit.MILLISECONDS.toMinutes(time) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(time)),
                        TimeUnit.MILLISECONDS.toSeconds(time) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time)));
                totalSessionTime.setText(timeString);

                //populates the listView item with the data of the given tutor that matched with the query
                userName.setText(model.getUsername());
                schoolOrOccupation.setText(model.getSchool());
                areaOfExpertise.setText(model.getAreaOfExpertise());
                SetUserProfileImage(model.getProfilePhoto(),profileImage);
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
                TextView userName = v.findViewById(R.id.student_name);
                TextView email = v.findViewById(R.id.email_data);
                email.setText(model.getEmail());
                //populates the listView item with the data of the given tutor that matched with the query
                userName.setText(model.getUsername());
                SetUserProfileImage(model.getProfilePhoto(),profileImage);

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
                ImageView messageImage = v.findViewById(R.id.message_image);

                if(model.getImage() == null) {
                    messageText.setText(model.getText());
                    //don't display image
                    messageImage.setImageBitmap(null);
                }
                else
                {
                    //if the image is not null decode the image from firebase
                    byte[] bytes = Base64.decode(model.getImage(),Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                    //don't display text
                    messageText.setText(null);
                    messageImage.setImageBitmap(bitmap);
                }
                messageTime.setText(DateFormat.format("hh:mm - MM-dd-yyyy", model.getTimeSent()));
                FirebaseDatabase.getInstance().getReference().child("Users").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.child("Tutors").hasChild(model.getSender())){
                            messageUser.setText(dataSnapshot.child("Tutors").child(model.getSender()).child("username").getValue(String.class));
                        }
                        else {
                            messageUser.setText(dataSnapshot.child("Students").child(model.getSender()).child("username").getValue(String.class));
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
            if(adminOptionSelected.equals("View chat logs")) {
                ViewChatLogs();
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    private void SetUserProfileImage(String profileString,CircleImageView profileImage)
    {
        if (!profileString.equals("default")) {
            try {
                byte[] encodeByte = Base64.decode(profileString, Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
                profileImage.setImageBitmap(bitmap);
            }
            catch (IllegalArgumentException IAE) {
                Log.e("Photo error","Loading profile photo error",IAE);
            }

        }
        else {
            profileImage.setImageResource(R.drawable.graduation_2841875_640);
        }

    }
    public void onResumeLinkClick(View v)
    {
        TextView file = v.findViewById(R.id.resume_data);
        Uri uri = Uri.parse(file.getText().toString());
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(uri);
        intent.setFlags(FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(intent);

    }
}