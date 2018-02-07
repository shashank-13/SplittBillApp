package com.shashank.singh.splitbill.Starter;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

import com.shashank.singh.splitbill.R;
import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;

/**
 * Created by shashank on 4/28/2017.
 */

public class IntroActivity extends AppIntro {



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       String[] titleName = this.getResources().getStringArray(R.array.titleArray);

        String[] descriptions=this.getResources().getStringArray(R.array.descriptionArray);
        //add the four slides

        addSlide(AppIntroFragment.newInstance(titleName[0],descriptions[0],R.drawable.ic_group_add_black_24dp, ContextCompat.getColor(this,R.color.firstslide)));
        addSlide(AppIntroFragment.newInstance(titleName[1],descriptions[1],R.drawable.ic_plus_one_black_24dp,ContextCompat.getColor(this,R.color.secondslide)));
        addSlide(AppIntroFragment.newInstance(titleName[2],descriptions[2],R.drawable.ic_notifications_active_black_24dp,ContextCompat.getColor(this,R.color.thirdslide)));
        addSlide(AppIntroFragment.newInstance(titleName[3],descriptions[3],R.drawable.ic_track_changes_black_24dp,ContextCompat.getColor(this,R.color.fourthslide)));

        // Override bar/separator color
        setBarColor(Color.parseColor("#3F51B5"));
        setSeparatorColor(Color.parseColor("#2196F3"));

        showStatusBar(true);


        // Hide Skip/Done button
        showSkipButton(false);

        // Turn vibration on and set intensity
        // NOTE: you will probably need to ask VIBRATE permisssion in Manifest
        setVibrate(true);
        setVibrateIntensity(30);

        setFlowAnimation();

    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        finish();
    }
}
