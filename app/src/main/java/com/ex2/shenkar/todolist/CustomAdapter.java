package com.ex2.shenkar.todolist;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Meschiany on 11/11/15.
 */
public class CustomAdapter extends BaseAdapter{
    private ArrayList<Task> result;
    private Context context;

    private static LayoutInflater inflater=null;

    public CustomAdapter(MainActivity mainActivity, ArrayList<Task> prgmNameList) {
        result=prgmNameList;
        context=mainActivity;

        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return result.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class Holder {
        TextView task;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        if (convertView == null){
            convertView = inflater.inflate(R.layout.task_view, null);
        }
        Holder holder=new Holder();


        holder.task=(TextView) convertView.findViewById(R.id.taskTextView);

        holder.task.setText(result.get(position).getTask() + " - " + result.get(position).getStatus());
        holder.task.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent editTaskIntent = new Intent(context, NewEditTask.class);
                editTaskIntent.putExtra("position", position);
                editTaskIntent.putExtra("task", result.get(position));
                ((Activity) context).startActivityForResult(editTaskIntent, Consts.EDIT_TASK_CODE);

            }
        });
        return convertView;
    }
}
