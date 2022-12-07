package com.jovanfunda.model;

public class JWTokenPermissionChecker {

    String jwtoken;
    Permission permission;

    public String getjwtoken() {
        return jwtoken;
    }

    public void setjwtoken(String jwtoken) {
        this.jwtoken = jwtoken;
    }

    public Permission getPermission() {
        return permission;
    }

    public void setPermission(Permission permission) {
        this.permission = permission;
    }
}
