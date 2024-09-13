package com.nistores.awesomeurch.nistores.folders.pages;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nistores.awesomeurch.nistores.folders.adapters.selectCategoryAdapter;
import com.nistores.awesomeurch.nistores.folders.helpers.ApiUrls;
import com.nistores.awesomeurch.nistores.folders.helpers.FileUpload;
import com.nistores.awesomeurch.nistores.folders.helpers.InitiateVolley;
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

public class EditProfileActivity extends AppCompatActivity {
    EditText firstNameView, lastNameView, emailView;
    ImageView picView;
    RecyclerView categoryRecycler;
    LinearLayout networkErrorLayout;
    ProgressBar progressBar;
    String firstname, lastname, email, picture, URL, postURL, interests, newPicture, userId, newFirstName, newLastName, newEmail;
    AppCompatButton btnSaveProfile, btnEditProfilePic, btnUpdateInterest, btnRetry;
    List<selectCategory> selectCategoryList;
    selectCategoryAdapter categoryAdapter;
    ApiUrls apiUrls;
    Bitmap bitmap;
    Intent intent;
    Utility utility;
    private static final int SELECT_PHOTO = 1;
    private static final int SELECT_MORE_PHOTO = 2;
    private static final String JPG = "jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        apiUrls = new ApiUrls();
        URL = apiUrls.getApiUrl();
        postURL = apiUrls.getProcessPost();

        utility = new Utility(getApplicationContext());

        firstNameView = findViewById(R.id.first_name);
        lastNameView = findViewById(R.id.last_name);
        emailView = findViewById(R.id.input_email);
        picView = findViewById(R.id.profile_picture);
        categoryRecycler = findViewById(R.id.recycler_category);
        networkErrorLayout = findViewById(R.id.network_error_layout);
        progressBar = findViewById(R.id.loader);

        btnSaveProfile = findViewById(R.id.btn_save_profile);
        btnEditProfilePic = findViewById(R.id.edit_profile_pic);
        btnUpdateInterest = findViewById(R.id.update_interest);

        btnSaveProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveProfileChanges();
            }
        });

        btnEditProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery(SELECT_PHOTO);
            }
        });

        btnUpdateInterest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateInterest();
            }
        });

        Bundle bundle = this.getIntent().getExtras();
        if(bundle!=null) {
            firstname = bundle.getString("firstname");
            lastname = bundle.getString("lastname");
            email = bundle.getString("email");
            picture = bundle.getString("picture");
            interests = bundle.getString("interests");


            selectCategoryList = new ArrayList<>();
            categoryAdapter = new selectCategoryAdapter(getApplicationContext(), selectCategoryList);
            categoryAdapter.setPreSelect(interests);
            RecyclerView.LayoutManager cLayoutManager = new LinearLayoutManager(getApplicationContext());
            //GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
            categoryRecycler.setLayoutManager(cLayoutManager);
            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(categoryRecycler.getContext(),
                    DividerItemDecoration.VERTICAL);
            categoryRecycler.addItemDecoration(dividerItemDecoration);
            categoryRecycler.setAdapter(categoryAdapter);

            setInitialValues();
            fetchCategories();
        }

    }


    private void openGallery(int requestType){
        Toast.makeText(getApplicationContext(),"Opening images folder...",Toast.LENGTH_SHORT).show();
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
                    bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), selectedImage);

                    String resourceBase = utility.bitmapToBase64(bitmap);
                    //Setting image to ImageView
                    switch (requestCode){
                        case SELECT_PHOTO:
                            //mainPhoto.setImageBitmap(bitmap);
                            uploadMainFile(resourceBase, JPG);

                            break;
                        case SELECT_MORE_PHOTO:
                            //uploadOtherFiles(resourceBase, JPG);
                            //Do nothing
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
        FileUpload fileUpload = new FileUpload(getApplicationContext()) {
            @Override
            public void onProcess() {
                preventInteraction();
                btnEditProfilePic.setText(getResources().getString(R.string.loading));
            }

            @Override
            public void onSuccess(String imgPath) {
                newPicture = imgPath;
                enableUserInteraction();
                Toast.makeText(context, "Uploaded Successfully", Toast.LENGTH_LONG).show();
                btnEditProfilePic.setText(getResources().getString(R.string.change_profile_pic));
                Picasso.with(getApplicationContext()).load(apiUrls.getUploadsFolder()+imgPath).placeholder(R.drawable.ic_crop_image).into(picView);
            }

            @Override
            public void onServerError() {
                enableUserInteraction();
                Toast.makeText(context, "Server error occurred. Try again", Toast.LENGTH_LONG).show();
                btnEditProfilePic.setText(getResources().getString(R.string.change_profile_pic));
            }

            @Override
            public void onNetworkError() {
                enableUserInteraction();
                Toast.makeText(context, "Network error occurred. Try again", Toast.LENGTH_LONG).show();
                btnEditProfilePic.setText(getResources().getString(R.string.change_profile_pic));
            }
        };
        fileUpload.uploadImage(fileEncoded, ext);
    }

    private void setInitialValues(){
        firstNameView.setText(firstname);
        lastNameView.setText(lastname);
        emailView.setText(email);
        newPicture = picture;

        if(!picture.isEmpty()){
            final String STRING_BASE_URL = "https://www.nistores.com.ng/";
            Picasso.with(getApplicationContext()).load(STRING_BASE_URL + picture).placeholder(R.drawable.ic_person_default).into(picView);
        }

    }

    private void saveProfileChanges(){
        newFirstName = firstNameView.getText().toString();
        newLastName = lastNameView.getText().toString();
        newEmail = emailView.getText().toString();
        if(newFirstName.equals(firstname) && newLastName.equals(lastname) && newEmail.equals(email) && newPicture.equals(picture) ){
            Toast.makeText(getApplicationContext(),"No change detected",Toast.LENGTH_SHORT).show();
        }else{
            updateChanges();
            //Toast.makeText(getApplicationContext(),newPicture,Toast.LENGTH_SHORT).show();
        }

    }

    private void updateInterest(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        userId = prefs.getString("user",null);
        final String interests = utility.getSelectedCats(categoryRecycler);
        preventInteraction();
        btnUpdateInterest.setText(getResources().getString(R.string.loading));
        StringRequest request = new StringRequest(Request.Method.POST, postURL, new Response.Listener<String>(){
            @Override
            public void onResponse(String s) {
                btnUpdateInterest.setText(getResources().getString(R.string.update_interest));
                enableUserInteraction();
                ////Log.d("DFILE",s);

                if(s.equals("error")){

                    Toast.makeText(getApplicationContext(),"Sorry an error occurred. Retry",Toast.LENGTH_SHORT).show();

                }
                else{
                    Toast.makeText(getApplicationContext(),"Changes have been saved!",Toast.LENGTH_SHORT).show();
                }
            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                enableUserInteraction();
                btnUpdateInterest.setText(getResources().getString(R.string.update_interest));
                Toast.makeText(getApplicationContext(),"Network error occurred. Please retry!",Toast.LENGTH_SHORT).show();

                ////Log.d("ERR",volleyError.toString());


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
                parameters.put("request", "edit_interest");
                parameters.put("interest", interests);
                parameters.put("id", userId);

                return parameters;
            }
        };

        //RequestQueue rQueue = Volley.newRequestQueue(getContext());
        request.setShouldCache(false);
        InitiateVolley.getInstance().addToRequestQueue(request);
    }

    private void updateChanges(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        userId = prefs.getString("user",null);
        final String serverImg = "api/src/routes/"+newPicture;

        preventInteraction();
        btnSaveProfile.setText(getResources().getString(R.string.loading));
        StringRequest request = new StringRequest(Request.Method.POST, postURL, new Response.Listener<String>(){
            @Override
            public void onResponse(String s) {
                btnSaveProfile.setText(getResources().getString(R.string.save_changes));
                enableUserInteraction();

                if(s.equals("error")){

                    Toast.makeText(getApplicationContext(),"Sorry an error occurred. Retry",Toast.LENGTH_SHORT).show();

                }
                else{
                    Toast.makeText(getApplicationContext(),"Changes have been saved!",Toast.LENGTH_SHORT).show();
                    firstname = newFirstName;
                    lastname = newLastName;
                    email = newEmail;
                }
            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                enableUserInteraction();
                btnSaveProfile.setText(getResources().getString(R.string.save_changes));
                Toast.makeText(getApplicationContext(),"Network error occurred. Please retry!",Toast.LENGTH_SHORT).show();

                ////Log.d("ERR",volleyError.toString());


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
                parameters.put("request", "edit_profile");
                parameters.put("surname", newLastName);
                parameters.put("firstname", newFirstName);
                parameters.put("email", newEmail);
                parameters.put("picture", serverImg);
                parameters.put("id", userId);

                return parameters;
            }
        };

        //RequestQueue rQueue = Volley.newRequestQueue(getContext());
        request.setShouldCache(false);
        InitiateVolley.getInstance().addToRequestQueue(request);
    }

    private void fetchCategories(){

        String originURL = URL + "request=categories";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, originURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        progressBar.setVisibility(View.GONE);

                        //Log.d("RTN",response.toString());
                        try {
                            Integer err = response.getInt("error");
                            JSONArray storeCategories = response.getJSONArray("data");
                            if(err==0){
                                btnUpdateInterest.setVisibility(View.VISIBLE);
                                List<selectCategory> items = new Gson().fromJson(storeCategories.toString(), new TypeToken<List<selectCategory>>() {
                                }.getType());
                                fillInCategories(items);
                                //fillInItems(storeCategories);

                            }else{
                                Toast.makeText(getApplicationContext(),"Sorry an error occurred",Toast.LENGTH_SHORT).show();
                                networkErrorLayout.setVisibility(View.VISIBLE);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"Sorry an error occurred. Try again",Toast.LENGTH_SHORT).show();
                networkErrorLayout.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);

            }
        });
        //jsonObjectRequest.setShouldCache(false);
        InitiateVolley.getInstance().addToRequestQueue(jsonObjectRequest);
    }

    private void fillInCategories(List<selectCategory> items){
        selectCategoryList.clear();
        selectCategoryList.addAll(items);
        // refreshing recycler view
        categoryAdapter.notifyDataSetChanged();
    }

    public void preventInteraction(){
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    public void enableUserInteraction(){
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }
}
