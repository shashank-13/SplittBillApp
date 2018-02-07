package com.shashank.singh.splitbill.Model;

/**
 * Created by shashank on 4/23/2017.
 */

public class NameModel  {
    private String groupName;

    public NameModel(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupName() {
        return groupName;

    }

    @Override
    public boolean equals(Object obj) {
        boolean result =false;
        if(obj==null || obj.getClass() != getClass())
            return  false;
        else
        {
            NameModel nameModel = (NameModel) obj;
            if(this.groupName.toLowerCase().equals(nameModel.getGroupName().toLowerCase()))
                result=true;
        }
        return result;
    }
}
