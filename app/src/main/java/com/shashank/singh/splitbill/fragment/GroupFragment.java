package com.shashank.singh.splitbill.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.query.Select;
import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.appinvite.FirebaseAppInvite;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.shashank.singh.splitbill.Helper.PickerDialog;
import com.shashank.singh.splitbill.Model.ActivityModel;
import com.shashank.singh.splitbill.Model.Group;
import com.shashank.singh.splitbill.Model.MapTable;
import com.shashank.singh.splitbill.Networking.Common;
import com.shashank.singh.splitbill.Networking.SendRequest;
import com.shashank.singh.splitbill.R;
import com.shashank.singh.splitbill.SharedPreferences.Preference;
import com.shashank.singh.splitbill.Starter.Dependency;
import com.shashank.singh.splitbill.Utils.MySingleton;
import com.shashank.singh.splitbill.Utils.TypefaceUtil;
import com.shashank.singh.splitbill.adapter.GroupAdapter;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.HttpUrl;

import static android.app.Activity.RESULT_OK;


/**
 * Created by shashank on 4/21/2017.
 */

public class GroupFragment extends android.support.v4.app.Fragment {


    private String TAG="COMMON LOG";
    private ListView mListView;
    private GroupAdapter mGroupAdapter;
    private ArrayList<Group> marrayList;
    private TextView mMessage;
    private ProgressBar progressBar;
    private FloatingActionButton mFloatAdd;
    private TextView flatMessage;
    private ImageView imageView;
    private boolean isLoaded=false,isVisibletoUser;


    private static final int REQUEST_INVITE = 0;


    @Override
    public void onResume() {
        super.onResume();
        Log.v(TAG,"GROUP ON RESUME CALLED");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.v(TAG,"GROUP ACTIVITY CREATED CALLED");
        mFloatAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(!Common.isDataConnectionAvailable(getActivity()))
                {
                    Toast.makeText(getActivity(),R.string.internetgroupmessage,Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Group groupModel = marrayList.get(i);
                    Intent intent = new Intent(getActivity(), Dependency.class);
                    intent.putExtra("group",groupModel.getGroupName());
                    startActivity(intent);
                }


            }
        });

        checkForDynamicLink();




    }

    private void checkForDynamicLink() {

        FirebaseDynamicLinks.getInstance().getDynamicLink(getActivity().getIntent())
                .addOnSuccessListener(getActivity(), new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData data) {
                        if (data == null) {
                            Log.d(TAG, "getInvitation: no data");
                            return;
                        }

                        // Get the deep link
                        Uri deepLink = data.getLink();
                        String invitationId="";
                        // Extract invite
                        FirebaseAppInvite invite = FirebaseAppInvite.getInvitation(data);
                        if (invite != null) {
                            invitationId = invite.getInvitationId();
                        }

                        Log.d(TAG, "Found Referral: " + invitationId + ":" + deepLink);

                        final HttpUrl url = HttpUrl.parse(deepLink.toString());
                        if (url != null) {
                            final String target = url.queryParameter("apn");
                            String url_s=getResources().getString(R.string.getHttpUrl) + "joinGroup";

                            invokeNetworkCall(url_s,target,false);
                            Log.d(TAG,"Found GroupName "+target);
                        }

                        // Handle the deep link
                        // ...
                    }
                })
                .addOnFailureListener(getActivity(), new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "getDynamicLink:onFailure", e);
                    }
                });
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        if(isVisibletoUser && (!isLoaded))
        {
            Log.v(TAG,"GROUP ON VIEW CREATED CALLED");
            isLoaded=true;
            progressBar.setVisibility(View.VISIBLE);
            queryCurrentStatus();
        }
    }

        public void queryCurrentStatus()
        {
            mMessage.setText(getString(R.string.settled));
            marrayList=new ArrayList<>();
            mGroupAdapter=new GroupAdapter(getActivity(),marrayList);
            mListView.setAdapter(mGroupAdapter);

            JSONArray jsonArray = queryDb();

            Log.v("TAG JSON ARRAY",jsonArray.toString());


            if(!jsonArray.toString().equals("[]"))
            {


                new SendRequest(getActivity()).makeArrayRequest(new SendRequest.VolleyCallback() {
                    @Override
                    public void onSuccess(String result) {
                        progressBar.setVisibility(View.GONE);
                        imageView.setVisibility(View.GONE);
                        flatMessage.setVisibility(View.GONE);
                        GsonBuilder builder = new GsonBuilder();
                        builder.excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC);
                        Gson gson = builder.create();
                        Type Listype= new TypeToken<List<Group>>(){}.getType();
                        marrayList= gson.fromJson(result,Listype);
                        mGroupAdapter.addAll(marrayList);
                        setMessage();

                    }

                    private void setMessage() {
                        long sum=0;
                        for (Group g : marrayList)
                        {
                            sum+=g.getmCurrent();

                        }
                        if(sum>0)
                        mMessage.setText(getString(R.string.owed) + sum);
                        else if (sum<0)
                            mMessage.setText(getString(R.string.owe)+Math.abs(sum));
                    }

                    @Override
                    public void onError(String result) {
                        progressBar.setVisibility(View.GONE);
                        mMessage.setText(getString(R.string.settled));
                        imageView.setVisibility(View.VISIBLE);
                        flatMessage.setVisibility(View.VISIBLE);
                    }
                },jsonArray, getResources().getString(R.string.getHttpUrl) +"updateVal");
            }
            else
            {
                progressBar.setVisibility(View.GONE);
                imageView.setVisibility(View.VISIBLE);
                flatMessage.setVisibility(View.VISIBLE);

            }

        }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {


        this.isVisibletoUser=isVisibleToUser;
        if(isVisibleToUser && isAdded())
        {
            Log.v(TAG,"GROUP VISIBLE EVENT CALLED");
            isLoaded=true;
            progressBar.setVisibility(View.VISIBLE);
            queryCurrentStatus();
        }

        if (this.isVisible()) {
           // Log.v(TAG,"SHOULD LOAD DATA1");

            if (!isVisibleToUser) {
                MySingleton.getInstance(getActivity().getApplicationContext()).cancelAllRequests("cancel");
            }
        }

    }



    private void invokeNetworkCall(String url, final String group, final boolean flag)
    {

        HashMap<String,String> hashMap = new HashMap<String, String>();
        hashMap.put("group",group);
        hashMap.put("token",new Preference(getActivity()).getTOKEN());
        hashMap.put("userid",new Preference(getActivity()).getUSERID());
        hashMap.put("avatar",new Preference(getActivity()).getAVATAR());

        Log.v("REQUEST MODE",hashMap.toString());

        SendRequest sendRequest =new SendRequest(getActivity());
        sendRequest.makeNetworkCall(new SendRequest.VolleyCallback(){
            @Override
            public void onSuccess(String result) {

                Log.v(TAG,result);
                if(!result.equals("null"))
                {

                    Log.v(TAG,"MAPTABLE UPDATE METHOD CALLED");
                    MapTable mapTable =new MapTable();
                    mapTable.groupName=group;
                    mapTable.notificationKey=result;
                    mapTable.save();
                    queryCurrentStatus();
                    Toast.makeText(getActivity(),"Data added successfully",Toast.LENGTH_SHORT).show();
                    if(flag)
                    {
                        String string="";
                        Date date=new Date();
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
                        simpleDateFormat.applyPattern("dd");
                        string+=simpleDateFormat.format(date);

                        simpleDateFormat.applyPattern("MM");

                        string=string+"/"+simpleDateFormat.format(date);
                        simpleDateFormat.applyPattern("yyyy");
                        string=string+"/" + simpleDateFormat.format(date);


                        String message = "You created a new group "+group;
                        if(new Select().from(ActivityModel.class).execute().size() > 15)
                        {
                            long id=new Select().from(ActivityModel.class).executeSingle().getId();

                            ActivityModel activityModel = ActivityModel.load(ActivityModel.class,id);
                            activityModel.delete();
                        }
                        ActivityModel activityModel = new ActivityModel();
                        activityModel.mMessage=message;
                        activityModel.mDate=string;
                        activityModel.save();
                    }
                    else
                    {
                        String message = "You joined the group "+group;
                        if(new Select().from(ActivityModel.class).execute().size() > 15)
                        {
                            long id=new Select().from(ActivityModel.class).executeSingle().getId();

                            ActivityModel activityModel = ActivityModel.load(ActivityModel.class,id);
                            activityModel.delete();
                        }
                        String string="";
                        Date date=new Date();
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
                        simpleDateFormat.applyPattern("dd");
                        string+=simpleDateFormat.format(date);

                        simpleDateFormat.applyPattern("MM");

                        string=string+"/"+simpleDateFormat.format(date);
                        simpleDateFormat.applyPattern("yyyy");
                        string=string+"/" + simpleDateFormat.format(date);


                        ActivityModel activityModel = new ActivityModel();
                        activityModel.mMessage=message;
                        activityModel.mDate=string;
                        activityModel.save();
                    }

                }
                else
                {
                    if(flag)
                        Toast.makeText(getActivity(),R.string.groupexists,Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(getActivity(),R.string.nogroup,Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String result) {

            }
        },hashMap,url);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.invite_friend,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.friend_invite:
                callInvites();
                return true;
         default:
             return super.onOptionsItemSelected(item);
        }

    }

    private void showMessage(String msg) {
       Toast.makeText(getActivity(),msg,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_INVITE) {
            if (resultCode == RESULT_OK) {
                // Get the invitation IDs of all sent messages
                String[] ids = AppInviteInvitation.getInvitationIds(resultCode, data);
                for (String id : ids) {
                    Log.d(TAG, "onActivityResult: sent invitation " + id);
                }
            } else {
                // Sending failed or it was canceled, show failure message to the user
                // [START_EXCLUDE]
                showMessage(getString(R.string.send_failed));
                // [END_EXCLUDE]
            }
        }
    }

    AlertDialog builderDialog;

    private void callInvites() {



        final CharSequence[] items = new CharSequence[marrayList.size()];
        for(int i=0;i<marrayList.size();i++)
        {
            items[i]= (marrayList.get(i).getGroupName());
        }
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle("Choose a group to invite");
        alertDialog.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String groupName="";

                groupName=items[i].toString();

                if (!groupName.isEmpty())
                {
                    DynamicLink dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
                            .setLink(Uri.parse(getString(R.string.invitation_deep_link)))
                            .setDynamicLinkDomain("abc123.app.goo.gl")

                            // Open links with this app on Android
                            .setAndroidParameters(new DynamicLink.AndroidParameters.Builder(groupName).build())
                            // Open links with com.example.ios on iOS
                            .setIosParameters(new DynamicLink.IosParameters.Builder("com.example.ios").build())
                            .buildDynamicLink();

                    Uri dynamicLinkUri = dynamicLink.getUri();

                    Intent intent = new AppInviteInvitation.IntentBuilder(getString(R.string.invitation_title))
                            .setMessage(getString(R.string.invitation_message)+" "+groupName)
                            .setDeepLink(dynamicLinkUri)
                            .setCustomImage(Uri.parse(getString(R.string.invitation_custom_image)))
                            .setCallToActionText(getString(R.string.invitation_cta))
                            .build();
                    startActivityForResult(intent, REQUEST_INVITE);
                    builderDialog.dismiss();
                }


            }
        });

        builderDialog = alertDialog.create();
        builderDialog.show();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.group, container, false);
        Log.v(TAG,"GROUP ON CREATE VIEW CALLED");
        mListView= (ListView) view.findViewById(R.id.group_listView);
        mMessage= (TextView) view.findViewById(R.id.final_amount_message);

        mFloatAdd= (FloatingActionButton) view.findViewById(R.id.buttonFloat);
        progressBar= (ProgressBar) view.findViewById(R.id.progressBarCircularIndeterminate);

        imageView= (ImageView) view.findViewById(R.id.dissatisfaction_image);
        flatMessage= (TextView) view.findViewById(R.id.flat_button_message);


        return  view;
    }

    private JSONArray queryDb() {

        List<MapTable> listMap = new Select().from(MapTable.class).execute();

        Log.v("LISTTAG",listMap.toString());
        JSONArray jsonArray = new JSONArray();
        for(MapTable map : listMap)
        {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("key",map.notificationKey);
                jsonArray.put(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return jsonArray;
    }

    public GroupFragment() {
    }

    private void showDialog() {
        LayoutInflater li = LayoutInflater.from(getActivity());
        View promptsView = li.inflate(R.layout.add_group, null);

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);


        final AlertDialog alertDialog = alertDialogBuilder.create();

        final EditText groupName = (EditText) promptsView
                .findViewById(R.id.et_group_name);

        final Button mCreate = (Button) promptsView.findViewById(R.id.button_Create);

        mCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String group = groupName.getText().toString();

                if(!(Common.isDataConnectionAvailable(getActivity())))
                {
                    Toast.makeText(getActivity(),R.string.internetgroupmessage,Toast.LENGTH_SHORT).show();
                    alertDialog.dismiss();
                }

                else
                {
                    if(!(group.isEmpty()) && !checkonClientSide(group))
                    {
                        String url=getResources().getString(R.string.getHttpUrl) + "createGroup";
                        alertDialog.dismiss();
                        invokeNetworkCall(url,group,true);
                    }
                    else
                    {
                        Toast.makeText(getActivity(),"Either field is empty or you are already in the group",Toast.LENGTH_SHORT).show();
                    }

                }

            }
        });

        // show it
        alertDialog.show();
    }

    private boolean checkonClientSide(String group) {

        String var="";
        MapTable mapTable = new Select()
                .from(MapTable.class)
                .where("groupName = ?", group)
                .executeSingle();
        if(mapTable!=null)
        {
            var=mapTable.groupName;
        }

        return var.equals(group);
    }

}
