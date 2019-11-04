package com.example.tutr;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MatchingFragment extends Fragment {
    private String majorSubject;
    private String minorSubject;
    private ListView tutorList;

    public MatchingFragment()
    {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View fragmentRootView = inflater.inflate(R.layout.fragment_matching,container,false);
        Bundle bundle = getArguments();
        if(bundle != null) {
            majorSubject = bundle.getString("Major Subject");
            minorSubject = bundle.getString("Minor Subject");

        }
        tutorList = fragmentRootView.findViewById(R.id.tutors_listView);
        DisplayTutors();
        ImageButton newQuestionButton = fragmentRootView.findViewById(R.id.new_question_button);
        newQuestionButton.setOnClickListener(newQuestionButtonOnClickListener);
        return fragmentRootView;
    }
    private View.OnClickListener newQuestionButtonOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //display the popup window that shows up when student is logged in
            LoggedInActivity loggedInActivity = (LoggedInActivity) getActivity();
            ((LoggedInActivity) getActivity()).Popup(loggedInActivity.myDataSnapshot);

        }
    };

    private void DisplayTutors()
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child("Tutors");
        //queries the firebase database on the tutors area of expertise which equals the most specific
        // subject the student asked about
        Query query = reference.orderByChild("areaOfExpertise").equalTo(minorSubject);
        FirebaseListAdapter<User> adapter = new FirebaseListAdapter<User>(getActivity(),
                                                User.class,R.layout.tutor_list_item,query) {
            @Override
            protected void populateView(View v, User model, int position) {
                CircleImageView profileImage = v.findViewById(R.id.profile_image);
                TextView userName = v.findViewById(R.id.tutor_name);
                TextView schoolOrOccupation = v.findViewById(R.id.tutor_school_occupation);
                TextView areaOfExpertise = v.findViewById(R.id.tutor_area_of_expertise);
                Button matchButton = v.findViewById(R.id.match_button);
               //populates the listview item with the data of the given tutor that matched with the query
                userName.setText(model.getUsername());
                schoolOrOccupation.setText(model.getSchool());
                areaOfExpertise.setText(model.getAreaOfExpertise());
                String profileString = model.getProfilePhoto();
                byte [] encodeByte = Base64.decode(profileString, Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte,0,encodeByte.length);
                profileImage.setImageBitmap(bitmap);


            }
        };
        tutorList.setAdapter(adapter);

    }
}
