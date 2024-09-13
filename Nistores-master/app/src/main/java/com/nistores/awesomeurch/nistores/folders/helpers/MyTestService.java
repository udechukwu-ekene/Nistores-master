package com.nistores.awesomeurch.nistores.folders.helpers;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.json.JSONObject;

public class MyTestService extends IntentService {
    public static final String ACTION = "com.nistores.awesomeurch.nistores.Folders.Helpers.MyTestService";

    // Must create a default constructor
    public MyTestService() {
        // Used to name the worker thread, important only for debugging.
        super("test-service");
    }

    @Override
    public void onCreate() {
        super.onCreate(); // if you override onCreate(), make sure to call super().
        // If a Context object is needed, call getApplicationContext() here.
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // This describes what will happen when service is triggered
        // Fetch data passed into the intent on start
        final String val = intent.getStringExtra("foo");
        // Construct an Intent tying it to the ACTION (arbitrary event namespace)
        final Intent in = new Intent(ACTION);

        VolleyRequest volleyRequest = new VolleyRequest(getApplicationContext(), "http://192.168.43.60/pagesn/process_one.php?request=notification&id=172") {
            @Override
            public void onProcess() {
                //Log.d("REZOTT","processing");
            }

            @Override
            public void onSuccess(JSONObject response) {
                //Log.d("REZOTT","response");
                // Put extras into the intent as usual
                in.putExtra("resultCode", Activity.RESULT_OK);
                in.putExtra("resultValue", "My Result Value. Passed in: " + val);
                in.putExtra("resultJson",response.toString());
                // Fire the broadcast with intent packaged
                LocalBroadcastManager.getInstance(new MyTestService()).sendBroadcast(in);
                //sendBroadcast(in);
                // or sendBroadcast(in) for a normal broadcast;
            }

            @Override
            public void onNetworkError() {
                //Log.d("REZOTT","network error");
            }
        };
        volleyRequest.setCache(false);
        volleyRequest.fetchResources();

    }

}
