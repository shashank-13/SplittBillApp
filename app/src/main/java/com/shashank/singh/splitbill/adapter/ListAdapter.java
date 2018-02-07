package com.shashank.singh.splitbill.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.activeandroid.Model;
import com.shashank.singh.splitbill.Model.ActivityModel;
import com.shashank.singh.splitbill.R;

import java.util.List;

/**
 * Created by shashank on 4/24/2017.
 */

public class ListAdapter extends ArrayAdapter<Model> {

    private List<Model> activityModels;
    public ListAdapter(@NonNull Context context, @NonNull List<Model> objects) {
        super(context, 0, objects);
        this.activityModels= objects;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ActivityModel modelBetween= (ActivityModel) activityModels.get(position);
        if(convertView==null)
            convertView= LayoutInflater.from(getContext()).inflate(R.layout.activity_list,parent,false);
        doComputation(convertView,modelBetween);
        return convertView;

    }

    private void doComputation(View convertView, ActivityModel modelBetween) {

        TextView textView1 = (TextView) convertView.findViewById(R.id.event_message);
        TextView textView2= (TextView) convertView.findViewById(R.id.event_date);
        textView1.setText(String.valueOf(modelBetween.mMessage));
        textView2.setText(String.valueOf(modelBetween.mDate));
    }

    @Override
    public int getCount() {
        return activityModels.size();
    }
}
