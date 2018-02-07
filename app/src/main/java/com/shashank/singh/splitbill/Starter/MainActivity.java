package com.shashank.singh.splitbill.Starter;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.query.Select;
import com.shashank.singh.splitbill.Model.Groupmembers;
import com.shashank.singh.splitbill.Model.MapTable;
import com.shashank.singh.splitbill.Model.OfflineTable;
import com.shashank.singh.splitbill.Networking.Common;
import com.shashank.singh.splitbill.Networking.SendRequest;
import com.shashank.singh.splitbill.R;
import com.shashank.singh.splitbill.SharedPreferences.Preference;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final int RC_SIGN_IN = 1;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthstateListener;
    private GoogleApiClient mGoogleApiClient;
    private SignInButton signInButton;
    private Button emailSingIn;
    private TextView signup;
    private String TAG="COMMON LOG";
    private String title="",message="";
    private ProgressBar progressBar;

    private ArrayList<MapTable> resultModels;


    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthstateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mAuthstateListener!=null)
            mAuth.removeAuthStateListener(mAuthstateListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                //  Initialize SharedPreferences

                //  Create a new boolean and preference and set it to true
                boolean isFirstStart = new Preference(MainActivity.this).getAPPINTRO();

                //  If the activity has never started before...
                if (isFirstStart) {

                    //  Launch app intro
                    Intent i = new Intent(MainActivity.this,IntroActivity.class);
                    startActivity(i);

                    //  Make a new preferences editor
                    new Preference(MainActivity.this).updateAppIntro(false);
                }
            }
        });

        // Start the thread
        t.start();


        setContentView(R.layout.activity_main);


      progressBar= (ProgressBar) findViewById(R.id.progressBarCircularIndeterminate);


        if(getIntent().getExtras()!=null)
        {
           title =  getIntent().getExtras().getString("title");
            message = getIntent().getExtras().getString("body");
        }
        else
        {
            title="";
            message="";
        }

        Log.v(TAG, String.valueOf(new Select().from(MapTable.class).execute().size()));

        //insertData();
      //   checkData();

        //deletedata();


        initFirebaseAuth();


        emailSingIn= (Button) findViewById(R.id.email_sign_in);
        signup= (TextView) findViewById(R.id.signup_text);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!Common.isDataConnectionAvailable(MainActivity.this))
                {
                    Toast.makeText(MainActivity.this,R.string.internetgroupmessage,Toast.LENGTH_SHORT).show();
                }

                else if(new Preference(MainActivity.this).getEmailFlag()||!(new Preference(MainActivity.this).getUSERID().isEmpty()))
                {
                    Toast.makeText(MainActivity.this,"Already Registered with one account",Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
                else {
                    progressBar.setVisibility(View.VISIBLE);
                    showDialog(false);
                }
            }
        });


        emailSingIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!Common.isDataConnectionAvailable(MainActivity.this))
                {
                    Toast.makeText(MainActivity.this,R.string.internetgroupmessage,Toast.LENGTH_SHORT).show();
                }
                else
                {
                    progressBar.setVisibility(View.VISIBLE);
                    showDialog(true);
                }

            }
        });


        configureGoogleSignIn();

        signInButton = (SignInButton) findViewById(R.id.sign_in_button);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!Common.isDataConnectionAvailable(MainActivity.this))
                {
                    Toast.makeText(MainActivity.this,R.string.internetgroupmessage,Toast.LENGTH_SHORT).show();
                }
                else if(new Preference(MainActivity.this).getEmailFlag())
                {
                    Toast.makeText(MainActivity.this,"Already Registered with one account",Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
                else
                {
                    progressBar.setVisibility(View.VISIBLE);
                    signInGoogle();
                }

            }
        });

    }



    public void insertData()
     {
         OfflineTable offlineTable =new OfflineTable();
         offlineTable.groupName="Xiaomi Group";
         offlineTable.expenseNumber=200;
         ArrayList<String> stringArrayList =new ArrayList<>();
         stringArrayList.add("shashank");
         stringArrayList.add("shashwat");
         stringArrayList.add("rohit");
         GsonBuilder builder = new GsonBuilder();
         builder.excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC);
         Gson gson = builder.create();
         String json = gson.toJson(stringArrayList);
         offlineTable.taggedFriends=json;
         offlineTable.save();

         checkData();
     }
    private void deletedata() {
        /*String groupName="mahisbathan";
        new Delete().from(MapTable.class)
                .where("groupName = ?", groupName)
                .execute();*/

        long id=new Select().from(OfflineTable.class).executeSingle().getId();

        OfflineTable activityModel = OfflineTable.load(OfflineTable.class,id);
        activityModel.delete();

        Log.v(TAG,"SIZE "+String.valueOf(new Select().from(OfflineTable.class).execute().size()));
    }

    private void checkData() {

       List<Groupmembers> groupmemberses = new Select().from(Groupmembers.class).execute();

        for(Groupmembers group : groupmemberses)
        {
            String s =group.members;
            GsonBuilder builder = new GsonBuilder();
            builder.excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC);
            Gson gson = builder.create();
            ArrayList<String> stringArrayList = gson.fromJson(s,ArrayList.class);
            Log.v(TAG,group.groupName);
            for(String v : stringArrayList)
            {
                Log.v(TAG,v);
            }
        }

      List<MapTable> mapTable = new Select().from(MapTable.class).execute();

        Log.v(TAG,new Preference(this).getTOKEN());
        Log.v(TAG,new Preference(this).getAVATAR());

        if(mapTable!=null)
        {
            for(MapTable map : mapTable)
            {
                Log.v(TAG,map.groupName);
                Log.v(TAG,map.notificationKey);
                Log.v(TAG, String.valueOf(map.getId()));
            }
        }
        else
        {
            Log.v(TAG,"TABLE STILL EMPTY FUCK OFF");
        }
    }



    private void showDialog(final boolean flag)              //  two layouts for sign up and sign in
    {
        if(flag)
        {
            LayoutInflater li = LayoutInflater.from(MainActivity.this);
            View promptsView = li.inflate(R.layout.input_fields, null);

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);

            // set prompts.xml to alertdialog builder
            alertDialogBuilder.setView(promptsView);

            final TextInputLayout textInputLayout = (TextInputLayout) promptsView.findViewById(R.id.ti_mail_id);

            final EditText userMail = (EditText) promptsView
                    .findViewById(R.id.et_mail_id);

            final EditText userPass= (EditText) promptsView.findViewById(R.id.et_new_password);

            TextView textView = (TextView) promptsView.findViewById(R.id.tv_forgot_password);





            alertDialogBuilder
                    .setCancelable(false)
                    .setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // get user input and set it to result
                                    // edit text


                                    if((!userMail.getText().toString().isEmpty()) && userMail.getText().toString().length()>0 && (!userPass.getText().toString().isEmpty()) && userPass.getText().toString().length()>0)
                                        signinUser(userMail.getText().toString(),userPass.getText().toString()); // sending the id to sign in method

                                    else {
                                        progressBar.setVisibility(View.GONE);
                                        Toast.makeText(MainActivity.this, "Please fields appropriately", Toast.LENGTH_SHORT).show();
                                    }
                                       // sing up method

                                }
                            })
                    .setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog,int id) {
                                    progressBar.setVisibility(View.GONE);
                                    dialog.cancel();
                                }
                            });

            // create alert dialog
            final AlertDialog alertDialog = alertDialogBuilder.create();

            // show it
            alertDialog.show();

            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(!TextUtils.isEmpty(userMail.getText().toString()) && android.util.Patterns.EMAIL_ADDRESS.matcher(userMail.getText().toString()).matches())
                    {
                        resetPassword(userMail.getText().toString());
                        alertDialog.dismiss();
                    }
                    else
                    {
                        textInputLayout.setError("Please enter a valid email address");
                    }



                }
            });


        }
        else
        {
            LayoutInflater li = LayoutInflater.from(MainActivity.this);
            View promptsView = li.inflate(R.layout.sign_up_input, null);

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);

            // set prompts.xml to alertdialog builder
            alertDialogBuilder.setView(promptsView);

            final EditText userMail = (EditText) promptsView
                    .findViewById(R.id.et_mail_id);

            final EditText userPass= (EditText) promptsView.findViewById(R.id.et_new_password);


            alertDialogBuilder
                    .setCancelable(false)
                    .setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // get user input and set it to result
                                    // edit text


                                    if((!userMail.getText().toString().isEmpty()) && userMail.getText().toString().length()>0 && (!userPass.getText().toString().isEmpty()) && userPass.getText().toString().length()>0 )
                                    {
                                        createUserAccountEmail(userMail.getText().toString(),userPass.getText().toString());
                                    }
                                    else {
                                        Toast.makeText(MainActivity.this, "Please fields appropriately", Toast.LENGTH_SHORT).show();// sing up method
                                        progressBar.setVisibility(View.GONE);
                                    }

                                }
                            })
                    .setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    dialog.cancel();
                                    progressBar.setVisibility(View.GONE);
                                }
                            });

            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();

            // show it
            alertDialog.show();

        }




        // set dialog message

    }

    private void resetPassword(String s) {
        progressBar.setVisibility(View.GONE);
        mAuth.sendPasswordResetEmail(s)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "Email Sent", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void signInGoogle() {

        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void initFirebaseAuth() {
        mAuth=FirebaseAuth.getInstance();
        mAuthstateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();


             if(user!=null)
                {

              new Preference(MainActivity.this).updateUserID(user.getUid());
                    new Preference(MainActivity.this).setMail(user.getEmail());

                    if(new Preference(MainActivity.this).getPollServer())
                    {
                        progressBar.setVisibility(View.VISIBLE);
                        resultModels=new ArrayList<>();
                        makeNetworkRequest(new Preference(MainActivity.this).getTOKEN());
                    }

                    else if (new Preference(MainActivity.this).isFirstTime())
                    {
                        manageAvatarDialog();
                    }
                    else
                    {
                        if(!((title == null ) || (message ==null ) ||(title.equals(""))||(message.equals(""))))
                        {
                            progressBar.setVisibility(View.GONE);
                            Intent intent = new Intent(MainActivity.this,TabActivity.class);
                            intent.putExtra("title",title);
                            intent.putExtra("body",message);
                            startActivity(intent);
                            finish();
                        }
                        else {
                            progressBar.setVisibility(View.GONE);
                            startActivity(new Intent(MainActivity.this, TabActivity.class));
                            finish();
                        }
                    }

                }
                else
                {
                    //Toast.makeText(MainActivity.this,"Global Sign Out",Toast.LENGTH_SHORT).show();
                    if(!(Common.isDataConnectionAvailable(MainActivity.this)))
                    {
                        Toast.makeText(MainActivity.this,"Turn on Internet Connection",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        };


    }

    private void manageAvatarDialog() {

        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.avatar_dialog, null);

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);


        final AlertDialog alertDialog = alertDialogBuilder.create();

        final TextInputLayout textLayout = (TextInputLayout) promptsView.findViewById(R.id.ti_avatar_name);

        final EditText editText = (EditText) promptsView
                .findViewById(R.id.et_avatar_name);

        editText.setText(new  Preference(MainActivity.this).getAVATAR());

        final Button mCheck = (Button) promptsView.findViewById(R.id.button_submit);

        final ProgressBar progress= (ProgressBar) promptsView.findViewById(R.id.progressBarCircularIndeterminate);

        mCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progress.setVisibility(View.VISIBLE);

                final String group = editText.getText().toString();

                if(!Common.isDataConnectionAvailable(MainActivity.this))
                {
                    progress.setVisibility(View.GONE);
                    textLayout.setError("Turn on internet connection for one time set up");
                }

                else
                {
                    if(editText.getText().toString().isEmpty())
                    {
                        progress.setVisibility(View.GONE);
                        textLayout.setError("Please name a valid avatar");
                    }
                    else if( editText.getText().toString().toLowerCase().equals(new  Preference(MainActivity.this).getAVATAR()))
                    {
                        progress.setVisibility(View.GONE);
                        Toast.makeText(MainActivity.this,"AVATAR CREAED",Toast.LENGTH_SHORT).show();
                        new Preference(MainActivity.this).updatefirst(false);
                        alertDialog.dismiss();
                        newActivity();
                    }
                    else {
                        //alertDialog.dismiss();
                        final String temp=editText.getText().toString().toLowerCase();
                        HashMap<String,String> hashMap =new HashMap<>();
                        hashMap.put("userid",new Preference(MainActivity.this).getUSERID());
                        hashMap.put("name",temp);

                        final String URL = getResources().getString(R.string.getHttpUrl)+"updateName";
                        SendRequest sendRequest =new SendRequest(MainActivity.this);
                        sendRequest.makeNetworkCall(new SendRequest.VolleyCallback(){
                            @Override
                            public void onSuccess(String result) {
                                Log.v(TAG,result);
                                if(result.equals("done"))
                                {
                                    progress.setVisibility(View.GONE);
                                    new Preference(getApplicationContext()).updateAvatar(temp);
                                    Toast.makeText(MainActivity.this,"AVATAR CREAED",Toast.LENGTH_SHORT).show();
                                    new Preference(MainActivity.this).updatefirst(false);
                                    alertDialog.dismiss();
                                    newActivity();

                                }
                                else
                                {
                                    progress.setVisibility(View.GONE);
                                    textLayout.setError("Try a different name ");
                                }

                            }

                            @Override
                            public void onError(String result) {
                                progress.setVisibility(View.GONE);
                                textLayout.setError("Server error try again ");
                            }
                        },hashMap,URL);
                    }
                }

            }
        });

        // show it
        alertDialog.show();
    }

    private void newActivity() {

        if(!((title == null ) || (message ==null ) ||(title.equals(""))||(message.equals(""))))
        {
            Intent intent = new Intent(MainActivity.this,TabActivity.class);
            intent.putExtra("title",title);
            intent.putExtra("body",message);
            startActivity(intent);
            finish();
        }
        else
        {
            startActivity(new Intent(MainActivity.this,TabActivity.class));
            finish();
        }

    }

    private void makeNetworkRequest(String token) {

        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject1;
        jsonObject1=new JSONObject();

        try {
            jsonObject1.put("userid",new Preference(this).getUSERID());
            jsonObject1.put("token", token);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        jsonArray.put(jsonObject1);



        final String URL = getResources().getString(R.string.getHttpUrl)+"updateToken";
        SendRequest sendRequest =new SendRequest(this);
        sendRequest.makeArrayRequest(new SendRequest.VolleyCallback(){
            @Override
            public void onSuccess(String result) {

                Log.v(TAG,result);
                if(!result.equals("[]"))
                {
                    GsonBuilder builder = new GsonBuilder();
                    builder.excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC);
                    Gson gson = builder.create();
                    Type Listype= new TypeToken<List<MapTable>>(){}.getType();
                    resultModels= gson.fromJson(result,Listype);
                    setMessage();
                }
                buildRequest();
            }

            @Override
            public void onError(String result) {

                progressBar.setVisibility(View.GONE);
                Log.v(TAG,"Sign in failed from Token update "+result.toString());
                Toast.makeText(MainActivity.this,"Sign in Failed",Toast.LENGTH_LONG).show();

            }
        },jsonArray,URL);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {

            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            Log.v(TAG,result.getStatus().toString());
            handleSignInResult(result);
        }

    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {

            GoogleSignInAccount account = result.getSignInAccount();

            if((!new Preference(this).getEMAIL().isEmpty())&&(!new Preference(this).getEMAIL().equals(account.getEmail())))
            {
                Toast.makeText(MainActivity.this,"Already Registered with one account",Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
            else
            {
                firebaseAuthWithGoogle(account);
            }




        }  else
        {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(MainActivity.this,"Please Turn on Internet Connectivity",Toast.LENGTH_SHORT).show();

        }
    }

    private void firebaseAuthWithGoogle(final GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(MainActivity.this,"Google signed out",Toast.LENGTH_SHORT).show();
                        }


                        // ...
                    }
                });
    }

    private void createUserAccountEmail(final String email, final String password)
    {

        Log.v(TAG,email);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                        progressBar.setVisibility(View.GONE);

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, R.string.auth_failed,
                                    Toast.LENGTH_SHORT).show();
                        }



                        // ...
                    }
                });
    }

    private void configureGoogleSignIn()
    {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this , this )
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    private  void signinUser(final String email, String password)
    {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                        progressBar.setVisibility(View.GONE);

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithEmail:failed", task.getException());
                            Toast.makeText(MainActivity.this, R.string.auth_failed,
                                    Toast.LENGTH_SHORT).show();
                        }

                        else
                        {
                           new Preference(MainActivity.this).setEmailFlag(true);

                        }

                        // ...
                    }
                });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        Toast.makeText(MainActivity.this,"On Connection Failed Listener Called",Toast.LENGTH_SHORT).show();

    }


    private void buildRequest() {

        HashMap<String,String> hashMap =new HashMap<>();
        hashMap.put("userid",new Preference(this).getUSERID());

        final String URL = getResources().getString(R.string.getHttpUrl)+"getAvatar";
        SendRequest sendRequest =new SendRequest(this);
        sendRequest.makeNetworkCall(new SendRequest.VolleyCallback(){
            @Override
            public void onSuccess(String result) {
                Log.v(TAG,result);
                new Preference(getApplicationContext()).updateAvatar(result);
                new Preference(getApplicationContext()).setPOLLSERVER(false);
                progressBar.setVisibility(View.GONE);

                manageAvatarDialog();

            }

            @Override
            public void onError(String result) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(MainActivity.this,"Sign in Failed",Toast.LENGTH_LONG).show();

            }
        },hashMap,URL);


    }

    private void setMessage() {

        for (MapTable map : resultModels)
        {
            Log.v(TAG,"FUCK TABLE BEING UPDATED IN SET MESSAGE");
            MapTable newTab =new MapTable();
            newTab.notificationKey=map.notificationKey;
            newTab.groupName=map.groupName;
            newTab.save();
        }
    }

}
