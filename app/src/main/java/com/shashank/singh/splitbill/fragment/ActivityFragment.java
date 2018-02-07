package com.shashank.singh.splitbill.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import com.activeandroid.Model;
import com.activeandroid.query.Select;
import com.shashank.singh.splitbill.Model.ActivityModel;
import com.shashank.singh.splitbill.R;
import com.shashank.singh.splitbill.Utils.MySingleton;
import com.shashank.singh.splitbill.adapter.ListAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by shashank on 4/21/2017.
 */

public class ActivityFragment extends android.support.v4.app.Fragment {

    private ListView listView;
    private List<Model> activityModels;
    private ListAdapter listAdapter;
    private String TAG="COMMON LOG";
    private boolean isLoaded=false,isVisibletoUser;
    private ImageView imageView;
    private Button button;



    private void setUpArrayList()
    {
        activityModels=new ArrayList<>();
        activityModels= new Select().from(ActivityModel.class).execute();
        Collections.reverse(activityModels);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        if(isVisibletoUser && (!isLoaded))
        {
            isLoaded=true;
        }
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {

        this.isVisibletoUser=isVisibleToUser;
        if(isVisibleToUser && isAdded())
        {
            Log.v(TAG,"SHOULD LOAD DATA2");
            isLoaded=true;
            setUpArrayList();

            if(activityModels.size()>0) {
                listView.setVisibility(View.VISIBLE);
                imageView.setVisibility(View.GONE);
                button.setVisibility(View.GONE);
                ListAdapter listAdapter = new ListAdapter(getActivity(), activityModels);
                listView.setAdapter(listAdapter);
            }
            else
            {
                listView.setVisibility(View.GONE);
                imageView.setVisibility(View.VISIBLE);
                button.setVisibility(View.VISIBLE);

            }
        }

        if (this.isVisible()) {


            if (!isVisibleToUser) {
                MySingleton.getInstance(getActivity().getApplicationContext()).cancelAllRequests("cancel");
                // TODO stop audio playback
            }
        }

    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view;
        view = inflater.inflate(R.layout.activity, container, false);
        listView = (ListView) view.findViewById(R.id.activity_list_view);
        imageView= (ImageView) view.findViewById(R.id.dissatisfaction_image);
        button= (Button) view.findViewById(R.id.myButton);
        if(new Select().from(ActivityModel.class).execute().size()>0) {

            listView.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.GONE);
            button.setVisibility(View.GONE);

            return view;
        }
        else
        {
            listView.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);
            button.setVisibility(View.VISIBLE);
            return view;
        }
    }

    public ActivityFragment() {
    }
}
