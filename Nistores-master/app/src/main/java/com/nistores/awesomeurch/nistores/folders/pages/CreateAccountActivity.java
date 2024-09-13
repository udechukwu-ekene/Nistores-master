package com.nistores.awesomeurch.nistores.folders.pages;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.j256.ormlite.dao.Dao;
import com.nistores.awesomeurch.nistores.folders.adapters.selectCategoryAdapter;
import com.nistores.awesomeurch.nistores.folders.helpers.ApiUrls;
import com.nistores.awesomeurch.nistores.folders.helpers.DatabaseHelper;
import com.nistores.awesomeurch.nistores.folders.helpers.InitiateVolley;
import com.nistores.awesomeurch.nistores.folders.helpers.MyAlarmReceiver;
import com.nistores.awesomeurch.nistores.folders.helpers.UserTable;
import com.nistores.awesomeurch.nistores.folders.helpers.Utility;
import com.nistores.awesomeurch.nistores.folders.helpers.selectCategory;
import com.nistores.awesomeurch.nistores.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateAccountActivity extends AppCompatActivity {
    Intent intent;
    Button signInButton;
    RecyclerView categoryRecycler;
    List<selectCategory> selectCategoryList;
    selectCategoryAdapter categoryAdapter;
    Spinner spinner;
    AppCompatButton signUpBtn;
    LinearLayout networkErrorLayout;
    ConstraintLayout loaderLayout;
    EditText firstnameEdit, surnameEdit, usernameEdit, emailEdit, passwordEdit, confirmPasswordEdit;
    String categoryString, URL, postURL, firstname, surname, username, email, password, confirmPassword, location;
    Utility utility;
    DatabaseHelper helper;
    Dao<UserTable, Integer> dao;
    Integer const_id = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        URL = new ApiUrls().getApiUrl();
        postURL = new ApiUrls().getProcessPost();
        utility = new Utility(getApplicationContext());

        spinner = findViewById(R.id.location_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.locations, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                location = adapterView.getItemAtPosition(i).toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        categoryRecycler = findViewById(R.id.recycler_category);
        selectCategoryList = new ArrayList<>();
        categoryAdapter = new selectCategoryAdapter(getApplicationContext(), selectCategoryList);
        RecyclerView.LayoutManager cLayoutManager = new LinearLayoutManager(getApplicationContext());
        //GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        categoryRecycler.setLayoutManager(cLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(categoryRecycler.getContext(),
                DividerItemDecoration.VERTICAL);
        categoryRecycler.addItemDecoration(dividerItemDecoration);
        categoryRecycler.setAdapter(categoryAdapter);

        networkErrorLayout = findViewById(R.id.network_error_layout);
        loaderLayout = findViewById(R.id.loader_layout);

        firstnameEdit = findViewById(R.id.input_fname);
        surnameEdit = findViewById(R.id.input_surname);
        usernameEdit = findViewById(R.id.input_username);
        emailEdit = findViewById(R.id.input_email);
        passwordEdit = findViewById(R.id.input_password);
        confirmPasswordEdit = findViewById(R.id.input_cpassword);

        signUpBtn = findViewById(R.id.btn_sign_up);
        signInButton = findViewById(R.id.sign_in);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

        View.OnClickListener onCreate = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkInputs();
            }
        };
        signUpBtn.setOnClickListener(onCreate);

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

    public void signIn(){
        intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("categories",categoryString);
    }

    private void fetchCategories(){
        preventInteraction();
        String originURL = URL + "request=categories";
        //Log.d("CHECK",originURL);
        loaderLayout.setVisibility(View.VISIBLE);
        networkErrorLayout.setVisibility(View.GONE);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, originURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        enableUserInteraction();
                        loaderLayout.setVisibility(View.GONE);

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
                                Toast.makeText(getApplicationContext(),"Server error. Could not load categories",Toast.LENGTH_SHORT).show();
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
                enableUserInteraction();
                Toast.makeText(getApplicationContext(),"Network error. Could not load categories",Toast.LENGTH_SHORT).show();
                networkErrorLayout.setVisibility(View.VISIBLE);
                loaderLayout.setVisibility(View.GONE);
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
        firstname = firstnameEdit.getText().toString();
        surname = surnameEdit.getText().toString();
        username = usernameEdit.getText().toString();
        email = emailEdit.getText().toString();
        password = passwordEdit.getText().toString();
        confirmPassword = confirmPasswordEdit.getText().toString();

        if(firstname.isEmpty()){
            firstnameEdit.setError("Enter your first name");
            Toast.makeText(getApplicationContext(),"Enter your first name",Toast.LENGTH_SHORT).show();
            return;
        }
        if(surname.isEmpty()){
            surnameEdit.setError("Enter your surname");
            Toast.makeText(getApplicationContext(),"Enter your surname",Toast.LENGTH_SHORT).show();
            return;
        }
        if(username.isEmpty()){
            usernameEdit.setError("Enter your username");
            Toast.makeText(getApplicationContext(),"Enter your username",Toast.LENGTH_SHORT).show();
            return;
        }
        if(email.isEmpty()){
            emailEdit.setError("Enter your email address");
            Toast.makeText(getApplicationContext(),"Enter your email address",Toast.LENGTH_SHORT).show();
            return;
        }
        if(password.isEmpty()){
            passwordEdit.setError("Enter your password");
            Toast.makeText(getApplicationContext(),"Enter your password",Toast.LENGTH_SHORT).show();
            return;
        }
        if(confirmPassword.isEmpty()){
            confirmPasswordEdit.setError("Confirm your password");
            Toast.makeText(getApplicationContext(),"Confirm your password",Toast.LENGTH_SHORT).show();
            return;
        }
        if(!password.equals(confirmPassword)){
            Toast.makeText(getApplicationContext(),"Passwords do not match",Toast.LENGTH_SHORT).show();
            return;
        }
        if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailEdit.setError("Invalid email address");
            Toast.makeText(getApplicationContext(),"Invalid email address",Toast.LENGTH_SHORT).show();
            return;
        }

        createAccount();

    }

    private void createAccount(){
        final String cats = utility.getSelectedCats(categoryRecycler);

        preventInteraction();
        loaderLayout.setVisibility(View.VISIBLE);
        signUpBtn.setText(getResources().getString(R.string.processing));
        StringRequest request = new StringRequest(Request.Method.POST, postURL, new Response.Listener<String>(){
            @Override
            public void onResponse(String s) {
                loaderLayout.setVisibility(View.GONE);
                //uploading.dismiss();
                signUpBtn.setText(getResources().getString(R.string.complete_reg));
                enableUserInteraction();
                try {
                    JSONObject result = new JSONObject(s);

                    Integer error = result.getInt("error");
                    if(error == 0){
                        JSONObject data = result.getJSONObject("data");
                        //Log.d("KONFAMM",data.toString());
                        logUserIn(data);
                    }else{
                        Toast.makeText(getApplicationContext(),"Username or email already exists",Toast.LENGTH_SHORT).show();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),"Sorry an error occurred",Toast.LENGTH_SHORT).show();
                }


                //Log.d("DFILE",s);

                /*if(s.equals("success")){

                    Toast.makeText(getApplicationContext(),"Your store has been created!",Toast.LENGTH_SHORT).show();

                }
                else{
                    Toast.makeText(getApplicationContext(),"Sorry an error occurred. Retry",Toast.LENGTH_SHORT).show();
                }*/
            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                enableUserInteraction();
                loaderLayout.setVisibility(View.GONE);
                signUpBtn.setText(getResources().getString(R.string.complete_reg));
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
                parameters.put("request", "create_account");
                parameters.put("firstname", firstname);
                parameters.put("surname", surname);
                parameters.put("username", username);
                parameters.put("email", email);
                parameters.put("password", password);
                parameters.put("interest", cats);
                parameters.put("location", location);

                return parameters;
            }
        };

        //RequestQueue rQueue = Volley.newRequestQueue(getContext());
        request.setShouldCache(false);
        InitiateVolley.getInstance().addToRequestQueue(request);

    }

    public void logUserIn(JSONObject data){
        String id = null;
        try {
            id = data.getString("merchant_id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String info = data.toString();
        //save user id in shared preferences
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("user",id).apply();
        editor.putString("last_notification_id","0").apply();
        editor.putString("last_id","0").apply();
        scheduleAlarm();

        //save user data in user table
        helper = new DatabaseHelper(getApplicationContext());
        try{
            dao = helper.getUserDao();
            UserTable userTable = new UserTable();
            userTable.setId(const_id);
            userTable.setMerchant_id(id);
            userTable.setAll(info);
            dao.create(userTable);

        }catch(Exception e){
            e.printStackTrace();
            //Log.d("ERR",e.toString());
        }

        intent = new Intent(getApplicationContext(),HomeActivity.class);
        startActivity(intent);
        finish();
    }

    public void scheduleAlarm() {
        // Construct an intent that will execute the AlarmReceiver
        Intent intent = new Intent(getApplicationContext(), MyAlarmReceiver.class);
        // Create a PendingIntent to be triggered when the alarm goes off
        final PendingIntent pIntent = PendingIntent.getBroadcast(this, MyAlarmReceiver.REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        // Setup periodic alarm every every INTERVAL from this point onwards
        long firstMillis = System.currentTimeMillis(); // alarm is set right away
        AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        // First parameter is the type: ELAPSED_REALTIME, ELAPSED_REALTIME_WAKEUP, RTC_WAKEUP
        // Interval can be INTERVAL_FIFTEEN_MINUTES, INTERVAL_HALF_HOUR, INTERVAL_HOUR, INTERVAL_DAY
        if (alarm != null) {
            alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, firstMillis,
                    utility.NOTIF_INTERVAL, pIntent);
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
