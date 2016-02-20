package com.ex2.shenkar.todolist;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    private ListView lv;
    private Context context;
    private CustomAdapter myListAdapter;
    private ArrayList<Task> prgmNameList = new ArrayList<Task>();
    private Button btn;
    private TaskDBHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        myListAdapter = new CustomAdapter(this, prgmNameList);

        setTaskList();

        btn = (Button) findViewById(R.id.fab);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newTaskIntent = new Intent(MainActivity.this, NewEditTask.class);
                startActivityForResult(newTaskIntent, Consts.NEW_TASK_CODE);
            }
        });
    }

    public void setTaskList(){
        prgmNameList.clear();
        SQLiteDatabase sqlDB = new TaskDBHelper(this).getWritableDatabase();
        Cursor cursor = sqlDB.query(TaskContract.TABLE,
                new String[]{TaskContract.Columns.ID,
                        TaskContract.Columns.TASK,
                        TaskContract.Columns.PRIORITY,
                        TaskContract.Columns.LAT,
                        TaskContract.Columns.LNG,
                        TaskContract.Columns.LOCATION,
                        TaskContract.Columns.MEMBER,
                        TaskContract.Columns.DATE,
                        TaskContract.Columns.STATUS
                },
                null,null,null,null,null);

        cursor.moveToFirst();
        Task currentTask;
        while(cursor.moveToNext()) {
            currentTask = new Task(
                    cursor.getInt(cursor.getColumnIndexOrThrow(TaskContract.Columns.ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(TaskContract.Columns.TASK)),
                    cursor.getString(cursor.getColumnIndexOrThrow(TaskContract.Columns.PRIORITY)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(TaskContract.Columns.LAT)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(TaskContract.Columns.LNG)),
                    cursor.getString(cursor.getColumnIndexOrThrow(TaskContract.Columns.LOCATION)),
                    cursor.getString(cursor.getColumnIndexOrThrow(TaskContract.Columns.MEMBER)),
                    cursor.getLong(cursor.getColumnIndexOrThrow(TaskContract.Columns.DATE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(TaskContract.Columns.STATUS))
            );
            prgmNameList.add(currentTask);
        }

        context=this;

        lv=(ListView) findViewById(R.id.list);
        lv.setAdapter(myListAdapter);
    }

    public ContentValues setRecordToDB(Intent data){
        String task=data.getStringExtra("TASK");
        String priority=data.getStringExtra("PRIORITY");
        Double lat = data.getDoubleExtra("LAT", 1);
        Double lng = data.getDoubleExtra("LNG", 1);
        String location = data.getStringExtra("LOCATION");
        String member = data.getStringExtra("MEMBER");
        Long selectedDate = data.getLongExtra("DATE", 1);

        ContentValues values = new ContentValues();
        values.put(TaskContract.Columns.TASK, task);
        values.put(TaskContract.Columns.PRIORITY, priority);
        values.put(TaskContract.Columns.LAT, lat);
        values.put(TaskContract.Columns.LNG, lng);
        values.put(TaskContract.Columns.LOCATION, location);
        values.put(TaskContract.Columns.MEMBER, member);
        values.put(TaskContract.Columns.DATE, selectedDate);

        return values;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Toast.makeText(this, "resultCode: "+resultCode, Toast.LENGTH_LONG).show();
        ContentValues values;
        helper = new TaskDBHelper(MainActivity.this);
        SQLiteDatabase db = helper.getWritableDatabase();
        switch(requestCode) {
            case (Consts.NEW_TASK_CODE) : {
                if (resultCode == Activity.RESULT_OK) {

                    values = setRecordToDB(data);
                    values.put(TaskContract.Columns.STATUS,Consts.STATUS_PENDING);

                    db.insertWithOnConflict(TaskContract.TABLE, null, values,
                            SQLiteDatabase.CONFLICT_IGNORE);

                    setTaskList();
                }
                break;
            }
            case (Consts.EDIT_TASK_CODE) : {
                if (resultCode == Activity.RESULT_OK) {
                    int id = data.getIntExtra("ID", 0);

                    values = setRecordToDB(data);

                    db.update(TaskContract.TABLE, values, TaskContract.Columns.ID + " = "
                            + id, null);

                    setTaskList();
                }
                break;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu,menu);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
//            case R.id.action_add_task:
//                return true;
            default:
                return false;
        }
    }
}