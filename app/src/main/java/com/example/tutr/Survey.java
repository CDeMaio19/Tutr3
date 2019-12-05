package com.example.tutr;

public class Survey {
    private String respondent;
    private String comment;
    private int focusedRating;
    private int accurateRating;
    private int friendlyRating;
    private int overallRating;

    public Survey()
    {

    }

    public Survey(String respondent,String comment, int focusedRating, int accurateRating, int friendlyRating, int overallRating) {
        this.respondent = respondent;
        this.focusedRating = focusedRating;
        this.accurateRating = accurateRating;
        this.friendlyRating = friendlyRating;
        this.overallRating = overallRating;
        this.comment = comment;
    }

    public String getRespondent() {
        return respondent;
    }

    public void setRespondent(String respondent) {
        this.respondent = respondent;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getFocusedRating() {
        return focusedRating;
    }

    public void setFocusedRating(int focusedRating) {
        this.focusedRating = focusedRating;
    }

    public int getAccurateRating() {
        return accurateRating;
    }

    public void setAccurateRating(int accurateRating) {
        this.accurateRating = accurateRating;
    }

    public int getFriendlyRating() {
        return friendlyRating;
    }

    public void setFriendlyRating(int friendlyRating) {
        this.friendlyRating = friendlyRating;
    }

    public int getOverallRating() {
        return overallRating;
    }

    public void setOverallRating(int overallRating) {
        this.overallRating = overallRating;
    }
}
