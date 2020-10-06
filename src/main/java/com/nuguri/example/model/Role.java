package com.nuguri.example.model;

public enum Role {

    USER, ADMIN;

    public static final String PREFIX = "ROLE_";

    public String getFullName() {
        return PREFIX + name();
    }

}
