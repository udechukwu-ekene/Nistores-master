package com.nistores.awesomeurch.nistores.folders.pages;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.j256.ormlite.dao.Dao;
import com.nistores.awesomeurch.nistores.folders.helpers.ApiUrls;
import com.nistores.awesomeurch.nistores.folders.helpers.DatabaseHelper;
import com.nistores.awesomeurch.nistores.folders.helpers.JobSchedulerService;
import com.nistores.awesomeurch.nistores.folders.helpers.MyAlarmReceiver;
import com.nistores.awesomeurch.nistores.folders.helpers.UserTable;
import com.nistores.awesomeurch.nistores.folders.helpers.Utility;
import com.nistores.awesomeurch.nistores.R;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    EditText username;
    EditText password;
    Intent intent;
    ConstraintLayout loader;
    String error;
    SharedPreferences preferences;
    DatabaseHelper helper;
    Dao<UserTable, Integer> dao;
    Integer const_id = 1;
    ApiUrls apiUrls;
    Utility utility;
    int INTERVAL;
    JobScheduler mJobScheduler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        username = findViewById(R.id.input_name);
        password = findViewById(R.id.input_password);
        Button sign_in = findViewById(R.id.btn_sign_in);
        AppCompatButton sign_up = findViewById(R.id.sign_up);
        loader = findViewById(R.id.loader_layout);
        utility = new Utility(getApplicationContext());
        INTERVAL = utility.NOTIF_INTERVAL;

        sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });
        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUp();
            }
        });

        createNotificationChannel();

        performJobScheduler();

        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        if (preferences.contains("user")) {
            Toast.makeText(this,"contains user",Toast.LENGTH_LONG).show();
            scheduleAlarm();
            intent = new Intent(this,HomeActivity.class);
            startActivity(intent);
            finish();
        }

    }

    //@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void performJobScheduler(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            //Log.d("RUNNN","Scheduler");
            mJobScheduler = (JobScheduler)
                    getSystemService( Context.JOB_SCHEDULER_SERVICE );

            JobInfo.Builder builder = new JobInfo.Builder( 1,
                    new ComponentName( getPackageName(),
                            JobSchedulerService.class.getName() ) );

            builder.setPeriodic( 3000 );
        }
    }

    public void scheduleAlarm() {
        // Construct an intent that will execute the AlarmReceiver
        Intent intent = new Intent(getApplicationContext(), MyAlarmReceiver.class);
        // Create a PendingIntent to be triggered when the alarm goes off
        final PendingIntent pIntent = PendingIntent.getBroadcast(this, MyAlarmReceiver.REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        // Setup periodic alarm every every INTERVAL from this point onwards
        long firstMillis = System.currentTimeMillis(); // alarm is set right away
        AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        // First parameter is the type: ELAPSED_REALTIME, ELAPSED_REALTIME_WAKEUP, RTC_WAKEUP
        // Interval can be INTERVAL_FIFTEEN_MINUTES, INTERVAL_HALF_HOUR, INTERVAL_HOUR, INTERVAL_DAY
        if (alarm != null) {
            alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, firstMillis,
                    INTERVAL, pIntent);
        }
    }

    public void signIn(){

        boolean valid = true;
        String name = username.getText().toString();
        String pass = password.getText().toString();

        if(name.isEmpty()){
            valid = false;
            username.setError("Please enter your username");
        }
        if(pass.isEmpty()){
            valid = false;
            password.setError("Enter your password");
        }
        if(valid){

            preventInteraction();
            loader.setVisibility(View.VISIBLE);
            // Instantiate the RequestQueue.
            RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
            //this is the url where you want to send the request
            apiUrls = new ApiUrls();
            String ur = apiUrls.getApiUrl();
            String url = ur+"request=login&username="+name+"&password="+pass;
            // Request a string response from the provided URL.
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            loader.setVisibility(View.GONE);
                            enableUserInteraction();
                            // Display the response string.
                            //Toast.makeText(getApplicationContext(),""+response,Toast.LENGTH_SHORT).show();
                            //Log.d("RTN",response.toString());
                            try {
                                Integer err = response.getInt("error");

                                if(err==0){
                                    JSONObject data = response.getJSONObject("data");
                                    logUserIn(data);

                                }else{
                                    Toast.makeText(getApplicationContext(),"Invalid username or password",Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    enableUserInteraction();
                    loader.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(),"Sorry an error occurred. Try again",Toast.LENGTH_SHORT).show();
                    //Log.d("VOLLEY",error.toString());

                }
            });

            queue.add(jsonObjectRequest);

        }else{
            Toast.makeText(getApplicationContext(),"Fill the required fields",Toast.LENGTH_SHORT).show();
        }

    }

    public void logUserIn(JSONObject data){
        String id = null;
        try {
            id = data.getString("merchant_id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String info = data.toString();
        //save user id in shared preferences
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("user",id).apply();
        editor.putString("last_notification_id","0").apply();
        editor.putString("last_id","0").apply();
        scheduleAlarm();

        //save user data in user table
        helper = new DatabaseHelper(getApplicationContext());
        try{
            dao = helper.getUserDao();
            UserTable userTable = new UserTable();
            userTable.setId(const_id);
            userTable.setMerchant_id(id);
            userTable.setAll(info);
            dao.create(userTable);

        }catch(Exception e){
            e.printStackTrace();
            //Log.d("ERR",e.toString());
        }

        intent = new Intent(getApplicationContext(),HomeActivity.class);
        startActivity(intent);
        finish();
    }

    public void signUp(){
        intent = new Intent(this,CreateAccountActivity.class);
        startActivity(intent);
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.delivery_order_channel_name);
            String description = getString(R.string.delivery_order_channel_desc);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(utility.getDeliveryOrder_channelID(), name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }else{
                //Log.d("REZOT",utility.getDeliveryOrder_channelID()+" notification channel not registered");
            }
        }
    }

    public void preventInteraction(){
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    public void enableUserInteraction(){
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

}
