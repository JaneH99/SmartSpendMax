package edu.northeastern.smartspendmax;

public class Users {

    private String userName;
    private Boolean online; // This now indicates if the user is logged in
    private String loginTime;

    private String role;

    // Default constructor
    public Users() {}

    // Constructor
//    public Users(String userName, Boolean online, String loginTime) {
//        this.userName = userName;
//        this.online = online;
//        this.loginTime = loginTime;
//    }

    public Users(String userName, Boolean online, String loginTime, String role) {
        this.userName = userName;
        this.online = online;
        this.loginTime = loginTime;
        this.role = role;
    }

    // Getters and setters
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public Boolean getOnline() { return online; }
    public void setOnline(Boolean online) { this.online = online; }
    public String getLoginTime() { return loginTime; }
    public void setLoginTime(String loginTime) { this.loginTime = loginTime; }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
