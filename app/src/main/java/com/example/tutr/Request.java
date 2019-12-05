package com.example.tutr;

public class Request {
    private String studentID;
    private String tutorID;
    private String question;
    private String requestID;

    Request()
    {

    }

    public Request(String studentID,String tutorID, String question, String requestID) {
        this.studentID = studentID;
        this.question = question;
        this.requestID = requestID;
        this.tutorID = tutorID;
    }

    public String getStudentID() {
        return studentID;
    }

    public void setStudentID(String studentID) {
        this.studentID = studentID;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getRequestID() {
        return requestID;
    }

    public void setRequestID(String requestID) {
        this.requestID = requestID;
    }

    public String getTutorID() {
        return tutorID;
    }

    public void setTutorID(String tutorID) {
        this.tutorID = tutorID;
    }
}
