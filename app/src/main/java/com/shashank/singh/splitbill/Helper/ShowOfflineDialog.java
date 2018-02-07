package com.shashank.singh.splitbill.Helper;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.activeandroid.query.Select;
import com.cunoraz.tagview.Tag;
import com.cunoraz.tagview.TagView;
import com.shashank.singh.splitbill.Model.Groupmembers;
import com.shashank.singh.splitbill.Model.OfflineTable;
import com.shashank.singh.splitbill.R;
import com.shashank.singh.splitbill.adapter.GroupCompleteAdapter;
import com.shashank.singh.splitbill.fragment.ExpenseFragment;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by shashank on 4/30/2017.
 */

public class ShowOfflineDialog {

    private Context mContext;

    private ArrayList<String> stringArrayList;

    private ArrayList<String> arrayList1,arrayList2;

    private ArrayList<ArrayList<String>> majorList;

    private GroupCompleteAdapter autoCompleteAdapter1,autoCompleteAdapter2;

    private String TAG="COMMON LOG";

    public ShowOfflineDialog(Context mContext) {
        this.mContext = mContext;
        stringArrayList=new ArrayList<>();
        arrayList1=new ArrayList<>();
        arrayList2=new ArrayList<>();
    }

    public void  showDialog(final ExpenseFragment.DialogCallback dialogCallback)
    {
        setUpDATA();
        LayoutInflater li = LayoutInflater.from(mContext);
        View promptsView = li.inflate(R.layout.offline_dialog, null);

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);



        final TagView tagGroup= (TagView) promptsView.findViewById(R.id.tag_group);

        final AutoCompleteTextView groupName= (AutoCompleteTextView) promptsView.findViewById(R.id.et_group_name);
        final AutoCompleteTextView editText= (AutoCompleteTextView) promptsView.findViewById(R.id.et_addMembers);
        final EditText expenseText= (EditText) promptsView.findViewById(R.id.et_expense);


        groupName.setAdapter(autoCompleteAdapter1);

        groupName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                arrayList2=new ArrayList<String>(majorList.get(i));
                autoCompleteAdapter2=new GroupCompleteAdapter(mContext,arrayList2);
                editText.setAdapter(autoCompleteAdapter2);

                tagGroup.removeAll();


            }
        });


        ImageButton imageButton = (ImageButton) promptsView.findViewById(R.id.pastePin);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s=editText.getText().toString();
                if(!s.isEmpty() && arrayList2.size()>0)
                {
                    editText.setText("");
                    Tag tag1 = new Tag(s);
                    tag1.layoutColor=R.color.colorAccent;
                    tag1.radius=10f;
                    tagGroup.addTag(tag1);
                }
            }
        });

        tagGroup.setOnTagClickListener(new TagView.OnTagClickListener() {
            @Override
            public void onTagClick(final Tag tag, final int position) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setMessage("\"" + tag.text + "\" will be delete. Are you sure?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                       tagGroup.remove(position);
                        Toast.makeText(mContext, "\"" + tag.text + "\" deleted", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("No", null);
                builder.show();
            }
        });


        tagGroup.setOnTagDeleteListener(new TagView.OnTagDeleteListener() {

            @Override
            public void onTagDeleted(final TagView view, final Tag tag, final int position) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setMessage("\"" + tag.text + "\" will be delete. Are you sure?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        view.remove(position);
                        Toast.makeText(mContext, "\"" + tag.text + "\" deleted", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("No", null);
                builder.show();

            }
        });



        // set dialog message
        alertDialogBuilder
                .setTitle("Offline Mode")
                .setMessage("Proceed to save in local storage")
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // get user input and set it to result
                                // edit text
                                if((!groupName.getText().toString().isEmpty()) && (!expenseText.getText().toString().isEmpty()) && arrayList1.size()>0) {
                                    ArrayList<Tag> tags = (ArrayList<Tag>) tagGroup.getTags();
                                    if(tags.size()>0){
                                    for (Tag f : tags) {
                                        stringArrayList.add(f.text);
                                    }

                                        OfflineTable offlineTable =new OfflineTable();
                                        offlineTable.groupName=groupName.getText().toString();
                                        offlineTable.expenseNumber= Long.parseLong(expenseText.getText().toString());
                                        GsonBuilder builder = new GsonBuilder();
                                        builder.excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC);
                                        Gson gson = builder.create();
                                        String json = gson.toJson(stringArrayList);
                                        offlineTable.taggedFriends=json;
                                        offlineTable.save();
                                        dialogCallback.onComplete(true);

                                    }
                                    else
                                    {
                                        dialogCallback.onComplete(false);
                                    }
                                }

                                else
                                {
                                    dialogCallback.onComplete(false);
                                }

                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialogCallback.onComplete(false);
                                dialog.cancel();
                            }
                        });

        // create alert dialog

        final AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();


    }

    private void setUpDATA() {

        List<Groupmembers> groupmemberses = new Select().from(Groupmembers.class).execute();
        arrayList1 = new ArrayList<>();

        majorList=new ArrayList<>();

        for(Groupmembers group : groupmemberses)
        {
            String s =group.members;
            GsonBuilder builder = new GsonBuilder();
            builder.excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC);
            Gson gson = builder.create();
            ArrayList<String> stringArrayList = gson.fromJson(s,ArrayList.class);
            arrayList1.add(group.groupName);
            majorList.add(stringArrayList);
        }
        autoCompleteAdapter1=new GroupCompleteAdapter(mContext,arrayList1);
    }
}
