package com.example.tutr;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import de.hdodenhof.circleimageview.CircleImageView;

public class MatchingFragment extends Fragment {
    private String majorSubject;
    private String minorSubject;
    private String question;
    private String description;
    private ListView tutorList;

    public MatchingFragment()
    {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View fragmentRootView = inflater.inflate(R.layout.fragment_matching,container,false);
        tutorList = fragmentRootView.findViewById(R.id.tutors_listView);
        Bundle bundle = getArguments();
        if(bundle != null) {
            majorSubject = bundle.getString("Major Subject");
            minorSubject = bundle.getString("Minor Subject");
            question = bundle.getString("Question");
            description = bundle.getString("Description");
            DisplayTutors();

        }
        TextView questionData = fragmentRootView.findViewById(R.id.question_data);
        TextView majorSubjectData = fragmentRootView.findViewById(R.id.major_subject_data);
        TextView minorSubjectData = fragmentRootView.findViewById(R.id.minor_subject_data);
        TextView descriptionData = fragmentRootView.findViewById(R.id.description_data);
        ImageButton newQuestionButton = fragmentRootView.findViewById(R.id.new_question_button);
        questionData.setText(question);
        majorSubjectData.setText(majorSubject);
        minorSubjectData.setText(minorSubject);
        descriptionData.setText(description);
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

    private void DisplayTutors() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child("Tutors");
        //queries the FireBase database on the tutors area of expertise which equals the most specific
        // subject the student asked about
        Query query = reference.orderByChild("subject").equalTo(majorSubject);
        FirebaseListAdapter<User> adapter = new FirebaseListAdapter<User>(getActivity(),
                User.class, R.layout.tutor_matching_list_item, query) {
            @Override
            protected void populateView(View v, final User model, int position) {
                CircleImageView profileImage = v.findViewById(R.id.profile_image);
                TextView userName = v.findViewById(R.id.tutor_name);
                TextView schoolOrOccupation = v.findViewById(R.id.tutor_school_occupation);
                TextView areaOfExpertise = v.findViewById(R.id.tutor_area_of_expertise);
                Button matchButton = v.findViewById(R.id.match_button);
                //populates the listView item with the data of the given tutor that matched with the query
                    userName.setText(model.getUsername());
                    schoolOrOccupation.setText(model.getSchool());
                    areaOfExpertise.setText(model.getAreaOfExpertise());
                    if (!model.getProfilePhoto().equals("default")) {
                        String profileString = model.getProfilePhoto();
                        byte[] encodeByte = Base64.decode(profileString, Base64.DEFAULT);
                        Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
                        profileImage.setImageBitmap(bitmap);
                    }
                    else {
                        profileImage.setImageResource(R.drawable.graduation_2841875_640);
                    }


                matchButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //adds the question to the FireBase database
                        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                        DatabaseReference questionsReference = FirebaseDatabase.getInstance().getReference("Questions").child(firebaseUser.getUid());
                        questionsReference.push().setValue(new Question(question,majorSubject,minorSubject,description));
                        ChatFragment chatFragment = new ChatFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("Question", question);
                        bundle.putString("Description", description);
                        bundle.putString("Major Subject", majorSubject);
                        bundle.putString("Minor Subject", minorSubject);
                        bundle.putString("Tutor ID", model.getId());
                        bundle.putString("Tutor Profile Photo", model.getProfilePhoto());
                        bundle.putString("Tutor Username", model.getUsername());
                        chatFragment.setArguments(bundle);
                        getFragmentManager().beginTransaction().replace(R.id.fragment_ui_container, chatFragment).commit();


                    }
                });
            }
        };
        tutorList.setAdapter(adapter);

    }
}
