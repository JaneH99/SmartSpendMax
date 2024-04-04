package edu.northeastern.smartspendmax.model;

public class AdsMaker {

    private String adsMakerName;
    private String loginTime;
    private Boolean online;

    public AdsMaker(String adsMakerName, String loginTime, Boolean online) {
        this.adsMakerName = adsMakerName;
        this.loginTime = loginTime;
        this.online = online;
    }

    public String getAdsMakerName() {
        return adsMakerName;
    }

    public String getLoginTime() {
        return loginTime;
    }

    public Boolean getOnline() {
        return online;
    }
}
