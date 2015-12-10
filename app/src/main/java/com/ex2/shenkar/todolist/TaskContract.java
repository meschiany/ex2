package com.ex2.shenkar.todolist;

import android.provider.BaseColumns;

/**
 * Created by Meschiany on 12/5/15.
 */
public class TaskContract {
    public static final String DB_NAME = "com.example.TodoList.db.tasks";
    public static final int DB_VERSION = 1;
    public static final String TABLE = "tasks";

    public class Columns {
        public static final String TASK = "task";
        public static final String _ID = BaseColumns._ID;
    }
}