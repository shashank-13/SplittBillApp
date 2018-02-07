package com.shashank.singh.splitbill.SharedPreferences;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by shashank on 4/17/2017.
 */

public class Preference {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Context mContext;

    private int PRIVATE_MODE = 0;

    private String PREF_NAME = "splitbillpref";


    private String TOKEN = "registrationtoken";

    private String PENDINGUPDATE = "pendingupdate";




    private String ISFIRSTTIME="isfirsttime";

    private String AVATAR="avatar";

    private String EMAIL="email";

    private String APPINTRO="appintro";

    private String USERID="userid";

    private String POLLSERVER="pollserver";

    private String UPDATEDB="updatedb";

    private String EMAILSIGININ="emailsignin";



    public Preference(Context context) {
        this.mContext = context;
        sharedPreferences = mContext.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = sharedPreferences.edit();
    }


    public void updateToken(String token) {
        editor.putString(TOKEN, token);
        editor.commit();
    }


    public String getTOKEN() {
        return sharedPreferences.getString(TOKEN, "");
    }

    public void updateValue(String group, long val) {
        long temp = sharedPreferences.getLong(group, 0);
        val += temp;
        editor.putLong(group, val);
    }

    public long getValue(String group) {
        return sharedPreferences.getLong(group, 0);
    }

    public void updateStatus(boolean flag) {
        editor.putBoolean(PENDINGUPDATE, flag);
        editor.commit();
    }

    public boolean getUpdateStatus()
    {
        return sharedPreferences.getBoolean(PENDINGUPDATE,false);
    }

    public void updatefirst(boolean flag)
    {
        editor.putBoolean(ISFIRSTTIME,flag);
        editor.commit();
    }

    public boolean isFirstTime()
    {
        return sharedPreferences.getBoolean(ISFIRSTTIME,true);
    }

    public void updateAvatar(String s)
    {
        editor.putString(AVATAR,s);
        editor.commit();
    }

    public String getAVATAR()
    {
        return sharedPreferences.getString(AVATAR,"");
    }

    public void setMail(String s)
    {
        editor.putString(EMAIL,s);
        editor.commit();
    }

    public String getEMAIL()
    {
        return sharedPreferences.getString(EMAIL,"");
    }

    public void updateAppIntro(boolean a)
    {
        editor.putBoolean(APPINTRO,a);
        editor.commit();
    }

    public boolean getAPPINTRO()
    {
        return sharedPreferences.getBoolean(APPINTRO,true);
    }


    public void updateUserID(String s)
    {
        editor.putString(USERID,s);
        editor.commit();
    }

    public String getUSERID()
    {
        return sharedPreferences.getString(USERID,"");
    }

    public void setPOLLSERVER(boolean flag)
    {
        editor.putBoolean(POLLSERVER,flag);
        editor.commit();
    }

    public boolean getPollServer()
    {
        return sharedPreferences.getBoolean(POLLSERVER,true);
    }

    public void setUpdatedb(boolean flag)
    {
        editor.putBoolean(UPDATEDB,flag);
        editor.commit();
    }

    public boolean getUpdatedb()
    {
        return sharedPreferences.getBoolean(UPDATEDB,false);
    }


    public void setEmailFlag(boolean flag)
    {
        editor.putBoolean(EMAILSIGININ,flag);
        editor.commit();
    }

    public boolean getEmailFlag()
    {
        return sharedPreferences.getBoolean(EMAILSIGININ,false);
    }



}
