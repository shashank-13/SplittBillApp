package com.shashank.singh.splitbill.Utils;

import com.shashank.singh.splitbill.Model.FinalResult;
import com.shashank.singh.splitbill.Model.ResultModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by shashank on 4/26/2017.
 */

public class SortList {

    public static ArrayList<ResultModel> sortList(ArrayList<ResultModel> arrayList)
    {
        Collections.sort(arrayList, new Comparator<ResultModel>() {
            @Override
            public int compare(ResultModel resultModel, ResultModel t1) {

                if(resultModel.getAmount() < t1.getAmount())
                    return -1;
                else if(resultModel.getAmount() == t1.getAmount())
                    return  0;
                else
                    return 1;
            }
        });

        return arrayList;
    }

    public static ArrayList<FinalResult> solveDependencies(ArrayList<ResultModel> arrayList)
    {
        ArrayList<FinalResult> finalResults = new ArrayList<>();
        int low_index,high_index=0;
        low_index=0;
        for(int j=0;j<arrayList.size();j++)
        {
            ResultModel resultModel =arrayList.get(j);
            high_index=j;
            if(resultModel.getAmount()>0)
                break;
        }
        long first_val,second_val;
        first_val=arrayList.get(low_index).getAmount()*-1;
        second_val=arrayList.get(high_index).getAmount();

        while(low_index<arrayList.size() && high_index < arrayList.size() && low_index < high_index)
        {
            if(first_val> second_val)
            {
                FinalResult finalResult =new FinalResult(arrayList.get(low_index).getGroupName(),arrayList.get(high_index).getGroupName(),second_val);
                finalResults.add(finalResult);
                high_index++;
                first_val-=second_val;
                if(high_index<arrayList.size())
                second_val=arrayList.get(high_index).getAmount();
            }
            else if(first_val==second_val)
            {
                FinalResult finalResult =new FinalResult(arrayList.get(low_index).getGroupName(),arrayList.get(high_index).getGroupName(),second_val);
                finalResults.add(finalResult);
                low_index++;
                high_index++;
                if(low_index< arrayList.size() && high_index <arrayList.size())
                {
                    first_val=arrayList.get(low_index).getAmount() *-1;
                    second_val=arrayList.get(high_index).getAmount();
                }
            }
            else if(second_val>first_val)
            {

                FinalResult finalResult =new FinalResult(arrayList.get(low_index).getGroupName(),arrayList.get(high_index).getGroupName(),first_val);
                finalResults.add(finalResult);
                low_index++;
               second_val-=first_val;
                if(low_index<arrayList.size())
                first_val=arrayList.get(low_index).getAmount()*-1;
            }
        }

        return finalResults;
    }
}
