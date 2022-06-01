package com.example.mailapp;

public class UserData {
    private static UserData userData;
    public String user;
    public String password;
    public String host;
    public Integer port;

    private UserData() {}

    public static UserData getInstance() {
        if (userData == null) {
            userData = new UserData();
        }
        return userData;
    }

    public void setUserData(String user, String password, String host, Integer port){
        this.user = user;
        this.password = password;
        this.host = host;
        this.port = port;
    }
}
