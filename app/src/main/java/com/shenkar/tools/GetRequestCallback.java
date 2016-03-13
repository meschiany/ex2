package com.shenkar.tools;

import org.json.JSONObject;

/**
 * Created by shnizle on 2/29/2016.
 */
public abstract class GetRequestCallback {
    public abstract void success(JSONObject jsonObject);
    public abstract void failed(Exception error);
}
