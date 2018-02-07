package com.shashank.singh.splitbill.Starter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;

import com.activeandroid.query.Select;
import com.shashank.singh.splitbill.Model.Groupmembers;
import com.shashank.singh.splitbill.Model.UserModel;
import com.shashank.singh.splitbill.Networking.SendRequest;
import com.shashank.singh.splitbill.R;
import com.shashank.singh.splitbill.adapter.UserAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by shashank on 5/3/2017.
 */

public class UserActivity extends AppCompatActivity {

    private List<Groupmembers> groupmembersArrayList;
    private Spinner spinner1,spinner2;
    private Button button;
    private  String[] strings;
    private ArrayList<UserModel> userModelArrayList;
    private ListView listView;
    private UserAdapter userAdapter;
    private Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(new Select().from(Groupmembers.class).execute().size()==0)
        {
            showAlterView();
        }
        else
        {
            setContentView(R.layout.user_activity);
            toolbar= (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            userModelArrayList = new ArrayList<>();
            spinner1= (Spinner) findViewById(R.id.spinner1);
            spinner2= (Spinner) findViewById(R.id.spinner2);
            button= (Button) findViewById(R.id.button_submit);
            listView = (ListView) findViewById(R.id.category_list_view);
            spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    String s = groupmembersArrayList.get(i).members;
                    GsonBuilder builder = new GsonBuilder();
                    builder.excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC);
                    Gson gson = builder.create();
                    ArrayList<String> tempList= gson.fromJson(s,ArrayList.class);
                    String[] stringArray =new String[tempList.size()+1];
                    stringArray[0]="all";
                    int j=1;
                    for(String temp : tempList)
                    {
                        stringArray[j]=temp;
                        j++;
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(UserActivity.this,
                            R.layout.simple_spinner_item,stringArray);
                    adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
                    spinner2.setAdapter(adapter);

                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String groupName = spinner1.getSelectedItem().toString();
                    String avatar=spinner2.getSelectedItem().toString();

                    Log.v("SELECTED ITEM",groupName);
                    Log.v("SELECTED ITEM",avatar);

                    final JSONArray jsonArray =new JSONArray();

                    JSONObject jsonObject = new JSONObject();


                    try {
                        jsonObject.put("group",groupName);
                        jsonObject.put("avatar",avatar);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    jsonArray.put(jsonObject);

                    new SendRequest(UserActivity.this).makeArrayRequest(new SendRequest.VolleyCallback() {
                        @Override
                        public void onSuccess(String result) {

                            if(!result.equals("[]"))
                            {
                                GsonBuilder builder = new GsonBuilder();
                                builder.excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC);
                                Gson gson = builder.create();
                                Type Listype = new TypeToken<List<UserModel>>() {
                                }.getType();
                                userModelArrayList = gson.fromJson(result, Listype);
                                performLastRights();
                            }

                            else
                            {
                                showAlterView();
                            }

                        }

                        @Override
                        public void onError(String result) {

                            showAlterView();

                        }
                    },jsonArray,getResources().getString(R.string.getHttpUrl)+"getactivity");
                }
            });
            setData();
        }

    }

    private void showAlterView() {

        setContentView(R.layout.activity_alter);
    }

    private void performLastRights() {
        spinner1.setVisibility(View.GONE);
        spinner2.setVisibility(View.GONE);
        button.setVisibility(View.GONE);
        listView.setVisibility(View.VISIBLE);
        userAdapter=new UserAdapter(this,userModelArrayList);
        listView.setAdapter(userAdapter);


    }

    private void setData() {

        groupmembersArrayList = new ArrayList<>();

        groupmembersArrayList= new Select().from(Groupmembers.class).execute();
        strings = new String[groupmembersArrayList.size()];
        int j=0;
        for(Groupmembers s : groupmembersArrayList)
        {
           strings[j]=s.groupName;
            j++;
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
               R.layout.simple_spinner_item,strings);
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(adapter);

    }
}
