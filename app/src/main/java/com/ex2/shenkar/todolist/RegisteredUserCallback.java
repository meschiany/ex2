package com.ex2.shenkar.todolist;

/**
 * Created by shnizle on 2/28/2016.
 */
public abstract class RegisteredUserCallback {
    abstract public void successful(RegisteredUser user);
    abstract public void failed();
}