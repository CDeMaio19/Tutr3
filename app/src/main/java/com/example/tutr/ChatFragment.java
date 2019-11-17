package com.example.tutr;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class ChatFragment extends Fragment {
    private DatabaseReference chatsReference;
    private DatabaseReference chatRoomIDReference;
    private CircleImageView profileImage;
    private UserAdapter userAdapter;
    private EditText chatEditText;
    private String userID;
    private FirebaseUser firebaseUser;
    private ListView listOfMessages;
    private RecyclerView conversationList;

    @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState ) {
            super.onCreate(savedInstanceState);
            View fragmentRootView = inflater.inflate(R.layout.fragment_chat,container,false);
            firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            chatsReference = FirebaseDatabase.getInstance().getReference("Chats").child(firebaseUser.getUid()).child("ChatRooms");
            profileImage = fragmentRootView.findViewById(R.id.profile_image);
            TextView toolbarUsername = fragmentRootView.findViewById(R.id.toolbarUsername);
            LayoutInflater navDrawerInflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View navDrawer = navDrawerInflater.inflate(R.layout.conversation_list_nav_drawer,null);
            conversationList = navDrawer.findViewById(R.id.list_of_conversations);
            Bundle bundle = getArguments();
            if(bundle!=null)
            {
                userID = bundle.getString("Tutor ID");
                toolbarUsername.setText(bundle.getString("Tutor Username"));
                String profileString = bundle.getString("Tutor Profile Photo");
                SetUserProfileImage(profileString,profileImage);
                chatRoomIDReference = chatsReference.push();
                chatRoomIDReference.setValue(new ChatRoom(userID,"0"));
            }
            else
            {
                chatRoomIDReference = FirebaseDatabase.getInstance().getReference();            }

            chatsReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    SetConversationList(dataSnapshot);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            final DrawerLayout drawerLayout = fragmentRootView.findViewById(R.id.side_nav_drawer_layout);
            ImageButton conversationMenu = fragmentRootView.findViewById(R.id.menu);
            ImageButton sendButton = fragmentRootView.findViewById(R.id.sendButton);
            chatEditText = fragmentRootView.findViewById(R.id.editTextMessage);
            listOfMessages = fragmentRootView.findViewById(R.id.list_of_messages);

            conversationMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawerLayout.openDrawer(GravityCompat.START);

                }
            });
            DisplayChatMessages();
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
                R.layout.final_message, chatsReference.child("Messages")) {
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
                        if(dataSnapshot.child("Tutors").hasChild(model.getSender())){
                            messageUser.setText(dataSnapshot.child("Tutors").child(model.getSender()).child("username").getValue().toString());
                        }
                        else
                        {
                            messageUser.setText(dataSnapshot.child("Students").child(model.getSender()).child("username").getValue().toString());
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
        chatsReference.child(chatRoomIDReference.getKey()).child("Messages").push().setValue(
                            new Message(chatEditText.getText().toString(),firebaseUser.getUid(),userID));
        chatEditText.setText("");

    }
    void SetConversationList(final DataSnapshot chatsSnapshot)
    {
        final ArrayList<User> users = new ArrayList<>();
        final DatabaseReference usersReference = FirebaseDatabase.getInstance().getReference("Users").child("Tutors");
        usersReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    System.out.println(chatsSnapshot.child("userID").getValue());
                    User user = snapshot.getValue(User.class);
                    if (user != null && user.getId().equals(chatsSnapshot.child("userID").getValue())) {
                        users.add(user);
                    }
                }
                System.out.println(users);
                userAdapter = new UserAdapter(getContext(),users);
                conversationList.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    void SetUserProfileImage(String profileString,CircleImageView profileImage)
    {
        if (!profileString.equals("default")) {
            byte[] encodeByte = Base64.decode(profileString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            profileImage.setImageBitmap(bitmap);
        } else {
            profileImage.setImageResource(R.drawable.graduation_2841875_640);
        }

    }

}


