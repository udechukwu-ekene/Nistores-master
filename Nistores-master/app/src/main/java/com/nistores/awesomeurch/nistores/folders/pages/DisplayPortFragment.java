package com.nistores.awesomeurch.nistores.folders.pages;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nistores.awesomeurch.nistores.folders.adapters.MorePhotoAdapter;
import com.nistores.awesomeurch.nistores.folders.adapters.selectCategoryAdapter;
import com.nistores.awesomeurch.nistores.folders.helpers.ApiUrls;
import com.nistores.awesomeurch.nistores.folders.helpers.FileUpload;
import com.nistores.awesomeurch.nistores.folders.helpers.InitiateVolley;
import com.nistores.awesomeurch.nistores.folders.helpers.MorePhoto;
import com.nistores.awesomeurch.nistores.folders.helpers.Utility;
import com.nistores.awesomeurch.nistores.folders.helpers.selectCategory;
import com.nistores.awesomeurch.nistores.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

public class DisplayPortFragment extends Fragment {
    RadioButton radioPrice, radioContact;
    RadioGroup radioGroupPrice;
    AppCompatButton btnMorePhotos, btnUpload;
    TextInputLayout priceLayout;
    Spinner storeSpinner;
    ApiUrls apiUrls;
    String URL, imgURL, postURL, storeCategoryString, userStoreString;
    SharedPreferences prefs;
    String userId;
    ArrayList<String> myStores;
    ImageView mainPhoto;
    TextView uploadClick;
    Intent intent;
    CoordinatorLayout coordinatorLayout;
    ConstraintLayout loaderLayout;
    Bitmap bitmap;
    RecyclerView imageRecycler, categoryRecycler;
    List<MorePhoto> morePhotos;
    MorePhotoAdapter mAdapter;
    List<selectCategory> selectCategoryList;
    selectCategoryAdapter categoryAdapter;
    ProgressBar uploadingMainPhoto, uploadingMorePhoto;
    EditText productNameView, productDescriptionView, priceView;
    ViewGroup categoriesGroup;
    HomeActivity homeActivity;
    Utility utility;
    JSONArray userStores;
    private static final int SELECT_PHOTO = 1;
    private static final int SELECT_MORE_PHOTO = 2;
    private int MAIN_PHOTO = 0;
    private int PRICE_TYPE = 1;
    private String mainPic, storeID, picMain;
    private static final String JPG = "jpg";

    public DisplayPortFragment() {
        // Required empty public constructor
    }

    public static DisplayPortFragment newInstance() {
        DisplayPortFragment fragment = new DisplayPortFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_display_port, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        homeActivity = new HomeActivity();
        apiUrls = new ApiUrls();
        URL = apiUrls.getApiUrl();
        imgURL = apiUrls.getApiURL2();
        postURL = apiUrls.getProcessPost();
        myStores = new ArrayList<>();
        utility = new Utility(getContext());

        productNameView = view.findViewById(R.id.product_name);
        productDescriptionView = view.findViewById(R.id.description);
        priceView = view.findViewById(R.id.price);

        loaderLayout = view.findViewById(R.id.loader_layout);
        uploadingMainPhoto = view.findViewById(R.id.progress_upload);
        coordinatorLayout = view.findViewById(R.id.myCoordinatorLayout);
        mainPhoto = view.findViewById(R.id.main_photo);
        mainPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //attachMainPhoto(view);
                openGallery(SELECT_PHOTO);
            }
        });

        btnMorePhotos = view.findViewById(R.id.btn_more_photos);
        btnMorePhotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //attachMainPhoto(view);
                openGallery(SELECT_MORE_PHOTO);
            }
        });
        btnUpload = view.findViewById(R.id.btn_upload);
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addProduct();
            }
        });

        uploadClick = view.findViewById(R.id.click_upload);
        storeSpinner = view.findViewById(R.id.select_store);
        storeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selStore = adapterView.getItemAtPosition(i).toString();
                //Log.d("SEL_ST",selStore+" "+i);
                loadCategories(i);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //Log.d("SEL_ST","nothing selected");
            }
        });

        priceLayout = view.findViewById(R.id.input_layout_price);
        radioGroupPrice = view.findViewById(R.id.radioGroupPrice);
        radioPrice = view.findViewById(R.id.radio_price);
        radioContact = view.findViewById(R.id.radio_contact);
        radioPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onRadioButtonClicked(view);
            }
        });
        radioContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onRadioButtonClicked(view);
            }
        });

        imageRecycler = view.findViewById(R.id.recycler_img_view);
        morePhotos = new ArrayList<>();
        mAdapter = new MorePhotoAdapter(getContext(), morePhotos);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), 3);
        imageRecycler.setLayoutManager(mLayoutManager);

        imageRecycler.setItemAnimator(new DefaultItemAnimator());
        imageRecycler.setAdapter(mAdapter);

        categoryRecycler = view.findViewById(R.id.recycler_category);
        selectCategoryList = new ArrayList<>();
        categoryAdapter = new selectCategoryAdapter(getContext(), selectCategoryList);
        RecyclerView.LayoutManager cLayoutManager = new LinearLayoutManager(getContext());
        //GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        categoryRecycler.setLayoutManager(cLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(categoryRecycler.getContext(),
                DividerItemDecoration.VERTICAL);
        categoryRecycler.addItemDecoration(dividerItemDecoration);
        categoryRecycler.setAdapter(categoryAdapter);

        if (savedInstanceState == null) {
            //Log.d("CHECKA","no save");
            Bundle args = getArguments();
            fetchItems();

        }else{
            //Log.d("CHECKA","saved");
            storeCategoryString = savedInstanceState.getString("storeCategoryString");
            if(storeCategoryString != null){
                fillInCategories(storeCategoryString);
            }
            userStoreString = savedInstanceState.getString("userStoreString");
            if(userStoreString != null){
                try {
                    JSONArray myStores = new JSONArray(userStoreString);
                    fillInItems(myStores);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else{
                fetchItems();
            }

        }

    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("storeCategoryString",storeCategoryString);
        outState.putString("userStoreString",userStoreString);
    }


    @Override
    public void onStop(){
        super.onStop();
        //TODO: stop all volleys using their tags
    }

    private void openGallery(int requestType){
        Toast.makeText(getContext(),"Opening images folder...",Toast.LENGTH_SHORT).show();
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, requestType);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(resultCode == RESULT_OK){
            Uri selectedImage = data.getData();
            if(selectedImage !=null){

                try {
                    //getting image from gallery
                    bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImage);

                    String resourceBase = utility.bitmapToBase64(bitmap);
                    //Setting image to ImageView
                    switch (requestCode){
                        case SELECT_PHOTO:
                            //mainPhoto.setImageBitmap(bitmap);
                            uploadMainFile(resourceBase, JPG);

                            break;
                        case SELECT_MORE_PHOTO:
                            uploadOtherFiles(resourceBase, JPG);

                            break;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }

    //method for uploading port main photo
    private void uploadMainFile(final String fileEncoded, final String ext){
        FileUpload fileUpload = new FileUpload(getContext()) {
            @Override
            public void onProcess() {
                preventInteraction();
                uploadClick.setVisibility(View.GONE);
                uploadingMainPhoto.setVisibility(View.VISIBLE);
            }

            @Override
            public void onSuccess(String imgPath) {
                MAIN_PHOTO = 1;
                picMain = imgPath;
                mainPic = apiUrls.getUploadsFolder()+imgPath;
                enableUserInteraction();
                Toast.makeText(context, "Uploaded Successfully", Toast.LENGTH_LONG).show();
                uploadingMainPhoto.setVisibility(View.GONE);
                Picasso.with(getContext()).load(mainPic).placeholder(R.drawable.ic_crop_image).into(mainPhoto);
            }

            @Override
            public void onServerError() {
                enableUserInteraction();
                Toast.makeText(context, "Server error occurred. Try again", Toast.LENGTH_LONG).show();
                uploadingMainPhoto.setVisibility(View.GONE);
                uploadClick.setVisibility(View.VISIBLE);
            }

            @Override
            public void onNetworkError() {
                enableUserInteraction();
                Toast.makeText(context, "Network error occurred. Try again", Toast.LENGTH_LONG).show();
                uploadClick.setVisibility(View.VISIBLE);
                uploadingMainPhoto.setVisibility(View.GONE);
            }
        };
        fileUpload.uploadImage(fileEncoded, ext);
    }

    //method for uploading other photos
    private void uploadOtherFiles(final String fileEncoded, final String ext){
        FileUpload fileUpload = new FileUpload(getContext()) {
            @Override
            public void onProcess() {
                preventInteraction();
                btnMorePhotos.setText(getResources().getString(R.string.uploading___));
            }

            @Override
            public void onSuccess(String imgPath) {
                enableUserInteraction();
                Toast.makeText(context, "Uploaded Successfully", Toast.LENGTH_LONG).show();
                btnMorePhotos.setText(getResources().getString(R.string.click_to_upload_more));
                appendMorePhotos(apiUrls.getUploadsFolder()+imgPath);
            }

            @Override
            public void onServerError() {
                enableUserInteraction();
                Toast.makeText(context, "Server error occurred. Try again", Toast.LENGTH_LONG).show();
                btnMorePhotos.setText(getResources().getString(R.string.click_to_upload_more));
            }

            @Override
            public void onNetworkError() {
                enableUserInteraction();
                Toast.makeText(context, "Network error occurred. Try again", Toast.LENGTH_LONG).show();
                btnMorePhotos.setText(getResources().getString(R.string.click_to_upload_more));
            }
        };
        fileUpload.uploadImage(fileEncoded, ext);
    }


    private void appendMorePhotos(String res){
        //Log.d("iPATH",""+res);
        try{
            JSONArray array = new JSONArray();
            JSONObject object = new JSONObject();
            object.put("image",res);
            array.put(object);

            List<MorePhoto> items = new Gson().fromJson(array.toString(), new TypeToken<List<MorePhoto>>() {
            }.getType());

            morePhotos.addAll(items);
            imageRecycler.post(new Runnable() {
                @Override
                public void run() {
                    //scroller.resetState();
                    mAdapter.notifyItemInserted(morePhotos.size() - 1);
                }
            });

        }catch(JSONException e){
            //Log.d("ERR",e.toString());
            e.printStackTrace();
        }
    }

    public void addProduct(){
        final String serverPic = "api/src/routes/"+picMain;
        boolean valid = true;
        final String pname = productNameView.getText().toString();
        final String pdesc = productDescriptionView.getText().toString();
        final String pprice = (PRICE_TYPE==1)?"":priceView.getText().toString();
        JSONArray morePhotsJson = new JSONArray();

        for(int i = 0; i < morePhotos.size(); i++){
            morePhotsJson.put(morePhotos.get(i).getImage());

        }
        final String morePhotosStr = morePhotsJson.toString();
        ////Log.d("MPH",morePhotosStr);
        final String selCats = utility.getSelectedCats(categoryRecycler);
        //Log.d("ALL_V",pname+"::"+pdesc+"::"+pprice+"::"+storeID+"::"+PRICE_TYPE+"::"+morePhotosStr+"::"+selCats);

        if(pname.isEmpty()){
            valid = false;
            productNameView.setError("Enter the name of product");
            Toast.makeText(getContext(),"Enter the name of product",Toast.LENGTH_SHORT).show();
        }
        if(pdesc.isEmpty()){
            valid = false;
            productDescriptionView.setError("Describe the product");
            Toast.makeText(getContext(),"Describe the product",Toast.LENGTH_SHORT).show();
        }
        if(MAIN_PHOTO == 0){
            valid = false;
            Toast.makeText(getContext(),"Upload picture of product",Toast.LENGTH_SHORT).show();
        }
        if(radioGroupPrice.getCheckedRadioButtonId() == -1){
            valid = false;
            Toast.makeText(getContext(),"Set price for the product",Toast.LENGTH_SHORT).show();
        }
        if(PRICE_TYPE==0){
            if(pprice.isEmpty()){
                valid = false;
                priceView.setError("Enter the price of product");
                Toast.makeText(getContext(),"Enter the price of product",Toast.LENGTH_SHORT).show();
            }
        }

        if(valid){

            //interstitial anxiety
            loaderLayout.setVisibility(View.VISIBLE);
            preventInteraction();
            StringRequest request = new StringRequest(Request.Method.POST, postURL, new Response.Listener<String>(){
                @Override
                public void onResponse(String s) {
                    loaderLayout.setVisibility(View.GONE);
                    enableUserInteraction();
                    //Log.d("DFILE",s);

                    if(s.equals("error")){

                        Toast.makeText(getContext(),"Sorry an error occurred. Retry",Toast.LENGTH_SHORT).show();

                    }
                    else{
                        Toast.makeText(getContext(),"Successfully uploaded",Toast.LENGTH_SHORT).show();
                        intent = new Intent(getContext(),SuccessActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                    }
                }
            },new Response.ErrorListener(){
                @Override
                public void onErrorResponse(VolleyError volleyError) {

                    Toast.makeText(getContext(),"Network error occurred. Please retry!",Toast.LENGTH_SHORT).show();
                    loaderLayout.setVisibility(View.GONE);

                    //Log.d("ERR",volleyError.toString());


                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError
                {
                    Map<String, String> parameters = new HashMap<String, String>();
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
                protected Map<String, String> getParams() throws AuthFailureError  {
                    Map<String, String> parameters = new HashMap<String, String>();
                    parameters.put("request", "add_product");
                    parameters.put("pname", pname);
                    parameters.put("pdesc", pdesc);
                    parameters.put("pprice", pprice);
                    parameters.put("pstore_id", storeID);
                    parameters.put("price_type", ""+PRICE_TYPE);
                    parameters.put("pphoto", serverPic);
                    parameters.put("pcategory", selCats);
                    parameters.put("extra_pics", morePhotosStr);

                    return parameters;
                }
            };


            //RequestQueue rQueue = Volley.newRequestQueue(getContext());
            request.setShouldCache(false);
            InitiateVolley.getInstance().addToRequestQueue(request);

            //rQueue.add(request);
        }


    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radio_price:
                if (checked)
                priceLayout.setVisibility(View.VISIBLE);
                PRICE_TYPE = 0;
                    break;
            case R.id.radio_contact:
                if (checked)
                priceLayout.setVisibility(View.GONE);
                PRICE_TYPE = 1;
                    break;
        }
    }

    private void fetchItems(){
        preventInteraction();
        prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        userId = prefs.getString("user",null);
        String originURL = URL + "request=my_stores&id=" + userId;
        //Log.d("CHECK",originURL);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, originURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        enableUserInteraction();
                        //Log.d("RTN",response.toString());
                        try {

                            Integer err = response.getInt("error");

                            if(err==0){
                                userStores = response.getJSONArray("data");
                                userStoreString = userStores.toString();
                                fillInItems(userStores);

                            }else if(err == 1){
                                Toast.makeText(getContext(),"Create a store first",Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(getContext(),CreateStoreActivity.class);
                                startActivity(intent);
                                //new HomeActivity().finish();

                            }else{
                                Toast.makeText(getContext(),"Sorry an error occurred",Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            //Log.e("ERR",e.toString());
                            e.printStackTrace();

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                enableUserInteraction();
                Toast.makeText(getContext(),"Sorry an error occurred. Try again",Toast.LENGTH_SHORT).show();

                Snackbar snackbar = Snackbar.make(coordinatorLayout, R.string.network_error,
                        Snackbar.LENGTH_LONG)
                        .setAction("RETRY", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        fetchItems();
                    }
                });
                snackbar.show();


            }
        });
        jsonObjectRequest.setShouldCache(false);
        InitiateVolley.getInstance().addToRequestQueue(jsonObjectRequest);
    }

    private void fillInItems(JSONArray items){
        try{
            for(int i=0;i<items.length();i++){
                JSONObject jsonObject1=items.getJSONObject(i);
                String storeName=jsonObject1.getString("sname");
                myStores.add(storeName);
            }

            storeSpinner.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, myStores));

        }catch (JSONException e){e.printStackTrace();}

    }

    private void loadCategories(final int index){


        //String storeId = null;
        try {
            JSONObject storeObj = userStores.getJSONObject(index);
            String storeCatId = storeObj.getString("scat_id");
            storeID = storeObj.getString("store_id");
            fetchCategories(storeCatId);


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void fetchCategories(final String cats){
        preventInteraction();
        String originURL = URL + "request=store_cats&catsStr="+cats;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, originURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        enableUserInteraction();
                        try {

                            Integer err = response.getInt("error");
                            JSONArray storeCategories = response.getJSONArray("data");
                            if(err==0){

                                storeCategoryString = storeCategories.toString();

                                fillInCategories(storeCategoryString);
                                //fillInItems(storeCategories);

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
                enableUserInteraction();
                Toast.makeText(getContext(),"Sorry an error occurred. Try again",Toast.LENGTH_SHORT).show();

                Snackbar snackbar = Snackbar.make(coordinatorLayout, R.string.network_error,
                        Snackbar.LENGTH_LONG)
                        .setAction("RETRY", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                fetchCategories(cats);
                            }
                        });
                snackbar.show();


            }
        });
        jsonObjectRequest.setShouldCache(false);
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

    public void preventInteraction(){
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    public void enableUserInteraction(){
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }


}
