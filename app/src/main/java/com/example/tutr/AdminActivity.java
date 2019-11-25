package com.example.tutr;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdminActivity extends AppCompatActivity {
    private String adminOptionSelected;
    private Spinner adminViewOptions;
    private ListView userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        adminOptionSelected = "View all users";
        adminViewOptions = findViewById(R.id.adminViewOptions);
        userList = findViewById(R.id.user_list);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.AdminOptions,android.R.layout.simple_spinner_item);
        adminViewOptions.setAdapter(adapter);
        adminViewOptions.setOnItemSelectedListener(adminViewOptionsItemSelected);
    }

    private AdapterView.OnItemSelectedListener adminViewOptionsItemSelected = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            adminOptionSelected = (String) adminViewOptions.getSelectedItem();
            switch (adminOptionSelected)
            {
                case "View all users":
                    ViewAllUsers();
                    break;
                case "View tutors":
                    ViewTutors();
                    break;
                case "View students":
                    ViewStudents();
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

    private void ViewAllUsers()
    {

    }
    private void ViewTutors() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child("Tutors");
        //queries the FireBase database on the tutors area of expertise which equals the most specific
        // subject the student asked about
        FirebaseListAdapter<User> adapter = new FirebaseListAdapter<User>(this,
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

                System.out.println(model.getTotalSessionTimes());
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
        userList.setAdapter(adapter);
    }
    private void ViewStudents()
    {

    }
    private void ViewChatLogs()
    {

    }
}