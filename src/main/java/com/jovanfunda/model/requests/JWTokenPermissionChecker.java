package com.jovanfunda.model.requests;

import com.jovanfunda.model.enums.Permission;

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

    @Override
    public String toString() {
        return "JWTokenPermissionChecker{" +
                "jwtoken='" + jwtoken + '\'' +
                ", permission=" + permission +
                '}';
    }
}
