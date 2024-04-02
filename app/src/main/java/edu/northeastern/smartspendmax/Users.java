package edu.northeastern.smartspendmax;

public class Users {

    private String userName;
    private Boolean online; // This now indicates if the user is logged in
    private String loginTime;

    // Default constructor
    public Users() {}

    // Constructor
    public Users(String userName, Boolean online, String loginTime) {
        this.userName = userName;
        this.online = online;
        this.loginTime = loginTime;
    }

    // Getters and setters
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public Boolean getOnline() { return online; }
    public void setOnline(Boolean online) { this.online = online; }
    public String getLoginTime() { return loginTime; }
    public void setLoginTime(String loginTime) { this.loginTime = loginTime; }
}
