package com.example.tutr;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
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
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatFragment extends Fragment {
    private DatabaseReference chatsReference;
    private TextView toolbarUsername;
    private String currentUserType;
    private String otherUserType;
    private String userID;
    private String chatRoomIDReference;
    private CircleImageView profileImage;
    private EditText chatEditText;
    private ImageButton sendButton;
    private ImageButton endSessionButton;
    private FirebaseUser firebaseUser;
    private ListView listOfMessages;
    private ListView conversationList;

    @Override
        public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState ) {
            super.onCreate(savedInstanceState);
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
            //views
            View fragmentRootView = inflater.inflate(R.layout.fragment_chat,container,false);
            profileImage = fragmentRootView.findViewById(R.id.profile_image);
            toolbarUsername = fragmentRootView.findViewById(R.id.toolbarUsername);
            chatEditText = fragmentRootView.findViewById(R.id.editTextMessage);
            listOfMessages = fragmentRootView.findViewById(R.id.list_of_messages);
            conversationList = fragmentRootView.findViewById(R.id.list_of_conversations);
            conversationList.setOnItemClickListener(chatRoomSelection);
            ImageButton conversationMenu = fragmentRootView.findViewById(R.id.menu);
            endSessionButton = fragmentRootView.findViewById(R.id.end_session_button);
            sendButton = fragmentRootView.findViewById(R.id.sendButton);


            final DrawerLayout drawerLayout = fragmentRootView.findViewById(R.id.conversation_list_NavDrawer);

            Bundle bundle;
            bundle = getArguments();
            //get the tutors information from the match
            if(bundle!=null)
            {
                userID = bundle.getString("Tutor ID");
                toolbarUsername.setText(bundle.getString("Tutor Username"));
                String profileString = bundle.getString("Tutor Profile Photo");
                String questionAsked = bundle.getString("Question");
                if(profileString!=null) {
                    SetUserProfileImage(profileString, profileImage);
                }
                chatRoomIDReference = chatsReference.push().getKey();
                //creates a new chat room with the tutor and current users information
                if(chatRoomIDReference!=null) {
                    chatsReference.child(chatRoomIDReference).setValue(new ChatRoom(chatRoomIDReference,
                            userID, firebaseUser.getUid(), 0,questionAsked,true));
                    endSessionButton.setVisibility(View.VISIBLE);
                    DisplayChatMessages();
                }
            }
            else
            {
                toolbarUsername.setVisibility(View.INVISIBLE);
                profileImage.setVisibility(View.INVISIBLE);
            }


                usersReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    //sets the chat rooms for the current user
                    SetConversationList(dataSnapshot);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
                });

                conversationMenu.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        drawerLayout.openDrawer(GravityCompat.START);

                    }
                });
                //on click send the users message
                sendButton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (!chatEditText.getText().toString().equals("")) {
                            SendMessage();

                        }
                    }
                });
                endSessionButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        chatsReference.child(chatRoomIDReference).child("active").setValue(false);
                        chatEditText.setEnabled(false);
                        sendButton.setEnabled(false);
                        endSessionButton.setVisibility(View.GONE);
                    }
                });

            return fragmentRootView;
    }
        //displays the chat messages for the current chat room
    private void DisplayChatMessages() {

        FirebaseListAdapter <Message> adapter = new FirebaseListAdapter<Message>(getActivity(), Message.class,
                R.layout.final_message, chatsReference.child(chatRoomIDReference).child("Messages")) {
            @Override
            protected void populateView(View v, final Message model, int position) {
                TextView messageText = v.findViewById(R.id.message_text);
                final TextView messageUser = v.findViewById(R.id.sent_by);
                TextView messageTime = v.findViewById(R.id.time_sent);

                messageText.setText(model.getText());
                messageTime.setText(DateFormat.format("hh:mm - MM-dd-yyyy", model.getTimeSent()));
                FirebaseDatabase.getInstance().getReference().child("Users").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.child(currentUserType).hasChild(model.getSender())){
                            messageUser.setText(dataSnapshot.child(currentUserType).child(model.getSender()).child("username").getValue().toString());
                        }
                        else if (dataSnapshot.child(otherUserType).hasChild(model.getSender()))
                        {
                            messageUser.setText(dataSnapshot.child(otherUserType).child(model.getSender()).child("username").getValue().toString());

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
                            new Message(chatEditText.getText().toString(),firebaseUser.getUid(),userID));
        chatEditText.setText("");
        //stores the chat room timestamp in reverse order so when queried, the result set will show in descending order
        chatsReference.child(chatRoomIDReference).child("timeOfLastMessage").setValue(-1 * new Date().getTime());

    }
    private void SetConversationList(final DataSnapshot usersSnapshot) {
        final FirebaseListAdapter<ChatRoom> adapter = new FirebaseListAdapter<ChatRoom>(getActivity(),ChatRoom.class,
                                                                    R.layout.conversation_list_item,chatsReference) {
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
                    //if the current user is a student
                    if(!currentUserType.equals("Tutors")) {
                        //if the chat room's tutorID matches the id of the current user then populate the views with their
                        // information
                        if (model.getTutorID().equals(users.child("id").getValue())) {
                            chatRoomIDReference = model.getId();
                            String profileString = users.child("profilePhoto").getValue(String.class);
                            username.setText(users.child("username").getValue(String.class));
                            if (profileString != null) {
                                SetUserProfileImage(profileString, profileImage);
                            }
                        }
                    }
                    //otherwise a tutor is logged in and the chat room information should be filled with the students info
                    else {
                        if (model.getStudentID().equals(users.child("id").getValue())) {
                            String profileString = users.child("profilePhoto").getValue(String.class);
                            username.setText(users.child("username").getValue(String.class));
                            if (profileString != null) {
                                SetUserProfileImage(profileString, profileImage);
                            }
                        }
                    }
                }
            }
        };
        conversationList.setAdapter(adapter);

    }
    //method to convert the profile string stored into Firebase into a bitmap to be loaded into the circle imageView
    private void SetUserProfileImage(String profileString,CircleImageView profileImage)
    {
        if (!profileString.equals("default")) {
            try {
                byte[] encodeByte = Base64.decode(profileString, Base64.DEFAULT);
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
                            CircleImageView tempProfile;
                            TextView tempUsername;
                            tempProfile = view.findViewById(R.id.profile_image);
                            tempUsername = view.findViewById(R.id.username);
                            profileImage.setImageDrawable(tempProfile.getDrawable());
                            toolbarUsername.setText(tempUsername.getText());
                            //checks if the chat room is active or inactive
                            if(!chatRoom.isActive())
                            {
                                //if it is not active then do not allow the user to send messages
                                chatEditText.setEnabled(false);
                                sendButton.setEnabled(false);
                                endSessionButton.setVisibility(View.GONE);
                            }
                            else
                            {
                                chatEditText.setEnabled(true);
                                sendButton.setEnabled(true);
                                endSessionButton.setVisibility(View.VISIBLE);

                            }
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




}


