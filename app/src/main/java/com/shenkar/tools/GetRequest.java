package com.shenkar.tools;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by shnizle on 2/29/2016.
 */
public class GetRequest {

    public static void send(String query, Context context, final GetRequestCallback requestCallback){

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(context);
        String url ="http://shnizle.site90.com/webService.php";

// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url+"?"+query,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject res = new JSONObject(response);
                            if(res.getBoolean("status"))
                            {
                                requestCallback.success(res);
                            }else{
                                requestCallback.failed(new Exception(res.getString("error")));
                            }
                        }catch(JSONException e){

                            requestCallback.failed(new Exception(response));
                        }
                    }
                }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    requestCallback.failed(error);
                }
        });
// Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
}
