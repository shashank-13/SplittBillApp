package com.shashank.singh.splitbill.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.shashank.singh.splitbill.Model.UserModel;
import com.shashank.singh.splitbill.R;

import java.util.List;

import static android.text.Spanned.SPAN_INCLUSIVE_INCLUSIVE;

/**
 * Created by shashank on 5/3/2017.
 */

public class UserAdapter extends ArrayAdapter<UserModel> {

    private List<UserModel> activityModels;
    private Context mContext;
    public UserAdapter(@NonNull Context context, @NonNull List<UserModel> objects) {
        super(context, 0, objects);
        this.activityModels= objects;
        mContext=context;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        UserModel modelBetween= activityModels.get(position);
        if(convertView==null)
            convertView= LayoutInflater.from(getContext()).inflate(R.layout.user_details,parent,false);
        doComputation(convertView,modelBetween);
        return convertView;

    }

    private void doComputation(View convertView, UserModel modelBetween) {

        String text1= modelBetween.getUseravatar()+" made an expense of ";
        String text2=modelBetween.getValueAmount();
        String output = text1.substring(0, 1).toUpperCase() + text1.substring(1);
        int textsize1 = mContext.getResources().getDimensionPixelSize(R.dimen.small_text);
        int textsize2= mContext.getResources().getDimensionPixelSize(R.dimen.large_text);

        SpannableString span1 = new SpannableString(output);
        span1.setSpan(new AbsoluteSizeSpan(textsize1), 0, output.length(), SPAN_INCLUSIVE_INCLUSIVE);

        SpannableString span2 = new SpannableString(text2);
        span2.setSpan(new AbsoluteSizeSpan(textsize2), 0, text2.length(), SPAN_INCLUSIVE_INCLUSIVE);

// let's put both spans together with a separator and all
        CharSequence finalText = TextUtils.concat(span1, " ", span2);

        TextView textView1 = (TextView) convertView.findViewById(R.id.event_message);
        TextView textView2= (TextView) convertView.findViewById(R.id.event_date);
        TextView textView3 = (TextView) convertView.findViewById(R.id.event_category);
        textView1.setText(finalText);
        textView2.setText(modelBetween.getDatemessage());
        textView3.setText(modelBetween.getCategories());
    }

    @Override
    public int getCount() {
        return activityModels.size();
    }

}
