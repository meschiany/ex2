package com.ex2.shenkar.todolist;

/**
 * Created by shnizle on 1/14/2016.
 */
public class Contact {

    private String email;
    private String mobile;
    private String name;

    public Contact(String email, String mobile, String name) {
        this.email = email;
        this.mobile = mobile;
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
