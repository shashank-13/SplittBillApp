package com.shashank.singh.splitbill.Model;

/**
 * Created by shashank on 5/3/2017.
 */

public class ListModel {

    private String itemName;
    private int drawableVal;

    public ListModel(String itemName, int drawableVal) {
        this.itemName = itemName;
        this.drawableVal = drawableVal;
    }

    public String getItemName() {
        return itemName;
    }

    public int getDrawableVal() {
        return drawableVal;
    }
}
