package com.example.tutr;

import java.sql.Time;
import java.time.Instant;
import java.util.Date;

public class Message {
    private String text;
    private String currentUser;
    private long timeSent;

    Message(String text, String currentUser)
    {
        this.text = text;
        this.currentUser= currentUser;
        this.timeSent = new Date().getTime();

    }
    Message(Message message)
    {
        this.text = message.getText();
        this.currentUser = message.getCurrentUser();
        this.timeSent = getTimeSent();
    }
    Message()
    {

    }

    public String getText()
    {
        return text;
    }

    public String getCurrentUser()
    {
        return currentUser;
    }
    public long getTimeSent()
    {
        return timeSent;
    }
    public void setText(String text){this.text = text;}
    public void setCurrentUser(String currentUser){this.currentUser = currentUser;}
    public void setTimeSent(long timeSent){this.timeSent = timeSent;}




}
