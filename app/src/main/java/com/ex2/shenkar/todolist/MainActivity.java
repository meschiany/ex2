package com.ex2.shenkar.todolist;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private ListView lv;
    private Context context;
    private CustomAdapter myListAdapter;
    private ArrayList<String> prgmNameList = new ArrayList<String>();
    private Button btn;

    static final int GET_NEW_TASK = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prgmNameList.add("Change dimensios to relative");
        prgmNameList.add("fix request codes");
        prgmNameList.add("fix response codes");
        prgmNameList.add("design app");
        prgmNameList.add("get 100 in course");
        prgmNameList.add("buy flowers for wife");
        prgmNameList.add("take dog out");
        prgmNameList.add("fill 20 tasks");
        prgmNameList.add("clean house");
        prgmNameList.add("new shoes");
        prgmNameList.add("download last ep");
        prgmNameList.add("submit task");
        prgmNameList.add("fix bug");
        prgmNameList.add("work");
        prgmNameList.add("think of task");
        prgmNameList.add("why 20???");
        prgmNameList.add("mock 17");
        prgmNameList.add("is a real task");
        prgmNameList.add("almost there");
        prgmNameList.add("Last one");


        context=this;

        lv=(ListView) findViewById(R.id.list);
        myListAdapter = new CustomAdapter(this, prgmNameList);
        lv.setAdapter(myListAdapter);
        btn = (Button) findViewById(R.id.fab);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newTaskIntent = new Intent(MainActivity.this, AddNewTask.class );
                startActivityForResult(newTaskIntent, GET_NEW_TASK);
            }
        });

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("mesch", String.valueOf(resultCode));
        Log.d("mesch",String.valueOf(requestCode));
        switch(requestCode) {
            case (GET_NEW_TASK) : {
                if (resultCode == Activity.RESULT_OK) {
                    String task=data.getStringExtra("TASK");
                    prgmNameList.add(task);
                    myListAdapter.notifyDataSetChanged();
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
