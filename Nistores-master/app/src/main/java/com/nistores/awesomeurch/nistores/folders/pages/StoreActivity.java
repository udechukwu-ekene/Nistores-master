package com.nistores.awesomeurch.nistores.folders.pages;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nistores.awesomeurch.nistores.folders.adapters.ProductAdapter;
import com.nistores.awesomeurch.nistores.folders.adapters.selectCategoryAdapter;
import com.nistores.awesomeurch.nistores.folders.helpers.ApiUrls;
import com.nistores.awesomeurch.nistores.folders.helpers.InitiateVolley;
import com.nistores.awesomeurch.nistores.folders.helpers.Product;
import com.nistores.awesomeurch.nistores.folders.helpers.VolleyRequest;
import com.nistores.awesomeurch.nistores.folders.helpers.selectCategory;
import com.nistores.awesomeurch.nistores.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class StoreActivity extends AppCompatActivity {
    TextView navbarTitle, storeNameView, regDateView, viewsView, followersView, ownerView, phoneNoView, emailView, supportView,
            websiteView, addressView, addInfoView;
    ImageView backNavigate, logoView;
    ConstraintLayout loaderLayout, infoLayout;
    RecyclerView categoryRecycler, productRecycler;
    List<selectCategory> categoryList;
    selectCategoryAdapter categoryAdapter;
    List<Product> productList;
    ProductAdapter productAdapter;
    ApiUrls apiUrls;
    String storeId, storeUid, userId, ownerId, storeName, regDate, views, followers, owner, phoneNo, email, support, website, address, addInfo, logo, storeCover;
    AppCompatButton followBtn, messageBtn, reveiewBtn, editBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        navbarTitle = findViewById(R.id.nav_title);
        backNavigate = findViewById(R.id.btn_back);
        backNavigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendBack();
            }
        });

        followBtn = findViewById(R.id.follow_store);
        messageBtn = findViewById(R.id.message_owner);
        reveiewBtn = findViewById(R.id.store_reviews);
        editBtn = findViewById(R.id.edit_store);
        reveiewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toReviews();
            }
        });


        logoView = findViewById(R.id.store_logo);
        storeNameView = findViewById(R.id.store_name);
        regDateView = findViewById(R.id.reg_date);
        ownerView = findViewById(R.id.owner);
        viewsView = findViewById(R.id.views);
        followersView = findViewById(R.id.followers);
        phoneNoView = findViewById(R.id.phone_no);
        emailView = findViewById(R.id.email);
        supportView = findViewById(R.id.support);
        websiteView = findViewById(R.id.website);
        addressView = findViewById(R.id.address);
        addInfoView = findViewById(R.id.additional_info);

        loaderLayout = findViewById(R.id.loader_layout);
        infoLayout = findViewById(R.id.main_layout);

        categoryRecycler = findViewById(R.id.recycler_category);
        categoryList = new ArrayList<>();
        categoryAdapter = new selectCategoryAdapter(getApplicationContext(), categoryList);
        int DESIGN = 2;
        categoryAdapter.setDesign(DESIGN);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        categoryRecycler.setLayoutManager(mLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(categoryRecycler.getContext(),
                DividerItemDecoration.VERTICAL);
        categoryRecycler.addItemDecoration(dividerItemDecoration);
        categoryRecycler.setItemAnimator(new DefaultItemAnimator());
        categoryRecycler.setAdapter(categoryAdapter);

        productRecycler = findViewById(R.id.recycler_product);
        productList = new ArrayList<>();
        productAdapter = new ProductAdapter(getApplicationContext(), productList);
        int FAVOURITE = 1;
        productAdapter.setDesign(FAVOURITE);
        RecyclerView.LayoutManager nLayoutManager = new LinearLayoutManager(getApplicationContext());
        productRecycler.setLayoutManager(nLayoutManager);
        productRecycler.setItemAnimator(new DefaultItemAnimator());
        productRecycler.setAdapter(productAdapter);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        userId = prefs.getString("user",null);

        apiUrls = new ApiUrls();

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            storeId = bundle.getString("id");
            String storeName = bundle.getString("sName");
            navbarTitle.setText(storeName);
            fetchData();
            fetchStoreItems(0);
        }

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
    }

    private void sendBack(){
        super.onBackPressed();
        /*Intent intent = new Intent(this,MyStoresActivity.class);
        startActivity(intent);*/
    }

    private void toReviews(){
        Bundle bundle = new Bundle();
        bundle.putString("sid",storeId);
        bundle.putString("name",storeName);
        bundle.putString("owner_id",ownerId);
        bundle.putString("store_uid",storeUid);
        Intent intent = new Intent(this,StoreReviewActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public void fetchData(){

        loaderLayout.setVisibility(View.VISIBLE);
        //?request=view_product&pid=633&uid=172&sid=10
        String URL = apiUrls.getApiUrl();
        String originURL = URL + "request=view_store&sid=" + storeId;
        //Log.d("RTN",originURL);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, originURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        //Log.d("RTN",response.toString());
                        loaderLayout.setVisibility(View.GONE);
                        try {

                            Integer err = response.getInt("error");
                            JSONObject data = response.getJSONObject("data");
                            if(err==0){
                                fillInItems(data);

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
                loaderLayout.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(),"Sorry an error occurred. Try again",Toast.LENGTH_SHORT).show();
                //Log.d("VOLLEY",error.toString());

            }
        });

        jsonObjectRequest.setShouldCache(false);
        InitiateVolley.getInstance().addToRequestQueue(jsonObjectRequest);
    }

    public void fillInItems(JSONObject data){
        infoLayout.setVisibility(View.VISIBLE);

        try {
            ownerId = data.getString("sowner");
            storeName = data.getString("sname");
            regDate = data.getString("sdate");
            address = data.getString("saddress");
            email = data.getString("semail");
            views = data.getString("views");
            phoneNo = data.getString("sphone");
            support = data.getString("support_no");
            website = data.getString("website");
            addInfo = data.getString("sinfo");
            logo = data.getString("slogo");
            storeCover = data.getString("store_cover");
            String firstname = data.getString("firstname");
            String surname = data.getString("surname");
            storeUid = data.getString("store_uid");
            owner = firstname + " " + surname;

            try {
                Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(regDate);
                String dateString = new SimpleDateFormat("EEE, MMM d, ''yy", Locale.ENGLISH).format(date);
                regDateView.setText(dateString);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            String fullStoreName = storeName + " (" + storeUid + ")";

            storeNameView.setText(fullStoreName);
            addressView.setText(address);
            emailView.setText(email);
            viewsView.setText(views);
            phoneNoView.setText(phoneNo);
            supportView.setText(support);
            websiteView.setText(website);
            addInfoView.setText(addInfo);
            ownerView.setText(owner);

            if(ownerId.equals(userId)){
                messageBtn.setVisibility(View.GONE);
                followBtn.setVisibility(View.GONE);
            }else{
                editBtn.setVisibility(View.GONE);
            }

            //display categories
            JSONArray categoryArray = data.getJSONArray("categories");
            List<selectCategory> items = new Gson().fromJson(categoryArray.toString(), new TypeToken<List<selectCategory>>() {
            }.getType());
            categoryList.clear();
            categoryList.addAll(items);
            // refreshing recycler view
            categoryAdapter.notifyDataSetChanged();

            final String STRING_BASE_URL = "https://www.nistores.com.ng/";
            Picasso.with(getApplicationContext()).load(STRING_BASE_URL + logo).placeholder(R.drawable.ic_person_default).into(logoView);


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void fetchStoreItems(final int start){

        String newURL = apiUrls.getApiUrl() + "request=view_store_products&sid=" + storeId + "&start=" + start;
        VolleyRequest volleyRequest = new VolleyRequest(getApplicationContext(), newURL) {
            @Override
            public void onProcess() {
                //do nothing while processing

            }

            @Override
            public void onSuccess(JSONObject response) {
                //progressBar.setVisibility(View.GONE);
                //networkErrorLayout.setVisibility(View.GONE);
                try {

                    Integer err = response.getInt("error");
                    if(err==0){

                        JSONArray data = response.getJSONArray("data");
                        //Log.d("MPUTA",data.toString());
                        if(start > 0){
                            appendData(data);
                        }else{
                            fillInStoreItems(data);
                        }

                    }else{

                        //networkErrorLayout.setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
                    //Log.e("V_ERROR",e.toString());
                    e.printStackTrace();
                }
            }


            @Override
            public void onNetworkError() {
                if(start == 0){
                    //progressBar.setVisibility(View.GONE);
                    //networkErrorLayout.setVisibility(View.VISIBLE);
                }
            }
        };
        //volleyRequest.setCache(false);
        volleyRequest.fetchResources();
    }

    private void fillInStoreItems(JSONArray data){
        List<Product> productItems = new Gson().fromJson(data.toString(), new TypeToken<List<Product>>() {
        }.getType());
        productList.clear();
        productList.addAll(productItems);
        // refreshing recycler view
        productAdapter.notifyDataSetChanged();
    }

    private void appendData(JSONArray data){
        List<Product> productItems = new Gson().fromJson(data.toString(), new TypeToken<List<Product>>() {
        }.getType());
        productList.addAll(productItems);
        productRecycler.post(new Runnable() {
            @Override
            public void run() {
                //scroller.resetState();
                productAdapter.notifyItemInserted(productList.size() - 1);
            }
        });
    }

}
