package com.shashank.singh.splitbill.Utils;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by shashank on 8/25/2016.
 */
public class MySingleton {
    private  static MySingleton mInstance;
    private RequestQueue requestQueue;
    private static Context context;

    private MySingleton(Context contextx)
    {
       context=contextx;
        requestQueue=getRequestQueue();
    }
    public RequestQueue getRequestQueue()
    {

        if(requestQueue==null)
        {
            requestQueue= Volley.newRequestQueue(context.getApplicationContext());///makes sure that this particular instance lasts through lifetime
        }
        return  requestQueue;
    }
    public static  synchronized MySingleton getInstance(Context context)
    {
        if(mInstance==null)
            mInstance=new MySingleton(context);
        return mInstance;
    }
    public<T> void addtoRequestqueue(Request<T> request)
    {
        requestQueue.add(request);
    }
    public<T> void cancelAllRequests(String timepass)
    {
        requestQueue.cancelAll(timepass);

    }


}
