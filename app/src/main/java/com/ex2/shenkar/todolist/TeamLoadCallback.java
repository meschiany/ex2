package com.ex2.shenkar.todolist;

/**
 * Created by shnizle on 2/28/2016.
 */
public abstract class TeamLoadCallback {
    abstract public void successful(Team team);
    abstract public void failed();
}
