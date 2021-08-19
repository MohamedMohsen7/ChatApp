package com.example.chatapp.User;

public class UserObject {
    private String name, phone, uid;

    public UserObject(String uid, String name, String phone){
        this.uid = uid;
        this.name = name;
        this.phone = phone;
    }

    public String getUid(){
        return uid;
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
