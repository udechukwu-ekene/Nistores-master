package com.nistores.awesomeurch.nistores.folders.pages;

import android.content.Intent;
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
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nistores.awesomeurch.nistores.folders.adapters.InitiatedOrderAdapter;
import com.nistores.awesomeurch.nistores.folders.helpers.ApiUrls;
import com.nistores.awesomeurch.nistores.folders.helpers.InitiatedOrder;
import com.nistores.awesomeurch.nistores.folders.helpers.VolleyRequest;
import com.nistores.awesomeurch.nistores.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class StateOrdersActivity extends AppCompatActivity {
    Intent intent;
    private ProgressBar progressBar;
    private LinearLayout networkErrorLayout;
    private RecyclerView recyclerView;
    private ApiUrls apiUrls;
    private List<InitiatedOrder> initiatedOrders;
    private InitiatedOrderAdapter mAdapter;
    private String URL, state, stateOrders, stateName;
    private TextView errorTextView;
    private AppCompatButton retryBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_state_orders);

        progressBar = findViewById(R.id.loader);
        networkErrorLayout = findViewById(R.id.network_error);
        errorTextView = findViewById(R.id.error_statement);
        retryBtn = findViewById(R.id.btn_retry);
        retryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fetchItems();
            }
        });

        recyclerView = findViewById(R.id.recycler_view);
        initiatedOrders = new ArrayList<>();
        mAdapter = new InitiatedOrderAdapter(getApplicationContext(), initiatedOrders);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        apiUrls = new ApiUrls();
        URL = apiUrls.getApiUrl();

        Bundle bundle = this.getIntent().getExtras();
        if(bundle!=null){
            state = bundle.getString("state");
            stateName = bundle.getString("stateName");
            if(state != null){
               fetchItems();
            }
            if(stateName != null){
                setTitle(stateName + " Delivery Orders");
            }
        }

        if(savedInstanceState != null){
            stateOrders = savedInstanceState.getString("stateOrders");
            if(stateOrders != null){
                try {
                    JSONArray allOrdersArray = new JSONArray(stateOrders);
                    //userStores = storeArray;
                    fillInItems(allOrdersArray);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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
        outState.putString("stateOrders",stateOrders);
        //Log.d("SAVED","onSaveIns");
    }

    private void fetchItems(){

        String newURL = URL + "request=state_orders&state=" + state;
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
                        stateOrders = data.toString();
                        //Log.d("MPUTA",stateOrders);
                        fillInItems(data);

                    }else{
                        networkErrorLayout.setVisibility(View.VISIBLE);
                        if(err==1){
                            errorTextView.setText(getResources().getString(R.string.no_data));
                        }else{
                            errorTextView.setText(getResources().getString(R.string.network_error));
                        }
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
        volleyRequest.setCache(false);
        volleyRequest.fetchResources();
    }

    private void fillInItems(JSONArray data){
        List<InitiatedOrder> items = new Gson().fromJson(data.toString(), new TypeToken<List<InitiatedOrder>>() {
        }.getType());
        initiatedOrders.clear();
        initiatedOrders.addAll(items);
        // refreshing recycler view
        mAdapter.notifyDataSetChanged();
    }

}
