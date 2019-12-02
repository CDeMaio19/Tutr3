package com.example.tutr;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class TutorRatingsFragment extends Fragment {
    private RatingBar ratingBar;
    private ProgressBar focusedProgressBar;
    private ProgressBar accuracyProgressBar;
    private ProgressBar friendlyProgressBar;
    private DatabaseReference reference;
    private ListView surveyListView;
    private DatabaseReference surveyReference;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View fragmentRootView = inflater.inflate(R.layout.fragment_tutor_ratings, container,false);
        final TextView usernameTextView = fragmentRootView.findViewById(R.id.username);
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child("Tutors").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usernameTextView.setText(dataSnapshot.child("username").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        surveyReference = FirebaseDatabase.getInstance().getReference("Surveys").child(firebaseUser.getUid());
        surveyReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

               SetRatingData(dataSnapshot);
               SetListView();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        ratingBar = fragmentRootView.findViewById(R.id.overall_rating);
        focusedProgressBar = fragmentRootView.findViewById(R.id.focused_progress);
        accuracyProgressBar = fragmentRootView.findViewById(R.id.accuracy_progress);
        friendlyProgressBar = fragmentRootView.findViewById(R.id.friendly_progress);
        surveyListView = fragmentRootView.findViewById(R.id.survey_list_view);

        return fragmentRootView;
    }

    private void SetRatingData(DataSnapshot dataSnapshot)
    {
        focusedProgressBar.setProgress(0);
        accuracyProgressBar.setProgress(0);
        friendlyProgressBar.setProgress(0);
        long focusedTotal = 0;
        long accuracyTotal = 0;
        long friendlyTotal = 0;
        int count = 0;

        //accumulates all of the survey results in each category
        for(DataSnapshot snapshot:dataSnapshot.getChildren()) {
                Integer focusedValue = snapshot.child("focusedRating").getValue(int.class);
                Integer accuracyValue = snapshot.child("accurateRating").getValue(int.class);
                Integer friendlyValue =  snapshot.child("friendlyRating").getValue(int.class);
                ++count;
                if(focusedValue!=null||accuracyValue!=null||friendlyValue!=null)
                {
                    focusedTotal += focusedValue;
                    accuracyTotal += accuracyValue;
                    friendlyTotal += friendlyValue;
                }
        }
        //using float to allow for rounding
        float tempFocused;
        float tempAccuracy;
        float tempFriendly;
        float tempCount = count;
        //divides by the count to get the average, then divided by 4 because it is our of 4 stars and then multiplies by 100
        //to get the value in the form of its percentage
        tempFocused = (((focusedTotal / tempCount)/4f )*100);
        tempAccuracy = (((accuracyTotal / tempCount)/4f)*100);
        tempFriendly = (((friendlyTotal / tempCount)/4f )*100);
        focusedProgressBar.setProgress(Math.round(tempFocused));
        accuracyProgressBar.setProgress(Math.round(tempAccuracy));
        friendlyProgressBar.setProgress(Math.round(tempFriendly));
        //gets the total average across each category, adds them, and divides by 3 to account for the 3 separate categories
        float rating = (((accuracyTotal+focusedTotal+friendlyTotal)/tempCount)/3);
        System.out.println(rating);
        reference.child("rating").setValue(rating);

        ratingBar.setRating(rating);
    }
    private void SetListView()
    {
        FirebaseListAdapter<Survey> adapter = new FirebaseListAdapter<Survey>(getActivity(),Survey.class,R.layout.survey_list_item,surveyReference) {
            @Override
            protected void populateView(View v, Survey model, int position) {
                TextView focusedData = v.findViewById(R.id.focused_data);
                TextView accuracyData = v.findViewById(R.id.accurate_data);
                TextView friendlyData = v.findViewById(R.id.friendly_data);
                TextView comments = v.findViewById(R.id.comment_data);
                RatingBar ratingBar = v.findViewById(R.id.item_rating);
                if(model!=null && model.getComment() != null)
                {
                    comments.setText(model.getComment());
                }
                focusedData.setText(String.valueOf(model.getFocusedRating()));
                accuracyData.setText(String.valueOf(model.getAccurateRating()));
                friendlyData.setText(String.valueOf(model.getFriendlyRating()));

                float rating = ((model.getFocusedRating() + model.getAccurateRating() + model.getFriendlyRating())/3f);
                ratingBar.setRating(rating);

            }
        };

        surveyListView.setAdapter(adapter);


    }
}
