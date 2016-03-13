package com.shenkar.managy.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.ex2.shenkar.todolist.ContactListItem;
import com.ex2.shenkar.todolist.R;
import com.ex2.shenkar.todolist.RegisteredUser;
import com.ex2.shenkar.todolist.Team;
import com.shenkar.tools.GetRequest;
import com.shenkar.tools.GetRequestCallback;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by shnizle on 1/14/2016.
 */
public class TeammateListAdapter extends BaseAdapter {

    //private ArrayList<RegisteredUser> contactListItems;
    private LayoutInflater mInflater;
    private Context context;
    private Team team;

    public TeammateListAdapter(Context context, Team team){

        updateTeam(team);
        this.context = context;
        //this.contactListItems = contactListItems;
        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void updateTeam(Team team){

        this.team = team;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return team.getTeamMates().size();
    }

    @Override
    public Object getItem(int position) {
        return team.getTeamMates().get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if(convertView == null){

            convertView = mInflater.inflate(R.layout.teammate_item, null);
        }

        TextView nameTv = (TextView) convertView.findViewById(R.id.teammateName);

        nameTv.setText(team.getTeamMates().get(position).getEmail());

        Button deleteBtn = (Button) convertView.findViewById(R.id.removeTeammateButton);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetRequest.send("MODEL=Users&COMMAND=delete&filters[id]=" + String.valueOf(team.getTeamMates().get(position).getId()),
                        context, new GetRequestCallback() {
                            @Override
                            public void success(JSONObject jsonObject) {
                                team.getTeamMates().remove(position);
                                notifyDataSetChanged();
                            }

                            @Override
                            public void failed(Exception error) {
                                Toast.makeText(context, "delete failed, please check connection", Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });

        return convertView;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}
