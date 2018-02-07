package com.shashank.singh.splitbill.Model;

/**
 * Created by shashank on 5/3/2017.
 */

public class UserModel  {

    private  String useravatar;
    private String valueAmount;
    private String datemessage;
    private String categories;

    public String getUseravatar() {
        return useravatar;
    }

    public String getValueAmount() {
        return valueAmount;
    }

    public String getDatemessage() {
        return datemessage;
    }

    public String getCategories() {
        return categories;
    }

    public String getGroup() {
        return group;
    }

    public String getCreated_at() {
        return created_at;
    }

    private String group;
    private  String created_at;
}
