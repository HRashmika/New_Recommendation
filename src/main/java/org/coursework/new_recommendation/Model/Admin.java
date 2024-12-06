package org.coursework.new_recommendation.Model;

import java.util.List;

public class Admin {
    private String adminId;
    private String password;
    private List<String> loginTimes;

    public Admin(String adminId, String password, List<String> loginTimes) {
        this.adminId = adminId;
        this.password = password;
        this.loginTimes = loginTimes;
    }

    // Constructor with no parameter
    public Admin() {
    }

    public String getAdminId() {
        return adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<String> getLoginTimes() {
        return loginTimes;
    }

    public void setLoginTimes(List<String> loginTimes) {
        this.loginTimes = loginTimes;
    }
}
