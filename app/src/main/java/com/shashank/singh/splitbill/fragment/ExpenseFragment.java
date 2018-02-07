package com.shashank.singh.splitbill.fragment;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.query.Select;
import com.cunoraz.tagview.Tag;
import com.cunoraz.tagview.TagView;
import com.shashank.singh.splitbill.Helper.PickerDialog;
import com.shashank.singh.splitbill.Helper.ShowOfflineDialog;
import com.shashank.singh.splitbill.Model.ActivityModel;
import com.shashank.singh.splitbill.Model.ExpenseModel;
import com.shashank.singh.splitbill.Model.Groupmembers;
import com.shashank.singh.splitbill.Model.MapTable;
import com.shashank.singh.splitbill.Model.NameModel;
import com.shashank.singh.splitbill.Model.OfflineTable;
import com.shashank.singh.splitbill.Networking.Common;
import com.shashank.singh.splitbill.Networking.SendRequest;
import com.shashank.singh.splitbill.Networking.TestAsyncTask;
import com.shashank.singh.splitbill.R;
import com.shashank.singh.splitbill.SharedPreferences.Preference;
import com.shashank.singh.splitbill.Utils.MySingleton;
import com.shashank.singh.splitbill.adapter.AutoCompleteAdapter;
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
 * Created by shashank on 4/21/2017.
 */

public class ExpenseFragment extends android.support.v4.app.Fragment  {

    private ArrayList<NameModel> mArrayList;
    private ArrayList<ExpenseModel> marrayList;
    private AutoCompleteAdapter autoCompleteAdapter;
    private AutoCompleteTextView autoCompleteTextView;
    private TagView tagGroup;
    private TextView editText;           // Declaring tagview and edittext

    private HashMap<String,ArrayList<Tag>> stringArrayListHashMap;
    private ArrayList<String> stringArrayList;
    private HashMap<String,Boolean> booleanHashMap;
    private Button tagButton,saveButton;
    private EditText expenseText,expenseMessage;
    private TextInputLayout textInputLayout;
    private String TAG = "COMMON LOG";
    private boolean isLoaded=false,isVisibletoUser;
    private int index;
    private boolean allowDialog;
    private ImageButton imageButton1,datePickerButton;
    String groupName,date="";

    private boolean UPDATEFROMDB;
    private TextView textView;
    private ImageButton imageButton;
    private ArrayList<String> checkBoxList;


    public ArrayList<Tag> getHashMapList(String groupName)
    {
        return stringArrayListHashMap.get(groupName);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.v(TAG,"EXPENSE ON OPTIONS MENU CALLED");
            inflater.inflate(R.menu.action_pending, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        imageButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PickerDialog pickerDialog = new PickerDialog(getActivity());
                pickerDialog.prepareData();
                pickerDialog.showDialog(new DialogCall() {
                    @Override
                    public void onComplete(ArrayList<String> arrayList) {

                        checkBoxList=arrayList;

                    }
                });
            }
        });
        datePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                date=dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();


            }
        });

    }
            @Override
            public void onPrepareOptionsMenu(Menu menu) {

                MenuItem menuItem = menu.findItem(R.id.menu_pick_color);

                RelativeLayout relativeLayout = (RelativeLayout) menuItem.getActionView();

                textView = (TextView) relativeLayout.findViewById(R.id.textOne);
                textView.setText(String.valueOf(new Select().from(OfflineTable.class).execute().size()));
                imageButton = (ImageButton) relativeLayout.findViewById(R.id.myButton);
                imageButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle("Pending Updates");
                        builder.setMessage("Sync to Server");
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (new Select().from(OfflineTable.class).execute().size() > 0) {
                                    if (allowDialog) {
                                        UPDATEFROMDB = true;
                                        OfflineTable offlineTables = new Select().from(OfflineTable.class).executeSingle();

                                        String s = offlineTables.taggedFriends;
                                        GsonBuilder builder = new GsonBuilder();
                                        builder.excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC);
                                        Gson gson = builder.create();
                                        stringArrayList = gson.fromJson(s, ArrayList.class);
                                        autoCompleteTextView.setText(offlineTables.groupName);
                                        expenseText.setText(String.valueOf(offlineTables.expenseNumber));
                                    } else {
                                        Toast.makeText(getActivity(), "Turn on Internet Connectivity to sync", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(getActivity(), "NO Pending updates refresh once", Toast.LENGTH_SHORT).show();
                                }

                            }
                        });
                        builder.setNegativeButton("No", null);
                        builder.show();
                    }
                });

                if (new Select().from(OfflineTable.class).execute().size() == 0) {
                    textView.setVisibility(View.GONE);
                    imageButton.setVisibility(View.GONE);
                } else {
                    textView.setVisibility(View.VISIBLE);
                    imageButton.setVisibility(View.VISIBLE);
                }

            }


            @Override
            public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {


                if (isVisibletoUser && (!isLoaded)) {
                    Log.v(TAG, "EXPENSE VIEW CREATED");
                    checkBoxList=new ArrayList<>();
                    isLoaded = true;
                    UPDATEFROMDB = false;
                    autoCompleteTextView.setText("");
                    expenseMessage.setText("");
                    expenseText.setText("");
                    date="";
                    startAsyncTask();

                }
            }


            @Override
            public void setUserVisibleHint(boolean isVisibleToUser) {


                this.isVisibletoUser = isVisibleToUser;
                if (isVisibleToUser && isAdded()) {
                    // Log.v(TAG,"SHOULD LOAD DATA2");
                    if(new Select().from(MapTable.class).execute().size()==0)
                        Toast.makeText(getActivity(),"You must be present in atleast one group to start spending",Toast.LENGTH_LONG).show();
                    checkBoxList=new ArrayList<>();
                    isLoaded = true;
                    UPDATEFROMDB = false;
                    autoCompleteTextView.setText("");
                    expenseMessage.setText("");
                    expenseText.setText("");
                    date="";
                    startAsyncTask();
                }

                if (this.isVisible()) {


                    if (!isVisibleToUser) {
                        MySingleton.getInstance(getActivity().getApplicationContext()).cancelAllRequests("cancel");

                    }
                }

            }

            public void startAsyncTask() {

                TestAsyncTask testAsyncTask = new TestAsyncTask(new FragmentCallback() {

                    @Override
                    public void onTaskDone(boolean flag) {
                        Log.v("SERVER RESPONSE", String.valueOf(flag));
                        if (flag) {
                            allowDialog = true;
                            precompute();
                        } else {
                            allowDialog = false;
                            new ShowOfflineDialog(getActivity()).showDialog(new DialogCallback() {
                                @Override
                                public void onComplete(boolean flag) {
                                    if (flag) {
                                        if (new Select().from(OfflineTable.class).execute().size() == 1) {
                                            textView.setVisibility(View.VISIBLE);
                                            imageButton.setVisibility(View.VISIBLE);
                                        }

                                        textView.setText(String.valueOf(new Select().from(OfflineTable.class).execute().size()));

                                    }

                                }
                            });
                        }
                    }
                });

                testAsyncTask.execute();
            }

            public void precompute() {

                Log.v(TAG, "PRECOMPUTE CALLED");
                mArrayList = new ArrayList<NameModel>();
                stringArrayListHashMap = new HashMap<String, ArrayList<Tag>>();
                booleanHashMap = new HashMap<>();

                autoCompleteAdapter = new AutoCompleteAdapter(getActivity(), mArrayList);
                autoCompleteTextView.setAdapter(autoCompleteAdapter);

                JSONArray jsonArray = queryDb();

                if (!jsonArray.toString().equals("[]")) {
                    new SendRequest(getActivity()).makeArrayRequest(new SendRequest.VolleyCallback() {
                        @Override
                        public void onSuccess(String result) {
                            //progressBar.setVisibility(View.GONE);
                            GsonBuilder builder = new GsonBuilder();
                            builder.excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC);
                            Gson gson = builder.create();
                            Type Listype = new TypeToken<List<ExpenseModel>>() {
                            }.getType();
                            marrayList = gson.fromJson(result, Listype);
                            beautify_parser(marrayList);

                        }

                        @Override
                        public void onError(String result) {

                        }
                    }, jsonArray, getResources().getString(R.string.getHttpUrl) + "queryexpense");
                }


                handlesaveButton();
            }

            private void handlesaveButton() {

                Log.v(TAG, "HANDLE SAVE BUTTON CALLED");

                saveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (autoCompleteTextView.getText().toString().isEmpty() || autoCompleteTextView.getText().toString().length() == 0) {
                            autoCompleteTextView.setError("Please select group name");
                        } else {
                            if (!Common.isDataConnectionAvailable(getActivity())) {
                                Toast.makeText(getActivity(), R.string.internetgroupmessage, Toast.LENGTH_SHORT).show();
                                return;
                            }
                           groupName = autoCompleteTextView.getText().toString();

                            MapTable mapTable = new Select().from(MapTable.class).where("groupName = ?", groupName).executeSingle();

                            if (mapTable == null) {
                                autoCompleteTextView.setError("Please select group name");

                            } else if (expenseMessage.getText().toString().isEmpty()) {
                                textInputLayout.setError("Give an appropriate message");
                            }
                            else if(checkBoxList.isEmpty())
                            {
                                Toast.makeText(getActivity(),"Please select atleast one category",Toast.LENGTH_SHORT).show();
                            }
                            else if(booleanHashMap.isEmpty() )
                            {
                                Toast.makeText(getActivity(),"You must tag atleast a single friend",Toast.LENGTH_SHORT).show();
                            }
                            else if(date.isEmpty())
                            {
                                Toast.makeText(getActivity(),"You must select a date for the expense",Toast.LENGTH_SHORT).show();
                            }
                            else {
                                if ((!booleanHashMap.isEmpty()) && (!expenseText.getText().toString().isEmpty()) && (!mapTable.notificationKey.isEmpty()) && (!expenseMessage.getText().toString().isEmpty())) {
                                    if (UPDATEFROMDB)
                                        new Preference(getActivity()).setUpdatedb(true);
                                    Log.v(TAG, "CALL EXECUTED");
                                    computeExpenses(booleanHashMap.size()+1, Long.parseLong(expenseText.getText().toString()), expenseMessage.getText().toString(), mapTable.notificationKey);
                                } else {
                                    Toast.makeText(getActivity(), "Please Fields all Appropriately", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }
                });
            }

            private void beautify_parser(ArrayList<ExpenseModel> local_var) {

                Log.v(TAG, " BEAUTIFY PARSER CALLED");

                for (ExpenseModel expenseModel : local_var) {
                    Groupmembers groupmembers = new Groupmembers();
                    String n = expenseModel.getGroup();
                    ArrayList<String> arrayList = expenseModel.getList_name();
                    groupmembers.groupName = n;
                    GsonBuilder builder = new GsonBuilder();
                    builder.excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC);
                    Gson gson = builder.create();
                    groupmembers.members = gson.toJson(arrayList);
                    groupmembers.save();
                    NameModel name = new NameModel(n);
                    autoCompleteAdapter.add(name);
                    //autoCompleteAdapter.addManually(name);
                    ArrayList<Tag> tag_List = new ArrayList<>();
                    for (String s : expenseModel.getList_name()) {
                        if(s.toLowerCase().equals(new Preference(getActivity()).getAVATAR().toLowerCase()))
                            continue;
                        Tag tag1 = new Tag(s);
                        tag1.layoutColor = R.color.colorAccent;
                        tag1.radius = 10f;
                        tag1.isDeletable = true;
                        tag_List.add(tag1);
                    }
                    stringArrayListHashMap.put(n, tag_List);

                }
                autoCompleteAdapter.addManually(mArrayList);


                tagButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mArrayList.size() == 0 || (!mArrayList.contains(new NameModel(autoCompleteTextView.getText().toString())))) {
                            autoCompleteTextView.setError("Please select group name");
                        } else {
                            booleanHashMap.clear();
                            showDialog(autoCompleteTextView.getText().toString());
                        }
                    }
                });


            }


            @Override
            public void onCreate(@Nullable Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                Log.v(TAG, "EXPENSE ON CREATE CALLED");
                setHasOptionsMenu(true);
            }

            @Nullable
            @Override
            public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

                Log.v(TAG, "EXPENSE FRAGMENT ON CREATE VIEW CALLED");

                View view = inflater.inflate(R.layout.expense, container, false);
                tagButton = (Button) view.findViewById(R.id.button_tag);
                autoCompleteTextView = (AutoCompleteTextView) view.findViewById(R.id.autoCompleteTextView);
                imageButton1 = (ImageButton) view.findViewById(R.id.image_button_list);
                datePickerButton= (ImageButton) view.findViewById(R.id.date_Picker);

                saveButton = (Button) view.findViewById(R.id.button_save);
                expenseText = (EditText) view.findViewById(R.id.edit_expense);
                expenseMessage = (EditText) view.findViewById(R.id.expense_message);

                textInputLayout = (TextInputLayout) view.findViewById(R.id.ti_group_message);

                autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        booleanHashMap.clear();
                        String temp = autoCompleteAdapter.getItem(i).getGroupName();
                        Toast.makeText(getActivity(), autoCompleteAdapter.getItem(i).getGroupName() + " Selected", Toast.LENGTH_SHORT).show();
                    }
                });


                return view;
            }

            private void computeExpenses(int size, final long l, final String s, String notificationKey) {

                long other_expense = Math.round(l / (size*1.0))  * -1;
                long my_expense = (other_expense*-1*(size-1));

                JSONArray jsonArray = new JSONArray();
                JSONObject json1 = new JSONObject();
                try {
                    json1.put("key", new Preference(getActivity()).getAVATAR().toLowerCase());
                    json1.put("value", my_expense);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                jsonArray.put(json1);

                for (String keys : booleanHashMap.keySet()) {

                        JSONObject json = new JSONObject();
                        try {
                            json.put("key", keys.toLowerCase());
                            json.put("value", other_expense);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        jsonArray.put(json);

                }

                autoCompleteTextView.setText("");
                expenseMessage.setText("");
                expenseText.setText("");
                booleanHashMap.clear();

                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("Key", notificationKey);
                hashMap.put("message", s);

                new SendRequest(getActivity()).makeSpecialArrayRequest(new SendRequest.VolleyCallback() {
                    @Override
                    public void onSuccess(String result) {
                        String message = "You made an expense of " + String.valueOf(l);
                        if (new Select().from(ActivityModel.class).execute().size() > 15) {
                            long id = new Select().from(ActivityModel.class).executeSingle().getId();

                            ActivityModel activityModel = ActivityModel.load(ActivityModel.class, id);
                            activityModel.delete();
                        }
                        ActivityModel activityModel = new ActivityModel();
                        activityModel.mMessage = message;
                        activityModel.mDate = date;
                        activityModel.save();

                        activityRequest(l);


                        if (new Preference(getActivity()).getUpdatedb()) {
                            if (new Select().from(OfflineTable.class).execute().size() > 0) {
                                long id = new Select().from(OfflineTable.class).executeSingle().getId();
                                OfflineTable offlineTable = OfflineTable.load(OfflineTable.class, id);
                                offlineTable.delete();
                                new Preference(getActivity()).setUpdatedb(false);
                                if (new Select().from(OfflineTable.class).execute().size() == 0) {
                                    textView.setVisibility(View.GONE);
                                    imageButton.setVisibility(View.GONE);
                                } else {
                                    textView.setText(String.valueOf(new Select().from(OfflineTable.class).execute().size()));
                                }

                            } else {
                                new Preference(getActivity()).setUpdatedb(false);
                            }
                        }
                    }

                    @Override
                    public void onError(String result) {
                        new Preference(getActivity()).setUpdatedb(false);

                    }
                }, jsonArray, getResources().getString(R.string.getHttpUrl) + "updatedb", hashMap);


            }

    private void activityRequest(long l) {

        String temp="";
        for(String s: checkBoxList)
        {
            temp+=s;
            temp+=" , ";
        }
        temp=temp.substring(0,temp.length()-2);


        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("avatar",new Preference(getActivity()).getAVATAR());
        hashMap.put("datemessage",date);
        hashMap.put("amount", String.valueOf(l));
        hashMap.put("category",temp);
        hashMap.put("group",groupName);
        new SendRequest(getActivity()).makeNetworkCall(new SendRequest.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                checkBoxList.clear();
                date="";
            }

            @Override
            public void onError(String result) {
                checkBoxList.clear();
                date="";
            }
        },hashMap, getResources().getString(R.string.getHttpUrl)+"addactivity");
    }

    private void showDialog(String s) {
                index = 0;


                LayoutInflater li = LayoutInflater.from(getActivity());
                View promptsView = li.inflate(R.layout.tag_fields, null);

                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(promptsView);

                if (UPDATEFROMDB && stringArrayList != null && stringArrayList.size() > 0) {
                    LinearLayout linearLayout = (LinearLayout) promptsView.findViewById(R.id.view_Linear);
                    linearLayout.setVisibility(View.VISIBLE);
                    ImageView imageButton1 = (ImageView) promptsView.findViewById(R.id.left_Move);
                    ImageView imageButton2 = (ImageView) promptsView.findViewById(R.id.right_Move);
                    final TextView textView = (TextView) promptsView.findViewById(R.id.current_Text);
                    textView.setText(stringArrayList.get(0));
                    imageButton2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (index < stringArrayList.size() - 1) {
                                index++;
                                textView.setText(stringArrayList.get(index));
                            }
                        }
                    });
                    imageButton1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (index > 0) {
                                index--;
                                textView.setText(stringArrayList.get(index));

                            }
                        }
                    });

                } else {
                    LinearLayout linearLayout = (LinearLayout) promptsView.findViewById(R.id.view_Linear);
                    linearLayout.setVisibility(View.GONE);
                }


                tagGroup = (TagView) promptsView.findViewById(R.id.tag_group);


                editText = (TextView) promptsView.findViewById(R.id.et_tag_name);


                Log.v("GROUP NAME IN DIALOG",s);


                tagGroup.addTags(getHashMapList(s));


                tagGroup.setOnTagClickListener(new TagView.OnTagClickListener() {
                    @Override
                    public void onTagClick(Tag tag, int position) {
                        if (!(booleanHashMap.containsKey(tag.text))) {
                            editText.append(" " + tag.text);
                            booleanHashMap.put(tag.text, true);
                        }
                    }
                });

                tagGroup.setOnTagDeleteListener(new TagView.OnTagDeleteListener() {

                    @Override
                    public void onTagDeleted(final TagView view, final Tag tag, final int position) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setMessage("\"" + tag.text + "\" will be delete. Are you sure?");
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String s = editText.getText().toString();
                                s = s.replaceAll(tag.text, "");
                                editText.setText(s);
                                booleanHashMap.remove(tag.text);
                                Toast.makeText(getActivity(), "\"" + tag.text + "\" deleted", Toast.LENGTH_SHORT).show();
                            }
                        });
                        builder.setNegativeButton("No", null);
                        builder.show();

                    }
                });


                // set dialog message
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // get user input and set it to result
                                        // edit text

                                        final String group = editText.getText().toString();


                                        if (!(Common.isDataConnectionAvailable(getActivity()))) {
                                            Toast.makeText(getActivity(), R.string.internetgroupmessage, Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

                // create alert dialog

                final AlertDialog alertDialog = alertDialogBuilder.create();
                // show it
                alertDialog.show();


            }


            public ExpenseFragment() {
            }

            private JSONArray queryDb() {

                List<MapTable> listMap = new Select().from(MapTable.class).execute();

                Log.v("LISTTAG", listMap.toString());
                JSONArray jsonArray = new JSONArray();
                for (MapTable map : listMap) {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("key", map.notificationKey);
                        jsonObject.put("group", map.groupName);
                        jsonArray.put(jsonObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                return jsonArray;
            }


            public interface FragmentCallback {
                void onTaskDone(boolean flag);
            }

            public interface DialogCallback {
                void onComplete(boolean flag);
            }

            public interface DialogCall {

                void onComplete(ArrayList<String> arrayList);
            }


        }
