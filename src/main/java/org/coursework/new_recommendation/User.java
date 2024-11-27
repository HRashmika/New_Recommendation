package org.coursework.new_recommendation;

import java.util.List;

public class User {
    private String username;
    private String email;
    private String preferences;
    private List<String> loginTime; // Store login times as a list of strings

    // Constructor with parameters
    public User(String username, String email, String preferences, List<String> loginTime) {
        this.username = username;
        this.email = email;
        this.preferences = preferences;
        this.loginTime = loginTime;
    }

    // Default constructor
    public User() {
        this.username = "";
        this.email = "";
        this.preferences = "";
        this.loginTime = null;
    }

    // Getters and setters
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

    public List<String> getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(List<String> loginTime) {
        this.loginTime = loginTime;
    }

    // Method to get the most recent login time
    public String getMostRecentLoginTime() {
        if (loginTime != null && !loginTime.isEmpty()) {
            return loginTime.get(loginTime.size() - 1); // Get the last element
        }
        return "N/A";
    }
}
