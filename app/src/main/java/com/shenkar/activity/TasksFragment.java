package com.shenkar.activity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.ex2.shenkar.todolist.Consts;
import com.ex2.shenkar.todolist.CustomAdapter;
import com.ex2.shenkar.todolist.NewEditTask;
import com.ex2.shenkar.todolist.R;
import com.ex2.shenkar.todolist.Task;
import com.ex2.shenkar.todolist.TaskContract;
import com.ex2.shenkar.todolist.TaskDBHelper;
import com.ex2.shenkar.todolist.User;
import com.ex2.shenkar.todolist.Welcome;
import com.shenkar.tools.GetRequest;
import com.shenkar.tools.GetRequestCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by shnizle on 3/15/2016.
 */
public class TasksFragment extends Fragment {

    private View rootView;
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
    private Welcome mainActivity;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.activity_main, container, false);

        txtAll=(TextView)rootView.findViewById(R.id.txtAll);
        txtPending=(TextView)rootView.findViewById(R.id.txtPending);
        txtPrgs=(TextView)rootView.findViewById(R.id.txtPrgs);

        myListAdapter = new CustomAdapter(getActivity(), prgmNameList);

        setActiveList();
        refreshLists();

        btn = (Button) rootView.findViewById(R.id.fab);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newTaskIntent = new Intent(getActivity(), NewEditTask.class);
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

        mainActivity = (Welcome) getActivity();

        return rootView;
    }

    public void getAllTasksFromDB(){

        int user_id = mainActivity.getUser().getId();

        String query = "MODEL=Tasks&COMMAND=view" + ((mainActivity.getUser().getType() == User.Type.MANAGER) ?
                "&filters[manager_id]="+String.valueOf(user_id) : "&filters[member]="+String.valueOf(user_id));

        GetRequest.send(query, getContext(), new GetRequestCallback() {
            @Override
            public void success(JSONObject jsonObject) {

                try {
                    if (jsonObject.getBoolean("status")) {

                        JSONArray taskList = jsonObject.getJSONArray("data");

                    }
                }catch(JSONException e){

                }
            }

            @Override
            public void failed(Exception error) {

            }
        });

        SQLiteDatabase sqlDB = new TaskDBHelper(getActivity()).getWritableDatabase();
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

        context=getContext();
        lv=(ListView) rootView.findViewById(R.id.list);
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
        helper = new TaskDBHelper(getActivity());
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
}
