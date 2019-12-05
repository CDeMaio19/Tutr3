package com.example.tutr;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;


import de.hdodenhof.circleimageview.CircleImageView;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class ProfileFragment extends Fragment {
    private CircleImageView profileImage;
    private String profileString;
    private boolean isTutor;
    private TextView username;
    private DatabaseReference reference;
    private final int REQUEST_CODE = 1;
    private FirebaseUser firebaseUser;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View fragmentRootView = inflater.inflate(R.layout.fragment_profile, container,false);
        profileImage = fragmentRootView.findViewById(R.id.profile_image);
        username = fragmentRootView.findViewById(R.id.username);
        ImageButton changePhotoButton = fragmentRootView.findViewById(R.id.change_photo_button);
        Bundle bundle = getArguments();
        if(bundle!=null) {
            isTutor = bundle.getBoolean("User Status");
        }

        // sets up the tabs for the tab layout and the swiping feature for the tab layout
        ViewPager viewPager = fragmentRootView.findViewById(R.id.profile_viewpager);
        TabFragmentPagerAdapter adapter = new TabFragmentPagerAdapter(getContext(), getChildFragmentManager(),isTutor);
        viewPager.setAdapter(adapter);
        TabLayout tabLayout = fragmentRootView.findViewById(R.id.profile_tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));

        Toolbar toolbar = fragmentRootView.findViewById(R.id.profile_toolbar);
        if(getActivity()!=null) {
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
            toolbar.setTitle("");
            setHasOptionsMenu(true);
        }

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(valueEventListener);
        changePhotoButton.setOnClickListener(changePhotoOnClickListener);


         return fragmentRootView;
    }
    //inflates the options menu from resource file
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.options_menu,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    //creates cases for when each option item is selected
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {
            case R.id.settings:
                return true;
            case R.id.log_out:
               try
               {
                   //log the user out and bring them to the login page
                   FirebaseAuth.getInstance().signOut();
                   reference.child("isOnline").setValue(false);
                   startActivity(new Intent(getActivity(), MainActivity.class));

               }
               catch (Exception e)
               {
                   Log.e("Profile Fragment", "Failed Log Out", e);
               }
                return true;

                default:
                    return super.onOptionsItemSelected(item);

        }
    }
    //called when the profile tab is first clicked and when any changes are made in the edit mode
    private ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            User user = null;
            if(dataSnapshot.child("Tutors").hasChild(firebaseUser.getUid()))
            {
                user = dataSnapshot.child("Tutors").child(firebaseUser.getUid()).getValue(User.class);
                reference = FirebaseDatabase.getInstance().getReference("Users").child("Tutors").child(firebaseUser.getUid());
            }
            else if (dataSnapshot.child("Students").hasChild(firebaseUser.getUid()))
            {
                user = dataSnapshot.child("Students").child(firebaseUser.getUid()).getValue(User.class);
                reference = FirebaseDatabase.getInstance().getReference("Users").child("Students").child(firebaseUser.getUid());

            }

            if (user != null) {
                username.setText(user.getUsername());
                //if the string in getProfilePhoto is "default" then load the default image for profile photo
                if (user.getProfilePhoto().equals("default")) {

                    profileImage.setImageResource(R.drawable.graduation_2841875_640);
                } else {
                    //otherwise convert the profileString into a bitmap and set the profileImage bitmap
                    profileString = user.getProfilePhoto();
                    byte [] encodeByte = Base64.decode(profileString,Base64.DEFAULT);
                    Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
                    profileImage.setImageBitmap(bitmap);
                }
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };
    //opens the photo gallery for the user to choose their profile photo
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
                        //creates bitmap from the image selected by the user
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(),data.getData());
                        // sets profileImage to the created bitmap
                        profileImage.setImageBitmap(bitmap);
                        //converts the bitmap into a string to store in the FireBase database
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG,100, byteArrayOutputStream);
                        byte [] b= byteArrayOutputStream.toByteArray();
                        profileString = Base64.encodeToString(b, Base64.DEFAULT);
                        reference.child("profilePhoto").setValue(profileString);
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

