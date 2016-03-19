package com.shenkar.tools;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Base64;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by shnizle on 3/18/2016.
 */
public class UploadImage extends AsyncTask<Bitmap,Void,String>{

    ProgressDialog loading;
    private String UPLOAD_URL = "http://shnizle.site90.com/webService.php?MODEL=Tasks&COMMAND=upload";
    public static final String UPLOAD_KEY = "image";
    RequestHandler rh = new RequestHandler();
    GetRequestCallback getRequestCallback;

    public UploadImage setUrl(String url){

        UPLOAD_URL = url;
        return this;
    }
    public UploadImage setRequestCallback(GetRequestCallback requestCallback){

        getRequestCallback = requestCallback;
        return this;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        if(getRequestCallback != null) {
            if (s != null) {

                try {

                    getRequestCallback.success(new JSONObject(s));
                } catch (Exception e) {
                    getRequestCallback.failed(e);
                }
            } else {
                getRequestCallback.failed(new Exception("empty res"));
            }
        }
    }

    @Override
    protected String doInBackground(Bitmap... params) {
        Bitmap bitmap = params[0];
        String uploadImage = getStringImage(bitmap);

        HashMap<String,String> data = new HashMap<>();

        data.put(UPLOAD_KEY, uploadImage);
        String result = rh.sendPostRequest(UPLOAD_URL,data);

        return result;
    }

    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }
}

class RequestHandler {

    public String sendPostRequest(String requestURL,
                                  HashMap<String, String> postDataParams) {

        URL url;

        StringBuilder sb = new StringBuilder();
        try {
            url = new URL(requestURL);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);


            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(getPostDataString(postDataParams));

            writer.flush();
            writer.close();
            os.close();
            int responseCode = conn.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                sb = new StringBuilder();
                String response;
                while ((response = br.readLine()) != null){
                    sb.append(response);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }
}