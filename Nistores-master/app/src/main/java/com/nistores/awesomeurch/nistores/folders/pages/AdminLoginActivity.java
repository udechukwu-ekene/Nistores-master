package com.nistores.awesomeurch.nistores.folders.pages;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.nistores.awesomeurch.nistores.folders.helpers.ApiUrls;
import com.nistores.awesomeurch.nistores.folders.helpers.VolleyRequest;
import com.nistores.awesomeurch.nistores.R;

import org.json.JSONException;
import org.json.JSONObject;

public class AdminLoginActivity extends AppCompatActivity {
    EditText emailInput, passwordInput;
    AppCompatButton processBtn;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);

        emailInput = findViewById(R.id.input_email);
        passwordInput = findViewById(R.id.input_password);
        processBtn = findViewById(R.id.btn_proceed);

        View.OnClickListener logger = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        };
        processBtn.setOnClickListener(logger);

        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        if (preferences.contains("isAdmin")) {
            Intent intent = new Intent(this,AllOrdersActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void login(){
        String email = emailInput.getText().toString();
        String password = passwordInput.getText().toString();
        if(email.isEmpty() || password.isEmpty() ){
            Toast.makeText(AdminLoginActivity.this,"Fill all fields",Toast.LENGTH_SHORT).show();
        }else{
            processLogin(email,password);

        }
    }

    private void processLogin(String email, String password){

        String newURL = new ApiUrls().getApiUrl() + "request=admin_login&email=" + email + "&password=" + password;
        VolleyRequest volleyRequest = new VolleyRequest(getApplicationContext(), newURL) {
            @Override
            public void onProcess() {
                //do nothing while processing
                processBtn.setText(getResources().getString(R.string.processing));
            }

            @Override
            public void onSuccess(JSONObject response) {
                processBtn.setText(getResources().getString(R.string.proceed));
                try {

                    Integer err = response.getInt("error");
                    if(err==0){

                        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putBoolean("isAdmin",true).apply();
                        Intent intent = new Intent(getApplicationContext(),AllOrdersActivity.class);
                        startActivity(intent);
                        finish();

                    }else{
                        if(err==1){
                            Toast.makeText(getApplicationContext(),"Invalid details",Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(getApplicationContext(),"Network Error",Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (JSONException e) {
                    //Log.e("V_ERROR",e.toString());
                    e.printStackTrace();
                }
            }


            @Override
            public void onNetworkError() {
                Toast.makeText(getApplicationContext(),"Network Error",Toast.LENGTH_SHORT).show();
            }
        };
        volleyRequest.setCache(false);
        volleyRequest.fetchResources();
    }

}
