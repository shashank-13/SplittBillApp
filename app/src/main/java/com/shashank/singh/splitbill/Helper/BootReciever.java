package com.shashank.singh.splitbill.Helper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by shashank on 6/29/2017.
 */

public class BootReciever  extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {

        PollService.executeNotifications(context.getApplicationContext());

    }
}
