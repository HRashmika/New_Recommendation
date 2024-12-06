package org.coursework.new_recommendation.Model;

import java.util.List;

public class User {
    private String username;
    private String email;
    private String preferences;
    private List<String> loginTimes;  // Change here from loginTime to loginTimes

    public User(String username, String email, String preferences, List<String> loginTimes) {
        this.username = username;
        this.email = email;
        this.preferences = preferences;
        this.loginTimes = loginTimes;
    }

    // For login times
    public User() {
        this.username = "";
        this.email = "";
        this.preferences = "";
        this.loginTimes = null;  // Changed here as well
    }

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

    public List<String> getLoginTimes() {
        return loginTimes;
    }

    public void setLoginTimes(List<String> loginTimes) {
        this.loginTimes = loginTimes;
    }
}
