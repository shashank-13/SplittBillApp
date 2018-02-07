package com.shashank.singh.splitbill.Model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by shashank on 4/18/2017.
 */

public class Group {

    @SerializedName("total")
    private long mTotal;

    @SerializedName("current")
    private  long mCurrent;

    @SerializedName("group")
    private String groupName;

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public long getmTotal() {
        return mTotal;
    }

    public void setmTotal(long mTotal) {
        this.mTotal = mTotal;
    }

    public long getmCurrent() {
        return mCurrent;
    }

    public void setmCurrent(long mCurrent) {
        this.mCurrent = mCurrent;
    }



}
