package com.example.tutr;


public class ChatRoom {
    private String tutorID;
    private String studentID;
    private String questionAsked;
    private boolean active;
    private String id;
    private long timeOfLastMessage;

    public ChatRoom()
    {

    }

    public ChatRoom(String id, String tutorID, String studentID, long timeOfLastMessage, String questionAsked, boolean active) {
        this.timeOfLastMessage = timeOfLastMessage;
        this.tutorID = tutorID;
        this.studentID = studentID;
        this.id = id;
        this.questionAsked = questionAsked;
        this.active = active;
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
    public String getQuestionAsked() {
        return questionAsked;
    }

    public void setQuestionAsked(String questionAsked) {
        this.questionAsked = questionAsked;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
