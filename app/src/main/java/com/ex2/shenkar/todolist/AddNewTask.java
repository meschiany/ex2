package com.ex2.shenkar.todolist;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class AddNewTask extends AppCompatActivity {
    private EditText editText1;
    private Button button1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_task);
        editText1=(EditText)findViewById(R.id.editText1);
        button1=(Button)findViewById(R.id.btnNewTask);
        button1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                String task=editText1.getText().toString();
                Intent intent=new Intent();
                intent.putExtra("TASK",task);
                setResult(Activity.RESULT_OK,intent);
                finish();//finishing activity
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.second, menu);
        return false;
    }
}
