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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Collections;
import java.util.Comparator;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by shnizle on 3/15/2016.
 */
public class TasksFragment extends Fragment {

    private View rootView;
    private ListView lv;
    private Context context;
    private CustomAdapter myListAdapter;
    private ArrayList<Task> adapterTasks = new ArrayList<Task>();
    private Button btn;
    private TaskDBHelper helper;

    private String currentStatus = Consts.STATUS_ALL;

    private TextView txtAll;
    private TextView txtPending;

    private TextView txtPrgs;

    private Switch swtOrder;
    private TextView txtOrder;
    private Switch swtSort;
    private TextView txtSort;


    private Welcome mainActivity;

    private ArrayList<Task> tasks = new ArrayList<Task>();


//    private Cursor cursor;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.activity_main, container, false);
        mainActivity = (Welcome) getActivity();

        txtAll=(TextView)rootView.findViewById(R.id.txtAll);
        txtPending=(TextView)rootView.findViewById(R.id.txtPending);
        txtPrgs=(TextView)rootView.findViewById(R.id.txtPrgs);
        mainActivity = (Welcome) getActivity();
        myListAdapter = new CustomAdapter(mainActivity, adapterTasks);


        int refreshInterval = mainActivity.getUser().getSyncIntervalDelay()*10000;

        setActiveList();

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                getAllTasksFromDB();
            }
        }, 0, refreshInterval);

        getAllTasksFromDB();

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

        swtOrder = (Switch)rootView.findViewById(R.id.swtOrder);
        swtSort = (Switch)rootView.findViewById(R.id.swtSort);
        txtOrder = (TextView)rootView.findViewById(R.id.txtOrder);
        txtSort = (TextView)rootView.findViewById(R.id.txtSort);

        swtOrder.setChecked(false);
        swtOrder.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean bChecked) {
                if (!bChecked) {
                    txtOrder.setText("priority");
                } else {
                    txtOrder.setText("date");
                }
                setTaskList();
            }
        });

        swtSort.setChecked(false);
        swtSort.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean bChecked) {
                if (!bChecked) {
                    txtSort.setText("DESC");
                } else {
                    txtSort.setText("ASC");
                }
                setTaskList();
            }
        });

        return rootView;
    }

    public void getAllTasksFromDB(){
        int user_id = mainActivity.getUser().getId();
        String query = "MODEL=TasksAndMembers&COMMAND=view" + ((mainActivity.getUser().getType() == User.Type.MANAGER) ?
                "&filters[manager_id]="+String.valueOf(user_id) : "&filters[member]="+String.valueOf(user_id));

        GetRequest.send(query, getContext(), new GetRequestCallback() {
            @Override
            public void success(JSONObject jsonObject) {

                try {
                    if (jsonObject.getBoolean("status")) {
                        tasks.clear();
                        JSONArray taskList = jsonObject.getJSONArray("data");
                        for (int i = 0; i < taskList.length(); i++) {
                            JSONObject jo = taskList.getJSONObject(i);
                            tasks.add(new Task(
                                    jo.getInt("id"),
                                    jo.getString("task"),
                                    jo.getString("priority"),
                                    jo.getDouble("lat"),
                                    jo.getDouble("lng"),
                                    jo.getString("location"),
                                    jo.getInt("member"),
                                    jo.getString("member_name"),
                                    jo.getLong("date"),
                                    jo.getString("status"),
                                    jo.getString("floor")
                            ));
                        }

                        setTaskList();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failed(Exception error) {

            }
        });

//        SQLiteDatabase sqlDB = new TaskDBHelper(getActivity()).getWritableDatabase();
//        cursor = sqlDB.query(TaskContract.TABLE,
//                new String[]{TaskContract.Columns.ID,
//                        TaskContract.Columns.TASK,
//                        TaskContract.Columns.PRIORITY,
//                        TaskContract.Columns.LAT,
//                        TaskContract.Columns.LNG,
//                        TaskContract.Columns.LOCATION,
//                        TaskContract.Columns.MEMBER,
//                        TaskContract.Columns.DATE,
//                        TaskContract.Columns.STATUS,
//                        TaskContract.Columns.FLOOR
//                },
//                null,null,null,null,null);

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

    public class DateComparator implements Comparator<Task> {
        @Override
        public int compare(Task o1, Task o2) {
            return o1.getDate().compareTo(o2.getDate());
        }
    }

    public class PriorityComparator implements Comparator<Task> {
        @Override
        public int compare(Task o1, Task o2) {
            String status1 = o1.getStatus();
            String status2 = o2.getStatus();
            if ( status1.equals(status2) ) {
                return 0;
            }
            if (status1.equals(Consts.STATUS_PENDING)){
                return 1;
            }
            if (status1.equals(Consts.STATUS_PROGRESS) && status2.equals(Consts.STATUS_PENDING)){
                return -1;
            }
            if (status1.equals(Consts.STATUS_PROGRESS) && status2.equals(Consts.STATUS_DONE)){
                return 1;
            }

            return -1;
        }
    }

    public void orderTasks(){
        if (swtOrder.isChecked()) {// meaning date
            Collections.sort(adapterTasks, new DateComparator());
        }else{ //meaning priority
            Collections.sort(adapterTasks, new PriorityComparator());
        }

        if (swtSort.isChecked()){
            Collections.reverse(adapterTasks);
        }
    }

    public void setTaskList(){
        adapterTasks.clear();
//        cursor.moveToFirst();
        for (Task t : tasks){
//        while(cursor.moveToNext()) {
//            currentTask = new Task(
//                    cursor.getInt(cursor.getColumnIndexOrThrow(TaskContract.Columns.ID)),
//                    cursor.getString(cursor.getColumnIndexOrThrow(TaskContract.Columns.TASK)),
//                    cursor.getString(cursor.getColumnIndexOrThrow(TaskContract.Columns.PRIORITY)),
//                    cursor.getDouble(cursor.getColumnIndexOrThrow(TaskContract.Columns.LAT)),
//                    cursor.getDouble(cursor.getColumnIndexOrThrow(TaskContract.Columns.LNG)),
//                    cursor.getString(cursor.getColumnIndexOrThrow(TaskContract.Columns.LOCATION)),
//                    cursor.getString(cursor.getColumnIndexOrThrow(TaskContract.Columns.MEMBER)),
//                    cursor.getLong(cursor.getColumnIndexOrThrow(TaskContract.Columns.DATE)),
//                    cursor.getString(cursor.getColumnIndexOrThrow(TaskContract.Columns.STATUS)),
//                    cursor.getString(cursor.getColumnIndexOrThrow(TaskContract.Columns.FLOOR))
//            );

            if (currentStatus.equals(Consts.STATUS_ALL)||t.getStatus().equals(currentStatus)){
                adapterTasks.add(t);
            }
            orderTasks();
        }
        myListAdapter.notifyDataSetChanged();
        context=getContext();
        lv=(ListView) rootView.findViewById(R.id.list);
        lv.setAdapter(myListAdapter);
        lv.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                Intent editTaskIntent = new Intent(context, NewEditTask.class);
                editTaskIntent.putExtra("position", position);
                editTaskIntent.putExtra("task", adapterTasks.get(position));
                startActivityForResult(editTaskIntent, Consts.EDIT_TASK_CODE);

            }
        });
    }

    public void setRecordToDB(Intent data){
        int action = data.getIntExtra("ACTION",0);
        String task=data.getStringExtra("TASK");
        String priority=data.getStringExtra("PRIORITY");
        Double lat = data.getDoubleExtra("LAT", 1);
        Double lng = data.getDoubleExtra("LNG", 1);
        String location = data.getStringExtra("LOCATION");
        String member_id = data.getStringExtra("MEMBER_ID");
        String floor = data.getStringExtra("FLOOR");
        Long selectedDate = data.getLongExtra("DATE", 1);
        String status = data.getStringExtra("STATUS");

        String model = "MODEL=Tasks";
        String command;
        String idAttr;
        if (action == 1){ //action 0 - new; action 1 - update
            int id = data.getIntExtra("ID", 0);
            command = "&COMMAND=update";
            idAttr = "&filters[id]="+id;
        }else{
            command = "&COMMAND=add";
            idAttr = "";
        }
        String query = model+command+idAttr+
                "&attrs[task]="+task+
                "&attrs[member]="+member_id+
                "&attrs[priority]="+priority+
                "&attrs[lat]="+lat+
                "&attrs[lng]="+lng+
                "&attrs[location]="+location+
                "&attrs[date]="+selectedDate+
                "&attrs[status]="+status+
                "&attrs[floor]="+floor;
        Log.d("mesch", query);
        GetRequest.send(query, getContext(), new GetRequestCallback() {
            @Override
            public void success(JSONObject jsonObject) {}

            @Override
            public void failed(Exception error) {}
        });
//        ContentValues values = new ContentValues();
//        values.put(TaskContract.Columns.TASK, task);
//        values.put(TaskContract.Columns.PRIORITY, priority);
//        values.put(TaskContract.Columns.LAT, lat);
//        values.put(TaskContract.Columns.LNG, lng);
//        values.put(TaskContract.Columns.LOCATION, location);
//        values.put(TaskContract.Columns.MEMBER, member);
//        values.put(TaskContract.Columns.FLOOR, floor);
//        values.put(TaskContract.Columns.DATE, selectedDate);
//        values.put(TaskContract.Columns.STATUS, status);
//        return values;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

//        ContentValues values;
//        helper = new TaskDBHelper(getActivity());
//        SQLiteDatabase db = helper.getWritableDatabase();

//        values = setRecordToDB(data);
        setRecordToDB(data);
        getAllTasksFromDB();
//        switch(requestCode) {
//            case (Consts.NEW_TASK_CODE) : {
//                if (resultCode == Activity.RESULT_OK) {
//                    db.insertWithOnConflict(TaskContract.TABLE, null, values,
//                            SQLiteDatabase.CONFLICT_IGNORE);
//
//                    getAllTasksFromDB();
//                }
//                break;
//            }
//            case (Consts.EDIT_TASK_CODE) : {
//                if (resultCode == Activity.RESULT_OK) {
//                    db.update(TaskContract.TABLE, values, TaskContract.Columns.ID + " = "
//                            + id, null);
//                    getAllTasksFromDB();
//                }
//                break;
//            }
//        }
    }
}
