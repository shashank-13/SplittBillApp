package com.shashank.singh.splitbill.Starter;

import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.shashank.singh.splitbill.Helper.PollService;
import com.shashank.singh.splitbill.Networking.Common;
import com.shashank.singh.splitbill.R;
import com.shashank.singh.splitbill.SharedPreferences.Preference;
import com.shashank.singh.splitbill.Utils.TypefaceUtil;
import com.shashank.singh.splitbill.fragment.ActivityFragment;
import com.shashank.singh.splitbill.fragment.ExpenseFragment;
import com.shashank.singh.splitbill.fragment.GroupFragment;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shashank on 4/21/2017.
 */

public class TabActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private NavigationView navigationView;
    private DrawerLayout drawer;
    private View navHeader;
    private TextView txtName, txtEmail;
    private ExpenseFragment expenseFragment;
    private GroupFragment groupFragment;
    private String TAG="COMMON LOG";
    private ImageView imageView;

    private boolean firstLaunch= true;

    private NetworkChangeReceiver networkChangeReceiver;


    private int[] tabIcons = 
            {
              R.drawable.expense_drawable,
                    R.drawable.ic_group_black_24dp,
                    R.drawable.ic_description_black_24dp
            };

    private String[] activityTitles;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_layout);
        PollService.executeNotifications(getApplicationContext());
        toolbar= (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager= (ViewPager) findViewById(R.id.viewpager);
        setUpViewPager(viewPager);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);


        // Navigation view header
        navHeader = navigationView.getHeaderView(0);
        txtName = (TextView) navHeader.findViewById(R.id.name);
        txtEmail = (TextView) navHeader.findViewById(R.id.email);
        imageView= (ImageView) navHeader.findViewById(R.id.img_profile);


        // load toolbar titles from string resources
        activityTitles = getResources().getStringArray(R.array.nav_item_activity_titles);

        loadNavHeader();

        // initializing navigation menu
        setUpNavigationView();



        tabLayout= (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        
        setUpTabIcons();

        if(getIntent().getExtras()!=null)
        {
            String title =  getIntent().getExtras().getString("title");
            String message = getIntent().getExtras().getString("body");

            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle);
            builder.setTitle(title);
            builder.setMessage(message);
            builder.setIcon(R.drawable.ic_star_black_24dp);
            builder.setPositiveButton("OK", null);
            builder.show();
        }

        viewPager.setCurrentItem(1);



    }

    private void setUpTabIcons() {

        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
    }

    private void setUpViewPager(ViewPager viewPager) {

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        expenseFragment=new ExpenseFragment();
        viewPagerAdapter.addFragment(expenseFragment,"EXPENSE");
        groupFragment= new GroupFragment();
        viewPagerAdapter.addFragment(groupFragment,"GROUP");
        viewPagerAdapter.addFragment(new ActivityFragment(),"ACTIVITY");
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setOffscreenPageLimit(2);
    }


    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    private void loadNavHeader() {
        // name, website
        String input= new Preference(this).getAVATAR();
        String output=input.substring(0,1).toUpperCase()+input.substring(1);
        txtName.setText(output);
       
        imageView.setImageResource(R.drawable.ic_account_circle_black_24dp);
        txtEmail.setText(new Preference(this).getEMAIL());

    }

    @Override
    public void onResume() {
        Log.v(TAG,"EXPENSE ON RESUME CALLED");
        IntentFilter intentFilter = new IntentFilter(NetworkChangeReceiver.ACTION_RESP);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        networkChangeReceiver=new NetworkChangeReceiver();
         registerReceiver(networkChangeReceiver,intentFilter);
        super.onResume();
    }

    @Override
    public void onPause() {
        Log.v(TAG,"EXPENSE ON PAUSE CALLED");
        unregisterReceiver(networkChangeReceiver);
        super.onPause();
    }

    private void setUpNavigationView() {
        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {
                    //Replacing the main content with ContentFragment Which is our Inbox View;
                    case R.id.nav_home:
                        drawer.closeDrawers();
                        break;
                    case R.id.nav_group:
                        startActivity(new Intent(TabActivity.this,UserActivity.class));
                        break;
                    case R.id.nav_about:
                        callPlaystore();
                        break;
                    case R.id.nav_logout:
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(TabActivity.this,MainActivity.class));
                        finish();
                        break;

                }

                //Checking if the item is in checked state or not, if not make it in checked state
                if (menuItem.isChecked()) {
                    menuItem.setChecked(false);
                } else {
                    menuItem.setChecked(true);
                }
                menuItem.setChecked(true);


                return true;
            }

            private void callPlaystore() {
                Uri uri = Uri.parse("market://details?id=" + getApplicationContext().getPackageName());
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                // To count with Play market backstack, After pressing back button,
                // to taken back to our application, we need to add following flags to intent.
                goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                        Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                try {
                    startActivity(goToMarket);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/apps/details?id=" + getApplicationContext().getPackageName())));
                }
            }
        });


        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawer.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
    }

    boolean doubleBackToExitPressedOnce = false;



    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers();
        }

        if (doubleBackToExitPressedOnce) {
            FirebaseAuth.getInstance().signOut();
            finish();
            super.onBackPressed();
        }

        this.doubleBackToExitPressedOnce = true;

        Toast.makeText(TabActivity.this,"Press Back Again to exit",Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);

    }

    private class NetworkChangeReceiver extends BroadcastReceiver {

        public static final String ACTION_RESP = "android.net.conn.CONNECTIVITY_CHANGE";

        private  boolean connectedBoolean = true;
        private  boolean disconnectedBoolean = true;

        public NetworkChangeReceiver() {
        }
        @Override

        public void onReceive(final Context context, final Intent intent) {

            if(!firstLaunch) {
                Log.v(TAG, "ON RECIEVE CALLED");
                int id = tabLayout.getSelectedTabPosition();
                if (checkInternet(context) && connectedBoolean) {
                    //startAsyncTask();
                    connectedBoolean = false;
                    disconnectedBoolean = true;
                    if (id == 0)
                        expenseFragment.startAsyncTask();
                    else if (id == 1)
                        groupFragment.queryCurrentStatus();
                } else if (!checkInternet(context) && disconnectedBoolean) {
                    //startAsyncTask();
                    disconnectedBoolean = false;
                    connectedBoolean = true;
                    if (id == 0)
                        expenseFragment.startAsyncTask();
                    else if (id == 1)
                        groupFragment.queryCurrentStatus();
                }
            }
            else
            {
                firstLaunch=false;
            }

        }




        boolean checkInternet(Context context) {

            return Common.isDataConnectionAvailable(context);
        }

    }
}
