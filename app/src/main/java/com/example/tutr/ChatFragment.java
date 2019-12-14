package com.example.tutr;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
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

import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class ChatFragment extends Fragment {
    private DatabaseReference chatsReference;
    private DatabaseReference tutorSessionTime;
    private DatabaseReference surveyReference;
    private AlertDialog surveyAlert;
    private AlertDialog confirmEndSessionAlert;
    private View surveyPopup;
    private RadioGroup focusedGroup;
    private RadioGroup accurateGroup;
    private RadioGroup friendlyGroup;
    private DrawerLayout drawerLayout;
    private TextView toolbarUsername;
    private String username;
    private String profileImageString;
    private String currentUserType;
    private String otherUserType;
    private String tutorID;
    private String chatRoomIDReference;
    private CircleImageView profileImage;
    private EditText chatEditText;
    private ImageButton sendButton;
    private ImageButton endSessionButton;
    private ImageButton startDrawActivityButton;
    private FirebaseUser firebaseUser;
    private ListView listOfMessages;
    private ListView conversationList;
    private long sessionStartTime;
    private long sessionEndTime;
    private final int IMAGE_REQUEST = 2;

    @Override
        public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState ) {
            super.onCreate(savedInstanceState);
            View fragmentRootView = inflater.inflate(R.layout.fragment_chat,container,false);
        //verify the users status
            if(((LoggedInActivity)getActivity()).isTutor)
            {
                currentUserType = "Tutors";
                otherUserType = "Students";
            }
            else {
                currentUserType = "Students";
                otherUserType = "Tutors";
            }
            firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            chatsReference = FirebaseDatabase.getInstance().getReference("Chats").child("ChatRooms");
            DatabaseReference usersReference = FirebaseDatabase.getInstance().getReference("Users").child(otherUserType);
            profileImage = fragmentRootView.findViewById(R.id.profile_image);
            toolbarUsername = fragmentRootView.findViewById(R.id.toolbarUsername);
            chatEditText = fragmentRootView.findViewById(R.id.editTextMessage);
            chatEditText.setEnabled(false);
            listOfMessages = fragmentRootView.findViewById(R.id.list_of_messages);
            conversationList = fragmentRootView.findViewById(R.id.list_of_conversations);
            conversationList.setOnItemClickListener(chatRoomSelection);
            startDrawActivityButton = fragmentRootView.findViewById(R.id.start_canvas_activity_Button);
            endSessionButton = fragmentRootView.findViewById(R.id.end_session_button);
            endSessionButton.setVisibility(View.INVISIBLE);
            sendButton = fragmentRootView.findViewById(R.id.sendButton);
            drawerLayout = fragmentRootView.findViewById(R.id.conversation_list_NavDrawer);
            ImageButton conversationMenu = fragmentRootView.findViewById(R.id.menu);
        if(getActivity()!=null) {
                getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            }
            Bundle bundle;
            bundle = getArguments();
            //get the tutors information from the match
            GetMatchData(bundle);
            //this value event listener sets the conversation list menu with data from the Users node in fireBase
            usersReference.addValueEventListener(userReferenceValueEvent);
            conversationMenu.setOnClickListener(conversationMenuClickListener);
            //on click send the users message
            sendButton.setOnClickListener(sendClickListener);
            endSessionButton.setOnClickListener(endSessionClickListener);
            startDrawActivityButton.setOnClickListener(startDrawActivityClickListener);
            return fragmentRootView;
    }
    @Override
    public void onResume()
    {
        super.onResume();
        //when user switches between fragments, this restores last state of the chatFragment
        if(chatRoomIDReference!=null)
        {
            SetUserProfileImage(profileImageString,profileImage);
            toolbarUsername.setText(username);
            DisplayChatMessages();
        }


    }

        //displays the chat messages for the current chat room
    private void DisplayChatMessages() {

        FirebaseListAdapter <Message> adapter = new FirebaseListAdapter<Message>(getActivity(), Message.class,
                R.layout.final_message, chatsReference.child(chatRoomIDReference).child("Messages")) {
            @Override
            protected void populateView(View v, final Message model, int position) {
                final TextView messageText = v.findViewById(R.id.message_text);
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
                        if(dataSnapshot.child(currentUserType).hasChild(model.getSender())){
                            messageUser.setText(dataSnapshot.child(currentUserType).child(model.getSender()).child("username").getValue(String.class));
                        }
                        else if (dataSnapshot.child(otherUserType).hasChild(model.getSender()))
                        {
                            messageUser.setText(dataSnapshot.child(otherUserType).child(model.getSender()).child("username").getValue(String.class));

                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        };

       listOfMessages.setAdapter(adapter);
    }
    private void SendMessage()
    {
        chatsReference.child(chatRoomIDReference).child("Messages").push().setValue(
                            new Message(chatEditText.getText().toString(),firebaseUser.getUid(),null));
        chatEditText.setText("");
        //stores the chat room timestamp in reverse order so when queried, the result set will show in descending order
        chatsReference.child(chatRoomIDReference).child("timeOfLastMessage").setValue(-1 * new Date().getTime());

    }
    //Method to create and populate the firebase list adapter based on chat rooms pertaining to the current user
    private void SetConversationList(final DataSnapshot usersSnapshot) {
        Query listContentQuery;
        //creates a query that returns only chats belonging to the current user
        if(currentUserType.equals("Tutors")) {
            listContentQuery = chatsReference.orderByChild("tutorID").equalTo(firebaseUser.getUid());
        }
        else{
            listContentQuery = chatsReference.orderByChild("studentID").equalTo(firebaseUser.getUid());
        }
        //sets the default chat room to the last chat room created
        listContentQuery.addValueEventListener(listContentQueryValueEventListener);
        FirebaseListAdapter<ChatRoom> adapter = new FirebaseListAdapter<ChatRoom>(getActivity(),ChatRoom.class,
                                                                    R.layout.conversation_list_item,listContentQuery) {
            @Override
            protected void populateView(View v, ChatRoom model, int position) {
                CircleImageView profileImage = v.findViewById(R.id.profile_image);
                TextView username = v.findViewById(R.id.username);
                TextView timeOfLastMessage = v.findViewById(R.id.time_of_last_message);
                TextView questionText = v.findViewById(R.id.question);
                questionText.setText(model.getQuestionAsked());
                //if there are no messages leave this textView blank
                if(model.getTimeOfLastMessage() == 0)
                {
                    timeOfLastMessage.setText("");
                }
                else {
                                                                                //reverses the timestamp back to the correct date and time
                    timeOfLastMessage.setText(DateFormat.format("MM-dd-yyyy", -1*model.getTimeOfLastMessage()));
                }
                //check all of the users of the opposite status(this will be the previous chats the user has participated in)
                //chats always are from student to tutor or vice versa
                for(DataSnapshot users:usersSnapshot.getChildren())
                {
                    PopulateConversationListViews(model,users,username,profileImage);
                }
            }
        };
        conversationList.setAdapter(adapter);

    }
    private void PopulateConversationListViews(ChatRoom model,DataSnapshot users,TextView username,CircleImageView profileImage)
    {
        //if the current user is a student
        if(!currentUserType.equals("Tutors")) {
            //if the chat room's tutorID matches the id of the current user then populate the views with their
            // information
            if (model.getTutorID().equals(users.child("id").getValue())) {
                profileImageString = users.child("profilePhoto").getValue(String.class);
                username.setText(users.child("username").getValue(String.class));
                if (profileImageString != null) {
                    SetUserProfileImage(profileImageString, profileImage);
                }
            }
        }
        //otherwise a tutor is logged in and the chat room information should be filled with the students info
        else {
            if (model.getStudentID().equals(users.child("id").getValue())) {
                String profileImageString = users.child("profilePhoto").getValue(String.class);
                username.setText(users.child("username").getValue(String.class));
                if (profileImageString != null) {
                    SetUserProfileImage(profileImageString, profileImage);
                }
            }
        }
    }
    //method to convert the profile string stored into Firebase into a bitmap to be loaded into the circle imageView
    private void SetUserProfileImage(String profileImageString,CircleImageView profileImage)
    {
        if (!profileImageString.equals("default")) {
            try {
                byte[] encodeByte = Base64.decode(profileImageString, Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
                profileImage.setImageBitmap(bitmap);
            }
            catch (IllegalArgumentException IAE)
            {
                Log.e("Photo error","Loading profile photo error",IAE);
            }

        } else {
            profileImage.setImageResource(R.drawable.graduation_2841875_640);
        }

    }
    //event handler to get the information of the current chat room selected
    private AdapterView.OnItemClickListener chatRoomSelection = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
            final ChatRoom chatRoom = (ChatRoom) conversationList.getItemAtPosition(position);
            chatsReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    //search all of the chat rooms
                    for(DataSnapshot snapshot:dataSnapshot.getChildren())
                    {
                        //if the chat room id of the selected item equals the current snapshot's chatRoomID
                        //then they refer to the same chat and the information should be displayed in the chat fragment
                        if(chatRoom.getId().equals(snapshot.child("id").getValue())) {
                            //sets the charRoomIDReferences so messages can be displayed and sent in this chat room specifically
                            chatRoomIDReference = snapshot.child("id").getValue(String.class);
                            tutorID = snapshot.child("tutorID").getValue(String.class);
                            CircleImageView tempProfile;
                            TextView tempUsername;
                            tempProfile = view.findViewById(R.id.profile_image);
                            tempUsername = view.findViewById(R.id.username);
                            profileImage.setImageDrawable(tempProfile.getDrawable());
                            username = tempUsername.getText().toString();
                            toolbarUsername.setText(username);
                            //checks if the chat room is active or inactive
                            IsChatRoomActive(chatRoom.isActive());
                            if(profileImage.getVisibility()==View.INVISIBLE)
                            {
                                profileImage.setVisibility(View.VISIBLE);
                                toolbarUsername.setVisibility(View.VISIBLE);
                            }
                            DisplayChatMessages();
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    };
    //displays an alert dialog to ensure that the user would like to end the tutoring session
    private void DisplayEndSessionAlert()
    {
        LayoutInflater inflater;
        if(this.getContext()!=null) {
            inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (inflater!=null) {
                View endSessionPopup = inflater.inflate(R.layout.popup_end_session, null);
                Button confirmEndSessionButton = endSessionPopup.findViewById(R.id.final_end_session_button);
                Button cancelButton = endSessionPopup.findViewById(R.id.cancel_button);

                AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());

                builder.setView(endSessionPopup);
                confirmEndSessionAlert = builder.create();
                if(endSessionPopup.getParent()!=null)
                {
                    ((ViewGroup)endSessionPopup.getParent()).removeView(endSessionPopup);
                }
                //show popup window
                confirmEndSessionAlert.show();
                confirmEndSessionButton.setOnClickListener(confirmEndSessionClickListener);
                cancelButton.setOnClickListener(cancelClickListener);
            }
        }
    }
    private void DisplaySurveyAlert()
    {
        LayoutInflater inflater;
        if(this.getContext()!=null) {
            inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (inflater!=null) {
                surveyPopup = inflater.inflate(R.layout.popup_tutor_survey, null);
                Button surveySubmit = surveyPopup.findViewById(R.id.submit_button);
                AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
                builder.setView(surveyPopup);
                surveyAlert = builder.create();
                surveyAlert.show();
                surveySubmit.setOnClickListener(surveySubmitClickListener);
            }
        }

    }
    //sends the survey data to FireBase and stores it
    private void StoreSurveyData(RadioButton focusedButtonSelected, RadioButton accurateButtonSelected, RadioButton friendlyButtonSelected, String comments)
    {
        //gets the position of the button selected and adds one to get the actual value of the button selected because the position starts from zero
        String surveyID = surveyReference.push().getKey();
        if(surveyID!=null) {
            surveyReference.child(surveyID).child("respondent").setValue(firebaseUser.getUid());
            surveyReference.child(surveyID).child("focusedRating").setValue(focusedGroup.indexOfChild(focusedButtonSelected) + 1);
            surveyReference.child(surveyID).child("accurateRating").setValue(accurateGroup.indexOfChild(accurateButtonSelected) + 1);
            surveyReference.child(surveyID).child("friendlyRating").setValue(friendlyGroup.indexOfChild(friendlyButtonSelected) + 1);
            surveyReference.child(surveyID).child("comment").setValue(comments);
        }

    }
    //gets the data from the math that was made in the matching fragment and sets the views to the appropriate information
    private void GetMatchData(Bundle bundle)
    {
        //bundle contains data only if the chat fragment was opened from a match that was made in the match fragment
        if(bundle!=null)
        {
            tutorID = bundle.getString("Tutor ID");
            toolbarUsername.setText(bundle.getString("Tutor Username"));
            String profileImageString = bundle.getString("Tutor Profile Photo");
            String questionAsked = bundle.getString("Question");
            if(profileImageString!=null) {
                SetUserProfileImage(profileImageString, profileImage);
            }
            //gets a unique key generated by FireBase to use throughout the fragment
            chatRoomIDReference = chatsReference.push().getKey();
            if(chatRoomIDReference!=null) {
                //creates a new chat room with the tutor and current users information
                chatsReference.child(chatRoomIDReference).setValue(new ChatRoom(chatRoomIDReference,
                        tutorID, firebaseUser.getUid(), 0,questionAsked,true));
                //starts timer for the specific session
                sessionStartTime = System.currentTimeMillis();
                endSessionButton.setVisibility(View.VISIBLE);
                DisplayChatMessages();
            }
        }
    }
    //checks if the chat room is active or not and displays,hides, enables, or disables the appropriate views
    private void IsChatRoomActive(boolean isActive)
    {
        if(!isActive)
        {
            //if it is not active then do not allow the user to send messages
            chatEditText.setEnabled(false);
            sendButton.setEnabled(false);
            startDrawActivityButton.setEnabled(false);
            endSessionButton.setVisibility(View.GONE);
        }
        else
        {
            chatEditText.setEnabled(true);
            sendButton.setEnabled(true);
            startDrawActivityButton.setEnabled(true);
            endSessionButton.setVisibility(View.VISIBLE);

        }
    }
    //**************************************************************************************
    // Value Event Listeners
    //***************************************************************************************

    private ValueEventListener userReferenceValueEvent = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            SetConversationList(dataSnapshot);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };
    //value event to update to the tutor's rating when a new survey is submitted
    private  ValueEventListener surveyReferenceValueEvent = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            long focusedTotal = 0;
            long accuracyTotal = 0;
            long friendlyTotal = 0;
            float count = 0;

            //accumulates all of the survey results in each category
            for(DataSnapshot snapshot:dataSnapshot.getChildren()) {
                Integer focusedValue = snapshot.child("focusedRating").getValue(int.class);
                Integer accuracyValue = snapshot.child("accurateRating").getValue(int.class);
                Integer friendlyValue =  snapshot.child("friendlyRating").getValue(int.class);
                ++count;

                focusedTotal += focusedValue;
                accuracyTotal += accuracyValue;
                friendlyTotal += friendlyValue;

            }
            //gets the total average across each category, adds them, and divides by 3 to account for the 3 separate categories
            float rating = (((accuracyTotal+focusedTotal+friendlyTotal)/count)/3);
            FirebaseDatabase.getInstance().getReference("Users").child("Tutors").child(tutorID).child("rating").setValue(rating);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };
    //value event listener to get the number of items in the conversationListView and selects the last created chat room as default
    private ValueEventListener listContentQueryValueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            //gets the number of items in the conversation list
            int itemPosition = (int)dataSnapshot.getChildrenCount() -1;
            if(itemPosition >= 0 && itemPosition < conversationList.getCount()) {
                //selects the last created chat room as a default
                conversationList.performItemClick(conversationList.getAdapter().getView(itemPosition, null, null), itemPosition, conversationList.getItemIdAtPosition(itemPosition));
            }
            //loops through the available chat rooms
            for(DataSnapshot snapshot:dataSnapshot.getChildren())
            {
                if(snapshot.getKey()!=null) {
                    //chatRoomIDReference is initialized through the default click
                    if (snapshot.getKey().equals(chatRoomIDReference)) {
                        boolean isActive = (boolean) snapshot.child("active").getValue();
                        IsChatRoomActive(isActive);
                    }
                }
            }
        }
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };
    private ValueEventListener tutorSessionTimeValueEvent = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            long totalSessionTime;
            if(dataSnapshot.child("totalSessionTimes").getValue() != null) {
                totalSessionTime = (long) dataSnapshot.child("totalSessionTimes").getValue();
            }else{
                totalSessionTime = 0;
            }
            totalSessionTime += sessionEndTime - sessionStartTime;
            tutorSessionTime.child("totalSessionTimes").setValue(totalSessionTime);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };
    //**************************************************************************************
    // On click methods
    //***************************************************************************************
    private View.OnClickListener endSessionClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            DisplayEndSessionAlert();

        }
    };
    private View.OnClickListener sendClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!chatEditText.getText().toString().equals("")) {
                SendMessage();

            }
        }
    };
    private View.OnClickListener conversationMenuClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            drawerLayout.openDrawer(GravityCompat.START);
        }
    };
    private View.OnClickListener surveySubmitClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            surveyReference = FirebaseDatabase.getInstance().getReference("Surveys").child(tutorID);
            focusedGroup = surveyPopup.findViewById(R.id.focused_group);
            accurateGroup = surveyPopup.findViewById(R.id.accurate_group);
            friendlyGroup = surveyPopup.findViewById(R.id.friendly_group);
            EditText comments = surveyPopup.findViewById(R.id.comments_edit_text);
            int focusedID = focusedGroup.getCheckedRadioButtonId();
            int accurateID = accurateGroup.getCheckedRadioButtonId();
            int friendlyID = friendlyGroup.getCheckedRadioButtonId();
            RadioButton focusedButtonSelected = surveyPopup.findViewById(focusedID);
            RadioButton accurateButtonSelected = surveyPopup.findViewById(accurateID);
            RadioButton friendlyButtonSelected = surveyPopup.findViewById(friendlyID);
            //checks if any of the radio buttons have not been selected
            if(focusedGroup.getCheckedRadioButtonId() == -1 || accurateGroup.getCheckedRadioButtonId() == -1 ||
                    friendlyGroup.getCheckedRadioButtonId() == -1)
            {
                Toast.makeText(getActivity(),"Please select one button in each category",Toast.LENGTH_SHORT).show();
            }
            else
            {
                StoreSurveyData(focusedButtonSelected,accurateButtonSelected,friendlyButtonSelected,comments.getText().toString());
                surveyAlert.dismiss();
            }
            //takes the data from the completed survey and updates the tutors rating so that other users an view their new rating
            surveyReference.addListenerForSingleValueEvent(surveyReferenceValueEvent);
        }
    };
    private View.OnClickListener confirmEndSessionClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            endSessionButton.setVisibility(View.INVISIBLE);
            chatsReference.child(chatRoomIDReference).child("active").setValue(false);
            chatEditText.setEnabled(false);
            sendButton.setEnabled(false);
            startDrawActivityButton.setEnabled(true);
            sessionEndTime = System.currentTimeMillis();
            tutorSessionTime = FirebaseDatabase.getInstance().getReference("Users").child("Tutors").child(tutorID);
            tutorSessionTime.addListenerForSingleValueEvent(tutorSessionTimeValueEvent);
            confirmEndSessionAlert.dismiss();
            if(currentUserType.equals("Students"))
            {
                DisplaySurveyAlert();
            }
        }
    };
    private View.OnClickListener cancelClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            confirmEndSessionAlert.dismiss();
        }
    };
    //starts a new activity to get a bitmap image to send to other user
    private View.OnClickListener startDrawActivityClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivityForResult(new Intent(getActivity(),DrawActivity.class),IMAGE_REQUEST);
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case IMAGE_REQUEST:
                if(resultCode == RESULT_OK) {
                    byte[] bytes = (byte[]) data.getExtras().get("data");
                    String image = "";
                    if(bytes!=null)
                    {
                        image = Base64.encodeToString(bytes,Base64.DEFAULT);
                    }
                    chatsReference.child(chatRoomIDReference).child("Messages").push().setValue(new Message(null,firebaseUser.getUid(),image));
                }
                break;
        }
    }

}


