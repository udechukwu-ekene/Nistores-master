package com.nistores.awesomeurch.nistores.folders.pages;

import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nistores.awesomeurch.nistores.folders.adapters.MemberAdapter;
import com.nistores.awesomeurch.nistores.folders.adapters.ProductAdapter;
import com.nistores.awesomeurch.nistores.folders.adapters.TopStoresAdapter;
import com.nistores.awesomeurch.nistores.folders.adapters.TopicAdapter;
import com.nistores.awesomeurch.nistores.folders.helpers.ApiUrls;
import com.nistores.awesomeurch.nistores.folders.helpers.Member;
import com.nistores.awesomeurch.nistores.folders.helpers.Product;
import com.nistores.awesomeurch.nistores.folders.helpers.TopStores;
import com.nistores.awesomeurch.nistores.folders.helpers.Topic;
import com.nistores.awesomeurch.nistores.folders.helpers.VolleyRequest;
import com.nistores.awesomeurch.nistores.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class searchResultActivity extends AppCompatActivity {
    ConstraintLayout loaderLayout,networkErrorLayout;
    RecyclerView recyclerProduct, recyclerStore, recyclerMember, recyclerTopic;
    LinearLayout productSection, storeSection, memberSection, topicSection;
    AppCompatButton allProductsBtn, allStoresBtn, allMembersBtn, allTopicsBtn, retryBtn;
    List<Product> products;
    List<TopStores> stores;
    List<Member> members;
    List<Topic> topics;
    ProductAdapter productAdapter;
    TopStoresAdapter topStoresAdapter;
    MemberAdapter memberAdapter;
    TopicAdapter topicAdapter;
    String URL;
    ApiUrls apiUrls;
    String originURL;
    String[] typeList = {"product","store","member","topic"};
    int[] startList = {0,0,0,0};

    private static int PRODUCT = 0;
    private static int STORE = 1;
    private static int MEMBER = 2;
    private static int TOPIC = 3;
    String specSearch = "all";
    private String specWord = "";
    private static int FAVOURITE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        recyclerProduct = findViewById(R.id.recycler_product);
        products = new ArrayList<>();
        productAdapter = new ProductAdapter(getApplicationContext(), products);
        productAdapter.setDesign(FAVOURITE);
        RecyclerView.LayoutManager cLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerProduct.setLayoutManager(cLayoutManager);

        recyclerProduct.setAdapter(productAdapter);

        recyclerStore = findViewById(R.id.recycler_store);
        stores = new ArrayList<>();
        topStoresAdapter = new TopStoresAdapter(getApplicationContext(), stores);
        RecyclerView.LayoutManager tLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerStore.setLayoutManager(tLayoutManager);
        recyclerStore.setAdapter(topStoresAdapter);


        recyclerMember = findViewById(R.id.recycler_member);
        members = new ArrayList<>();
        memberAdapter = new MemberAdapter(getApplicationContext(), members);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerMember.setLayoutManager(mLayoutManager);
        recyclerMember.setAdapter(memberAdapter);

        recyclerTopic = findViewById(R.id.recycler_topic);
        topics = new ArrayList<>();
        topicAdapter = new TopicAdapter(getApplicationContext(), topics);
        RecyclerView.LayoutManager topicLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerTopic.setLayoutManager(topicLayoutManager);
        recyclerTopic.setAdapter(topicAdapter);

        productSection = findViewById(R.id.product_section);
        storeSection = findViewById(R.id.store_section);
        memberSection = findViewById(R.id.member_section);
        topicSection = findViewById(R.id.topic_section);

        allProductsBtn = findViewById(R.id.all_products);
        allStoresBtn = findViewById(R.id.all_stores);
        allMembersBtn = findViewById(R.id.all_members);
        allTopicsBtn = findViewById(R.id.all_topics);
        retryBtn = findViewById(R.id.btn_retry);

        allProductsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                allProductsBtn.setText(getResources().getString(R.string.loading));
                allResults(PRODUCT);
            }
        });
        allStoresBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                allStoresBtn.setText(getResources().getString(R.string.loading));
                allResults(STORE);
            }
        });
        allMembersBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                allMembersBtn.setText(getResources().getString(R.string.loading));
                allResults(MEMBER);
            }
        });
        allTopicsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                allTopicsBtn.setText(getResources().getString(R.string.loading));
                allResults(TOPIC);
            }
        });
        retryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                retryBtn.setText(getResources().getString(R.string.loading));
                getSearchResults();
            }
        });


        apiUrls = new ApiUrls();
        URL = apiUrls.getApiUrl();
        loaderLayout = findViewById(R.id.loader_layout);
        networkErrorLayout = findViewById(R.id.network_error_layout);

        Bundle bundle = this.getIntent().getExtras();
        if(bundle!=null){
            specSearch = bundle.getString("searchType");
            specWord = bundle.getString("searchWord");
            setTitle("Search Results for '" + specWord + "' ");
            originURL = URL + "request=search&term=" + specWord + "&type=" + specSearch;
            getSearchResults();
        }
    }

    public void allResults(int type){

        originURL = URL + "request=search&term=" + specWord + "&type=" + typeList[type] + "&start=" + startList[type];
        //Log.d("myURL",originURL);
        getMore();

    }

    public void getMore(){
        //Log.d("CHECK",originURL);
        VolleyRequest volleyRequest = new VolleyRequest(getApplicationContext(), originURL) {
            @Override
            public void onProcess() {
                preventInteraction();
            }

            @Override
            public void onSuccess(JSONObject response) {
                enableUserInteraction();
                try {

                    Integer err = response.getInt("error");
                    if(err==0){
                        JSONObject data = response.getJSONObject("data");
                        fillInItems(data, true);

                    }else{
                        Toast.makeText(getApplicationContext(),"Network Error Occurred",Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    //Log.e("V_ERROR",e.toString());
                    e.printStackTrace();
                }
            }


            @Override
            public void onNetworkError() {
                enableUserInteraction();
            }
        };
        volleyRequest.setCache(false);
        volleyRequest.fetchResources();
    }

    public void getSearchResults(){
        //Log.d("CHECK",originURL);
        VolleyRequest volleyRequest = new VolleyRequest(getApplicationContext(), originURL) {
            @Override
            public void onProcess() {
                //do nothing while processing
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
                        JSONObject data = response.getJSONObject("data");
                        //Log.d("MPUTA",data.toString());
                        fillInItems(data, false);

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
                loaderLayout.setVisibility(View.GONE);
                networkErrorLayout.setVisibility(View.VISIBLE);
            }
        };
        volleyRequest.setCache(false);
        volleyRequest.fetchResources();

    }

    public void fillInItems(JSONObject data, boolean append){
        allProductsBtn.setText(getResources().getString(R.string.see_more));
        allStoresBtn.setText(getResources().getString(R.string.see_more));
        allMembersBtn.setText(getResources().getString(R.string.see_more));
        allTopicsBtn.setText(getResources().getString(R.string.see_more));

        try {
            JSONArray productsArray = data.getJSONArray("products");
            int productSize = productsArray.length();
            if(productSize > 0){
                productSection.setVisibility(View.VISIBLE);
                List<Product> productItems = new Gson().fromJson(productsArray.toString(), new TypeToken<List<Product>>() {
                }.getType());

                if(append){
                    products.addAll(productItems);
                    recyclerProduct.post(new Runnable() {
                        @Override
                        public void run() {
                            //scroller.resetState();
                            productAdapter.notifyItemInserted(products.size() - 1);
                        }
                    });
                }else{
                    products.clear();
                    products.addAll(productItems);
                    // refreshing recycler view
                    productAdapter.notifyDataSetChanged();
                }

                if(productSize > 19){
                    allProductsBtn.setVisibility(View.VISIBLE);
                    int currStart = startList[PRODUCT];
                    startList[PRODUCT] = currStart + 20;

                }else{
                    allProductsBtn.setVisibility(View.GONE);
                }
            }
        }catch(Exception e){
            //Log.d("ERROR", e+"");
        }


        try{
            JSONArray storesArray = data.getJSONArray("stores");
            int storeSize = storesArray.length();
            if(storeSize > 0){
                storeSection.setVisibility(View.VISIBLE);
                List<TopStores> storeItems = new Gson().fromJson(storesArray.toString(), new TypeToken<List<TopStores>>() {
                }.getType());

                if(append){
                    stores.addAll(storeItems);
                    recyclerStore.post(new Runnable() {
                        @Override
                        public void run() {
                            //scroller.resetState();
                            topStoresAdapter.notifyItemInserted(stores.size() - 1);
                        }
                    });
                }else{
                    stores.clear();
                    stores.addAll(storeItems);
                    // refreshing recycler view
                    topStoresAdapter.notifyDataSetChanged();
                }

                stores.clear();
                stores.addAll(storeItems);
                // refreshing recycler view
                topStoresAdapter.notifyDataSetChanged();
                if(storeSize > 19){
                    allStoresBtn.setVisibility(View.VISIBLE);
                    int currStart = startList[STORE];
                    startList[STORE] = currStart + 20;
                }else{
                    allStoresBtn.setVisibility(View.GONE);
                }
            }
        }catch(Exception e){
            //Log.d("ERROR", e+"");
        }

        try{
            JSONArray membersArray = data.getJSONArray("members");
            int memberSize = membersArray.length();
            if(memberSize > 0){
                memberSection.setVisibility(View.VISIBLE);
                List<Member> memberItems = new Gson().fromJson(membersArray.toString(), new TypeToken<List<Member>>() {
                }.getType());

                if(append){
                    members.addAll(memberItems);
                    recyclerMember.post(new Runnable() {
                        @Override
                        public void run() {
                            //scroller.resetState();
                            memberAdapter.notifyItemInserted(members.size() - 1);
                        }
                    });
                }else{
                    members.clear();
                    members.addAll(memberItems);
                    // refreshing recycler view
                    memberAdapter.notifyDataSetChanged();
                }

                if(memberSize > 19){
                    allMembersBtn.setVisibility(View.VISIBLE);
                    int currStart = startList[MEMBER];
                    startList[MEMBER] = currStart + 20;
                }else{
                    allMembersBtn.setVisibility(View.GONE);
                }
            }
        }catch(Exception e){
            //Log.d("ERROR", e+"");
        }

        try{
            JSONArray topicsArray = data.getJSONArray("topics");
            int topicSize = topicsArray.length();
            if(topicSize > 0){
                topicSection.setVisibility(View.VISIBLE);
                List<Topic> topicItems = new Gson().fromJson(topicsArray.toString(), new TypeToken<List<Topic>>() {
                }.getType());

                if(append){
                    topics.addAll(topicItems);
                    recyclerTopic.post(new Runnable() {
                        @Override
                        public void run() {
                            //scroller.resetState();
                            topicAdapter.notifyItemInserted(topics.size() - 1);
                        }
                    });
                }else{
                    topics.clear();
                    topics.addAll(topicItems);
                    // refreshing recycler view
                    topicAdapter.notifyDataSetChanged();
                }

                if(topicSize > 19){
                    allTopicsBtn.setVisibility(View.VISIBLE);
                    int currStart = startList[TOPIC];
                    startList[TOPIC] = currStart + 20;
                }else{
                    allTopicsBtn.setVisibility(View.GONE);
                }
            }
        }catch(Exception e){
            //Log.d("ERROR", e+"");
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