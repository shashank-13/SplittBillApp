package com.shashank.singh.splitbill.Model;

import java.util.ArrayList;

/**
 * Created by shashank on 4/24/2017.
 */

public class ExpenseModel {

    private String group;
    private ArrayList<String> list_name;

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public ArrayList<String> getList_name() {
        return list_name;
    }

    public void setList_name(ArrayList<String> list_name) {
        this.list_name = list_name;
    }
}
