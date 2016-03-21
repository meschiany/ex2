package com.ex2.shenkar.todolist;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.shenkar.tools.GetRequest;
import com.shenkar.tools.GetRequestCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by shnizle on 1/2/2016.
 */
public class RegisteredUser extends User{

    private static String KEY_ID = "id";
    private static String KEY_SYNC_INTERVAL_DELAY = "KEY_SYNC_INTERVAL_DELAY";
    private static int DEFAULT_SYNC_INTERVAL = 5;
    private static RegisteredUser localUser;
    private SharedPreferences cache;
    private int teamId;
    private int id;
    private Context context;

    public RegisteredUser(int id, String email, String mobile, Type type, Context context) throws Exception{

        super(type, email, mobile);

        setId(id);

        cache = context.getSharedPreferences("User", 0);
    }

    public static void getUser(final Context context, final RegisteredUserCallback callback){

        if(localUser == null) {

            try {
                final int cached_id = context.getSharedPreferences("User", 0).getInt(KEY_ID, 0);
                if (cached_id == 0) {

                    callback.failed();

                }

                Log.i("User", "loading user #"+String.valueOf(cached_id));
                /**
                 * get remote user
                 */
                GetRequest.send("MODEL=Users&COMMAND=view&filters[id]=" + String.valueOf(cached_id), context, new GetRequestCallback() {
                    @Override
                    public void success(JSONObject jsonObject) {

                        try {

                            JSONArray users = jsonObject.getJSONArray("data");
                            JSONObject jo = users.getJSONObject(0);

                            localUser = new RegisteredUser(jo.getInt("id"),
                                    jo.getString("email"),
                                    "",
                                    jo.getString("type").equals("MANAGER") ? Type.MANAGER : Type.TEAMMATE, context);

                            localUser.setTeamId(jo.getInt("team_id"));

                            callback.successful(localUser);

                        } catch (Exception e) {
                            Log.e("User", e.getStackTrace().toString());
                            callback.failed();
                        }
                    }

                    @Override
                    public void failed(Exception error) {
                        callback.failed();
                    }
                });
            }catch(Exception e){
                callback.failed();
            }

        }else{

            callback.successful(localUser);
        }

    }

    public static void save(Context context, int id){

        if(id==0)localUser=null;

        context.getSharedPreferences("User",0).edit().putInt(KEY_ID, id)
                .commit();
    }

    public static void login(String email, String pass, final Context context, final LoginCallback callback){

        /**
         * get remote user
         */
        GetRequest.send("MODEL=Users&COMMAND=view&filters[email]=" + email+"&filters[pass]="+pass, context, new GetRequestCallback() {

            @Override
            public void success(JSONObject jsonObject) {

                try {

                    JSONArray users = jsonObject.getJSONArray("data");
                    JSONObject jo = users.getJSONObject(0);

                    // save local token
                    RegisteredUser.save(context, jo.getInt("id"));

                    callback.success(jo.getInt("id"));

                }catch(JSONException e){

                    e.printStackTrace();
                    callback.failed("Login failed, please try again");
                }
            }

            @Override
            public void failed(Exception error) {

                error.printStackTrace();
                callback.failed("Connection error");
            }
        });
    }

    public int getTeamId() {
        return teamId;
    }

    public void setTeamId(int teamId){

        this.teamId = teamId;
    }

    public int getId() {
        return id;
    }

    protected void setId(int id) {
        this.id = id;
    }

    public void setSyncIntervalDelay(int delay){

        cache.edit().putInt(KEY_SYNC_INTERVAL_DELAY, delay).commit();
    }
    public int getSyncIntervalDelay(){

        return cache.getInt(KEY_SYNC_INTERVAL_DELAY, DEFAULT_SYNC_INTERVAL);
    }
}

class UserNotFound extends Exception{};