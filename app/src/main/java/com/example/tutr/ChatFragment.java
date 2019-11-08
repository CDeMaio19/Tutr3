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
import de.hdodenhof.circleimageview.CircleImageView;

public class ChatFragment extends Fragment {
    private DatabaseReference chatsReference;
    private String chatRoomIDReference;
    private TextView toolbarUsername;
    private String currentUserType;
    private String otherUserType;
    private CircleImageView profileImage;
    private EditText chatEditText;
    private String userID;
    private FirebaseUser firebaseUser;
    private ListView listOfMessages;
    private ListView conversationList;

    @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState ) {
            super.onCreate(savedInstanceState);
            firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            chatsReference = FirebaseDatabase.getInstance().getReference("Chats").child("ChatRooms");
            if(((LoggedInActivity)getActivity()).isTutor)
            {
                currentUserType = "Tutors";
                otherUserType = "Students";


            }
            else {
                currentUserType = "Students";
                otherUserType = "Tutors";
            }
            //views
            View fragmentRootView = inflater.inflate(R.layout.fragment_chat,container,false);
            profileImage = fragmentRootView.findViewById(R.id.profile_image);
            toolbarUsername = fragmentRootView.findViewById(R.id.toolbarUsername);
            ImageButton conversationMenu = fragmentRootView.findViewById(R.id.menu);
            ImageButton sendButton = fragmentRootView.findViewById(R.id.sendButton);
            chatEditText = fragmentRootView.findViewById(R.id.editTextMessage);
            listOfMessages = fragmentRootView.findViewById(R.id.list_of_messages);
            conversationList = fragmentRootView.findViewById(R.id.list_of_conversations);
            conversationList.setOnItemClickListener(chatRoomSelection);

            final DrawerLayout drawerLayout = fragmentRootView.findViewById(R.id.conversation_list_NavDrawer);

            Bundle bundle;
            bundle = getArguments();
                if(bundle!=null)
                {
                    userID = bundle.getString("Tutor ID");
                    toolbarUsername.setText(bundle.getString("Tutor Username"));
                    String profileString = bundle.getString("Tutor Profile Photo");
                    SetUserProfileImage(profileString,profileImage);
                    chatRoomIDReference = chatsReference.push().getKey();
                    chatsReference.child(chatRoomIDReference).setValue(new ChatRoom(chatRoomIDReference,userID,firebaseUser.getUid(),0));

                }

                DatabaseReference usersReference = FirebaseDatabase.getInstance().getReference("Users").child(otherUserType);
                usersReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

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
                sendButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!chatEditText.getText().toString().equals("")) {
                    SendMessage();
                }
            }
        });

            return fragmentRootView;
        }

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

    }
    private void SetConversationList(final DataSnapshot usersSnapshot) {
        final FirebaseListAdapter<ChatRoom> adapter = new FirebaseListAdapter<ChatRoom>(getActivity(),ChatRoom.class,
                                                                    R.layout.conversation_list_item,chatsReference) {
            @Override
            protected void populateView(View v, ChatRoom model, int position) {
                CircleImageView profileImage = v.findViewById(R.id.profile_image);
                TextView username = v.findViewById(R.id.username);
                TextView timeOfLastMessage = v.findViewById(R.id.time_of_last_message);
                if(model.getTimeOfLastMessage() == 0)
                {
                    timeOfLastMessage.setText("");
                }
                else {
                    timeOfLastMessage.setText(DateFormat.format("hh:mm - MM-dd-yyyy", model.getTimeOfLastMessage()));
                }
                for(DataSnapshot users:usersSnapshot.getChildren())
                {
                    if(!currentUserType.equals("Tutors")) {
                        if (model.getTutorID().equals(users.child("id").getValue())) {
                            chatRoomIDReference = model.getId();
                            String profileString = users.child("profilePhoto").getValue(String.class);
                            username.setText(users.child("username").getValue(String.class));
                            if (profileString != null) {
                                SetUserProfileImage(profileString, profileImage);
                            }
                        }
                    }
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
    private AdapterView.OnItemClickListener chatRoomSelection = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
            final ChatRoom chatRoom = (ChatRoom) conversationList.getItemAtPosition(position);

            chatsReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot snapshot:dataSnapshot.getChildren())
                    {
                        if(chatRoom.getId().equals(snapshot.child("id").getValue())) {
                            chatRoomIDReference = snapshot.child("id").getValue(String.class);
                            if(otherUserType.equals("Tutors"))
                            {
                                CircleImageView tempProfile;
                                TextView tempUsername;
                                tempProfile = view.findViewById(R.id.profile_image);
                                tempUsername = view.findViewById(R.id.username);
                                profileImage.setImageDrawable(tempProfile.getDrawable());
                                toolbarUsername.setText(tempUsername.getText());

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


