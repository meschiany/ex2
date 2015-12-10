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
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ListView lv;
    private Context context;
    private CustomAdapter myListAdapter;
    private ArrayList<String> prgmNameList = new ArrayList<String>();
    private Button btn;
    private TaskDBHelper helper;

    static final int GET_NEW_TASK = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SQLiteDatabase sqlDB = new TaskDBHelper(this).getWritableDatabase();
        Cursor cursor = sqlDB.query(TaskContract.TABLE,
                new String[]{TaskContract.Columns.TASK},
                null,null,null,null,null);

        cursor.moveToFirst();
        while(cursor.moveToNext()) {
            prgmNameList.add(cursor.getString(cursor.getColumnIndexOrThrow(TaskContract.Columns.TASK)));
        }

        context=this;

        lv=(ListView) findViewById(R.id.list);
        myListAdapter = new CustomAdapter(this, prgmNameList);
        lv.setAdapter(myListAdapter);

        btn = (Button) findViewById(R.id.fab);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newTaskIntent = new Intent(MainActivity.this, AddNewTask.class);
                startActivityForResult(newTaskIntent, GET_NEW_TASK);
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case (GET_NEW_TASK) : {
                if (resultCode == Activity.RESULT_OK) {
                    String task=data.getStringExtra("TASK");
                    prgmNameList.add(task);
                    myListAdapter.notifyDataSetChanged();

                    helper = new TaskDBHelper(MainActivity.this);
                    SQLiteDatabase db = helper.getWritableDatabase();
                    ContentValues values = new ContentValues();

                    values.clear();
                    values.put(TaskContract.Columns.TASK,task);

                    db.insertWithOnConflict(TaskContract.TABLE,null,values,
                            SQLiteDatabase.CONFLICT_IGNORE);

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
