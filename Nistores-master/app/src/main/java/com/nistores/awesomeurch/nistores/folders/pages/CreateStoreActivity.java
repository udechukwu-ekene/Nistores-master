package com.nistores.awesomeurch.nistores.folders.pages;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nistores.awesomeurch.nistores.folders.adapters.selectCategoryAdapter;
import com.nistores.awesomeurch.nistores.folders.helpers.ApiUrls;
import com.nistores.awesomeurch.nistores.folders.helpers.InitiateVolley;
import com.nistores.awesomeurch.nistores.folders.helpers.Utility;
import com.nistores.awesomeurch.nistores.folders.helpers.VolleyRequest;
import com.nistores.awesomeurch.nistores.folders.helpers.selectCategory;
import com.nistores.awesomeurch.nistores.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateStoreActivity extends AppCompatActivity {
    RecyclerView categoryRecycler;
    List<selectCategory> selectCategoryList;
    selectCategoryAdapter categoryAdapter;
    ProgressBar cProgress;
    String URL, postURL, categoryString, stateCode, stateNoString, sname, saddress, semail, storeNo, supportNo, website, additionalInfo, uid, userId;
    LinearLayout networkErrorLayout;
    ArrayList<String> statesArrayList, availableNosList;
    Spinner myLocationSpinner, storeNumberSpinner;
    Utility utility;
    ConstraintLayout loaderLayout;
    EditText nameView, addressView, emailView, storeNumberView, supportNumberView, websiteView, additionalInfoView;
    AppCompatButton createStoreBtn;
    SharedPreferences prefs;
    Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_store);

        nameView = findViewById(R.id.store_name);
        addressView = findViewById(R.id.store_address);
        emailView = findViewById(R.id.store_email);
        storeNumberView = findViewById(R.id.store_phone);
        supportNumberView = findViewById(R.id.support);
        websiteView = findViewById(R.id.website);
        additionalInfoView = findViewById(R.id.additional_info);
        createStoreBtn = findViewById(R.id.btn_create_store);
        createStoreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkInputs();
            }
        });

        categoryRecycler = findViewById(R.id.recycler_category);
        selectCategoryList = new ArrayList<>();
        categoryAdapter = new selectCategoryAdapter(getApplicationContext(), selectCategoryList);
        RecyclerView.LayoutManager cLayoutManager = new LinearLayoutManager(getApplicationContext());
        categoryRecycler.setLayoutManager(cLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(categoryRecycler.getContext(),
                DividerItemDecoration.VERTICAL);
        categoryRecycler.addItemDecoration(dividerItemDecoration);
        categoryRecycler.setAdapter(categoryAdapter);

        cProgress = findViewById(R.id.cloader);
        networkErrorLayout = findViewById(R.id.network_error_layout);
        loaderLayout = findViewById(R.id.loader_layout);

        statesArrayList = new ArrayList<>();
        myLocationSpinner = findViewById(R.id.my_location);

        availableNosList = new ArrayList<>();
        storeNumberSpinner = findViewById(R.id.store_number);

        storeNumberSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                uid = adapterView.getItemAtPosition(i).toString();

                //Log.d("STATENUMBA",uid);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        utility = new Utility(getApplicationContext());

        myLocationSpinner.setAdapter(ArrayAdapter.createFromResource(this,
                R.array.locations, R.layout.spinner_item));

        myLocationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String srch = adapterView.getItemAtPosition(i).toString();
                stateCode = utility.returnStateShortCode(srch);
                //Log.d("STATECODE",stateCode);
                fetchStoreNos();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        URL = new ApiUrls().getApiUrl();
        postURL = new ApiUrls().getProcessPost();

        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        userId = prefs.getString("user",null);

        if(savedInstanceState != null){
            categoryString = savedInstanceState.getString("categories");
            if(categoryString!=null){
                fillInCategories(categoryString);
            }else{
                fetchCategories();
            }

        }else{
            fetchCategories();
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //outState.putStringArrayList("photos",morePhotos);
        outState.putString("categories",categoryString);
        outState.putString("states",stateNoString);
    }

    private void fetchStoreNos(){

        String newURL = URL + "request=store_numbers&scode=" + stateCode;
        //Log.d("REZOT",newURL);
        VolleyRequest volleyRequest = new VolleyRequest(getApplicationContext(), newURL) {
            @Override
            public void onProcess() {
                //do nothing while processing
                //networkErrorLayout.setVisibility(View.GONE);
                loaderLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onSuccess(JSONObject response) {
                //networkErrorLayout.setVisibility(View.GONE);
                loaderLayout.setVisibility(View.GONE);
                try {

                    Integer err = response.getInt("error");
                    if(err==0){

                        JSONArray data = response.getJSONArray("data");
                        stateNoString = data.toString();
                        //Log.d("REZOT",stateNoString);
                        fillInStates(data);

                    }else{
                        Toast.makeText(getApplicationContext(),"Sorry an error occurred. Couldn't get available store numbers",Toast.LENGTH_SHORT).show();
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
                //networkErrorLayout.setVisibility(View.VISIBLE);
            }
        };
        volleyRequest.fetchResources();
    }

    private void fillInStates(JSONArray items){
        try{
            ArrayList<String> list = new ArrayList<>();
            for(int i=0;i<items.length();i++){

                String no = items.getString(i);
                list.add(no);
            }

            storeNumberSpinner.setAdapter(new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_item, list));


        }catch (JSONException e){e.printStackTrace();}

    }

    private void fetchCategories(){

        String originURL = URL + "request=categories";
        //Log.d("CHECK",originURL);
        cProgress.setVisibility(View.VISIBLE);
        networkErrorLayout.setVisibility(View.GONE);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, originURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        cProgress.setVisibility(View.GONE);

                        //Log.d("RTN",response.toString());
                        try {
                            Integer err = response.getInt("error");
                            JSONArray storeCategories = response.getJSONArray("data");
                            if(err==0){
                                //btnUpdateInterest.setVisibility(View.VISIBLE);
                                categoryString = storeCategories.toString();

                                fillInCategories(categoryString);
                                //fillInItems(storeCategories);

                            }else{
                                Toast.makeText(getApplicationContext(),"Sorry an error occurred",Toast.LENGTH_SHORT).show();
                                networkErrorLayout.setVisibility(View.VISIBLE);
                            }
                        } catch (JSONException e) {
                            //Log.e("ERR",e.toString());
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"Sorry an error occurred. Try again",Toast.LENGTH_SHORT).show();
                networkErrorLayout.setVisibility(View.VISIBLE);
                cProgress.setVisibility(View.GONE);
                //Log.d("VOLLEY",error.toString());

            }
        });
        //jsonObjectRequest.setShouldCache(false);
        InitiateVolley.getInstance().addToRequestQueue(jsonObjectRequest);
    }

    private void fillInCategories(String storeCategories){
        List<selectCategory> items = new Gson().fromJson(storeCategories, new TypeToken<List<selectCategory>>() {
        }.getType());
        selectCategoryList.clear();
        selectCategoryList.addAll(items);
        // refreshing recycler view
        categoryAdapter.notifyDataSetChanged();
    }

    private void checkInputs(){
        boolean valid = true;
        sname = nameView.getText().toString();
        saddress = addressView.getText().toString();
        semail = emailView.getText().toString();
        storeNo = storeNumberView.getText().toString();
        supportNo = supportNumberView.getText().toString();
        website = websiteView.getText().toString();
        additionalInfo = additionalInfoView.getText().toString();

        if(sname.isEmpty()){
            valid = false;
            nameView.setError("Enter the store name");
            Toast.makeText(getApplicationContext(),"Enter the store name",Toast.LENGTH_SHORT).show();
        }
        if(storeNo.isEmpty()){
            valid = false;
            storeNumberView.setError("Enter the store phone number");
            Toast.makeText(getApplicationContext(),"Enter the store phone number",Toast.LENGTH_SHORT).show();
        }

        if(valid){
            createStore();
        }

    }

    private void createStore(){
        final String cats = utility.getSelectedCats(categoryRecycler);
        if(cats.isEmpty()){
            Toast.makeText(getApplicationContext(),"Please choose your store category",Toast.LENGTH_SHORT).show();
            return;
        }

        preventInteraction();
        loaderLayout.setVisibility(View.VISIBLE);
        createStoreBtn.setText(getResources().getString(R.string.processing));
        StringRequest request = new StringRequest(Request.Method.POST, postURL, new Response.Listener<String>(){
            @Override
            public void onResponse(String s) {
                loaderLayout.setVisibility(View.GONE);
                //uploading.dismiss();
                createStoreBtn.setText(getResources().getString(R.string.create_store));
                enableUserInteraction();
                //Log.d("DFILE",s);

                try {
                    JSONObject result = new JSONObject(s);

                    Integer error = result.getInt("error");
                    if(error == 0){
                        String id = result.getString("data");
                        //Log.d("KONFAMM",id);

                        Bundle bundle = new Bundle();
                        bundle.putString("sName",sname);
                        bundle.putString("id",id);
                        intent = new Intent(getApplicationContext(), StoreActivity.class);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        finish();

                    }else if(error == 1){
                        Toast.makeText(getApplicationContext(),"Please select another store number. The store number you selected already exists",Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(getApplicationContext(),"Sorry an error occurred. Retry",Toast.LENGTH_SHORT).show();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),"Sorry an error occurred",Toast.LENGTH_SHORT).show();
                }

            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                enableUserInteraction();
                loaderLayout.setVisibility(View.GONE);
                createStoreBtn.setText(getResources().getString(R.string.create_store));
                Toast.makeText(getApplicationContext(),"Network error occurred. Please retry!",Toast.LENGTH_SHORT).show();

                //Log.d("ERR",volleyError.toString());


            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> parameters = new HashMap<>();
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
            protected Map<String, String> getParams() {
                Map<String, String> parameters = new HashMap<>();
                parameters.put("request", "add_store");
                parameters.put("sname", sname);
                parameters.put("saddress", saddress);
                parameters.put("store_uid", stateCode+uid);
                parameters.put("sphone", storeNo);
                parameters.put("semail", semail);
                parameters.put("support_no", supportNo);
                parameters.put("scat_id", cats);
                parameters.put("sstate", stateCode);
                parameters.put("sowner", userId);
                parameters.put("website", website);
                parameters.put("info", additionalInfo);

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

}
