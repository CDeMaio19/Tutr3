package com.example.tutr;

public class User {
    private String id;
    private String username;
    private String profileImageURL;

    User(String id, String username, String profileImageURL)
    {
        this.id = id;
        this.username = username;
        this.profileImageURL = profileImageURL;

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

    public String getProfileImageURL() {
        return profileImageURL;
    }

    public void setProfileImageURL(String profileImageURL) {
        this.profileImageURL = profileImageURL;
    }
}
