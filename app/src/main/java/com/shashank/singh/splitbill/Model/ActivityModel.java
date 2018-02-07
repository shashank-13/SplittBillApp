package com.shashank.singh.splitbill.Model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by shashank on 4/24/2017.
 */
@Table(name="ActivityModel")
public class ActivityModel extends Model {

    @Column(name="mMessage", index = true )
    public String mMessage;

    public ActivityModel() {
        super();
    }


    @Column(name="mDate", index = true )
    public String mDate;

    public ActivityModel(String mMessage, String mDate) {
        this.mMessage = mMessage;
        this.mDate = mDate;
    }


}
