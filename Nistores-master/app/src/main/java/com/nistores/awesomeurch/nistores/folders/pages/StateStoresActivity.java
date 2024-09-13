package com.nistores.awesomeurch.nistores.folders.pages;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nistores.awesomeurch.nistores.folders.adapters.TopStoresAdapter;
import com.nistores.awesomeurch.nistores.folders.helpers.ApiUrls;
import com.nistores.awesomeurch.nistores.folders.helpers.TopStores;
import com.nistores.awesomeurch.nistores.folders.helpers.Utility;
import com.nistores.awesomeurch.nistores.folders.helpers.VolleyRequest;
import com.nistores.awesomeurch.nistores.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class StateStoresActivity extends AppCompatActivity {
    ProgressBar progressBar;
    LinearLayout networkErrorLayout;
    String URL, state, stateCode, storeString;
    List<TopStores> topStores;
    TopStoresAdapter mAdapter;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_state_stores);

        progressBar = findViewById(R.id.loader);
        networkErrorLayout = findViewById(R.id.network_error);
        AppCompatButton retryBtn = findViewById(R.id.btn_retry);
        retryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fetchItems();
            }
        });

        recyclerView = findViewById(R.id.recycler_view);
        topStores = new ArrayList<>();
        mAdapter = new TopStoresAdapter(this, topStores);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        URL = new ApiUrls().getApiUrl();

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            stateCode = bundle.getString("state");
            state = bundle.getString("stateName");
            setTitle(state + " Stores");

        }

        if(savedInstanceState != null){
            storeString = savedInstanceState.getString("stores");
            if(storeString != null){
                fillInItems(storeString);
            }else{
                fetchItems();
            }
        }else{
            fetchItems();
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("stores",storeString);
    }


    private void fetchItems(){
        String sCode = new Utility(getApplicationContext()).returnStateShortCode(state);
        String newURL = URL + "request=state_stores&state=" + sCode;
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
                        storeString = data.toString();
                        //Log.d("MPUTA",data.toString());
                        fillInItems(storeString);

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

    private void fillInItems(String data){
        List<TopStores> items = new Gson().fromJson(data, new TypeToken<List<TopStores>>() {
        }.getType());
        topStores.clear();
        topStores.addAll(items);
        // refreshing recycler view
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();

    }
}
