package com.example.zainab.studentcare.utils;

import java.io.Serializable;

/**
 * Created by mhamedsayed on 3/8/2019.
 */

public class StudentRequest  implements Serializable {
    private String name, description, status,key,deptAmount,userId;

    public StudentRequest() {
    }

    public StudentRequest(String name, String description, String status, String deptAmount) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.deptAmount = deptAmount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDeptAmount() {
        return deptAmount;
    }

    public void setDeptAmount(String deptAmount) {
        this.deptAmount = deptAmount;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
