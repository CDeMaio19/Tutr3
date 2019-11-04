package com.example.tutr;

public class User {
    private String email;
    private String school;
    private String id;
    private String profilePhoto;
    private String username;
    private String areaOfExpertise;
    private String description;



    public User(String email, String school, String id, String profilePhoto, String username, String areaOfExpertise, String description) {
        this.email = email;
        this.school = school;
        this.id = id;
        this.profilePhoto = profilePhoto;
        this.username = username;
        this.areaOfExpertise = areaOfExpertise;
        this.description = description;
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

}

