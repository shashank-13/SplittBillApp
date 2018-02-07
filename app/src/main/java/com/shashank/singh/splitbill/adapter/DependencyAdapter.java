package com.shashank.singh.splitbill.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.shashank.singh.splitbill.Model.FinalResult;
import com.shashank.singh.splitbill.R;

import java.util.ArrayList;

/**
 * Created by shashank on 4/26/2017.
 */

public class DependencyAdapter extends ArrayAdapter<FinalResult> {
    private ArrayList<FinalResult> arrayList;

    public DependencyAdapter(@NonNull Context context, ArrayList<FinalResult> arrayList) {
        super(context, 0, arrayList);
        this.arrayList = arrayList;
    }
    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        FinalResult modelBetween= arrayList.get(position);
        if(convertView==null)
            convertView= LayoutInflater.from(getContext()).inflate(R.layout.item_dependency,parent,false);
        doComputation(convertView,modelBetween);
        return convertView;

    }

    private void doComputation(View convertView, FinalResult modelBetween) {

        TextView textView1 = (TextView) convertView.findViewById(R.id.first_Name);
        TextView textView2= (TextView) convertView.findViewById(R.id.amount);
        TextView textView3= (TextView) convertView.findViewById(R.id.last_Name);
        ImageView imageView1= (ImageView) convertView.findViewById(R.id.left_Drawable);
        ImageView imageView2= (ImageView) convertView.findViewById(R.id.right_Drawable);

        String output1=modelBetween.getFirstName().substring(0,1).toUpperCase() + modelBetween.getFirstName().substring(1);
        String output2=modelBetween.getLastName().substring(0,1).toUpperCase()+modelBetween.getLastName().substring(1);


        textView1.setText(output1);
        textView2.setText(String.valueOf(modelBetween.getAmount()));
        textView3.setText(output2);

        String c1= output1.substring(0,1);
        String c2=output2.substring(0,1);

        TextDrawable drawable1 = TextDrawable.builder().buildRound(c1, Color.RED);
        imageView1.setImageDrawable(drawable1);

        TextDrawable drawable2 = TextDrawable.builder().buildRound(c2, Color.RED);
        imageView2.setImageDrawable(drawable2);
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }
}
