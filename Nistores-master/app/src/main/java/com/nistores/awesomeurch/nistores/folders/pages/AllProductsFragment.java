package com.nistores.awesomeurch.nistores.folders.pages;


import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import com.j256.ormlite.dao.Dao;
import com.nistores.awesomeurch.nistores.folders.adapters.ProductAdapter;
import com.nistores.awesomeurch.nistores.folders.helpers.AllProductsTable;
import com.nistores.awesomeurch.nistores.folders.helpers.ApiUrls;
import com.nistores.awesomeurch.nistores.folders.helpers.DatabaseHelper;
import com.nistores.awesomeurch.nistores.folders.helpers.EndlessRecyclerViewScrollListener;
import com.nistores.awesomeurch.nistores.folders.helpers.GridSpacingItemDecoration;
import com.nistores.awesomeurch.nistores.folders.helpers.InitiateVolley;
import com.nistores.awesomeurch.nistores.folders.helpers.Product;
import com.nistores.awesomeurch.nistores.R;


public class AllProductsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final String TAG = AllProductsFragment.class.getSimpleName();

    ApiUrls apiUrls;
    private String URL;

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    LinearLayout networkErrorLayout;
    ProgressBar progressBar;
    private List<Product> productList;
    private ProductAdapter mAdapter;
    DatabaseHelper helper;
    Integer rid = 1;
    Dao<AllProductsTable, Integer> dao;
    Dao<AllProductsTable, Long> dao1;
    List<AllProductsTable> allProductsTableList;
    JSONArray myProductArray = new JSONArray();
    int pageNo;
    private boolean refresh = false;
    EndlessRecyclerViewScrollListener scroller;

    //private OnFragmentInteractionListener mListener;

    public AllProductsFragment() {
        // Required empty public constructor
    }

    public static AllProductsFragment newInstance() {
        AllProductsFragment fragment = new AllProductsFragment();
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
        View view =  inflater.inflate(R.layout.fragment_all_products, container, false);

        return view;

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){
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
                getData(0);
            }
        });

        progressBar = view.findViewById(R.id.progress);
        swipeRefreshLayout = view.findViewById(R.id.swipe);
        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        setRefresh(true);
                        fetchProductItems(0);

                    }
                }
        );

        recyclerView = view.findViewById(R.id.recycler_view);
        productList = new ArrayList<>();
        mAdapter = new ProductAdapter(getContext(), productList);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), 2);
        //GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);

        recyclerView.setLayoutManager(mLayoutManager);

        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(8), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        recyclerView.setNestedScrollingEnabled(false);
        scroller = new EndlessRecyclerViewScrollListener ((GridLayoutManager) mLayoutManager) {
            @Override
            public void onLoadMore(final int page, int totalItemsCount, RecyclerView view) {
                //Toast.makeText(getContext(),""+page,Toast.LENGTH_SHORT).show();
                //scroller.resetState();
                pageNo = page * 20;
                getData(pageNo);

            }
        };

        recyclerView.addOnScrollListener(scroller);
        //recyclerView.setOnScrollListener(scroller);

        helper = new DatabaseHelper(getContext()).getInstance(getContext());
        AllProductsTable AllProductsTable = null;
        AllProductsTable dbCount = null;

        getData(0);

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private void getData(int start){

        //start = pageNo;
        //String origin = Integer.toString(start);
        fetchProductItems(start);
        /*try{
            //helper = new DatabaseHelper(getContext());
            dao1 = DaoManager.createDao(helper.getConnectionSource(), AllProductsTable.class);
            int length = 20;
            String startStr = Integer.toString(start);

            //Log.d("CHECK",startStr);

            QueryBuilder<AllProductsTable,Long> queryBuilder = dao1.queryBuilder().orderBy("id",true);
            queryBuilder.offset(Long.parseLong(startStr)).limit(Long.parseLong(Integer.toString(length)));
            allProductsTableList = queryBuilder.query();

            if(allProductsTableList.isEmpty()){ //Nothing is in the database
                //Log.d("CHECK","Nothing in db");
                fetchProductItems(origin);
            }else{ //Something is in the database
                //Log.d("CHECK","Something in db");

                for(AllProductsTable item:allProductsTableList){
                    String product_id = item.getProductId();
                    String pname = item.getName();
                    String pphoto = item.getPhoto();
                    String pprice = item.getPrice();
                    String views = item.getViews();
                    String store_uid = item.getStore_uid();
                    String store_id = item.getStore_id();
                    String likes = item.getLikes();
                    String featured = item.getFeatured();

                    JSONObject myProductObject = new JSONObject();
                    myProductObject.put("product_id",product_id);
                    myProductObject.put("pname",pname);
                    myProductObject.put("pphoto",pphoto);
                    myProductObject.put("pprice",pprice);
                    myProductObject.put("views",views);
                    myProductObject.put("store_uid",store_uid);
                    myProductObject.put("store_id",store_id);
                    myProductObject.put("likes",likes);
                    myProductObject.put("featured",featured);

                    myProductArray.put(myProductObject);
                }

                //Log.d("CHECK",myProductArray.toString());

                List<Product> items = new Gson().fromJson(myProductArray.toString(), new TypeToken<List<Product>>() {
                }.getType());

                fillInItems(items);

                //List<AllProductsTable> list1 = dao1.queryForAll();
            }

        }catch (Exception e){
            //Log.e("Err","Error occurred",e);
            //Log.d("ERR",e.toString());
        }*/
    }



    private void fetchProductItems(final int origin){
        String originURL = URL + "request=products&start=" + origin;
        //Log.d("CHECK",originURL);
        if(origin==0){
            if(!isRefresh()){
                progressBar.setVisibility(View.VISIBLE);
            }

        }
        networkErrorLayout.setVisibility(View.GONE);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, originURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        swipeRefreshLayout.setRefreshing(false);
                        progressBar.setVisibility(View.GONE);
                        //Log.d("RTN",response.toString());
                        try {

                            Integer err = response.getInt("error");
                            JSONArray data = response.getJSONArray("data");
                            if(err==0){

                                List<Product> items = new Gson().fromJson(data.toString(), new TypeToken<List<Product>>() {
                                }.getType());

                                fillInItems(items, origin);
                                /*for(int i=0;i<data.length();i++){
                                    JSONObject obj = data.getJSONObject(i);
                                    String product_id = obj.getString("product_id");
                                    String pname = obj.getString("pname");
                                    String pphoto = obj.getString("pphoto");
                                    String pprice = obj.getString("pprice");
                                    String views = obj.getString("views");
                                    String store_uid = obj.getString("store_uid");
                                    String store_id = obj.getString("store_id");
                                    String likes = obj.getString("likes");
                                    String featured = obj.getString("featured");

                                    helper = new DatabaseHelper(getContext()).getInstance(getContext());
                                    dao1 = DaoManager.createDao(helper.getConnectionSource(), AllProductsTable.class);
                                    QueryBuilder<AllProductsTable,Long> queryBuilder1 = dao1.queryBuilder();
                                    queryBuilder1.where().eq("productId",product_id);
                                    allProductsTableList = queryBuilder1.query();
                                    if(allProductsTableList.isEmpty()) {
                                        AllProductsTable all = new AllProductsTable();
                                        all.setProductId(product_id);
                                        all.setName(pname);
                                        all.setPhoto(pphoto);
                                        all.setPrice(pprice);
                                        all.setViews(views);
                                        all.setStore_uid(store_uid);
                                        all.setStore_id(store_id);
                                        all.setLikes(likes);
                                        all.setFeatured(featured);
                                        dao1.create(all);
                                    }
                                }*/

                            }else{
                                Toast.makeText(getContext(),"Sorry an error occurred",Toast.LENGTH_SHORT).show();
                                //networkErrorLayout.setVisibility(View.VISIBLE);
                            }
                        } catch (JSONException e) {
                            //Log.e("ERR",e.toString());
                            e.printStackTrace();
                        } catch (Exception e) {
                            //Log.e("ERR",e.toString());
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(origin==0){
                    networkErrorLayout.setVisibility(View.VISIBLE);
                    Toast.makeText(getContext(),"Sorry an error occurred. Try again",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getContext(),"No network connection",Toast.LENGTH_SHORT).show();
                }
                progressBar.setVisibility(View.GONE);
                ////Log.d("VOLLEY",error.toString());

            }
        });
        //jsonObjectRequest.setShouldCache("true");
        InitiateVolley.getInstance().addToRequestQueue(jsonObjectRequest);
    }

    private void fillInItems(List<Product> items, int startPoint){

        if(startPoint==0){
            scroller.resetState();
            //Log.d("ERRO","clear");
            productList.clear();
            productList.addAll(items);
            // refreshing recycler view
            mAdapter.notifyDataSetChanged();
        }else{
            //Log.d("ERRO","append");
            productList.addAll(items);
            final int curSize = mAdapter.getItemCount();
            recyclerView.post(new Runnable() {
                @Override
                public void run() {
                    //scroller.resetState();

                    //mAdapter.notifyItemInserted(productList.size() - 1);
                    mAdapter.notifyItemRangeInserted(curSize, productList.size() - 1);
                }
            });

        }

    }

    private boolean isRefresh() {
        return refresh;
    }

    public void setRefresh(boolean refresh) {
        this.refresh = refresh;
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }


}
