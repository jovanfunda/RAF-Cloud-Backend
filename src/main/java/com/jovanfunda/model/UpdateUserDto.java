package com.jovanfunda.model;

public class UpdateUserDto {

    String realEmail;
    User user;
    String password;

    public String getRealEmail() {
        return realEmail;
    }

    public void setRealEmail(String realEmail) {
        this.realEmail = realEmail;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
