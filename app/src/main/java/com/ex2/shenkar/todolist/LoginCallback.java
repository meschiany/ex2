package com.ex2.shenkar.todolist;

/**
 * Created by shnizle on 3/6/2016.
 */
public abstract class LoginCallback {
    public abstract void success(int id);
    public abstract void failed(String error);
}
