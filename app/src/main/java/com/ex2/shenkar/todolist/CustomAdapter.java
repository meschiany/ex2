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
        Button btn;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        if (convertView == null){
            convertView = inflater.inflate(R.layout.task_view, null);
        }
        Holder holder=new Holder();

        if (convertView != null){
            holder.btn=(Button) convertView.findViewById(R.id.doneButton);
            holder.task=(TextView) convertView.findViewById(R.id.taskTextView);
        }
        holder.task.setText(result.get(position).getTask());
        holder.task.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {

                Intent editTaskIntent = new Intent(context, NewEditTask.class);
                Log.d("mesch", String.valueOf(result.get(position).getID()));
                editTaskIntent.putExtra("position", position);
                editTaskIntent.putExtra("task", result.get(position));
                ((Activity) context).startActivityForResult(editTaskIntent, Consts.EDIT_TASK_CODE);
                return false;
            }
        });

        holder.btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("mesch", result.get(position).getTask());

                String task = result.get(position).getTask();
                Toast.makeText(context, "You finished " + task, Toast.LENGTH_LONG).show();
                Log.d("mesch", task);

                String sql = String.format("DELETE FROM %s WHERE %s = '%s'",
                        TaskContract.TABLE,
                        TaskContract.Columns.TASK,
                        task);


                TaskDBHelper helper = new TaskDBHelper(context);
                SQLiteDatabase sqlDB = helper.getWritableDatabase();
                sqlDB.execSQL(sql);

                result.remove(position);

                notifyDataSetChanged();
            }
        });
        return convertView;
    }
}
