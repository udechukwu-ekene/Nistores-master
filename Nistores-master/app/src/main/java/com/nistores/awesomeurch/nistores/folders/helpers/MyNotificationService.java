package com.nistores.awesomeurch.nistores.folders.helpers;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class MyNotificationService extends IntentService {
    SharedPreferences prefs;

    public MyNotificationService() {
        super("MyNotificationService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        WakefulBroadcastReceiver.completeWakefulIntent(intent);

        // Do the task here
        final Utility utility = new Utility(getApplicationContext());
        final String last_id = intent.getStringExtra("last_id");
        final String last_notification_id = intent.getStringExtra("last_notification_id");
        //Log.i("MyNotificationServicee", "l_n_id: "+last_notification_id + " l_id: "+last_id);

        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String userId = prefs.getString("user",null);
        String URL = new ApiUrls().getApiUrl();
        String cURL = URL + "request=check_notif&id=" + userId + "&last_id=" + last_id;
        //Log.d("REZOTT",cURL);

        VolleyRequest volleyRequest = new VolleyRequest(getApplicationContext(), cURL) {
            @Override
            public void onProcess() {
                //Log.d("REZOTT","processing");
            }

            @Override
            public void onSuccess(JSONObject response) {
                //Log.d("REZOTT","response ::"+response);
                // Put extras into the intent as usual
                try{
                    Integer err = response.getInt("error");
                    if(err==0){
                        JSONObject data = response.getJSONObject("data");
                        String id = data.getString("notify_id");
                        String content = data.getString("ncontent");
                        String event = data.getString("nevent");
                        //update last id that will be sent to server to check for new notifications
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("last_id",id).apply();

                        utility.createNotification(last_notification_id,content);
                    }
                }catch (JSONException e){
                    //Log.d("ERR",e.toString());
                }
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