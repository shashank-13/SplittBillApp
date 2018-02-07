package com.shashank.singh.splitbill.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.shashank.singh.splitbill.Model.NameModel;
import com.shashank.singh.splitbill.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shashank on 4/23/2017.
 */

public class AutoCompleteAdapter extends ArrayAdapter<NameModel> {

    private ArrayList<NameModel> mArrayList,tempArraylist,suggestions;


    public AutoCompleteAdapter(@NonNull Context context, @NonNull ArrayList<NameModel> objects) {
        super(context, R.layout.auto_complete_view, objects);
        this.mArrayList = objects;
        this.tempArraylist = new ArrayList<>(objects);
        this.suggestions= new ArrayList<>(objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.auto_complete_view, parent, false);
        }
            TextView textView = (TextView) convertView.findViewById(R.id.groupName);

            textView.setText(mArrayList.get(position).getGroupName());

        return  convertView;
    }



    public void addManually(ArrayList<NameModel> objects)
    {
        this.mArrayList = objects;
        this.tempArraylist = new ArrayList<>(objects);
        this.suggestions= new ArrayList<>(objects);
    }



    @Override
    public Filter getFilter() {
        return myFilter;
    }

    Filter myFilter = new Filter() {
        @Override
        public CharSequence convertResultToString(Object resultValue) {

            NameModel nameModel = (NameModel) resultValue;
            return  nameModel.getGroupName();
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            if (constraint != null ) {
                suggestions.clear();

                if(tempArraylist==null || tempArraylist.size() ==0)
                    Log.v("FUCKLIST","LIST IS STILL NULL");
                for (NameModel group : tempArraylist) {
                    //Log.v("RESULT RECIEVED",group);
                    if (group.getGroupName().toLowerCase().startsWith(constraint.toString().toLowerCase())) {
                        suggestions.add(group);
                    }
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = suggestions;
                filterResults.count = suggestions.size();

                Log.v("RESULT RECIEVED",suggestions.toString());
                return filterResults;
            } else {
                return new FilterResults();
            }
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
           List<NameModel> arrayList = (ArrayList<NameModel>) results.values;
            if (results != null && results.count > 0) {
                clear();
                for (NameModel cust : arrayList) {
                    add(cust);
                    notifyDataSetChanged();
                }
            }
            else{
                clear();
                notifyDataSetChanged();
            }
        }
    };
}
