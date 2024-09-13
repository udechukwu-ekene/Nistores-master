package com.nistores.awesomeurch.nistores.folders.pages;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nistores.awesomeurch.nistores.folders.adapters.BusinessLoungeAdapter;
import com.nistores.awesomeurch.nistores.folders.helpers.ApiUrls;
import com.nistores.awesomeurch.nistores.folders.helpers.BusinessLounge;
import com.nistores.awesomeurch.nistores.folders.helpers.InitiateVolley;
import com.nistores.awesomeurch.nistores.folders.helpers.VolleyRequest;
import com.nistores.awesomeurch.nistores.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeliveryOrderActivity extends AppCompatActivity {
    Intent intent;
    private ProgressBar progressBar;
    private LinearLayout networkErrorLayout;
    private RecyclerView recyclerView;
    private ApiUrls apiUrls;
    private List<BusinessLounge> businessLoungeList;
    private BusinessLoungeAdapter mAdapter;
    private String URL, postURL, userId, stateString;
    AppCompatButton initDeliveryBtn;
    SharedPreferences preferences;
    public static String ORDER_STATE = "state_orders";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_order);

        progressBar = findViewById(R.id.loader);
        networkErrorLayout = findViewById(R.id.network_error);
        initDeliveryBtn = findViewById(R.id.btn_init);
        initDeliveryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initDelivery();
            }
        });

        recyclerView = findViewById(R.id.recycler_view);
        businessLoungeList = new ArrayList<>();
        mAdapter = new BusinessLoungeAdapter(getApplicationContext(), businessLoungeList);
        mAdapter.setOpenActivity(ORDER_STATE);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        apiUrls = new ApiUrls();
        URL = apiUrls.getApiUrl();
        postURL = apiUrls.getProcessPost();

        if(savedInstanceState != null){
            stateString = savedInstanceState.getString("states");
            if(stateString != null){
                try {
                    JSONArray allStatesArray = new JSONArray(stateString);
                    //userStores = storeArray;
                    fillInItems(allStatesArray);
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
        outState.putString("states",stateString);
        //Log.d("SAVED","onSaveIns");
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
                        stateString = data.toString();
                        fillInItems(data);

                    }else{

                        networkErrorLayout.setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
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

    private void initDelivery(){

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        userId = prefs.getString("user",null);

        preventInteraction();
        initDeliveryBtn.setText(getResources().getString(R.string.loading));
        StringRequest request = new StringRequest(Request.Method.POST, postURL, new Response.Listener<String>(){
            @Override
            public void onResponse(String s) {
                initDeliveryBtn.setText(getResources().getString(R.string.initiate_delivery));
                enableUserInteraction();
                //Log.d("DFILE",s);

                if(s.equals("error")){

                    Toast.makeText(getApplicationContext(),"Sorry an error occurred. Retry",Toast.LENGTH_SHORT).show();

                }
                else{
                    Toast.makeText(getApplicationContext(),"Initiated successfully!",Toast.LENGTH_SHORT).show();
                    onInitSuccess(s);
                }
            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                enableUserInteraction();
                initDeliveryBtn.setText(getResources().getString(R.string.initiate_delivery));
                Toast.makeText(getApplicationContext(),"Network error occurred. Please retry!",Toast.LENGTH_SHORT).show();



            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError
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
            protected Map<String, String> getParams() throws AuthFailureError  {
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("request", "init_delivery");
                //parameters.put("picture", serverImg);
                parameters.put("id", userId);

                return parameters;
            }
        };

        //RequestQueue rQueue = Volley.newRequestQueue(getContext());
        request.setShouldCache(false);
        InitiateVolley.getInstance().addToRequestQueue(request);
    }

    private void onInitSuccess(String number){
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("orderNumber",number).apply();

        Bundle bundle = new Bundle();
        bundle.putString("orderNumber",number);
        bundle.putString("states",stateString);
        intent = new Intent(getApplicationContext(),InitiateDeliveryActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public void preventInteraction(){
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    public void enableUserInteraction(){
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }


}
