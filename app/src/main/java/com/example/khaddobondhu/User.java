package com.example.khaddobondhu;

public class User {
    private String id;
    private String name;
    private int profileImageResId; // image resource ID

    public User(String id, String name, int profileImageResId) {
        this.id = id;
        this.name = name;
        this.profileImageResId = profileImageResId;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public int getProfileImageResId() { return profileImageResId; }
}

