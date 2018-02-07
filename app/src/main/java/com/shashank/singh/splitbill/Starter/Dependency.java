package com.shashank.singh.splitbill.Starter;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.shashank.singh.splitbill.Model.ActivityModel;
import com.shashank.singh.splitbill.Model.FinalResult;
import com.shashank.singh.splitbill.Model.Groupmembers;
import com.shashank.singh.splitbill.Model.MapTable;
import com.shashank.singh.splitbill.Model.ResultModel;
import com.shashank.singh.splitbill.Networking.SendRequest;
import com.shashank.singh.splitbill.R;
import com.shashank.singh.splitbill.SharedPreferences.Preference;
import com.shashank.singh.splitbill.Utils.SortList;
import com.shashank.singh.splitbill.adapter.DependencyAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

/**
 * Created by shashank on 4/26/2017.
 */

public class Dependency extends AppCompatActivity {
    private Toolbar toolbar;
    private ListView mListView;
    private String TAG="COMMON LOG";
    private ArrayList<ResultModel> resultModels;
    private ArrayList<FinalResult> finalResults;
    private DependencyAdapter dependencyAdapter;
    private ProgressBar progressBar;
    private String groupName;
    private ImageView imagetView;
    private Button displayMessage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dependency);
        toolbar= (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        mListView= (ListView) findViewById(R.id.group_listView);
        progressBar= (ProgressBar) findViewById(R.id.progressBarCircularIndeterminate);
        imagetView= (ImageView) findViewById(R.id.dissatisfaction_image);
        displayMessage= (Button) findViewById(R.id.flat_button_message);
        init();
        groupName=getIntent().getExtras().getString("group");
        makeRequest();


    }

    private void makeRequest() {

        JSONArray jsonArray = new JSONArray();
        JSONObject json = new JSONObject();
        try {
            json.put("group",groupName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        jsonArray.put(json);


        new SendRequest(this).makeArrayRequest(new SendRequest.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
               if(result.equals("[]"))
               {
                   progressBar.setVisibility(View.GONE);
                   imagetView.setVisibility(View.VISIBLE);
                   displayMessage.setVisibility(View.VISIBLE);
               }
               else {
                   GsonBuilder builder = new GsonBuilder();
                   builder.excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC);
                   Gson gson = builder.create();
                   Type Listype= new TypeToken<List<ResultModel>>(){}.getType();
                   resultModels= gson.fromJson(result,Listype);
                   setMessage();
               }


            }

            private void setMessage() {
                resultModels= SortList.sortList(resultModels);
                finalResults=SortList.solveDependencies(resultModels);
                progressBar.setVisibility(View.GONE);
                dependencyAdapter= new DependencyAdapter(Dependency.this,finalResults);
                mListView.setAdapter(dependencyAdapter);
            }

            @Override
            public void onError(String result) {
                progressBar.setVisibility(View.GONE);
                imagetView.setVisibility(View.VISIBLE);
                displayMessage.setVisibility(View.VISIBLE);
            }
        },jsonArray, getResources().getString(R.string.getHttpUrl) +"dependency");
    }

    private void init() {
        resultModels = new ArrayList<>();
        finalResults = new ArrayList<>();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.action_clear,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.clear_list:
                AlertDialog.Builder builder = new AlertDialog.Builder(Dependency.this);
                builder.setMessage("\"" + groupName + "\" current expenses will be cleared. Are you sure?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        doOperations(false);
                    }
                });
                builder.setNegativeButton("No", null);
                builder.show();
                break;

            case R.id.leave_group:
                AlertDialog.Builder builder1 = new AlertDialog.Builder(Dependency.this);
                builder1.setMessage("Leaving " + groupName + ". Are you sure?");
                builder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                       doOperations(true);
                    }
                });
                builder1.setNegativeButton("No", null);
                builder1.show();
                break;
        }
        return true;
    }

    private void doOperations(final boolean flag) {

        MapTable mapTable = new Select()
                .from(MapTable.class)
                .where("groupName = ?", groupName)
                .executeSingle();
        final String notification_key = mapTable.notificationKey;
        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("key",notification_key);
        hashMap.put("name",new Preference(this).getAVATAR());


        final String URL = getResources().getString(R.string.getHttpUrl)+"clearlist";
        SendRequest sendRequest =new SendRequest(this);
        sendRequest.makeNetworkCall(new SendRequest.VolleyCallback(){
            @Override
            public void onSuccess(String result) {
                Log.v("COMMON LOG",result);
                if(flag)
                    leaveGroup(notification_key);
                else
                   setActivityLog("You cleared the pending expenses in "+groupName);
            }

            @Override
            public void onError(String result) {
                startActivity(new Intent(Dependency.this,TabActivity.class));
                finish();

            }
        },hashMap,URL);

    }

    private void leaveGroup(String notification_key) {



        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("key",notification_key);
        hashMap.put("name",new Preference(this).getAVATAR());
        hashMap.put("groupName",groupName);
        hashMap.put("userid",new Preference(this).getUSERID());

        final String URL = getResources().getString(R.string.getHttpUrl)+"leavegroup";
        SendRequest sendRequest =new SendRequest(this);
        sendRequest.makeNetworkCall(new SendRequest.VolleyCallback(){
            @Override
            public void onSuccess(String result) {
                Log.v("COMMON LOG",result);
                new Delete().from(MapTable.class)
                        .where("groupName = ?", groupName)
                        .execute();
                new Delete().from(Groupmembers.class).where("groupName = ?", groupName).execute();
                setActivityLog("You left the group "+groupName);
            }

            @Override
            public void onError(String result) {
                startActivity(new Intent(Dependency.this,TabActivity.class));
                finish();
            }
        },hashMap,URL);



        
    }

    private void setActivityLog(String message)
    {

            SimpleDateFormat month_date = new SimpleDateFormat(" MMMMMM");
            String date = Calendar.getInstance().get(Calendar.DATE) + " " + month_date.format(Calendar.getInstance().getTime());
            if(new Select().from(ActivityModel.class).execute().size() > 15)
            {
                long id=new Select().from(ActivityModel.class).executeSingle().getId();

                ActivityModel activityModel = ActivityModel.load(ActivityModel.class,id);
                activityModel.delete();
            }
            ActivityModel activityModel = new ActivityModel();
            activityModel.mMessage=message;
            activityModel.mDate=date;
            activityModel.save();
        startActivity(new Intent(Dependency.this,TabActivity.class));
        finish();

    }
}
