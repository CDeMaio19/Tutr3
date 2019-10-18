package com.example.tutr;

import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class ChatTranscriptActivity extends AppCompatActivity{
    private DatabaseReference myDatabase;
    private EditText chatEditText;
    private ImageButton sendButton;
    private FirebaseListAdapter<Message> adapter;
    private FirebaseUser firebaseUser;


    @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_chat_transcript);
            TextView toolbarUsername = findViewById(R.id.toolbarUsername);
            sendButton = findViewById(R.id.sendButton);
            chatEditText = findViewById(R.id.editTextMessage);


            chatEditText.addTextChangedListener(textMessageWatcher);
            DisplayChatMessages();
            sendButton.setOnClickListener(new View.OnClickListener() {

            @Override
                public void onClick(View v) {
                    SendMessage();
                }
        });

        myDatabase = FirebaseDatabase.getInstance().getReference();


        }

    public void DisplayChatMessages() {
        ListView listOfMessages = findViewById(R.id.list_of_messages);

        adapter = new FirebaseListAdapter<Message>(this, Message.class,
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

    public void SendMessage()
    {
        chatEditText = findViewById(R.id.editTextMessage);
        //Change last argument to - FirebaseAuth.getInstance().getCurrentUser().getDisplayName())); when profiles are made
        myDatabase.child("Messages").push().setValue(new Message(chatEditText.getText().toString(), "Bob"));

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


