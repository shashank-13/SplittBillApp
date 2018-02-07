package com.shashank.singh.splitbill.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.shashank.singh.splitbill.Model.Group;
import com.shashank.singh.splitbill.R;

import java.util.ArrayList;

/**
 * Created by shashank on 4/18/2017.
 */

public class GroupAdapter extends ArrayAdapter<Group> {

    private ArrayList<Group> mArrayList;


    public GroupAdapter(Context mContext,ArrayList<Group> mArrayList)
    {
      super(mContext,0,mArrayList);
        this.mArrayList=mArrayList;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Group modelBetween=mArrayList.get(position);
        if(convertView==null)
            convertView= LayoutInflater.from(getContext()).inflate(R.layout.group_cardview,parent,false);
        doComputation(convertView,modelBetween);
        return convertView;

    }

    private void doComputation(View convertView, Group modelBetween) {

        TextView textView1 = (TextView) convertView.findViewById(R.id.te_number);
        TextView textView2= (TextView) convertView.findViewById(R.id.my_number);
        TextView textView4= (TextView) convertView.findViewById(R.id.groupName_message);
        TextView textView5 = (TextView) convertView.findViewById(R.id.flow_text);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.image_Icon);
        if(modelBetween.getmCurrent()>0)
            textView5.setText("You are owed");
        else
            textView5.setText("You owe");
        textView1.setText(String.valueOf(modelBetween.getmTotal()));
        textView2.setText(String.valueOf(Math.abs(modelBetween.getmCurrent())));
        textView4.setText(modelBetween.getGroupName());
        String c= textView4.getText().toString().toUpperCase().substring(0,1);
        Log.v("STRING RECEIVED",c);
        TextDrawable drawable2 = TextDrawable.builder().buildRound(c, Color.parseColor("#00BFA5"));
        imageView.setImageDrawable(drawable2);
    }

    @Override
    public int getCount() {
        return mArrayList.size();
    }


}
