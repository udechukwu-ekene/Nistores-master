package com.nistores.awesomeurch.nistores.folders.helpers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.nistores.awesomeurch.nistores.folders.pages.AllOrdersActivity;
import com.nistores.awesomeurch.nistores.folders.pages.HomeActivity;
import com.nistores.awesomeurch.nistores.folders.pages.NotificationsActivity;
import com.nistores.awesomeurch.nistores.R;

import java.io.ByteArrayOutputStream;

/**
 * Created by Awesome Urch on 27/07/2018.
 * A class that utilizes all the methods frequently called in activity and fragment contexts
 */

public class Utility {
    public Context context;
    public SharedPreferences preferences;
    public Utility(Context c){
        context = c;
    }
    private String productsURL = "https://www.nistores.com.ng/api/src/routes/process_one.php?request=products";
    public String url2 = "https://www.nistores.com.ng/api/src/routes/process_user.php";
    public String getProductsURL(){
        return productsURL;
    }
    public String deliveryOrder_channelName = "Initiate Delivery";
    public String deliveryOrder_channelDesc = "Notifies you of the progress in initiating delivery";
    public String deliveryOrder_channelID = "channel_01";
    NotificationCompat.Builder mBuilder;
    public int NOTIF_INTERVAL = 1000 * 60 * 4; //4 mins

    public String bitmapToBase64(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        byte[] imageBytes = baos.toByteArray();
        final String imageString = Base64.encodeToString(imageBytes, 0);

        return imageString;
    }

    public String getSelectedCats(RecyclerView categoryRecycler){
        StringBuilder ret = new StringBuilder();
        int cnt = 0;
        for (int x = 0; x<categoryRecycler.getChildCount();x++){
            CheckBox cb = categoryRecycler.getChildAt(x).findViewById(R.id.name);
            TextView tv = categoryRecycler.getChildAt(x).findViewById(R.id.id);
            if(cb.isChecked()){
                String s = tv.getText().toString();
                String comma = (cnt>0)?",":"";
                ret.append(comma).append(s);
                cnt++;
                //Toast.makeText(getContext(),s,Toast.LENGTH_SHORT).show();
            }
        }
        return String.valueOf(ret);
    }

    public String getDeliveryOrder_channelDesc() {
        return deliveryOrder_channelDesc;
    }

    public String getDeliveryOrder_channelName() {
        return deliveryOrder_channelName;
    }

    public String getDeliveryOrder_channelID() {
        return deliveryOrder_channelID;
    }

    public void createNotification(String notifyId, String content){
        // Create an explicit intent for an Activity in your app
        Bundle bundle = new Bundle();
        bundle.putString("last_notification_id",notifyId);

        Intent intent = new Intent(context, NotificationsActivity.class);
        //intent.putExtra("last_notification_id",notifyId);
        intent.putExtras(bundle);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        int notificationId = Integer.parseInt(notifyId);
        String textTitle = "Nistores Notifications";
        mBuilder = new NotificationCompat.Builder(context, getDeliveryOrder_channelID())
                .setSmallIcon(R.drawable.ic_notification_color)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),
                        R.mipmap.ic_launcher))
                .setContentTitle(textTitle)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        mBuilder.setContentIntent(pendingIntent).setAutoCancel(true);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(notificationId, mBuilder.build());
        playSound();
    }

    public void playSound(){
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(context, notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void adminLogout(View view){
        Context vContext = view.getContext();
        preferences = PreferenceManager.getDefaultSharedPreferences(vContext);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove("isAdmin");
        editor.apply();
        Intent intent = new Intent(vContext,HomeActivity.class);
        vContext.startActivity(intent);
        new AllOrdersActivity().finish();
    }

    public void cancelAlarm(View view) {
        Context vContext = view.getContext();
        Intent intent = new Intent(vContext, MyAlarmReceiver.class);
        final PendingIntent pIntent = PendingIntent.getBroadcast(vContext, MyAlarmReceiver.REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarm = (AlarmManager) vContext.getSystemService(Context.ALARM_SERVICE);
        if (alarm != null) {
            alarm.cancel(pIntent);

        }
    }

    public String returnStateShortCode(String s){
        String code = "all";
        switch (s){
            case "FCT Abuja":
            case "Abuja":
                code = "Abj";
                break;
            case "Abia":
                code = "Abi";
                break;
            case "Adamawa":
                code = "Adm";
                break;
            case "Akwa Ibom":
                code = "Akw";
                break;
            case "Anambra":
                code = "Anb";
                break;
            case "Bauchi":
                code = "Bau";
                break;
            case "Bayelsa":
                code = "Bay";
                break;
            case "Benue":
                code = "Ben";
                break;
            case "Borno":
                code = "Bon";
                break;
            case "Cross River":
                code = "Cro";
                break;
            case "Delta":
                code = "Del";
                break;
            case "Ebonyi":
                code = "Ebn";
                break;
            case "Ekiti":
                code = "Ekt";
                break;
            case "Edo":
                code = "Edo";
                break;
            case "Enugu":
                code = "Eng";
                break;
            case "Gombe":
                code = "Gmb";
                break;
            case "Imo":
                code = "Imo";
                break;
            case "Jigawa":
                code = "Jgw";
                break;
            case "Kaduna":
                code = "Kad";
                break;
            case "Kano":
                code = "Kan";
                break;
            case "katsina":
                code = "Kat";
                break;
            case "Kebbi":
                code = "Keb";
                break;
            case "Kogi":
                code = "Kog";
                break;
            case "Kwara":
                code = "Kwa";
                break;
            case "Lagos":
                code = "Lag";
                break;
            case "Nasarawa":
                code = "Nas";
                break;
            case "Niger":
                code = "Nig";
                break;
            case "Ogun":
                code = "Ogn";
                break;
            case "Osun":
                code = "Osn";
                break;
            case "Oyo":
                code = "Oyo";
                break;
            case "Plateau":
                code = "Plt";
                break;
            case "Rivers":
                code = "Riv";
                break;
            case "Sokoto":
                code = "Skt";
                break;
            case "Taraba":
                code = "Tar";
                break;
            case "Yobe":
                code = "Yob";
                break;
            case "Zamfara":
                code = "Zam";
                break;
        }

        return code;
    }
}
