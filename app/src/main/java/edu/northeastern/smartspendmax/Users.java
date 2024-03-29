package edu.northeastern.smartspendmax;

public class Users {
    public String password;
    public boolean login;
    public String lastLogin;

    public Users(String password, boolean login, String lastLogin) {
        this.password = password;
        this.login = login;
        this.lastLogin = lastLogin;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isLogin() {
        return login;
    }

    public void setLogin(boolean login) {
        this.login = login;
    }

    public String getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(String lastLogin) {
        this.lastLogin = lastLogin;
    }
}
