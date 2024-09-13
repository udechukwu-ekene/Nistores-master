package com.nistores.awesomeurch.nistores.folders.pages;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nistores.awesomeurch.nistores.folders.adapters.NotificationAdapter;
import com.nistores.awesomeurch.nistores.folders.helpers.ApiUrls;
import com.nistores.awesomeurch.nistores.folders.helpers.MyTestService;
import com.nistores.awesomeurch.nistores.folders.helpers.Notification;
import com.nistores.awesomeurch.nistores.folders.helpers.VolleyRequest;
import com.nistores.awesomeurch.nistores.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class NotificationsActivity  extends AppCompatActivity {
    Intent intent;
    private ProgressBar progressBar;
    private LinearLayout networkErrorLayout;
    private RecyclerView recyclerView;
    private List<Notification> notifications;
    private NotificationAdapter mAdapter;
    private String URL, userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        progressBar = findViewById(R.id.loader);
        networkErrorLayout = findViewById(R.id.network_error);

        recyclerView = findViewById(R.id.recycler_view);
        notifications = new ArrayList<>();
        mAdapter = new NotificationAdapter(getApplicationContext(), notifications);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        ApiUrls apiUrls = new ApiUrls();
        URL = apiUrls.getApiUrl();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        userId = prefs.getString("user",null);

        Bundle bundle = this.getIntent().getExtras();
        if(bundle!=null){
            String last_notification_id = bundle.getString("last_notification_id");
            if(last_notification_id!=null){
                int curr_id = Integer.valueOf(last_notification_id) + 1;
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("last_notification_id",String.valueOf(curr_id)).apply();
            }
        }


        fetchItems();
    }

    // Launching the service
    /*public void onStartService(View v) {
        Intent i = new Intent(this, MyTestService.class);
        i.putExtra("foo", "bar");
        startService(i);
    }*/

   //Launching service for my mind
    public void onStartService() {
        Intent i = new Intent(this, MyTestService.class);
        i.putExtra("foo", "bar");
        startService(i);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Register for the particular broadcast based on ACTION string
        IntentFilter filter = new IntentFilter(MyTestService.ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(testReceiver, filter);
        // or `registerReceiver(testReceiver, filter)` for a normal broadcast
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister the listener when the application is paused
        LocalBroadcastManager.getInstance(this).unregisterReceiver(testReceiver);
        // or `unregisterReceiver(testReceiver)` for a normal broadcast
    }

    private BroadcastReceiver testReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int resultCode = intent.getIntExtra("resultCode", RESULT_CANCELED);
            //Log.d("RCODE",resultCode+"");
            if (resultCode == RESULT_OK) {
                String resultValue = intent.getStringExtra("resultJson");
                //Log.d("REZOTT",resultValue);
                //Toast.makeText(NotificationsActivity.this, resultValue, Toast.LENGTH_SHORT).show();
            }
        }
    };

    private void fetchItems(){

        String newURL = URL + "request=notification&id=" + userId;
        VolleyRequest volleyRequest = new VolleyRequest(getApplicationContext(), newURL) {
            @Override
            public void onProcess() {
                //do nothing while processing
                networkErrorLayout.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onSuccess(JSONObject response) {
                progressBar.setVisibility(View.GONE);
                networkErrorLayout.setVisibility(View.GONE);
                try {

                    Integer err = response.getInt("error");
                    if(err==0){

                        JSONArray data = response.getJSONArray("data");
                        //Log.d("MPUTA",data.toString());
                        fillInItems(data);

                    }else{

                        networkErrorLayout.setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
                    //Log.e("V_ERROR",e.toString());
                    e.printStackTrace();
                }
            }


            @Override
            public void onNetworkError() {
                progressBar.setVisibility(View.GONE);
                networkErrorLayout.setVisibility(View.VISIBLE);
            }
        };
        volleyRequest.fetchResources();
    }

    private void fillInItems(JSONArray data){
        List<Notification> items = new Gson().fromJson(data.toString(), new TypeToken<List<Notification>>() {
        }.getType());
        notifications.clear();
        notifications.addAll(items);
        // refreshing recycler view
        mAdapter.notifyDataSetChanged();
        onStartService();
    }
}