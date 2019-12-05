package com.example.tutr;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class BioTabFragment extends Fragment {

    private CircleImageView profileImage;
    private ImageButton changePhotoButton;
    private EditText contactInfoEditText;
    private EditText schoolOccupationEditText;
    private EditText descriptionEditText;
    private ImageButton editButton;
    private View saveProfileChanges;
    private FirebaseUser firebaseUser;
    private DatabaseReference reference;
    private ExpandableListView areaOfExpertiseListView;
    private TextView areaOfExpertiseText;
    private User user;
    BioTabFragment()
    {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View fragmentRootView = inflater.inflate(R.layout.fragment_bio_tab, container, false);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference= FirebaseDatabase.getInstance().getReference().child("Users");

        if(getParentFragment()!=null && getParentFragment().getActivity()!=null) {
            editButton = getParentFragment().getActivity().findViewById(R.id.edit_button);
            changePhotoButton = getParentFragment().getActivity().findViewById(R.id.change_photo_button);
            saveProfileChanges = getParentFragment().getActivity().findViewById(R.id.save_profile_changes);
        }
            Button cancelButton = saveProfileChanges.findViewById(R.id.cancel_button);
            Button doneButton = saveProfileChanges.findViewById(R.id.done_button);

        areaOfExpertiseListView = fragmentRootView.findViewById(R.id.area_of_expertise_list);
        LayoutInflater profileInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = profileInflater.inflate(R.layout.fragment_profile,null);
        //finds views for profile data including profile photo and text fields in the bio tab
        profileImage = view.findViewById(R.id.profile_image);
        contactInfoEditText = fragmentRootView.findViewById(R.id.contact_information_edit_text);
        schoolOccupationEditText = fragmentRootView.findViewById(R.id.school_occupation_edit_text);
        descriptionEditText = fragmentRootView.findViewById(R.id.description_edit_text);
        areaOfExpertiseText = fragmentRootView.findViewById(R.id.area_of_expertise_text);
        areaOfExpertiseText.setVisibility(View.INVISIBLE);
        editButton.setOnClickListener(editOnClickListener);
        cancelButton.setOnClickListener(cancelButtonOnClickListener);
        doneButton.setOnClickListener(doneButtonOnClickListener);
        reference.addValueEventListener(valueEventListener);
        DatabaseReference subjectsReference = FirebaseDatabase.getInstance().getReference("Subjects");
        subjectsReference.addValueEventListener(subjectsValueEventListener);


        return  fragmentRootView;
    }

    //when the edit button is clicked, hide the edit button and allow views in the profile to be edited
    private View.OnClickListener editOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            changePhotoButton.setVisibility(View.VISIBLE);
            changePhotoButton.setClickable(true);
            schoolOccupationEditText.setEnabled(true);
            descriptionEditText.setEnabled(true);
            editButton.setVisibility(View.INVISIBLE);
            saveProfileChanges.setVisibility(View.VISIBLE);




        }
    };
    //If the cancel button is clicked, hide the edit view and revert the text fields to their last values before edit mode started
    private View.OnClickListener cancelButtonOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            editButton.setVisibility(View.VISIBLE);
            saveProfileChanges.setVisibility(View.INVISIBLE);
            changePhotoButton.setVisibility(View.INVISIBLE);
            changePhotoButton.setClickable(false);
            contactInfoEditText.setEnabled(false);
            schoolOccupationEditText.setEnabled(false);
            descriptionEditText.setEnabled(false);
            descriptionEditText.setText(user.getDescription());
            schoolOccupationEditText.setText(user.getSchool());
            //converts the profileString into a bitmap to be loaded into the view
            String profileString = user.getProfilePhoto();
            byte [] encodeByte = Base64.decode(profileString,Base64.DEFAULT);
            Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            profileImage.setImageBitmap(bitmap);

        }
    };
    //saves the new values in the user profile to the firebase database and turns edit mode off
    private View.OnClickListener doneButtonOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            reference.child("description").setValue(descriptionEditText.getText().toString());
            reference.child("school").setValue(schoolOccupationEditText.getText().toString());
            saveProfileChanges.setVisibility(View.INVISIBLE);
            changePhotoButton.setVisibility(View.INVISIBLE);
            editButton.setVisibility(View.VISIBLE);
            changePhotoButton.setClickable(false);
            contactInfoEditText.setEnabled(false);
            schoolOccupationEditText.setEnabled(false);
            descriptionEditText.setEnabled(false);


        }
    };

    //called when the profile tab is first clicked and when any changes are made in the edit mode
    private ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            user = null;
            //check if the "Tutors" node contains the firebase user
            if(dataSnapshot.child("Tutors").hasChild(firebaseUser.getUid()))
            {
                //if it is contained in the node, take the data at that child node and pass it into the User instance
                user = dataSnapshot.child("Tutors").child(firebaseUser.getUid()).getValue(User.class);
                //change the reference to be within the "Tutors" node specifically
                reference = FirebaseDatabase.getInstance().getReference("Users").child("Tutors").child(firebaseUser.getUid());

            }
            //check if the "Students" node contains the firebase user
            else if (dataSnapshot.child("Students").hasChild(firebaseUser.getUid()))
            {
                //if it is contained in the node, take the data at that child node and pass it into the User instance
                user = dataSnapshot.child("Students").child(firebaseUser.getUid()).getValue(User.class);
                //change the reference to be within the "Students" node specifically
                reference = FirebaseDatabase.getInstance().getReference("Users").child("Students").child(firebaseUser.getUid());

            }
            if (user != null) {
                //set the fields in the user's profile
               contactInfoEditText.setText(user.getEmail());
               descriptionEditText.setText(user.getDescription());
               schoolOccupationEditText.setText(user.getSchool());

            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    private ValueEventListener subjectsValueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if(((LoggedInActivity)getActivity()).isTutor) {
                areaOfExpertiseText.setVisibility(View.VISIBLE);
                SetData(dataSnapshot);
            }


        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    private void SetData(DataSnapshot dataSnapshot)
    {
        ArrayList<String> listHeader = new ArrayList<>();
        HashMap <String, ArrayList<String>> listData = new HashMap<>();
        final ArrayList<String> temp = new ArrayList<>();

        int group = 0;

        for (DataSnapshot majorSubject: dataSnapshot.getChildren())
        {
            listHeader.add((majorSubject.getKey()));
            for (DataSnapshot minorSubject: majorSubject.getChildren())
            {
                temp.add(minorSubject.getKey());
            }
            //creates a clone of temp so we can clear it an not effect the data in listData
            listData.put(listHeader.get(group),(ArrayList<String>)temp.clone());
            temp.clear();

            group++;
        }
        ExpandableListAdapter expandableListAdapter = new ExpandableListAdapter(getContext(),listHeader,listData);
        areaOfExpertiseListView.setAdapter(expandableListAdapter);

    }



}
