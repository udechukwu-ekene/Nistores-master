package com.nistores.awesomeurch.nistores.folders.pages;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nistores.awesomeurch.nistores.folders.adapters.TopStoresAdapter;
import com.nistores.awesomeurch.nistores.folders.helpers.ApiUrls;
import com.nistores.awesomeurch.nistores.folders.helpers.InitiateVolley;
import com.nistores.awesomeurch.nistores.folders.helpers.TopStores;
import com.nistores.awesomeurch.nistores.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TopStoresFragment extends Fragment {

    ApiUrls apiUrls;
    private String URL;

    RecyclerView recyclerView;
    LinearLayout networkErrorLayout;
    ProgressBar progressBar;
    private List<TopStores> topStores;
    private TopStoresAdapter mAdapter;

    public TopStoresFragment() {
        // Required empty public constructor
    }

    public static TopStoresFragment newInstance() {
        TopStoresFragment fragment = new TopStoresFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    /*@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_top_stores, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (savedInstanceState == null) {
            Bundle args = getArguments();
        }

        apiUrls = new ApiUrls();

        URL = apiUrls.getApiUrl();

        networkErrorLayout = view.findViewById(R.id.network_error_layout);
        AppCompatButton retryButton = view.findViewById(R.id.btn_retry);
        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fetchProductItems();
            }
        });

        progressBar = view.findViewById(R.id.progress);
        recyclerView = view.findViewById(R.id.recycler_view);
        topStores = new ArrayList<>();
        mAdapter = new TopStoresAdapter(getContext(), topStores);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        //recyclerView.setNestedScrollingEnabled(false);

        fetchProductItems();

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private void fetchProductItems(){

        String originURL = URL + "request=top_stores";
        progressBar.setVisibility(View.VISIBLE);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, originURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        progressBar.setVisibility(View.GONE);
                        try {

                            Integer err = response.getInt("error");
                            JSONArray data = response.getJSONArray("data");
                            if(err==0){

                                List<TopStores> items = new Gson().fromJson(data.toString(), new TypeToken<List<TopStores>>() {
                                }.getType());

                                fillInItems(items);

                            }else{
                                Toast.makeText(getContext(),"Sorry an error occurred",Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                networkErrorLayout.setVisibility(View.VISIBLE);
                Toast.makeText(getContext(),"Sorry an error occurred. Try again",Toast.LENGTH_SHORT).show();


            }
        });
        jsonObjectRequest.setShouldCache(true);
        InitiateVolley.getInstance().addToRequestQueue(jsonObjectRequest);
    }

    private void fillInItems(List<TopStores> items){

        topStores.clear();
        topStores.addAll(items);
        // refreshing recycler view
        mAdapter.notifyDataSetChanged();

    }


}
