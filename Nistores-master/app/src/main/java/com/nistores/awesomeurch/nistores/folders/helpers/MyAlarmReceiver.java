package com.nistores.awesomeurch.nistores.folders.helpers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class MyAlarmReceiver extends BroadcastReceiver {
    public static final int REQUEST_CODE = 12345;
    public static final String ACTION = "com.nistores.awesomeurch.nistores.Folders.Helpers.alarm";
    SharedPreferences preferences;
    String last_id, last_notification_id;

    // Triggered by the Alarm periodically (starts the service to run task)
    @Override
    public void onReceive(Context context, Intent intent) {
        preferences = PreferenceManager.getDefaultSharedPreferences(context);


        if (preferences.contains("last_notification_id")) {
            last_notification_id = preferences.getString("last_notification_id",null);
            last_id = preferences.getString("last_id",null);
        }else{
            last_notification_id = "0";
            last_id = "0";
        }

        Intent i = new Intent(context, MyNotificationService.class);
        i.putExtra("foo", "momen");
        i.putExtra("last_notification_id",last_notification_id);
        i.putExtra("last_id",last_id);
        context.startService(i);
    }
}