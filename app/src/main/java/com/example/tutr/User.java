package com.example.tutr;

public class User {
    private String email;
    private String school;
    private String id;
    private String profilePhotoURL;
    private String username;

    public User(String email, String school, String id, String profilePhotoURL, String username) {
        this.email = email;
        this.school = school;
        this.id = id;
        this.profilePhotoURL = profilePhotoURL;
        this.username = username;
    }

    public User() {
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

    public String getProfilePhotoURL() {
        return profilePhotoURL;
    }

    public void setProfilePhotoURL(String profilePhotoURL) {
        this.profilePhotoURL = profilePhotoURL;
    }
}

