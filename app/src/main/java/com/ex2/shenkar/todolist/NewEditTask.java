package com.ex2.shenkar.todolist;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;

public class NewEditTask extends AppCompatActivity {
    private EditText taskDesk;
    private EditText etLoc;
    private Button btnNewTask;
    private Spinner spn_priority;
    private Spinner spn_member;
    private Spinner spn_floor;
    private LatLng latlng = new LatLng(0,0);
    private DatePicker selDate;
    private Button btnDone;
    ArrayAdapter<String> member_adapter;
    private int db_id = 0;
    private int position = 0;
    private String currentStatus = Consts.STATUS_PENDING;
    private HashMap userMailId = new HashMap();

    private Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_task);
        taskDesk=(EditText)findViewById(R.id.taskDesk);
        etLoc = (EditText)findViewById(R.id.etLoc);
        selDate = (DatePicker)findViewById(R.id.calendarView);
        btnNewTask=(Button)findViewById(R.id.btnNewTask);
        btnNewTask.setText("New Task");
        btnDone = (Button)findViewById(R.id.doneButton);

        RegisteredUser.getUser(this, new RegisteredUserCallback() {
            @Override
            public void successful(RegisteredUser user) {

                if (user.getType() == User.Type.MANAGER) {
                    Team.getTeam(user.getTeamId(), NewEditTask.this, new TeamLoadCallback() {

                        @Override
                        public void successful(Team team) {
                            ArrayList members_array = new ArrayList();

                            for (RegisteredUser u : team.getTeamMates()){
                                members_array.add(u.getEmail());
                                userMailId.put(u.getEmail(),u.getId());
                            }

                            member_adapter = new ArrayAdapter<String>(NewEditTask.this,
                                    android.R.layout.simple_spinner_item, members_array);

                            spn_member.setAdapter(member_adapter);
                            member_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        }

                        @Override
                        public void failed() {

                        }
                    });
                }
            }

            @Override
            public void failed() {

            }
        });

//        Priority Spinner
        spn_priority = (Spinner) findViewById(R.id.spn_priority);
        ArrayAdapter<CharSequence> priority_adapter = ArrayAdapter.createFromResource(this,
                R.array.priority_array, android.R.layout.simple_spinner_item);
        priority_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spn_priority.setAdapter(priority_adapter);

//      Members Spinner
        spn_member = (Spinner) findViewById(R.id.member);



//      Floor Spinner
        spn_floor = (Spinner) findViewById(R.id.spn_floor);
        ArrayAdapter<CharSequence> floor_adapter = ArrayAdapter.createFromResource(this,
                R.array.floors_array, android.R.layout.simple_spinner_item);
        floor_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spn_floor.setAdapter(floor_adapter);


        // check if for edit
        final Task existingTask;
        final boolean isForEdit = getIntent().hasExtra("task");
        if (isForEdit) {
            existingTask = (Task)getIntent().getSerializableExtra("task");
            int spinnerPosition = priority_adapter.getPosition(existingTask.getPriority());
            spn_priority.setSelection(spinnerPosition);
//            spinnerPosition = member_adapter.getPosition(existingTask.getMember());
//            spn_member.setSelection(spinnerPosition);
            spinnerPosition = floor_adapter.getPosition(existingTask.getFloor());
            spn_floor.setSelection(spinnerPosition);
            etLoc.setText(existingTask.getAddress());
            taskDesk.setText(existingTask.getTask());

            long dv = Long.valueOf(existingTask.getDate().toString())*1000;// its need to be in milisecond
            Date df = new java.util.Date(dv);
            selDate.updateDate(df.getYear(), df.getMonth(), df.getDay());

            db_id = existingTask.getID();
            position = getIntent().getIntExtra("position", position);
            btnNewTask.setText("Update");
            btnDone.setVisibility(View.VISIBLE);

            if (existingTask.getStatus().equals(Consts.STATUS_DONE)){
                btnDone.setText("Reopen");
                currentStatus = Consts.STATUS_DONE;
            }else{
                btnDone.setText("DONE");
                currentStatus = Consts.STATUS_PROGRESS;
            }
        }

        etLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent newMapIntent = new Intent(NewEditTask.this, MapsActivity.class);
                startActivityForResult(newMapIntent,Consts.GET_LOCATION);
            }
        });

        // new or edit
        btnNewTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent();
                intent.putExtra("ACTION",(isForEdit) ? 1 : 0);
                intent.putExtra("ID", db_id);
                intent.putExtra("TASK", taskDesk.getText().toString());
                intent.putExtra("PRIORITY", spn_priority.getSelectedItem().toString());
                intent.putExtra("LAT", latlng.latitude);
                intent.putExtra("LNG", latlng.longitude);
                intent.putExtra("LOCATION", etLoc.getText().toString());
                intent.putExtra("MEMBER_ID",userMailId.get(spn_member.getSelectedItem().toString()).toString());
                intent.putExtra("FLOOR", spn_floor.getSelectedItem().toString());
                Calendar date = new GregorianCalendar(selDate.getYear(), selDate.getMonth(), selDate.getDayOfMonth());
                intent.putExtra("DATE", date.getTimeInMillis());
                intent.putExtra("STATUS",currentStatus);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });

        context = this;

        btnDone.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View arg0) {
                if (!currentStatus.equals(Consts.STATUS_DONE)){
                    btnDone.setText("Reopen");
                    currentStatus = Consts.STATUS_DONE;
                }else{
                    btnDone.setText("DONE");
                    currentStatus = Consts.STATUS_PROGRESS;
                }

//                String sql = String.format("UPDATE %s SET %s = '%s' WHERE %s = %d",
//                        TaskContract.TABLE,
//                        TaskContract.Columns.STATUS,
//                        Consts.STATUS_DONE,
//                        TaskContract.Columns._ID,
//                        db_id);
//                Toast.makeText(context, "asd", Toast.LENGTH_LONG).show();
//                TaskDBHelper helper = new TaskDBHelper(getApplicationContext());
//                SQLiteDatabase sqlDB = helper.getWritableDatabase();
//                sqlDB.execSQL(sql);
            }
        });
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case (Consts.GET_LOCATION) : {
                Long lat = data.getLongExtra("lat",1);
                Long lng = data.getLongExtra("lng", 1);
                latlng = new LatLng(lat,lng);
                etLoc.setText(data.getStringExtra("location"));
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }
}
