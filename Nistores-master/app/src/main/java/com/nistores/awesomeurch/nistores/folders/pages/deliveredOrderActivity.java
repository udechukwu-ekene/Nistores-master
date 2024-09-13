package com.nistores.awesomeurch.nistores.folders.pages;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nistores.awesomeurch.nistores.folders.adapters.MorePhotoAdapter;
import com.nistores.awesomeurch.nistores.folders.helpers.ApiUrls;
import com.nistores.awesomeurch.nistores.folders.helpers.InitiateVolley;
import com.nistores.awesomeurch.nistores.folders.helpers.MorePhoto;
import com.nistores.awesomeurch.nistores.folders.helpers.Utility;
import com.nistores.awesomeurch.nistores.folders.helpers.VolleyRequest;
import com.nistores.awesomeurch.nistores.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class deliveredOrderActivity extends AppCompatActivity {

    TextView orderIdView, orderDescView, priceView, myLocationView, receiverLocationView, storeNameView, storeNumberView,
            receiverUsernameView, receiverFullNameView, confirmOrderView, orderStatusView, confirmReceiveView;
    RecyclerView photoRecyclerView;
    List<MorePhoto> morePhotos;
    MorePhotoAdapter mAdapter;
    VideoView videoView;
    ImageView playImage, stopImage;
    AppCompatButton payBtn, retryBtn, approveBtn, updateOrderBtn, confirmOrderBtn, confirmReceivingBtn;
    ConstraintLayout networkErrorLayout, loaderLayout;
    LinearLayout infoLayout, controlLayout, adminLayout, confirmOrderLayout, confirmReceivingLayout;
    RadioGroup orderRadioGroup;
    EditText confirmReceivingEditText, confirmOrderEditText;
    String userId, URL, postURL, id, number, storeNumber, desc, photosString, video, totalPrice, loc_from, loc_to, storeName,
            receiverFullName, receiverUsername, initiatedDate, orderStatus, savedVideo, confirmReceiving, confirmComment, sellerId, receiverId;
    String payment_received = "0";
    String package_sent = "0";
    ApiUrls apiUrls;
    JSONArray photos;
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivered_order);

        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        userId = prefs.getString("user",null);

        apiUrls = new ApiUrls();
        URL = apiUrls.getApiUrl();
        postURL = apiUrls.getProcessPost();

        orderIdView = findViewById(R.id.order_id);
        orderDescView = findViewById(R.id.order_desc);
        priceView = findViewById(R.id.total_price);
        myLocationView = findViewById(R.id.your_location);
        receiverLocationView = findViewById(R.id.receiver_location);
        storeNameView = findViewById(R.id.store_name);
        storeNumberView = findViewById(R.id.store_number);
        receiverUsernameView = findViewById(R.id.receiver_username);
        receiverFullNameView = findViewById(R.id.receiver_full_name);
        confirmOrderView = findViewById(R.id.confirm_order);
        orderStatusView = findViewById(R.id.order_status);
        confirmReceiveView = findViewById(R.id.confirm_receiving);
        videoView = findViewById(R.id.video);
        controlLayout = findViewById(R.id.controls);
        playImage = findViewById(R.id.play);
        stopImage = findViewById(R.id.stop);

        playImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                videoView.start();
            }
        });
        stopImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                videoView.pause();
            }
        });

        networkErrorLayout = findViewById(R.id.network_error_layout);
        loaderLayout = findViewById(R.id.loader_layout);
        infoLayout = findViewById(R.id.info_layout);
        adminLayout = findViewById(R.id.admin_section);
        confirmOrderLayout = findViewById(R.id.confirm_order_layout);
        confirmReceivingLayout = findViewById(R.id.confirm_receiving_layout);
        confirmOrderEditText = findViewById(R.id.msg_confirm_order);
        confirmReceivingEditText = findViewById(R.id.msg_confirm_receiving);
        orderRadioGroup = findViewById(R.id.orderGroup);

        View.OnClickListener onRadioButtonClicked = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSelectStatus(view);
            }
        };

        for(int x = 0; x < orderRadioGroup.getChildCount(); x++){
            View child = orderRadioGroup.getChildAt(x);
            child.setOnClickListener(onRadioButtonClicked);
            if(x==0){
                ((RadioButton) child).setChecked(true);
            }
        }

        photoRecyclerView = findViewById(R.id.recycler_photo_view);
        morePhotos = new ArrayList<>();
        mAdapter = new MorePhotoAdapter(getApplicationContext(), morePhotos);
        //mAdapter.setServer(false);

        GridLayoutManager cLayoutManager = new GridLayoutManager(getApplicationContext(), 1);
        cLayoutManager.setOrientation(LinearLayout.HORIZONTAL);
        //RecyclerView.LayoutManager cLayoutManager = new LinearLayoutManager(getApplicationContext());
        photoRecyclerView.setLayoutManager(cLayoutManager);

        photoRecyclerView.setItemAnimator(new DefaultItemAnimator());
        photoRecyclerView.setAdapter(mAdapter);

        View.OnClickListener onUpdate = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateOrder();
            }
        };
        View.OnClickListener onApprove = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                approveOrder();
            }
        };

        View.OnClickListener onSelectPay = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toSelectPayMethod();
            }
        };

        View.OnClickListener onConfirm1 = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String comment = confirmOrderEditText.getText().toString();
                if(!comment.isEmpty()){
                    String encodedMsg = "";
                    try {
                        encodedMsg = URLEncoder.encode(comment,"utf-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    String pURL = URL + "request=comment_confirm_order&comment=" + encodedMsg + "&order_no=" + number;
                    pushComment(pURL,"confirm_order");
                }
            }
        };

        View.OnClickListener onConfirm2 = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String comment = confirmReceivingEditText.getText().toString();
                if(!comment.isEmpty()){
                    String encodedMsg = "";
                    try {
                        encodedMsg = URLEncoder.encode(comment,"utf-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    String pURL = URL + "request=comment_confirm_receiving&comment=" + encodedMsg + "&order_id=" + number;
                    pushComment(pURL,"confirm_receive");
                }
            }
        };

        payBtn = findViewById(R.id.btn_pay);
        retryBtn = findViewById(R.id.btn_retry);
        approveBtn = findViewById(R.id.btn_approve);
        updateOrderBtn = findViewById(R.id.update_order);
        updateOrderBtn.setOnClickListener(onUpdate);
        approveBtn.setOnClickListener(onApprove);
        payBtn.setOnClickListener(onSelectPay);

        confirmOrderBtn = findViewById(R.id.btn_confirm_order);
        confirmReceivingBtn = findViewById(R.id.btn_confirm_receiving);
        confirmOrderBtn.setOnClickListener(onConfirm1);
        confirmReceivingBtn.setOnClickListener(onConfirm2);

        Bundle bundle = this.getIntent().getExtras();
        if(bundle!=null){
            id = bundle.getString("id");
            number = bundle.getString("number");
            if(number != null){
                getInfo();
            }

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        if (prefs.contains("isAdmin")) {
            getMenuInflater().inflate(R.menu.main_admin, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                new Utility(getApplicationContext()).adminLogout(orderIdView);
                // User chose the "Settings" item, show the app settings UI...
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    private void onSelectStatus(View view){
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.awaiting:
                if (checked)
                    //selectedMethod = ATM;
                break;
            case R.id.received:
                if (checked)
                    payment_received = "1";
                break;
            case R.id.sent:
                if (checked)
                    package_sent = "1";
                break;

        }
        //Toast.makeText(this,selectedMethod,Toast.LENGTH_SHORT).show();
    }

    private void toSelectPayMethod(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("current_store_id",number).apply();
        editor.putString("current_store_uid",number).apply();
        Bundle bundle = new Bundle();
        bundle.putString("pay_for","delivery_order");
        bundle.putString("uid",number);
        bundle.putString("amount",totalPrice);
        Intent intent = new Intent(this,SelectPaymentActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void getInfo(){

        String pURL = URL + "request=state_order&order_no=" + number;
        //Log.d("myURL",pURL);
        VolleyRequest volleyRequest = new VolleyRequest(getApplicationContext(), pURL) {
            @Override
            public void onProcess() {
                networkErrorLayout.setVisibility(View.GONE);
                loaderLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onSuccess(JSONObject response) {
                //Log.d("MPUTA",response.toString());
                loaderLayout.setVisibility(View.GONE);
                try {

                    Integer err = response.getInt("error");
                    if(err==0){
                        JSONObject data = response.getJSONObject("data");
                        fillInItems(data);

                    }else{
                        Toast.makeText(getApplicationContext(),"Network Error Occurred",Toast.LENGTH_SHORT).show();
                        networkErrorLayout.setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
                    //Log.e("V_ERROR",e.toString());
                    e.printStackTrace();
                }
            }

            @Override
            public void onNetworkError() {
                loaderLayout.setVisibility(View.GONE);
                networkErrorLayout.setVisibility(View.VISIBLE);

            }
        };
        volleyRequest.setCache(false);
        volleyRequest.fetchResources();
    }

    public void fillInItems(JSONObject data){
        infoLayout.setVisibility(View.VISIBLE);
        if (prefs.contains("isAdmin")) {
            adminLayout.setVisibility(View.VISIBLE);
        }

        try {
            storeNumber = data.getString("number");
            desc = data.getString("description");
            photos = data.getJSONArray("photos");
            video = data.getString("videos");
            totalPrice = data.getString("total_price");
            loc_from = data.getString("loc_from");
            loc_to = data.getString("loc_to");
            storeName = data.getString("store_name");
            receiverFullName = data.getString("receiver_fullname");
            receiverUsername = data.getString("receiver_username");
            initiatedDate = data.getString("initiated_date");
            orderStatus = data.getString("order_status");
            confirmReceiving = data.getString("confirm_receiving");
            confirmComment = data.getString("confirm_comment");
            payment_received = data.getString("payment_received");
            package_sent = data.getString("package_sent");
            sellerId = data.getString("seller_id");
            receiverId = data.getString("receiver_id");


            if(userId.equals(receiverId)){
                confirmReceivingLayout.setVisibility(View.VISIBLE);
                confirmOrderLayout.setVisibility(View.VISIBLE);
                if(!payment_received.equals("1")){
                    payBtn.setVisibility(View.VISIBLE);
                }
            }

            if(confirmComment.equals("null")){
                confirmOrderView.setText(getResources().getString(R.string.no_comment_from_receiver));
            }else{
                confirmOrderLayout.setVisibility(View.GONE);
            }

            if(confirmReceiving.equals("null")){
                confirmReceiveView.setText(getResources().getString(R.string.no_comment_from_receiver));
            }else{
                confirmReceiveView.setVisibility(View.GONE);
            }

            if(orderStatus.equals("1")){
                approveBtn.setText(getResources().getString(R.string.already_approved));
                approveBtn.setEnabled(false);
            }
            if(payment_received.equals("1")){
                ((RadioButton) orderRadioGroup.getChildAt(1)).setChecked(true);
                orderStatusView.setText(getResources().getString(R.string.payment_received));

            }
            if(package_sent.equals("1")){
                ((RadioButton) orderRadioGroup.getChildAt(2)).setChecked(true);
                orderStatusView.setText(getResources().getString(R.string.package_sent));
            }

            orderIdView.setText(number);
            storeNumberView.setText(storeNumber);
            orderDescView.setText(desc);
            priceView.setText(totalPrice);
            myLocationView.setText(loc_from);
            receiverLocationView.setText(loc_to);
            storeNameView.setText(storeName);
            receiverFullNameView.setText(receiverFullName);
            receiverUsernameView.setText(receiverUsername);

            //Loop through photos array and display in recyclerView
            for(int i = 0; i < photos.length(); i++){
                final String serverPic = photos.getString(i);
                new Handler()
                        .postDelayed(new Runnable(){
                            public void run(){
                                appendMorePhotos(serverPic);
                            }
                        }, 2000);
            }

            //get video uri and set the video
            String vidStr = new ApiUrls().getOffline() + video;
            Uri vidUri = Uri.parse(vidStr);
            setVideo(vidUri);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void appendMorePhotos(String res){
        //Log.d("iPATH",""+res);
        try{
            JSONArray array = new JSONArray();
            JSONObject object = new JSONObject();
            object.put("image",res);
            array.put(object);

            List<MorePhoto> items = new Gson().fromJson(array.toString(), new TypeToken<List<MorePhoto>>() {
            }.getType());

            /*if (!morePhotos.isEmpty())

                morePhotos.clear(); //The list for update recycle view

            mAdapter.notifyDataSetChanged();*/
            //final int curSize = mAdapter.getItemCount();
            morePhotos.addAll(items);
            photoRecyclerView.post(new Runnable() {
                @Override
                public void run() {
                    //scroller.resetState();
                    //photoRecycler.getRecycledViewPool().clear();
                    mAdapter.notifyItemInserted(morePhotos.size() - 1);
                    //mAdapter.notifyItemRangeInserted(curSize, morePhotos.size() - 1);

                }
            });

        }catch(JSONException e){
            //Log.d("ERR",e.toString());
            e.printStackTrace();
        }
    }

    private void setVideo(Uri videoUri){
        if(videoUri != null){
            savedVideo = videoUri+"";
        }

        //Log.d("REZOT",videoUri+"");
        videoView.setVideoURI(videoUri);
        videoView.requestFocus();
        videoView.start();
        controlLayout.setVisibility(View.VISIBLE);
    }

    private void approveOrder(){
        preventInteraction();
        approveBtn.setText(getResources().getString(R.string.processing));
        StringRequest request = new StringRequest(Request.Method.POST, postURL, new Response.Listener<String>(){
            @Override
            public void onResponse(String s) {
                approveBtn.setText(getResources().getString(R.string.approve_delivery_order));
                enableUserInteraction();
                //Log.d("DFILE",s);

                if(s.equals("success")){
                    Toast.makeText(getApplicationContext(),"Approved successfully!",Toast.LENGTH_LONG).show();
                    approveBtn.setText(getResources().getString(R.string.already_approved));
                    approveBtn.setEnabled(false);
                    orderStatus = "1";

                }
                else{
                    Toast.makeText(getApplicationContext(),"Sorry an error occurred. Retry",Toast.LENGTH_LONG).show();
                }
            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                enableUserInteraction();
                updateOrderBtn.setText(getResources().getString(R.string.approve_delivery_order));
                Toast.makeText(getApplicationContext(),"Network error occurred. Please retry!",Toast.LENGTH_SHORT).show();

                //Log.d("ERR",volleyError.toString());


            }
        }) {
            @Override
            public Map<String, String> getHeaders()
            {
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("Connection", "Keep-Alive");
                return parameters;
            }

            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            //adding parameters to send
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> parameters = new HashMap<>();
                parameters.put("request", "admin_approve_delivery");
                parameters.put("status", "1");
                parameters.put("seller_id", sellerId);
                parameters.put("receiver_id", receiverId);
                parameters.put("order_no", number);

                return parameters;
            }
        };

        //RequestQueue rQueue = Volley.newRequestQueue(getContext());
        request.setShouldCache(false);
        InitiateVolley.getInstance().addToRequestQueue(request);
    }

    private void updateOrder(){

        if(!orderStatus.equals("1")){
            Toast.makeText(getApplicationContext(),"Approve this order first",Toast.LENGTH_SHORT).show();
            return;
        }

        preventInteraction();
        updateOrderBtn.setText(getResources().getString(R.string.processing));
        StringRequest request = new StringRequest(Request.Method.POST, postURL, new Response.Listener<String>(){
            @Override
            public void onResponse(String s) {
                updateOrderBtn.setText(getResources().getString(R.string.update));
                enableUserInteraction();
                //Log.d("DFILE",s);

                if(s.equals("success")){

                    Toast.makeText(getApplicationContext(),"Updated successfully!",Toast.LENGTH_LONG).show();

                }
                else{
                    Toast.makeText(getApplicationContext(),"Sorry an error occurred. Retry",Toast.LENGTH_LONG).show();
                }
            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                enableUserInteraction();
                updateOrderBtn.setText(getResources().getString(R.string.update));
                Toast.makeText(getApplicationContext(),"Network error occurred. Please retry!",Toast.LENGTH_SHORT).show();

                //Log.d("ERR",volleyError.toString());


            }
        }) {
            @Override
            public Map<String, String> getHeaders()
            {
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("Connection", "Keep-Alive");
                return parameters;
            }

            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            //adding parameters to send
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> parameters = new HashMap<>();
                parameters.put("request", "update_delivery_order");
                parameters.put("package_sent", package_sent);
                parameters.put("payment_received", payment_received);
                parameters.put("order_no", number);

                return parameters;
            }
        };

        //RequestQueue rQueue = Volley.newRequestQueue(getContext());
        request.setShouldCache(false);
        InitiateVolley.getInstance().addToRequestQueue(request);
    }

    public void preventInteraction(){
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    public void enableUserInteraction(){
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }



    private void pushComment(final String URL, final String type){

        VolleyRequest volleyRequest = new VolleyRequest(getApplicationContext(), URL) {
            @Override
            public void onProcess() {
                preventInteraction();
                if(type.equals("confirm_order")){
                    confirmOrderBtn.setText(getResources().getString(R.string.sending));
                }else{
                    confirmReceivingBtn.setText(getResources().getString(R.string.sending));
                }
            }

            @Override
            public void onSuccess(JSONObject response) {
                enableUserInteraction();
                if(type.equals("confirm_order")){
                    confirmOrderBtn.setText(getResources().getString(R.string.confirm));
                }else{
                    confirmReceivingBtn.setText(getResources().getString(R.string.confirm));
                }
                try {

                    Integer err = response.getInt("error");
                    if(err==0){

                        String data = response.getString("data");
                        if(type.equals("confirm_order")){
                            confirmOrderLayout.setVisibility(View.GONE);
                            confirmOrderView.setText(data);
                        }else{
                            confirmReceivingLayout.setVisibility(View.GONE);
                            confirmReceiveView.setText(data);
                        }

                    }else{
                        Toast.makeText(getApplicationContext(),"Server error occurred. Please resend",Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    //Log.e("V_ERROR",e.toString());
                    e.printStackTrace();
                }
            }

            @Override
            public void onNetworkError() {
                enableUserInteraction();
                if(type.equals("confirm_order")){
                    confirmOrderBtn.setText(getResources().getString(R.string.confirm));
                }else{
                    confirmReceivingBtn.setText(getResources().getString(R.string.confirm));
                }
                Toast.makeText(getApplicationContext(),"Network error occurred. Please resend",Toast.LENGTH_SHORT).show();
            }
        };
        volleyRequest.setCache(false);
        volleyRequest.fetchResources();
    }

}
