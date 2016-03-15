package com.ex2.shenkar.todolist;

import android.provider.BaseColumns;

/**
 * Created by Meschiany on 12/5/15.
 */
public class TaskContract {
    public static final String DB_NAME = "com.example.TodoList.db.tasks";
    public static final int DB_VERSION = 10;
    public static final String TABLE = "tasks";

    public class Columns {
        public static final String ID = "_id";
        public static final String TASK = "task";
        public static final String PRIORITY = "priority";
        public static final String LAT= "lat";
        public static final String LNG= "lng";
        public static final String LOCATION= "location";
        public static final String MEMBER= "member";
        public static final String DATE= "date";
        public static final String STATUS= "status";
        public static final String FLOOR= "FLOOR";

        public static final String _ID = BaseColumns._ID;
    }
}
