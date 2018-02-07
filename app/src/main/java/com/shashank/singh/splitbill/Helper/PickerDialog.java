package com.shashank.singh.splitbill.Helper;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;

import com.shashank.singh.splitbill.Model.ListModel;
import com.shashank.singh.splitbill.R;
import com.shashank.singh.splitbill.adapter.ItemAdapter;
import com.shashank.singh.splitbill.fragment.ExpenseFragment;
import com.shashank.singh.splitbill.fragment.GroupFragment;

import java.util.ArrayList;

/**
 * Created by shashank on 5/3/2017.
 */

public class PickerDialog  {

    private Context mContext;
    private ArrayList<ListModel> mArrayList;
    private ItemAdapter itemAdapter;
    private ListView listView;

    public PickerDialog(Context mContext) {
        this.mContext = mContext;
        mArrayList=new ArrayList<>();
    }

    public void prepareData()
    {
        mArrayList.add(new ListModel("Movies", R.drawable.movies));
        mArrayList.add(new ListModel("Dining Out",R.drawable.dining_out));
        mArrayList.add(new ListModel("Groceries",R.drawable.groceries));
        mArrayList.add(new ListModel("Rent",R.drawable.rent));
        mArrayList.add(new ListModel("Water",R.drawable.water));
        mArrayList.add(new ListModel("Services",R.drawable.services));
        mArrayList.add(new ListModel("Gifts",R.drawable.gifts));
        mArrayList.add(new ListModel("Transportation",R.drawable.taxi));
        mArrayList.add(new ListModel("Cleaning",R.drawable.cleaning));
        mArrayList.add(new ListModel("Others",R.drawable.others));

    }


    public void showDialog(final ExpenseFragment.DialogCall dialogCallback)
    {
        LayoutInflater li = LayoutInflater.from(mContext);
        View promptsView = li.inflate(R.layout.main_category_chooser, null);


        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);

        alertDialogBuilder.setView(promptsView);

        listView= (ListView) promptsView.findViewById(R.id.category_list_view);

        itemAdapter=new ItemAdapter(mContext,mArrayList);
        listView.setAdapter(itemAdapter);

        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialogCallback.onComplete(itemAdapter.getResult());

                            }
                        })
                .setNegativeButton("cancel",null);

        // create alert dialog

        final AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();


    }

}
