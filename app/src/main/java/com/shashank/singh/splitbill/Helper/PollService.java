package com.shashank.singh.splitbill.Helper;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.activeandroid.query.Select;
import com.shashank.singh.splitbill.Model.MapTable;
import com.shashank.singh.splitbill.Networking.SendRequest;
import com.shashank.singh.splitbill.R;
import com.shashank.singh.splitbill.Starter.TabActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by shashank on 6/29/2017.
 */

public class PollService extends IntentService {

    private static final String TAG = "PollService";

    public static Intent newIntent(Context context) {
        return new Intent(context, PollService.class);
    }

    public PollService()
    {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        executeMethods();

    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_REDELIVER_INTENT;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v(TAG,"SERVICE DESTROY CALLED");
    }

    private void executeMethods() {

        JSONArray jsonArray= queryDb();
        if(!jsonArray.toString().equals("[]"))
        {


            new SendRequest(this).makeArrayRequest(new SendRequest.VolleyCallback() {
                @Override
                public void onSuccess(String result) {
                    try {
                        JSONArray json =new JSONArray(result);
                        for(int i=0;i<json.length();i++)
                        {
                            JSONObject jsonObject = json.getJSONObject(i);
                            long l=Long.parseLong(jsonObject.getString("current"));
                            if(l<0||l>0)
                            {
                                displayNotifications();
                                break;
                            }

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }


                @Override
                public void onError(String result) {
                }
            },jsonArray, getResources().getString(R.string.getHttpUrl) +"updateVal");
        }
    }

    private void displayNotifications() {

        PendingIntent pendingIntent = PendingIntent.getActivity(this,1,new Intent(this, TabActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);

        long[] pattern = {500,500,500,500,500};

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("SplitBill Notification")
                .setContentText("You have pending expenses")
                .setAutoCancel(true)
                .setVibrate(pattern)
                .setLights(Color.GREEN,1,1)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notificationBuilder.build());
    }

   public static void executeNotifications(Context context) {

       Intent intent = PollService.newIntent(context);

       PendingIntent pi = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_NO_CREATE);


        if(pi==null)
        {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            PendingIntent newPendingIntent= PendingIntent.getService(context,0,intent,0);
            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(),AlarmManager.INTERVAL_HALF_DAY, newPendingIntent);

        }


    }

    private JSONArray queryDb() {

        List<MapTable> listMap = new Select().from(MapTable.class).execute();

        Log.v("LISTTAG",listMap.toString());
        JSONArray jsonArray = new JSONArray();
        for(MapTable map : listMap)
        {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("key",map.notificationKey);
                jsonArray.put(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return jsonArray;
    }


}
