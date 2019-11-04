package com.example.tutr;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class ChatFragment extends Fragment {
    private DatabaseReference myDatabase;
    private EditText chatEditText;
    private ImageButton sendButton;
    private FirebaseListAdapter <Message> adapter;
    private FirebaseUser firebaseUser;
    private ListView listOfMessages;
    private ImageButton conversationMenu;


    @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState ) {
            super.onCreate(savedInstanceState);
            View fragmentRootView = inflater.inflate(R.layout.fragment_chat,container,false);
            TextView toolbarUsername = fragmentRootView.findViewById(R.id.toolbarUsername);
            final DrawerLayout drawerLayout = fragmentRootView.findViewById(R.id.side_nav_drawer_layout);
            conversationMenu = fragmentRootView.findViewById(R.id.menu);
            sendButton = fragmentRootView.findViewById(R.id.sendButton);
            chatEditText = fragmentRootView.findViewById(R.id.editTextMessage);
            listOfMessages = fragmentRootView.findViewById(R.id.list_of_messages);

            conversationMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawerLayout.openDrawer(GravityCompat.START);
                    System.out.println("Clicked");

                }
            });

        chatEditText.addTextChangedListener(textMessageWatcher);
            DisplayChatMessages();
            sendButton.setOnClickListener(new View.OnClickListener() {

            @Override
                public void onClick(View v) {

                if (!chatEditText.getText().toString().equals("")) {
                    SendMessage();
                }
            }
        });

        myDatabase = FirebaseDatabase.getInstance().getReference();

            return fragmentRootView;
        }

    private void DisplayChatMessages() {

        adapter = new FirebaseListAdapter<Message>(getActivity(), Message.class,
                R.layout.final_message, FirebaseDatabase.getInstance().getReference().child("Messages")) {
            @Override
            protected void populateView(View v, Message model, int position) {
                TextView messageText = v.findViewById(R.id.message_text);
                TextView messageUser = v.findViewById(R.id.sent_by);
                TextView messageTime = v.findViewById(R.id.time_sent);

                messageText.setText(model.getText());
                messageUser.setText(model.getCurrentUser());
                messageTime.setText(DateFormat.format("hh:mm - MM-dd-yyyy", model.getTimeSent()));

            }
        };

       listOfMessages.setAdapter(adapter);
    }

    private void SendMessage()
    {
            //Change last argument to - FirebaseAuth.getInstance().getCurrentUser().getDisplayName())); when profiles are made
            myDatabase.child("Messages").push().setValue(new Message(chatEditText.getText().toString(),
                                                        FirebaseAuth.getInstance().getCurrentUser().getDisplayName()));

        chatEditText.setText("");


    }
    private TextWatcher textMessageWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        //checks if the text in chatEditText has changed
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String textMessageInput = chatEditText.getText().toString().trim();
            //if the EditText is not empty then enable sendButton
                sendButton.setEnabled(!textMessageInput.isEmpty());

        }

        @Override
        public void afterTextChanged(Editable s) {

        }

    };

}


