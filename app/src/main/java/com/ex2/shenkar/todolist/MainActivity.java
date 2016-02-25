package com.ex2.shenkar.todolist;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    private ListView lv;
    private Context context;
    private CustomAdapter myListAdapter;
    private ArrayList<Task> prgmNameList = new ArrayList<Task>();
    private Button btn;
    private TaskDBHelper helper;

    private String currentStatus = Consts.STATUS_ALL;

    private TextView txtAll;
    private TextView txtPending;

    private TextView txtPrgs;
    private Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtAll=(TextView)findViewById(R.id.txtAll);
        txtPending=(TextView)findViewById(R.id.txtPending);
        txtPrgs=(TextView)findViewById(R.id.txtPrgs);

        myListAdapter = new CustomAdapter(this, prgmNameList);

        setActiveList();
        refreshLists();

        btn = (Button) findViewById(R.id.fab);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newTaskIntent = new Intent(MainActivity.this, NewEditTask.class);
                startActivityForResult(newTaskIntent, Consts.NEW_TASK_CODE);
            }
        });

        txtPrgs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentStatus = Consts.STATUS_PROGRESS;
                setActiveList();
                setTaskList();
            }
        });
        txtPending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentStatus = Consts.STATUS_PENDING;
                setActiveList();
                setTaskList();
            }
        });
        txtAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentStatus = Consts.STATUS_ALL;
                setActiveList();
                setTaskList();
            }
        });

    }
    public void getAllTasksFromDB(){
        SQLiteDatabase sqlDB = new TaskDBHelper(this).getWritableDatabase();
        cursor = sqlDB.query(TaskContract.TABLE,
                new String[]{TaskContract.Columns.ID,
                        TaskContract.Columns.TASK,
                        TaskContract.Columns.PRIORITY,
                        TaskContract.Columns.LAT,
                        TaskContract.Columns.LNG,
                        TaskContract.Columns.LOCATION,
                        TaskContract.Columns.MEMBER,
                        TaskContract.Columns.DATE,
                        TaskContract.Columns.STATUS,
                        TaskContract.Columns.FLOOR
                },
                null,null,null,null,null);

    }
    public void setActiveList(){
        txtAll.setBackgroundColor(Color.parseColor(Consts.COLOR_MENUBLUE));
        txtPending.setBackgroundColor(Color.parseColor(Consts.COLOR_MENUBLUE));
        txtPrgs.setBackgroundColor(Color.parseColor(Consts.COLOR_MENUBLUE));
        txtAll.setTextColor(Color.BLACK);
        txtPending.setTextColor(Color.BLACK);
        txtPrgs.setTextColor(Color.BLACK);
        switch (currentStatus){
            case Consts.STATUS_ALL:
                txtAll.setBackgroundColor(Color.parseColor(Consts.COLOR_LIGHTBLUE));
                txtAll.setTextColor(Color.WHITE);

                break;
            case Consts.STATUS_PENDING:
                txtPending.setBackgroundColor(Color.parseColor(Consts.COLOR_LIGHTBLUE));
                txtPending.setTextColor(Color.WHITE);
                break;
            case Consts.STATUS_PROGRESS:
                txtPrgs.setBackgroundColor(Color.parseColor(Consts.COLOR_LIGHTBLUE));
                txtPrgs.setTextColor(Color.WHITE);
                break;
        }
    }

    public void setTaskList(){
        prgmNameList.clear();

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
                    cursor.getString(cursor.getColumnIndexOrThrow(TaskContract.Columns.STATUS)),
                    cursor.getString(cursor.getColumnIndexOrThrow(TaskContract.Columns.FLOOR))
            );
            if (currentStatus.equals(Consts.STATUS_ALL)||currentTask.getStatus().equals(currentStatus)){
                prgmNameList.add(currentTask);
            }

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
        String floor = data.getStringExtra("FLOOR");
        Long selectedDate = data.getLongExtra("DATE", 1);
        String status = data.getStringExtra("STATUS");

        ContentValues values = new ContentValues();
        values.put(TaskContract.Columns.TASK, task);
        values.put(TaskContract.Columns.PRIORITY, priority);
        values.put(TaskContract.Columns.LAT, lat);
        values.put(TaskContract.Columns.LNG, lng);
        values.put(TaskContract.Columns.LOCATION, location);
        values.put(TaskContract.Columns.MEMBER, member);
        values.put(TaskContract.Columns.FLOOR, floor);
        values.put(TaskContract.Columns.DATE, selectedDate);
        values.put(TaskContract.Columns.STATUS, status);


        return values;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        ContentValues values;
        helper = new TaskDBHelper(MainActivity.this);
        SQLiteDatabase db = helper.getWritableDatabase();

        values = setRecordToDB(data);
        switch(requestCode) {
            case (Consts.NEW_TASK_CODE) : {
                if (resultCode == Activity.RESULT_OK) {

                    db.insertWithOnConflict(TaskContract.TABLE, null, values,
                            SQLiteDatabase.CONFLICT_IGNORE);

                    refreshLists();
                }
                break;
            }
            case (Consts.EDIT_TASK_CODE) : {
                if (resultCode == Activity.RESULT_OK) {
                    int id = data.getIntExtra("ID", 0);

                    db.update(TaskContract.TABLE, values, TaskContract.Columns.ID + " = "
                            + id, null);

                    refreshLists();

                }
                break;
            }
        }
    }
    public void refreshLists(){
        getAllTasksFromDB();
        setTaskList();
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