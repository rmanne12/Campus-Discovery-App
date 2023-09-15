package com.example.loginstart;

public class userInfo {
    private String fullName;
    private String email;
    private String userType;

    public userInfo() {}
    public userInfo(String name, String uEmail, String uType) {
        fullName = name;
        email = uEmail;
        userType = uType;
    }
    public String getFullName() {
        return fullName;
    }
    public String getEmail() {
        return email;
    }
    public String getUserType() {
        return userType;
    }
    public void printOut() {
        System.out.println(fullName);
        System.out.println(email);
        System.out.println(userType);
    }
}
