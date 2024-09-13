package com.nistores.awesomeurch.nistores.folders.pages;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.VideoView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nistores.awesomeurch.nistores.folders.adapters.MorePhotoAdapter;
import com.nistores.awesomeurch.nistores.folders.helpers.ApiUrls;
import com.nistores.awesomeurch.nistores.folders.helpers.FileUpload;
import com.nistores.awesomeurch.nistores.folders.helpers.InitiateVolley;
import com.nistores.awesomeurch.nistores.folders.helpers.MorePhoto;
import com.nistores.awesomeurch.nistores.folders.helpers.Utility;
import com.nistores.awesomeurch.nistores.folders.helpers.VolleyRequest;
import com.nistores.awesomeurch.nistores.folders.helpers.uploadFile;
import com.nistores.awesomeurch.nistores.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InitiateDeliveryActivity extends AppCompatActivity {
    Spinner storeSpinner, myLocationSpinner, receiverLocationSpinner;
    ArrayList<String> myStores, statesArrayList;
    SharedPreferences prefs;
    String userId, states;
    ApiUrls apiUrls;
    String URL, postURL, storeID, savedVideo, buyerFullName, buyerID;
    JSONArray userStores, statesJsonArray, picturesArray;
    CoordinatorLayout coordinatorLayout;
    ConstraintLayout loaderLayout;
    LinearLayout controlLayout;
    ImageView playImage, stopImage;
    RecyclerView photoRecycler;
    List<MorePhoto> morePhotos;
    MorePhotoAdapter mAdapter;
    AppCompatButton deliveryBtn, btnLivePhotos, btnLiveVideos;
    VideoView videoView;
    String orderId, savedPhotos, myStoresString, storeName,
            myLocation, buyersLocation, myFullLocation, buyersFullLocation, videoPath, allPhotos, uploadedVideoPath;
    StringBuilder uploadedPhotos;
    int picturesCount;
    EditText orderNumberView, descView, totalPriceView, myStoreNo, receiverFullname, receiverUsername;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_VIDEO_CAPTURE = 2;
    static final String selectMyLocation = "myLocation";
    static final String selectBuyerLocation = "buyerLocation";
    static final String JPG = "jpg";
    String desc, totalPrice, storeNumber, usernameReceiver, fullnameReceiver;
    Uri videoUri;
    Bitmap bitmap;
    Utility utility;
    NotificationCompat.Builder mBuilder;
    ProgressDialog uploading;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initiate_delivery);

        photoRecycler = findViewById(R.id.recycler_photo_view);
        morePhotos = new ArrayList<>();
        mAdapter = new MorePhotoAdapter(getApplicationContext(), morePhotos);
        mAdapter.setServer(false);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
        //RecyclerView.LayoutManager cLayoutManager = new LinearLayoutManager(getApplicationContext());
        photoRecycler.setLayoutManager(mLayoutManager);
        //photoRecycler.setLayoutManager(new WrapContentGridLayoutManager(getApplicationContext(), 2));

        //photoRecycler.setHasFixedSize(true);

        photoRecycler.setItemAnimator(new DefaultItemAnimator());
        photoRecycler.setAdapter(mAdapter);

        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        userId = prefs.getString("user",null);

        loaderLayout = findViewById(R.id.loader_layout);
        videoView = findViewById(R.id.video);
        btnLivePhotos = findViewById(R.id.btn_live_photos);
        btnLiveVideos = findViewById(R.id.btn_live_videos);
        btnLivePhotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCamera();
            }
        });
        btnLiveVideos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakeVideoIntent();
            }
        });

        deliveryBtn = findViewById(R.id.btn_deliver);
        deliveryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkInputs();
            }
        });

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

        orderNumberView = findViewById(R.id.order_id);
        descView = findViewById(R.id.content_desc);
        totalPriceView = findViewById(R.id.price);

        myStoreNo = findViewById(R.id.snumber);
        receiverFullname = findViewById(R.id.input_receiver_name);
        receiverUsername = findViewById(R.id.input_receiver_username);
        receiverUsername.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                /* When focus is lost check that the text field
                 * has valid values.
                 */
                if (!hasFocus) {
                    confirmUsername(); //you can pass view
                }
            }
        });

        myStores = new ArrayList<>();
        storeSpinner = findViewById(R.id.select_store);
        storeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                storeName = adapterView.getItemAtPosition(i).toString();
                //Log.d("SEL_ST",storeName+" "+i);
                loadCategories(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //Log.d("SEL_ST","nothing selected");
            }
        });

        statesArrayList = new ArrayList<>();
        myLocationSpinner = findViewById(R.id.my_location);
        receiverLocationSpinner = findViewById(R.id.receiver_location);
        myLocationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                myFullLocation = adapterView.getItemAtPosition(i).toString();
                selectLocation(i,selectMyLocation);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //Log.d("SEL_ST","nothing selected");
            }
        });
        receiverLocationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                buyersFullLocation = adapterView.getItemAtPosition(i).toString();
                selectLocation(i,selectBuyerLocation);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //Log.d("SEL_ST","nothing selected");
            }
        });

        coordinatorLayout = findViewById(R.id.myCoordinatorLayout);
        apiUrls = new ApiUrls();
        URL = apiUrls.getApiUrl();
        postURL = apiUrls.getProcessPost();
        utility = new Utility(getApplicationContext());
        uploadedPhotos = new StringBuilder();

        Bundle bundle = this.getIntent().getExtras();
        if(bundle!=null){
            orderId = bundle.getString("orderNumber");
            states = bundle.getString("states");
            orderNumberView.setText(orderId);
            //fetchItems();
            if(states != null){
                try {
                    statesJsonArray = new JSONArray(states);
                    fillInStates(statesJsonArray);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        if(savedInstanceState != null){
            savedPhotos = savedInstanceState.getString("photos");
            myStoresString = savedInstanceState.getString("myStores");
            savedVideo = savedInstanceState.getString("video");
            if(savedPhotos != null){
                //Log.d("SAVED",savedPhotos);
                try {
                    JSONArray array = new JSONArray(savedPhotos);
                    //Log.d("LEN",""+array.length());
                    for(int i = 0; i < array.length(); i++){
                        final String encodedPic = array.getString(i);
                        new Handler()
                                .postDelayed(new Runnable(){
                                    public void run(){
                                        appendMorePhotos(encodedPic);
                                    }
                                }, 1600);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            if(myStoresString != null){
                try {
                    userStores = new JSONArray(myStoresString);
                    //userStores = storeArray;
                    fillInItems(userStores);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else{
                fetchItems();
            }

            if(savedVideo != null){
                setVideo(Uri.parse(savedVideo));
            }

        }else{
            //Log.d("SAVED","nothing saved");
            fetchItems();
        }


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //outState.putStringArrayList("photos",morePhotos);
        outState.putString("photos",getRecyclerResources());
        outState.putString("myStores",myStoresString);
        outState.putString("video",savedVideo);
        //Log.d("SAVED","onSaveIns");
    }

    private void openCamera(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private void dispatchTakeVideoIntent() {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(resultCode == RESULT_OK){


            if(requestCode == REQUEST_IMAGE_CAPTURE){
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                final String resourceBase = utility.bitmapToBase64(imageBitmap);
                //Log.d("REZOT",resourceBase);
                //uploadOtherFiles(resourceBase, JPG);
                new Handler()
                        .postDelayed(new Runnable(){
                            public void run(){
                                appendMorePhotos(resourceBase);
                            }
                        }, 1500);

            }else if(requestCode == REQUEST_VIDEO_CAPTURE){
                videoUri = data.getData();
                setVideo(videoUri);
                videoPath = getPath(videoUri);
                //Log.d("VIDEOP",videoPath);
            }
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

    public String getPath(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();

        cursor = getContentResolver().query(
                android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
        cursor.close();

        return path;
    }

    private void fillInStates(JSONArray items){
        try{
            for(int i=0;i<items.length();i++){
                JSONObject jsonObject1=items.getJSONObject(i);
                String storeName=jsonObject1.getString("mcat_title");
                statesArrayList.add(storeName);
            }

            myLocationSpinner.setAdapter(new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_item, statesArrayList));
            receiverLocationSpinner.setAdapter(new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_item, statesArrayList));

        }catch (JSONException e){e.printStackTrace();}

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
            photoRecycler.post(new Runnable() {
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


    private void networkError(){
        Snackbar snackbar = Snackbar.make(coordinatorLayout, R.string.network_error,
                Snackbar.LENGTH_LONG)
                .setAction("RETRY", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        fetchItems();
                    }
                });
        snackbar.show();
    }

    private void fetchItems(){
        String originURL = URL + "request=my_stores&id=" + userId;
        //Log.d("CHECK",originURL);
        loaderLayout.setVisibility(View.VISIBLE);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, originURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        loaderLayout.setVisibility(View.GONE);
                        //Log.d("RTN",response.toString());
                        try {

                            Integer err = response.getInt("error");
                            if(err==0){
                                userStores = response.getJSONArray("data");
                                myStoresString = userStores.toString();
                                fillInItems(userStores);

                            }else{
                                Toast.makeText(getApplicationContext(),"Sorry an error occurred",Toast.LENGTH_SHORT).show();
                                networkError();
                            }
                        } catch (JSONException e) {
                            //Log.e("ERR",e.toString());
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loaderLayout.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(),"Sorry an error occurred. Try again",Toast.LENGTH_SHORT).show();
                networkError();
                //Log.d("VOLLEY",error.toString());

            }
        });
        jsonObjectRequest.setShouldCache(false);
        InitiateVolley.getInstance().addToRequestQueue(jsonObjectRequest);
    }

    private void fillInItems(JSONArray items){
        try{
            for(int i=0;i<items.length();i++){
                JSONObject jsonObject1=items.getJSONObject(i);
                String storeName=jsonObject1.getString("sname");
                myStores.add(storeName);
            }

            storeSpinner.setAdapter(new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_item, myStores));

        }catch (JSONException e){e.printStackTrace();}

    }

    private void loadCategories(final int index){
        //String storeId = null;
        try {
            JSONObject storeObj = userStores.getJSONObject(index);
            String storeCatId = storeObj.getString("scat_id");
            storeID = storeObj.getString("store_id");
            //Log.d("storeID", storeID);

        } catch (JSONException e) {
            e.printStackTrace();
            //Log.d("storeCatID", ""+e);
        }
    }

    private void selectLocation(final int index, final String whoseLocation){
        try{
            JSONObject locObj = statesJsonArray.getJSONObject(index);
            String loc = locObj.getString("mcat_link");
            if(whoseLocation.equals(selectMyLocation)){
                myLocation = loc;
            }else if(whoseLocation.equals(selectBuyerLocation)){
                buyersLocation = loc;
            }
            //Log.d("locID",loc);
        }catch(JSONException e){
            //Log.d("locID",""+e);
        }
    }

    private void checkInputs(){
        boolean valid = true;
        desc = descView.getText().toString();
        totalPrice = totalPriceView.getText().toString();
        storeNumber = myStoreNo.getText().toString();
        usernameReceiver = receiverUsername.getText().toString();
        fullnameReceiver = receiverFullname.getText().toString();
        final String myPicturesString = getRecyclerResources();

        if(savedVideo==null){
            valid = false;
            Toast.makeText(getApplicationContext(),"Take live video of you and the product",Toast.LENGTH_SHORT).show();
        }
        if(myPicturesString.equals("[]")){
            valid = false;
            Toast.makeText(getApplicationContext(),"Take live photos of the product",Toast.LENGTH_SHORT).show();
        }
        if(desc.isEmpty()){
            valid = false;
            descView.setError("Enter the description");
            Toast.makeText(getApplicationContext(),"Enter the description",Toast.LENGTH_SHORT).show();
        }
        if(totalPrice.isEmpty()){
            valid = false;
            totalPriceView.setError("Enter the total price");
            Toast.makeText(getApplicationContext(),"Enter the total price",Toast.LENGTH_SHORT).show();
        }
        if(storeNumber.isEmpty()){
            valid = false;
            myStoreNo.setError("Enter your store number");
            Toast.makeText(getApplicationContext(),"Enter your store number",Toast.LENGTH_SHORT).show();
        }

        if(buyerID==null){
            valid = false;
            receiverUsername.setError("Re-enter correctly the receiver's username");
            Toast.makeText(getApplicationContext(),"Enter receiver's username",Toast.LENGTH_SHORT).show();
        }else{
            if(buyerID.equals(userId)){
                valid = false;
                receiverUsername.setError("You can't sell to yourself!");
                Toast.makeText(getApplicationContext(),"Please enter receiver's username. You can't sell to yourself!",Toast.LENGTH_SHORT).show();
            }
        }
        if(usernameReceiver.isEmpty()){
            valid = false;
            receiverUsername.setError("Enter receiver's username");
            Toast.makeText(getApplicationContext(),"Enter receiver's username",Toast.LENGTH_SHORT).show();
        }

        if(fullnameReceiver.isEmpty()){
            valid = false;
            receiverFullname.setError("Enter receiver's full name");
            Toast.makeText(getApplicationContext(),"Enter receiver's full name",Toast.LENGTH_SHORT).show();
        }


        if(valid){
            try {
                picturesArray = new JSONArray(myPicturesString);
                picturesCount = picturesArray.length();
                if(picturesCount > 0){
                    uploadMediaFiles(picturesArray.getString(0),JPG, 0);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    //method for uploading other photos
    private void uploadMediaFiles(final String fileEncoded, final String ext, final int status){


        FileUpload fileUpload = new FileUpload(getApplicationContext()) {
            @Override
            public void onProcess() {
                loaderLayout.setVisibility(View.VISIBLE);
                preventInteraction();
                deliveryBtn.setText(getResources().getString(R.string.uploading___));
            }

            @Override
            public void onSuccess(String imgPath) {
                //Log.d("PTHH",imgPath);
                String serverImg = "api/src/routes/"+imgPath;
                String comma = (status>0)?",":"";
                uploadedPhotos.append(comma).append(serverImg);

                int current = status + 1;
                if(picturesCount > current){

                    try {
                        uploadMediaFiles(picturesArray.getString(current),JPG,current);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else{
                    //Log.d("PTHH","finished pics");
                    uploadVideo();
                }

                //enableUserInteraction();
                //Toast.makeText(context, "Uploaded Successfully", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onServerError() {
                loaderLayout.setVisibility(View.GONE);
                enableUserInteraction();
                Toast.makeText(getApplicationContext(), "Server error occurred. Try again", Toast.LENGTH_LONG).show();
                deliveryBtn.setText(getResources().getString(R.string.deliver_order));

            }

            @Override
            public void onNetworkError() {
                loaderLayout.setVisibility(View.GONE);
                enableUserInteraction();
                Toast.makeText(getApplicationContext(), "Network error occurred. Try again", Toast.LENGTH_LONG).show();
                deliveryBtn.setText(getResources().getString(R.string.deliver_order));

            }
        };
        fileUpload.uploadImage(fileEncoded, ext);
    }


    private void uploadVideo() {
        class UploadVideo extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                Toast.makeText(getApplicationContext(), "Network error occurred. Try again", Toast.LENGTH_LONG).show();
                //uploading = ProgressDialog.show(InitiateDeliveryActivity.this, "Uploading Video", "Please wait...", false, false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                //Log.d("REZOT",s);
                uploadedVideoPath = s;
                updateChanges();

                /*textViewResponse.setText(Html.fromHtml("<b>Uploaded at <a href='" + s + "'>" + s + "</a></b>"));
                textViewResponse.setMovementMethod(LinkMovementMethod.getInstance());*/
            }

            @Override
            protected String doInBackground(Void... params) {
                uploadFile u = new uploadFile();
                return u.uploadVideo(videoPath);
            }
        }
        UploadVideo uv = new UploadVideo();
        uv.execute();
    }

    private void createNotification(){
        int notificationId = 1;
        String textTitle = "Delivery Order Status";
        String textContent = "Creating your delivery order";
        mBuilder = new NotificationCompat.Builder(this, utility.getDeliveryOrder_channelID())
                .setSmallIcon(R.drawable.ic_file_upload_black_24dp)
                .setContentTitle(textTitle)
                .setContentText(textContent)
                .setPriority(NotificationCompat.PRIORITY_LOW);

        mBuilder.setProgress(0,0,true)
                .setAutoCancel(true);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(notificationId, mBuilder.build());
    }

    private void updateChanges(){
        final String serverVideo = "api/src/routes/"+uploadedVideoPath;
        preventInteraction();
        //btnSaveProfile.setText(getResources().getString(R.string.loading));
        StringRequest request = new StringRequest(Request.Method.POST, postURL, new Response.Listener<String>(){
            @Override
            public void onResponse(String s) {
                loaderLayout.setVisibility(View.GONE);
                //uploading.dismiss();
                deliveryBtn.setText(getResources().getString(R.string.deliver_order));
                enableUserInteraction();
                //Log.d("DFILE",s);

                if(s.equals("error")){

                    Toast.makeText(getApplicationContext(),"Sorry an error occurred. Retry",Toast.LENGTH_SHORT).show();

                }
                else{
                    Toast.makeText(getApplicationContext(),"Your order has been delivered!",Toast.LENGTH_SHORT).show();
                    Bundle bundle = new Bundle();
                    bundle.putString("number",orderId);
                    //bundle.putString("id",id);
                    intent = new Intent(getApplicationContext(),deliveredOrderActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                enableUserInteraction();
                loaderLayout.setVisibility(View.GONE);
                deliveryBtn.setText(getResources().getString(R.string.deliver_order));
                Toast.makeText(getApplicationContext(),"Network error occurred. Please retry!",Toast.LENGTH_SHORT).show();

                //Log.d("ERR",volleyError.toString());


            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError
            {
                Map<String, String> parameters = new HashMap<String, String>();
                //parameters.put("Content-Type", "application/form-data");
                //parameters.put("Content-Length", ""+97957);
                parameters.put("Connection", "Keep-Alive");
                return parameters;
            }

            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
                //return "application/x-www-form-urlencoded";
            }

            //adding parameters to send
            @Override
            protected Map<String, String> getParams() throws AuthFailureError  {
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("request", "deliver_order");
                parameters.put("receiver_id", buyerID);
                parameters.put("number", storeNumber);
                parameters.put("desc", desc);
                parameters.put("photos", String.valueOf(uploadedPhotos));
                parameters.put("video", serverVideo);
                parameters.put("price", totalPrice);
                parameters.put("loc_from", myLocation);
                parameters.put("loc_to", buyersLocation);
                parameters.put("store_name", storeName);
                parameters.put("store_id", storeID);
                parameters.put("seller_id", userId);
                parameters.put("store_number", storeNumber);
                parameters.put("receiver_fullname", buyerFullName);
                parameters.put("receiver_username", receiverUsername.getText().toString());
                parameters.put("order_no", orderId);

                return parameters;
            }
        };

        //RequestQueue rQueue = Volley.newRequestQueue(getContext());
        request.setShouldCache(false);
        InitiateVolley.getInstance().addToRequestQueue(request);
    }

    private String getRecyclerResources(){
        JSONArray morePhotsJson = new JSONArray();

        for(int i = 0; i < morePhotos.size(); i++){
            morePhotsJson.put(morePhotos.get(i).getImage());

        }
        return morePhotsJson.toString();
        //return morePhotosStr;
    }

    private void confirmUsername(){
        String username = receiverUsername.getText().toString();
        if(!username.isEmpty()){
            String newURL = URL + "request=check_username&username=" + username;
            VolleyRequest volleyRequest = new VolleyRequest(getApplicationContext(), newURL) {
                @Override
                public void onProcess() {
                    //do nothing while processing
                    //networkErrorLayout.setVisibility(View.GONE);
                    preventInteraction();
                    Toast.makeText(getApplicationContext(),"Please wait let us confirm the username...",Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSuccess(JSONObject response) {
                    enableUserInteraction();
                    try {

                        Integer err = response.getInt("error");
                        if(err==0){

                            JSONArray data = response.getJSONArray("data");
                            setReceiver(data);

                        }else{
                            if(err==1){
                                receiverUsername.setError("No user has this username. Please recheck this");
                            }
                            //networkErrorLayout.setVisibility(View.VISIBLE);
                        }
                    } catch (JSONException e) {
                        //Log.e("V_ERROR",e.toString());
                        e.printStackTrace();
                    }
                }


                @Override
                public void onNetworkError() {
                    //progressBar.setVisibility(View.GONE);
                    //networkErrorLayout.setVisibility(View.VISIBLE);
                    enableUserInteraction();
                    Toast.makeText(getApplicationContext(),"Network error occurred. Check your internet and retype username",Toast.LENGTH_SHORT).show();
                }
            };
            volleyRequest.fetchResources();
        }

    }

    private void setReceiver(JSONArray jsonArray){
        try {
            JSONObject object = jsonArray.getJSONObject(0);
            buyerFullName = object.getString("surname") + " " + object.getString("firstname");
            buyerID = object.getString("id");
            receiverFullname.setText(buyerFullName);
        } catch (JSONException e) {
            e.printStackTrace();
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
