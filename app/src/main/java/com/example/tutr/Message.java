package com.example.tutr;

import java.sql.Time;
import java.time.Instant;
import java.util.Date;

public class Message {
    private String text;
    private String image;
    private String sender;
    private long timeSent;

    Message()
    {

    }

    public Message(String text, String sender, String image) {
        this.text = text;
        this.sender = sender;
        this.image = image;
        this.timeSent = new Date().getTime();
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getText()
    {
        return text;
    }
    public long getTimeSent()
    {
        return timeSent;
    }
    public void setText(String text){this.text = text;}
    public void setTimeSent(long timeSent){this.timeSent = timeSent;}

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
