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
import com.nistores.awesomeurch.nistores.folders.adapters.PollAdapter;
import com.nistores.awesomeurch.nistores.folders.helpers.ApiUrls;
import com.nistores.awesomeurch.nistores.folders.helpers.Polls;
import com.nistores.awesomeurch.nistores.folders.helpers.VolleyRequest;
import com.nistores.awesomeurch.nistores.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class pollsActivity extends AppCompatActivity {
    Intent intent;
    private ProgressBar progressBar;
    private LinearLayout networkErrorLayout;
    private RecyclerView recyclerView;
    private ApiUrls apiUrls;
    private List<Polls> PollsList;
    private PollAdapter mAdapter;
    private String URL;
    private AppCompatButton retryBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_polls);

        progressBar = findViewById(R.id.loader);
        networkErrorLayout = findViewById(R.id.network_error);
        retryBtn = findViewById(R.id.btn_retry);
        retryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fetchItems();
            }
        });

        recyclerView = findViewById(R.id.recycler_view);
        PollsList = new ArrayList<>();
        mAdapter = new PollAdapter(getApplicationContext(), PollsList);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        apiUrls = new ApiUrls();
        URL = apiUrls.getApiUrl();

        fetchItems();
    }

    private void fetchItems(){

        String newURL = URL + "request=polls";
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
        List<Polls> items = new Gson().fromJson(data.toString(), new TypeToken<List<Polls>>() {
        }.getType());
        PollsList.clear();
        PollsList.addAll(items);
        // refreshing recycler view
        mAdapter.notifyDataSetChanged();
    }
}