package com.example.chatapp;

public class UserObject {
    private String name, phone;

    public UserObject(String name, String phone){
        this.name = name;
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public void setName(String name){
        this.name = name;
    }
    
}
