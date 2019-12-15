package com.example.tutr;

import android.content.Context;
import android.content.Intent;
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
import android.widget.RatingBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class MatchingFragment extends Fragment {
    private String majorSubject;
    private String minorSubject;
    private String question;
    private String description;
    private ListView tutorList;
    private String requestID;
    private RadioButton Online;
    private RadioButton App;

    public MatchingFragment()
    {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View fragmentRootView = inflater.inflate(R.layout.fragment_matching,container,false);
        tutorList = fragmentRootView.findViewById(R.id.tutors_listView);
        Online = fragmentRootView.findViewById(R.id.Online);
        App = fragmentRootView.findViewById(R.id.App);
        RadioGroup Group = fragmentRootView.findViewById(R.id.group);
        //check fist element automatically
        Group.getChildAt(0).setSelected(true);
        Bundle bundle = getArguments();
        if(bundle != null) {
            majorSubject = bundle.getString("Major Subject");
            minorSubject = bundle.getString("Minor Subject");
            question = bundle.getString("Question");
            description = bundle.getString("Description");
            Group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int i) {
                    DisplayTutors();
                }
            });
            DisplayTutors();

        }
        TextView questionData = fragmentRootView.findViewById(R.id.question_data);
        TextView majorSubjectData = fragmentRootView.findViewById(R.id.major_subject_data);
        TextView minorSubjectData = fragmentRootView.findViewById(R.id.minor_subject_data);
        ImageButton newQuestionButton = fragmentRootView.findViewById(R.id.new_question_button);
        questionData.setText(question);
        majorSubjectData.setText(majorSubject);
        minorSubjectData.setText(minorSubject);
        newQuestionButton.setOnClickListener(newQuestionButtonOnClickListener);
        return fragmentRootView;
    }
    private View.OnClickListener newQuestionButtonOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //display the popup window that shows up when student is logged in
            LoggedInActivity loggedInActivity = (LoggedInActivity) getActivity();
            if(getActivity()!=null && loggedInActivity!=null) {
                ((LoggedInActivity) getActivity()).Popup(loggedInActivity.myDataSnapshot);
            }

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
                RatingBar ratingBar = v.findViewById(R.id.tutor_rating);
                ratingBar.setRating(model.getRating());
                Button matchButton = v.findViewById(R.id.match_button);
                if (App.isChecked()){
                    matchButton.setText(R.string.Schedule);
                }
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
                        final DatabaseReference questionsReference = FirebaseDatabase.getInstance().getReference("Questions").child(firebaseUser.getUid());
                        final ChatFragment chatFragment = new ChatFragment();
                        final Bundle bundle = new Bundle();

                        if(App.isChecked()){
                            Intent intent = new Intent(getActivity(), AppointmentActivity.class);
                            intent.putExtra("EXTRA_ID", model.getId());

                            intent.putExtra("Extra_Username", model.getUsername());
                            intent.putExtra("Extra_MonAv", model.getMondayAvalibility());
                            intent.putExtra("Extra_TueAv", model.getTuedayAvalibility());
                            intent.putExtra("Extra_WedAv", model.getWednesdayAvalibility());
                            intent.putExtra("Extra_ThuAv", model.getThursdayAvalibility());
                            intent.putExtra("Extra_FriAv", model.getFridayAvalibility());
                            intent.putExtra("Extra_SatAv", model.getSaturdayAvalibility());
                            intent.putExtra("Extra_SunAv", model.getSundayAvalibility());
                            startActivity(intent);
                        }
                        else {
                            SendRequest(model.getId());
                            //displays a waiting popup for the tutor to respond to the request
                            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            View waitingPopup = inflater.inflate(R.layout.popup_waiting_for_match,null);
                            Button cancelButton = waitingPopup.findViewById(R.id.cancel_button);
                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder.setView(waitingPopup);
                            final AlertDialog alertDialog = builder.create();
                            alertDialog.show();
                            final DatabaseReference requestReference = FirebaseDatabase.getInstance().getReference("Requests").child(model.getId());
                            requestReference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    //checks if the tutor declined the request and if so, dismisses the waiting popup
                                    if(dataSnapshot.child(requestID).getValue() == null)
                                    {
                                        alertDialog.dismiss();
                                    }
                                    //checks if the tutors id is in the request which means they accepted the request and the match should be made
                                    if(dataSnapshot.child(requestID).child("tutorID").getValue()!=null && dataSnapshot.child(requestID).child("tutorID").getValue().equals(model.getId()))
                                    {
                                        alertDialog.dismiss();
                                        //set the data required in the chat fragment
                                        questionsReference.push().setValue(new Question(question,majorSubject,minorSubject,description));
                                        bundle.putString("Question", question);
                                        bundle.putString("Tutor ID", model.getId());
                                        bundle.putString("Tutor Profile Photo", model.getProfilePhoto());
                                        bundle.putString("Tutor Username", model.getUsername());
                                        chatFragment.setArguments(bundle);
                                        if(getFragmentManager()!=null) {
                                            getFragmentManager().beginTransaction().replace(R.id.fragment_ui_container, chatFragment).commit();
                                            //removes the request from the database since it has been accepted
                                            requestReference.child(requestID).removeValue();
                                        }
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                            //removes the request and closes the popup
                            cancelButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    requestReference.child(requestID).removeValue();
                                    alertDialog.dismiss();

                                }
                            });
                        }
                    }
                });
            }
        };
        tutorList.setAdapter(adapter);
    }
    private void SendRequest(String id)
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference requestsReference = FirebaseDatabase.getInstance().getReference("Requests").child(id);
        requestID = requestsReference.push().getKey();
        if(requestID!=null && user!=null) {
            requestsReference.child(requestID).child("studentID").setValue(user.getUid());
            requestsReference.child(requestID).child("question").setValue(question);
            requestsReference.child(requestID).child("requestID").setValue(requestID);
        }

    }

}
