package com.shashank.singh.splitbill.Utils;

import android.text.TextUtils;
import android.util.Patterns;

/**
 * Created by shashank on 3/8/2017.
 */
public class Validation {

    public static boolean validateFields(String name){

        return !TextUtils.isEmpty(name);
    }

    public static boolean validateEmail(String string) {

        return !(TextUtils.isEmpty(string) || !Patterns.EMAIL_ADDRESS.matcher(string).matches());
    }
}
