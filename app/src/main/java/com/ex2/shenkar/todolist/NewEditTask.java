package com.ex2.shenkar.todolist;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

public class NewEditTask extends AppCompatActivity {
    private EditText taskDesk;
    private EditText etLoc;
    private Button btnNewTask;
    private Spinner spn_priority;
    private Spinner spn_member;
    private Spinner spn_floor;
    private LatLng latlng = new LatLng(0,0);
    private CalendarView selDate;
    private int db_id = 0;
    private int position = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_task);
        taskDesk=(EditText)findViewById(R.id.taskDesk);
        etLoc = (EditText)findViewById(R.id.etLoc);
        selDate = (CalendarView)findViewById(R.id.calendarView);
        btnNewTask=(Button)findViewById(R.id.btnNewTask);
        btnNewTask.setText("New Task");

//        Priority Spinner
        spn_priority = (Spinner) findViewById(R.id.spn_priority);
        ArrayAdapter<CharSequence> priority_adapter = ArrayAdapter.createFromResource(this,
                R.array.priority_array, android.R.layout.simple_spinner_item);
        priority_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spn_priority.setAdapter(priority_adapter);

//      Members Spinner
        spn_member = (Spinner) findViewById(R.id.member);
        ArrayAdapter<CharSequence> member_adapter = ArrayAdapter.createFromResource(this,
                R.array.members_array, android.R.layout.simple_spinner_item);
        member_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spn_member.setAdapter(member_adapter);

//      Floor Spinner
        spn_floor = (Spinner) findViewById(R.id.spn_floor);
        ArrayAdapter<CharSequence> floor_adapter = ArrayAdapter.createFromResource(this,
                R.array.floors_array, android.R.layout.simple_spinner_item);
        floor_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spn_floor.setAdapter(floor_adapter);


        // check if for edit
        final Task existingTask;
        if (getIntent().hasExtra("task")) {
            existingTask = (Task)getIntent().getSerializableExtra("task");
            int spinnerPosition = priority_adapter.getPosition(existingTask.getPriority());
            spn_priority.setSelection(spinnerPosition);
            spinnerPosition = member_adapter.getPosition(existingTask.getMember());
            spn_member.setSelection(spinnerPosition);
            spinnerPosition = floor_adapter.getPosition(existingTask.getFloor());
            spn_floor.setSelection(spinnerPosition);
            etLoc.setText(existingTask.getAddress());
            taskDesk.setText(existingTask.getTask());
            selDate.setDate(existingTask.getDate());
            db_id = existingTask.getID();
            position = getIntent().getIntExtra("position",position);
            btnNewTask.setText("Update");
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
                intent.putExtra("ID", db_id);
//                intent.putExtra("POSITION", position);
                intent.putExtra("TASK", taskDesk.getText().toString());
                intent.putExtra("PRIORITY", spn_priority.getSelectedItem().toString());
                intent.putExtra("LAT", latlng.latitude);
                intent.putExtra("LNG", latlng.longitude);
                intent.putExtra("LOCATION", etLoc.getText().toString());
                intent.putExtra("MEMBER",spn_member.getSelectedItem().toString());
                intent.putExtra("FLOOR",spn_floor.getSelectedItem().toString());
                intent.putExtra("DATE", selDate.getDate());
                setResult(Activity.RESULT_OK, intent);
                finish();
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
