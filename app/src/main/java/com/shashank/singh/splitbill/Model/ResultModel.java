package com.shashank.singh.splitbill.Model;

/**
 * Created by shashank on 4/26/2017.
 */

public class ResultModel {
    private String groupName;
    private long amount;

    public String getGroupName() {
        return groupName;
    }

    public ResultModel(String groupName, long amount) {
        this.groupName = groupName;
        this.amount = amount;
    }

    public long getAmount() {
        return amount;
    }
}
