package com.nistores.awesomeurch.nistores.folders.pages;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
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
import com.nistores.awesomeurch.nistores.folders.adapters.BusinessLoungeAdapter;
import com.nistores.awesomeurch.nistores.folders.helpers.ApiUrls;
import com.nistores.awesomeurch.nistores.folders.helpers.BusinessLounge;
import com.nistores.awesomeurch.nistores.folders.helpers.InitiateVolley;
import com.nistores.awesomeurch.nistores.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link //BusinessLoungeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BusinessLoungeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BusinessLoungeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    ApiUrls apiUrls;
    private String URL;

    RecyclerView recyclerView;
    LinearLayout networkErrorLayout;
    ProgressBar progressBar;
    private List<BusinessLounge> businessLoungeList;
    private BusinessLoungeAdapter mAdapter;

    public BusinessLoungeFragment() {
        // Required empty public constructor
    }

    public static BusinessLoungeFragment newInstance() {
        BusinessLoungeFragment fragment = new BusinessLoungeFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_business_lounge, container, false);
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
        businessLoungeList = new ArrayList<>();
        mAdapter = new BusinessLoungeAdapter(getContext(), businessLoungeList);
        mAdapter.setOpenActivity("state_topics");
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        //GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(mLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
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
        String originURL = URL + "request=biz_lounge";
        //Log.d("CHECK",originURL);
        progressBar.setVisibility(View.VISIBLE);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, originURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        progressBar.setVisibility(View.GONE);
                        //Log.d("RTN",response.toString());
                        try {

                            Integer err = response.getInt("error");
                            if(err==0){
                                JSONArray data = response.getJSONArray("data");
                                List<BusinessLounge> items = new Gson().fromJson(data.toString(), new TypeToken<List<BusinessLounge>>() {
                                }.getType());

                                fillInItems(items);

                            }else{
                                Toast.makeText(getContext(),"Sorry an error occurred",Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            //Log.e("ERR",e.toString());
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                    networkErrorLayout.setVisibility(View.VISIBLE);
                    Toast.makeText(getContext(),"Sorry an error occurred. Try again",Toast.LENGTH_SHORT).show();

                //Log.d("VOLLEY",error.toString());

            }
        });
        jsonObjectRequest.setShouldCache(true);
        InitiateVolley.getInstance().addToRequestQueue(jsonObjectRequest);
    }

    private void fillInItems(List<BusinessLounge> items){

        businessLoungeList.clear();
        businessLoungeList.addAll(items);
        // refreshing recycler view
        mAdapter.notifyDataSetChanged();

    }

}
