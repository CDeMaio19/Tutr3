package com.example.tutr;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

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

public class BioTabFragment extends Fragment {

    private ImageButton changePhotoButton;
    private EditText contactInfoEditText;
    private EditText schoolOccupationEditText;
    private EditText areaOfExpertiseEditText;
    private EditText descriptionEditText;
    private Button cancelButton;
    private Button doneButton;
    private ImageButton editButton;
    private View saveProfileChanges;
    private FirebaseUser firebaseUser;
    private DatabaseReference reference;
    private User user;
    BioTabFragment()
    {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View fragmentRootView = inflater.inflate(R.layout.fragment_bio_tab, container, false);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference= FirebaseDatabase.getInstance().getReference().child("Users").child("Tutors").child(firebaseUser.getUid());

        editButton = getParentFragment().getActivity().findViewById(R.id.edit_button);
        changePhotoButton = getParentFragment().getActivity().findViewById(R.id.change_photo_button);
        saveProfileChanges = getParentFragment().getActivity().findViewById(R.id.save_profileChanges);
        cancelButton = saveProfileChanges.findViewById(R.id.cancel_button);
        doneButton = saveProfileChanges.findViewById(R.id.done_button);

        contactInfoEditText = fragmentRootView.findViewById(R.id.contact_information_edit_text);
        schoolOccupationEditText = fragmentRootView.findViewById(R.id.school_occupation_edit_text);
        areaOfExpertiseEditText = fragmentRootView.findViewById(R.id.area_of_expertise_text_edit_text);
        descriptionEditText = fragmentRootView.findViewById(R.id.description_edit_text);
        editButton.setOnClickListener(editOnClickListener);
        cancelButton.setOnClickListener(cancelButtonOnClickListener);
        doneButton.setOnClickListener(doneButtonOnClickListener);
        reference.addValueEventListener(valueEventListener);

        return  fragmentRootView;
    }

    private View.OnClickListener editOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            changePhotoButton.setVisibility(View.VISIBLE);
            changePhotoButton.setClickable(true);
            schoolOccupationEditText.setEnabled(true);
            areaOfExpertiseEditText.setEnabled(true);
            descriptionEditText.setEnabled(true);
            editButton.setVisibility(View.INVISIBLE);
            saveProfileChanges.setVisibility(View.VISIBLE);




        }
    };
    private View.OnClickListener cancelButtonOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            editButton.setVisibility(View.VISIBLE);
            saveProfileChanges.setVisibility(View.INVISIBLE);
            changePhotoButton.setVisibility(View.INVISIBLE);
            changePhotoButton.setClickable(false);
            contactInfoEditText.setEnabled(false);
            schoolOccupationEditText.setEnabled(false);
            areaOfExpertiseEditText.setEnabled(false);
            descriptionEditText.setEnabled(false);
            descriptionEditText.setText(user.getDescription());
            areaOfExpertiseEditText.setText(user.getAreaOfExpertise());
            schoolOccupationEditText.setText(user.getSchool());

        }
    };
    private View.OnClickListener doneButtonOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            reference.child("description").setValue(descriptionEditText.getText().toString());
            reference.child("areaOfExpertise").setValue(areaOfExpertiseEditText.getText().toString());
            reference.child("school").setValue(schoolOccupationEditText.getText().toString());
            saveProfileChanges.setVisibility(View.INVISIBLE);
            changePhotoButton.setVisibility(View.INVISIBLE);
            changePhotoButton.setClickable(false);
            contactInfoEditText.setEnabled(false);
            schoolOccupationEditText.setEnabled(false);
            areaOfExpertiseEditText.setEnabled(false);
            descriptionEditText.setEnabled(false);


        }
    };

    private ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            user = dataSnapshot.getValue(User.class);
            if (user != null) {
               contactInfoEditText.setText(user.getEmail());
               descriptionEditText.setText(user.getDescription());
               schoolOccupationEditText.setText(user.getSchool());
               areaOfExpertiseEditText.setText(user.getAreaOfExpertise());
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

}
