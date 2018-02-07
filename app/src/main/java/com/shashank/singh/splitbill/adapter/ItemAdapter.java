package com.shashank.singh.splitbill.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.shashank.singh.splitbill.Model.ListModel;
import com.shashank.singh.splitbill.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shashank on 5/3/2017.
 */

public class ItemAdapter extends ArrayAdapter<ListModel>{

    private List<ListModel> activityModels;
    private ArrayList<String> stringArrayList;

    public ItemAdapter(@NonNull Context context, @NonNull List<ListModel> objects) {
        super(context, 0, objects);
        this.activityModels= objects;
        stringArrayList = new ArrayList<>();
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ListModel modelBetween= activityModels.get(position);
        if(convertView==null)
            convertView= LayoutInflater.from(getContext()).inflate(R.layout.category_chooser,parent,false);
        doComputation(convertView,modelBetween,position);
        return convertView;

    }

    private void doComputation(View convertView, final ListModel modelBetween, int pos) {

        TextView textView1 = (TextView) convertView.findViewById(R.id.category_text);
        final CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.category_checkbox);
        textView1.setText(String.valueOf(modelBetween.getItemName()));
        textView1.setCompoundDrawablesWithIntrinsicBounds(modelBetween.getDrawableVal(), 0, 0, 0);
        textView1.setCompoundDrawablePadding(16);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

               if(b)
                   stringArrayList.add(modelBetween.getItemName());
                else
                    stringArrayList.remove(modelBetween.getItemName());
            }
        });
    }

    @Override
    public int getCount() {
        return activityModels.size();
    }

    public ArrayList<String> getResult()
    {
        return stringArrayList;
    }
}
