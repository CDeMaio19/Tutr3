package com.example.tutr;


public class ChatRoom {
    private String tutorID;
    private String studentID;
    private String id;
    private long timeOfLastMessage;

    public ChatRoom()
    {

    }



    public ChatRoom(String id, String tutorID, String studentID, long timeOfLastMessage) {
        this.timeOfLastMessage = timeOfLastMessage;
        this.tutorID = tutorID;
        this.studentID = studentID;
        this.id = id;
    }

    public long getTimeOfLastMessage() {
        return timeOfLastMessage;
    }

    public void setTimeOfLastMessage(long timeOfLastMessage) {
        this.timeOfLastMessage = timeOfLastMessage;
    }
    public String getTutorID() {
        return tutorID;
    }

    public void setTutorID(String tutorID) {
        this.tutorID = tutorID;
    }

    public String getStudentID() {
        return studentID;
    }

    public void setStudentID(String studentID) {
        this.studentID = studentID;
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
