package com.shashank.singh.splitbill.Model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by shashank on 4/15/2017.
 */
@Table(name="User")
public class User extends Model{
    @Column(name="GroupName",index = true)
    public String groupName;

    public User() {
    }

    @Column(name = "AmountDue",index = true)
    public long amountValue;

    @Column(name = "Token",index = true)
    public String userToken;

    public User(String groupKey,long amountValue,String userToken)
    {
        this.groupName=groupKey;
        this.amountValue=amountValue;
        this.userToken=userToken;
    }



}
