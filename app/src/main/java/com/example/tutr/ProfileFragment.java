package com.example.tutr;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class ProfileFragment extends Fragment {
    private ImageView profileImage;
    private TextView username;
    private DatabaseReference reference;
    private final int REQUEST_CODE = 1;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View fragmentRootView = inflater.inflate(R.layout.fragment_profile, container,false);
        profileImage = fragmentRootView.findViewById(R.id.user_profile);
        username = fragmentRootView.findViewById(R.id.username);
        ImageButton changePhotoButton = fragmentRootView.findViewById(R.id.change_photo_button);


        ViewPager viewPager = fragmentRootView.findViewById(R.id.profile_viewpager);
        TabFragmentPagerAdapter adapter = new TabFragmentPagerAdapter(getContext(), getChildFragmentManager());
        viewPager.setAdapter(adapter);
        TabLayout tabLayout = fragmentRootView.findViewById(R.id.profile_tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));


        Toolbar toolbar = fragmentRootView.findViewById(R.id.profile_toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        toolbar.setTitle("");
        setHasOptionsMenu(true);

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference().child("Users").child("Tutors").child(firebaseUser.getUid());
        reference.addValueEventListener(valueEventListener);
        changePhotoButton.setOnClickListener(changePhotoOnClickListener);


         return fragmentRootView;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.options_menu,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {
            case R.id.settings:
                return true;
            case R.id.log_out:
               try
               {
                   FirebaseAuth.getInstance().signOut();
                   startActivity(new Intent(getActivity(), MainActivity.class));

               }catch (Exception e)
               {
                   Log.e("Profile Fragment", "Failed Log Out", e);
               }
                return true;

                default:
                    return super.onOptionsItemSelected(item);

        }
    }
    private ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            User user = dataSnapshot.getValue(User.class);
            if (user != null) {
                username.setText(user.getUsername());
            }
            if (user.getProfilePhotoURL().equals("default")) {

                profileImage.setImageResource(R.drawable.graduation_2841875_640);
            }
            else {
                Glide.with(getActivity()).load(user.getProfilePhotoURL()).into(profileImage);
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };
    // convert to edit mode when profile ui is finished
    private View.OnClickListener changePhotoOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //add this to separate button under profile photo to select profile photo
            Intent intent = new Intent();
            intent.setType("image/*");
            //starts activity to select a photo from the gallery
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_CODE);


        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {

            switch (requestCode)
            {

                case REQUEST_CODE:
                    if (resultCode == Activity.RESULT_OK) {
                        //loads selected photo stored in data and then updates the database for the user profile
                        Glide.with(getActivity()).load(data.getData()).into(profileImage);
                        reference.child("profilePhotoURL").setValue(data.getData().toString());
                        break;
                    }
                    else if (resultCode == Activity.RESULT_CANCELED) {
                        Log.e(TAG, "Selecting picture cancelled");
                    }
                    break;
            }
        }
        catch (Exception e) {
            Log.e(TAG, "Exception in onActivityResult : " + e.getMessage());
        }
    }
}

