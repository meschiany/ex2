package com.ex2.shenkar.todolist;

import android.content.Context;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;
import com.shenkar.tools.GetRequest;
import com.shenkar.tools.GetRequestCallback;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shnizle on 1/2/2016.
 */
public class NewUser extends User {

    public NewUser(String email, String mobile, Type type){

        super(type, email, mobile);
    }

    public void register(final Context context, String pass, String teamName, final UserCreationCallback callback){



        GetRequest.send("MODEL=Users&COMMAND=addNewManager&team_name="+teamName+"&attrs[email]="+getEmail()+"&attrs[pass]="+pass+"&attrs[type]=MANAGER", context, new GetRequestCallback() {
            @Override
            public void success(JSONObject jsonObject) {

                try {

                    JSONObject data = jsonObject.getJSONObject("data");

                    RegisteredUser.save(context, data.getInt("id"));

                    callback.success(new RegisteredUser(data.getInt("id"),
                            getEmail(),
                            getMobile(),
                            getType(), context));



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
}

abstract class UserCreationCallback{

    public abstract void success(RegisteredUser user);
    public abstract void failed();
}
