package com.shashank.singh.splitbill.Networking;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.shashank.singh.splitbill.SharedPreferences.Preference;
import com.shashank.singh.splitbill.Utils.MySingleton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by shashank on 4/17/2017.
 */

public class SendRequest {

    private String TAG = "COMMON LOG";

    private String answer;

    private Context mContext;

    private String FILE_NAME="log_server.txt";

    FileOutputStream fileoutputStream;

    private String NEWLINE="\n";

    File file;


    public SendRequest(Context mContext) {
        this.mContext = mContext;
    }

    public void makeNetworkCall(final VolleyCallback volleyCallback, HashMap<String, String> hashMap, String URL) {
        Log.v(TAG, "Network Call Entered");

        answer = "null";

        //writeToFile(hashMap.toString()+" "+URL);


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, new JSONObject(hashMap), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.v("JSONRESPONSE", response.toString());
                    if (response.has("notification_key")) {
                        answer = response.getString("notification_key");
                        //writeToFile(response.toString());
                        volleyCallback.onSuccess(answer);
                    } else if (response.has("message")) {
                        answer = response.getString("message");
                        volleyCallback.onSuccess(answer);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    volleyCallback.onSuccess(answer);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //writeToFile(error.toString());
                Log.v(TAG, error.toString());
                volleyCallback.onError(answer);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Log.v(TAG, "Headers Called");
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("x-access-token", new Preference(mContext).getTOKEN());
                return headers;
            }
        };
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(30000, -1, 0));
        MySingleton.getInstance(mContext.getApplicationContext()).addtoRequestqueue(jsonObjectRequest);

    }


    public void makeArrayRequest(final VolleyCallback volleyCallback, JSONArray jsonArray, String URL) {
        Log.v(TAG, jsonArray.toString());
        //writeToFile(jsonArray.toString()+" "+URL);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.POST, URL, jsonArray, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.v(TAG, response.toString());
                //writeToFile(response.toString());
                volleyCallback.onSuccess(response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //writeToFile(error.toString());
                Log.v(TAG, error.toString());
                volleyCallback.onError(error.toString());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Log.v(TAG, "Headers Called");
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("x-access-token", new Preference(mContext).getTOKEN());
                return headers;
            }
        };
        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(30000, -1, 0));
        MySingleton.getInstance(mContext.getApplicationContext()).addtoRequestqueue(jsonArrayRequest);
    }


    public void makeSpecialArrayRequest(final VolleyCallback volleyCallback, JSONArray jsonArray, String URL, final HashMap<String, String> global_Map) {
        Log.v(TAG, jsonArray.toString());

        //writeToFile(jsonArray.toString()+" "+URL+" "+global_Map.toString());
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.POST, URL, jsonArray, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.v(TAG, response.toString());
                //writeToFile(response.toString());
                volleyCallback.onSuccess(response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //writeToFile(error.toString());
                Log.v(TAG, error.toString());
                volleyCallback.onError(error.toString());

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Log.v(TAG, "Headers Called");
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("x-access-token", new Preference(mContext).getTOKEN());
                for (String s : global_Map.keySet()) {
                    headers.put(s, global_Map.get(s));
                }
                for (String s : headers.keySet()) {
                    Log.v("HEADERS", s);
                    Log.v("HEADERS", headers.get(s));
                }
                return headers;
            }
        };
        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(30000, -1, 0));
        MySingleton.getInstance(mContext.getApplicationContext()).addtoRequestqueue(jsonArrayRequest);
    }


    public interface VolleyCallback {
        void onSuccess(String result);

        void onError(String result);
    }


    private File getFile()
    {
        return new File(Environment.getExternalStorageDirectory(),FILE_NAME);
    }

    private void writeToFile(String s)
    {
        try {
            file=getFile();

            fileoutputStream=new FileOutputStream(file,true);

            fileoutputStream.write(s.getBytes());
            fileoutputStream.write(NEWLINE.getBytes());


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fileoutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


}

