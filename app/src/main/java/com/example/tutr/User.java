package com.example.tutr;

public class User {
    private String email;
    private String school;
    private String id;
    private String subject;
    private String profilePhoto;
    private String username;
    private String subject;
    private String areaOfExpertise;
    private String description;
    private String payment;
    private String paymentMeathod;




    public User(String email, String school, String id, String profilePhoto, String username, String areaOfExpertise, String description, String subject) {
        this.email = email;
        this.school = school;
        this.id = id;
        this.profilePhoto = profilePhoto;
        this.username = username;
        this.areaOfExpertise = areaOfExpertise;
        this.description = description;
        this.subject = subject;
        this.payment = payment;
        this.paymentMeathod = paymentMeathod;
    }

    public User() {
    }
    public String getPayment() {
        return payment;
    }

    public void setPayment(String payment) {
        this.payment = payment;
    }

    public String getPaymentMeathod() {
        return paymentMeathod;
    }

    public void setPaymentMeathod(String paymentMeathod) {
        this.paymentMeathod = paymentMeathod;
    }
    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(String profilePhoto) {
        this.profilePhoto = profilePhoto;
    }
    public String getAreaOfExpertise() {
        return areaOfExpertise;
    }

    public void setAreaOfExpertise(String areaOfExpertise) {
        this.areaOfExpertise = areaOfExpertise;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
}

