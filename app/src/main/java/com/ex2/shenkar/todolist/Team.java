package com.ex2.shenkar.todolist;

import android.content.Context;
import android.util.Log;

import com.parse.GetCallback;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.shenkar.tools.GetRequest;
import com.shenkar.tools.GetRequestCallback;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.ContentHandler;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by shnizle on 1/13/2016.
 */
public class Team {

    private ArrayList<RegisteredUser> teamMates;
    private int teamId;

    private String name;

    private Team(int id, String name, ArrayList<RegisteredUser> teamMates){

        teamId = id;
        this.teamMates = teamMates;
        setName(name);
    }
    public static void getTeam(final int teamId, final Context context, final TeamLoadCallback callback){

        GetRequest.send("MODEL=Teams&COMMAND=view&filters[id]=" + String.valueOf(teamId), context, new GetRequestCallback() {
            @Override
            public void success(JSONObject jsonObject) {

                try {

                    JSONArray users = jsonObject.getJSONArray("data");
                    JSONObject jo = users.getJSONObject(0);

                    /**
                     * get all members of this team
                     */

                    Team team = new Team(jo.getInt("id"), jo.getString("name"), new ArrayList<RegisteredUser>());

                    /**
                     * get list of team members
                     */
                    Team.getTeamMembers(team, callback, context);


                } catch (Exception e) {
                    e.printStackTrace();
                    callback.failed();
                }
            }

            @Override
            public void failed(Exception error) {
                error.printStackTrace();
                callback.failed();
            }
        });
    }

    public void reloadTeamMembers(Context context ,final TeamLoadCallback callback){

        this.teamMates = new ArrayList<>();

        getTeamMembers(this, callback, context);
    }

    private static void getTeamMembers(final Team team, final TeamLoadCallback callback, final Context context){

        GetRequest.send("MODEL=Users&COMMAND=view&filters[team_id]=" + String.valueOf(team.getTeamId()), context, new GetRequestCallback() {
            @Override
            public void success(JSONObject jsonObject) {

                try {

                    JSONArray users = jsonObject.getJSONArray("data");
                    for(int i=0; i < users.length(); i++){

                        JSONObject jo = users.getJSONObject(i);

                        // don't track manager as teammate
                        if(jo.getString("type").equals("MANAGER"))
                            continue;

                        team.getTeamMates().add(new RegisteredUser(jo.getInt("id"),
                                jo.getString("email"),
                                "",
                                User.Type.TEAMMATE, context));
                    }

                    callback.successful(team);

                } catch (Exception e) {
                    e.printStackTrace();
                    callback.failed();
                }
            }

            @Override
            public void failed(Exception error) {
                error.printStackTrace();
                callback.failed();
            }
        });
    }

    public void updateTeamName(final String newName, Context context, final GetRequestCallback callback){

        GetRequest.send("MODEL=Teams&COMMAND=update&filters[id]=" + String.valueOf(getTeamId()) + "&attrs[name]=" + newName, context,
                new GetRequestCallback() {
                    @Override
                    public void success(JSONObject jsonObject) {

                        name = newName;
                        callback.success(jsonObject);
                    }

                    @Override
                    public void failed(Exception error) {

                        callback.failed(error);
                    }
                });
    }

    public int getTeamId() {
        return teamId;
    }

    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }

    public ArrayList<RegisteredUser> getTeamMates() {
        return teamMates;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTeamMates(ArrayList<RegisteredUser> teamMates) {
        this.teamMates = teamMates;
    }
}
