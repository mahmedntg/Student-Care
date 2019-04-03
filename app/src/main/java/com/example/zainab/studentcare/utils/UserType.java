package com.example.zainab.studentcare.utils;

/**
 * Created by mhamedsayed on 3/8/2019.
 */

public enum UserType {
    STUDENT("Student"), DONOR("Donor"),ADMIN("Admin");
    private String value;

    UserType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

