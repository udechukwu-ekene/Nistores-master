package com.nistores.awesomeurch.nistores.folders.pages;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nistores.awesomeurch.nistores.folders.adapters.BusinessLoungeAdapter;
import com.nistores.awesomeurch.nistores.folders.helpers.ApiUrls;
import com.nistores.awesomeurch.nistores.folders.helpers.BusinessLounge;
import com.nistores.awesomeurch.nistores.folders.helpers.VolleyRequest;
import com.nistores.awesomeurch.nistores.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ExploreStoresActivity extends AppCompatActivity {
    Intent intent;
    private ProgressBar progressBar;
    private LinearLayout networkErrorLayout;

    private List<BusinessLounge> businessLoungeList;
    private BusinessLoungeAdapter mAdapter;
    private String URL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore_stores);

        progressBar = findViewById(R.id.loader);
        networkErrorLayout = findViewById(R.id.network_error);
        AppCompatButton retryBtn = findViewById(R.id.btn_retry);
        retryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fetchItems();
            }
        });

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        businessLoungeList = new ArrayList<>();
        mAdapter = new BusinessLoungeAdapter(getApplicationContext(), businessLoungeList);
        mAdapter.setOpenActivity("state_stores");
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        ApiUrls apiUrls = new ApiUrls();
        URL = apiUrls.getApiUrl();

        fetchItems();
    }

    private void fetchItems(){

        String newURL = URL + "request=biz_lounge";
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
        List<BusinessLounge> items = new Gson().fromJson(data.toString(), new TypeToken<List<BusinessLounge>>() {
        }.getType());
        businessLoungeList.clear();
        businessLoungeList.addAll(items);
        // refreshing recycler view
        mAdapter.notifyDataSetChanged();
    }
}