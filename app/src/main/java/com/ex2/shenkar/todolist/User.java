package com.ex2.shenkar.todolist;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by shnizle on 1/2/2016.
 */
public class User {

    public static enum Type {MANAGER, TEAMMATE};

    private static String KEY_TYPE = "type";
    private static String KEY_TEAMID = "teamid";

    private Type type;
    private String email;
    private String mobile;

    private int teamId;

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

    public User(Type type, String email, String mobile) {

        setType(type);

        setEmail(email);
        setMobile(mobile);
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Type getType() {
        return type;

    }

    public int getTeamId() {
        return teamId;
    }

    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }
}

