package com.ex2.shenkar.todolist;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Meschiany on 12/4/15.
 */
public class TaskDBHelper extends SQLiteOpenHelper {

    public TaskDBHelper(Context context) {
        super(context, TaskContract.DB_NAME, null, TaskContract.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqlDB) {
        String sqlQuery =
                String.format("CREATE TABLE %s (" +
                                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                "%s TEXT, " + //task
                                "%s TEXT, " + //priority
                                "%s INTEGER, " + //lat
                                "%s INTEGER, " + //lnt
                                "%s TEXT, " + //location
                                "%s TEXT, " + //member
                                "%s INTEGER, " + //date
                                "%s TEXT " + //status
                                    ")", TaskContract.TABLE,
                        TaskContract.Columns.TASK,
                        TaskContract.Columns.PRIORITY,
                        TaskContract.Columns.LAT,
                        TaskContract.Columns.LNG,
                        TaskContract.Columns.LOCATION,
                        TaskContract.Columns.MEMBER,
                        TaskContract.Columns.DATE,
                        TaskContract.Columns.STATUS
                );

        Log.d("TaskDBHelper", "Query to form table: " + sqlQuery);
        sqlDB.execSQL(sqlQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqlDB, int oldVer, int newVer) {
        Log.d("mesch","Version change "+TaskContract.DB_VERSION);
        sqlDB.execSQL("DROP TABLE IF EXISTS "+ TaskContract.TABLE);
        onCreate(sqlDB);
    }
}