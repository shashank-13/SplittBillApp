package com.shashank.singh.splitbill;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Configuration;
import com.shashank.singh.splitbill.Model.ActivityModel;
import com.shashank.singh.splitbill.Model.Groupmembers;
import com.shashank.singh.splitbill.Model.MapTable;
import com.shashank.singh.splitbill.Model.OfflineTable;

/**
 * Created by shashank on 4/16/2017.
 */

public class Application extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();


        Configuration dbConfiguration = new Configuration.Builder(this).setDatabaseName("MoneyDb.db").addModelClass(MapTable.class).addModelClass(ActivityModel.class).addModelClass(OfflineTable.class).addModelClass(Groupmembers.class)
                .create();
        ActiveAndroid.initialize(dbConfiguration);
    }
}
