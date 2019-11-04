package com.example.tutr;

import java.util.Date;


public class Question {
    private String question;
    private String majorSubject;
    private String minorSubject;
    private String description;
    private long timeAsked;

    Question()
    {

    }

    public Question(String question, String majorSubject, String minorSubject, String description) {
        this.question = question;
        this.majorSubject = majorSubject;
        this.minorSubject = minorSubject;
        this.description = description;
        this.timeAsked = new Date().getTime();
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getMajorSubject() {
        return majorSubject;
    }

    public void setMajorSubject(String majorSubject) {
        this.majorSubject = majorSubject;
    }

    public String getMinorSubject() {
        return minorSubject;
    }

    public void setMinorSubject(String minorSubject) {
        this.minorSubject = minorSubject;
    }

    public long getTimeAsked() {
        return timeAsked;
    }

    public void setTimeAsked(long timeAsked) {
        this.timeAsked = timeAsked;
    }
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
