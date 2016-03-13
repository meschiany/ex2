package com.ex2.shenkar.todolist;

import android.app.Activity;
import android.os.Bundle;

import com.parse.Parse;

/**
 * Created by shnizle on 1/2/2016.
 */
public class BaseActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Parse.enableLocalDatastore(this);

    }
}
