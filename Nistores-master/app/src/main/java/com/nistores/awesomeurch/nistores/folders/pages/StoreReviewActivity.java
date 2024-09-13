package com.nistores.awesomeurch.nistores.folders.pages;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nistores.awesomeurch.nistores.folders.adapters.StoreReviewAdapter;
import com.nistores.awesomeurch.nistores.folders.helpers.ApiUrls;
import com.nistores.awesomeurch.nistores.folders.helpers.StoreReview;
import com.nistores.awesomeurch.nistores.folders.helpers.VolleyRequest;
import com.nistores.awesomeurch.nistores.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class StoreReviewActivity extends AppCompatActivity {
    ConstraintLayout loaderLayout, networkErrorLayout;
    ApiUrls apiUrls;
    String URL, storeId, storeName, userId, ownerId, storeUid;
    RecyclerView recyclerView;
    List<StoreReview> storeReviews;
    StoreReviewAdapter storeReviewAdapter;
    AppCompatButton reviewBtn, retryBtn;
    LinearLayout starsLayout;
    int starred = 0;
    EditText messageArea;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_review);

        messageArea = findViewById(R.id.msg_area);
        loaderLayout = findViewById(R.id.loader_layout);
        networkErrorLayout = findViewById(R.id.network_error_layout);
        reviewBtn = findViewById(R.id.btn_review);
        retryBtn = findViewById(R.id.btn_retry);

        final View.OnClickListener postReview = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postReview();
            }
        };

        reviewBtn.setOnClickListener(postReview);

        recyclerView = findViewById(R.id.recycler_view);
        storeReviews = new ArrayList<>();
        storeReviewAdapter = new StoreReviewAdapter(getApplicationContext(), storeReviews);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(storeReviewAdapter);

        View.OnClickListener rater = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rateStore(view);
            }
        };

        starsLayout = findViewById(R.id.starsLayout);
        for(int x = 0; x < starsLayout.getChildCount(); x++){
            View child = starsLayout.getChildAt(x);
            child.setOnClickListener(rater);
        }

        URL = new ApiUrls().getApiUrl();
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        userId = preferences.getString("user", null);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            storeId = bundle.getString("sid");
            storeName = bundle.getString("name");
            ownerId = bundle.getString("owner_id");
            storeUid = bundle.getString("store_uid");
            String title = storeName + " Reviews";
            setTitle(title);
            fetchReviews();
        }

    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();

    }

    private void rateStore(View v){
        switch(v.getId()){
            case R.id.star1:
                starred=1;
                break;
            case R.id.star2:
                starred=2;
                break;
            case R.id.star3:
                starred=3;
                break;
            case R.id.star4:
                starred=4;
                break;
            case R.id.star5:
                starred=5;
                break;
        }
        setRateUI(starred);
    }

    private void setRateUI(int rate){

        //reset first
        for(int x = 0; x < starsLayout.getChildCount(); x++){
            View child = starsLayout.getChildAt(x);
            ((ImageView) child).setImageResource(R.drawable.ic_star_border);
        }

        //...then set accordingly
        for(int x = 0; x < rate; x++){
            View child = starsLayout.getChildAt(x);
            ((ImageView) child).setImageResource(R.drawable.ic_star_border_green);

        }
    }

    private void fetchReviews(){

        String newURL = URL + "request=store_reviews&sid=" + storeId;
        VolleyRequest volleyRequest = new VolleyRequest(getApplicationContext(), newURL) {
            @Override
            public void onProcess() {
                networkErrorLayout.setVisibility(View.GONE);
                loaderLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onSuccess(JSONObject response) {
                loaderLayout.setVisibility(View.GONE);
                networkErrorLayout.setVisibility(View.GONE);
                try {

                    Integer err = response.getInt("error");
                    if(err==0){

                        JSONArray data = response.getJSONArray("data");
                        //Log.d("MPUTA",data.toString());
                        fillInItems(data);

                    }else{
                        Toast.makeText(getApplicationContext(),"No reviews yet",Toast.LENGTH_SHORT).show();
                        //networkErrorLayout.setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
                    //Log.e("V_ERROR",e.toString());
                    e.printStackTrace();
                }
            }

            @Override
            public void onNetworkError() {
                loaderLayout.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(),"Network Error Occurred",Toast.LENGTH_SHORT).show();

            }
        };
        volleyRequest.setCache(false);
        volleyRequest.fetchResources();
    }

    private void fillInItems(JSONArray data){
        List<StoreReview> items = new Gson().fromJson(data.toString(), new TypeToken<List<StoreReview>>() {
        }.getType());
        storeReviews.clear();
        storeReviews.addAll(items);
        // refreshing recycler view
        storeReviewAdapter.notifyDataSetChanged();
    }

    private void postReview(){
        Boolean valid = true;
        String review = messageArea.getText().toString();
        if(review.isEmpty()){
            valid = false;
            messageArea.setError("Type your review of this store");
        }
        if(starred < 1){
            valid = false;
            Toast.makeText(getApplicationContext(),"Please rate this store before submitting",Toast.LENGTH_SHORT).show();
        }

        if(valid){
           pushMessage(review);

        }
    }

    public void pushMessage(final String typed){
        //disable button
        reviewBtn.setEnabled(false);
        reviewBtn.setText(R.string.sending);
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(StoreReviewActivity.this);
        //this is the url where you want to send the request
        String encodedMsg = "";
        try {
            encodedMsg = URLEncoder.encode(typed,"utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String url = URL+"request=add_review&review=" + encodedMsg + "&stars=" + starred + "&r_id=" + storeId + "&owner_id=" + ownerId + "&user_id=" + userId + "&store_uid=" + storeUid;
        //Log.d("RTN",url);
        // Request a string response from the provided URL.
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Display the response string.
                        reviewBtn.setText(R.string.review);
                        reviewBtn.setEnabled(true);
                        //Log.d("RTN",""+response);
                        try {

                            Integer err = response.getInt("error");
                            JSONArray data = response.getJSONArray("data");
                            if(err==0){
                                appendComment(data);

                            }else{
                                Toast.makeText(getApplicationContext(),"Sorry an error occurred",Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            //Log.e("ERR",e.toString());
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //_response.setText("That didn't work!");
                Toast.makeText(getApplicationContext(),"Sorry an error occurred. Try again",Toast.LENGTH_SHORT).show();
                //Log.d("VOLLEY",error.toString());
                reviewBtn.setText(R.string.review);
                reviewBtn.setEnabled(true);

            }
        });
        // Add the request to the RequestQueue.
        jsonObjectRequest.setShouldCache(false);
        queue.add(jsonObjectRequest);
    }

    public void appendComment(JSONArray commentsArray){
        List<StoreReview> comments = new Gson().fromJson(commentsArray.toString(), new TypeToken<List<StoreReview>>() {
        }.getType());

        messageArea.setText("");

        //final int currSize = chatAdapter.getItemCount();
        storeReviews.addAll(comments);
        recyclerView.post(new Runnable() {
            @Override
            public void run() {
                storeReviewAdapter.notifyItemInserted(storeReviews.size() - 1);
            }
        });
    }

}
