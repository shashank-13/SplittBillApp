package com.shashank.singh.splitbill.Model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by shashank on 5/1/2017.
 */

@Table(name="OfflineTable")
public class OfflineTable  extends Model
{
    @Column(name="GroupName", index = true)
    public String groupName;


    @Column(name="Tagfriends", index = true)
    public String taggedFriends;

    @Column(name="expense", index = true)
    public long expenseNumber;

    public OfflineTable() {
    }
}
