package com.example.tutr;


public class ChatRoom {
    private String userID;
    private String timeOfLastMessage;

    public ChatRoom()
    {

    }

    public ChatRoom(String userID,String timeOfLastMessage) {
        this.timeOfLastMessage = timeOfLastMessage;
        this.userID = userID;
    }

    public String getTimeOfLastMessage() {
        return timeOfLastMessage;
    }

    public void setTimeOfLastMessage(String timeOfLastMessage) {
        this.timeOfLastMessage = timeOfLastMessage;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}
