package com.shashank.singh.splitbill.Model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by shashank on 4/16/2017.
 */
@Table(name="MapTable")
public class MapTable  extends Model{
    @Column(name="GroupName", index = true , unique = true)
    public String groupName;

    public MapTable() {
    }

    @Column(name="Notification",index = true , unique = true)
    public  String notificationKey;

    public MapTable(String groupName,String notificationKey)
    {
        this.notificationKey=notificationKey;
        this.groupName=groupName;
    }
}
