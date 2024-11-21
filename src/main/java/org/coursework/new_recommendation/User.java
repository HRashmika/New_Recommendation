package org.coursework.new_recommendation;

public class User {
    private String username;
    private String email;
    private String preferences;

    // Parameterized constructor
    public User(String username, String email, String preferences) {
        this.username = username;
        this.email = email;
        this.preferences = preferences;
    }

    // Default constructor
    public User() {
        this.username = "";
        this.email = "";
        this.preferences = "";
    }

    // Getters and Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPreferences() {
        return preferences;
    }

    public void setPreferences(String preferences) {
        this.preferences = preferences;
    }
}
