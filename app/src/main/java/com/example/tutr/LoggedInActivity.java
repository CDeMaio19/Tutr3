package com.example.tutr;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoggedInActivity extends AppCompatActivity{
    private DatabaseReference myDatabase;
    private EditText chatEditText;
    private Button sendButton;


    @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loggedin);

        final TextView chatTextView = findViewById(R.id.current_user_messages);
        sendButton = findViewById(R.id.sendButton);
        chatEditText = findViewById(R.id.editTextMessage);
        chatEditText.addTextChangedListener(textMessageWatcher);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendMessage();
            }
        });

        myDatabase = FirebaseDatabase.getInstance().getReferenceFromUrl("https://tutr-84394.firebaseio.com").child("message");
        myDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //handles cases where the database only has null value
                if(dataSnapshot.getValue()!=null) {
                    //separates each key value pair into their own element in an array
                    String[] Messages = dataSnapshot.getValue().toString().split(",");
                    chatTextView.setText("");
                    //for each element in Messages separate the key value pairs from one another and places into an array
                    for (String s : Messages) {
                        String[] FinalMessage = s.split("=");
                        chatTextView.append(FinalMessage[FinalMessage.length - 1] + "\n");


                    }
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                chatTextView.setText("Cancelled_Message");
            }
        });

    }
    public void SendMessage()
    {
            chatEditText = findViewById(R.id.editTextMessage);
            myDatabase.child(Long.toString(System.currentTimeMillis())).setValue(chatEditText.getText().toString());
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


