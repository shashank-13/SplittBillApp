package com.shashank.singh.splitbill.Service;

import android.util.Log;

import com.shashank.singh.splitbill.SharedPreferences.Preference;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by shashank on 4/15/2017.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private String TAG="COMMON LOG";



    @Override
    public void onTokenRefresh() {
        Log.v(TAG,new Preference(this).getUSERID());
        String refreshed_token = FirebaseInstanceId.getInstance().getToken();
        new Preference(this).updateToken(refreshed_token);
        new Preference(this).setPOLLSERVER(true);

    }



}
