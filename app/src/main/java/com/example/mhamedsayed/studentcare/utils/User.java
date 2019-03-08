package com.example.mhamedsayed.studentcare.utils;

/**
 * Created by mhamedsayed on 3/7/2019.
 */

public class User {
    private String userId, name, age, level, email, password, state, type, work;

    public User() {
    }

    public User(String name, String age, String level, String email, String password, String state, String type, String work) {
        this.name = name;
        this.age = age;
        this.level = level;
        this.email = email;
        this.password = password;
        this.state = state;
        this.type = type;
        this.work = work;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getWork() {
        return work;
    }

    public void setWork(String work) {
        this.work = work;
    }


}
