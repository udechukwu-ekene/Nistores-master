package com.nistores.awesomeurch.nistores.folders.pages;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.nistores.awesomeurch.nistores.folders.helpers.ApiUrls;
import com.nistores.awesomeurch.nistores.folders.helpers.InitiateVolley;
import com.nistores.awesomeurch.nistores.folders.helpers.VolleyRequest;
import com.nistores.awesomeurch.nistores.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import co.paystack.android.Paystack;
import co.paystack.android.PaystackSdk;
import co.paystack.android.Transaction;
import co.paystack.android.model.Card;
import co.paystack.android.model.Charge;

public class PayActivity extends AppCompatActivity {

    Card card;
    AppCompatButton payBtn;
    int amount = 0;
    String email, userId, storeId, storeUid, URL, postURL, ref_no, days, pay_for;
    EditText mEditCardNum;
    EditText mEditCVV;
    EditText mEditExpiryMonth;
    EditText mEditExpiryYear;
    TextView mTextError;
    ConstraintLayout loaderLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);

        mEditCardNum = findViewById(R.id.card_number);
        mEditCVV = findViewById(R.id.cvv);
        mEditExpiryMonth = findViewById(R.id.month);
        mEditExpiryYear = findViewById(R.id.year);
        mTextError = findViewById(R.id.error_statement);
        loaderLayout = findViewById(R.id.loader_layout);

        URL = new ApiUrls().getApiUrl();
        postURL = new ApiUrls().getProcessPost();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        userId = preferences.getString("user",null);
        storeId = preferences.getString("current_store_id",null);
        storeUid = preferences.getString("current_store_uid", null);

        View.OnClickListener pay = (new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkValidity();
            }
        });

        payBtn = findViewById(R.id.btn_pay);
        payBtn.setOnClickListener(pay);

        PaystackSdk.initialize(getApplicationContext());
        //pk_test_0d7331b46439f9b6d6e5db8c4d893fe3a461f0f0  Test public key

        String cardNumber = "4084084084084081";
        int expiryMonth = 11; //any month in the future
        int expiryYear = 18; // any year in the future. '2018' would work also!
        String cvv = "408";  // cvv of the test card

        card = new Card(cardNumber, expiryMonth, expiryYear, cvv);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            amount = bundle.getInt("amount");
            days = bundle.getString("days");
            String title = "Pay NGN "+amount;
            setTitle(title);
            pay_for = bundle.getString("pay_for");
            if(pay_for == null){
                PayActivity.this.pay_for = "store_renew";
            }

            getInfo();
        }


    }

    private void getInfo(){
        String pURL = URL + "request=info&id=" + userId;
        //Log.d("myURL",pURL);
        VolleyRequest volleyRequest = new VolleyRequest(getApplicationContext(), pURL) {
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
                        email = data.getString("email");

                    }else{
                        Toast.makeText(getApplicationContext(),"Server Error Occurred.",Toast.LENGTH_SHORT).show();
                        PayActivity.this.onBackPressed();
                    }
                } catch (JSONException e) {
                    //Log.e("V_ERROR",e.toString());
                    e.printStackTrace();
                }
            }

            @Override
            public void onNetworkError() {
                enableUserInteraction();
                Toast.makeText(getApplicationContext(),"Network Error Occurred. Check your internet connection.",Toast.LENGTH_SHORT).show();
                PayActivity.this.onBackPressed();
            }
        };
        volleyRequest.setCache(false);
        volleyRequest.fetchResources();

    }

    private Card loadCardFromForm() {
        //validate fields
        Card card;

        String cardNum = mEditCardNum.getText().toString().trim();

        //build card object with ONLY the number, update the other fields later
        card = new Card.Builder(cardNum, 0, 0, "").build();
        String cvc = mEditCVV.getText().toString().trim();
        //update the cvc field of the card
        card.setCvc(cvc);

        //validate expiry month;
        String sMonth = mEditExpiryMonth.getText().toString().trim();
        int month = 0;
        try {
            month = Integer.parseInt(sMonth);
        } catch (Exception ignored) {
        }

        card.setExpiryMonth(month);

        String sYear = mEditExpiryYear.getText().toString().trim();
        int year = 0;
        try {
            year = Integer.parseInt(sYear);
        } catch (Exception ignored) {
        }
        card.setExpiryYear(year);

        return card;
    }

    public void checkValidity(){
        Boolean valid = true;
        if (!loadCardFromForm().isValid()) {
           valid = false;
           Toast.makeText(this,"Card is not valid", Toast.LENGTH_SHORT).show();
        }

        if(amount == 0){
            valid = false;
        }

        if(storeId == null){
            valid = false;
        }

        if(valid){
            performCharge();
        }
    }

    public void performCharge(){
        //create a Charge object
        Charge charge = new Charge();
        charge.setAmount(amount*100);
        charge.setEmail(email);
        charge.setReference("Android_" + Calendar.getInstance().getTimeInMillis());
        try {
            charge.putCustomField("Merchant_id",userId);
            charge.putCustomField("Charged From", "Android SDK");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        loaderLayout.setVisibility(View.VISIBLE);
        preventInteraction();
        charge.setCard(loadCardFromForm()); //sets the card to charge

        PaystackSdk.chargeCard(PayActivity.this, charge, new Paystack.TransactionCallback() {
            @Override
            public void onSuccess(Transaction transaction) {
                enableUserInteraction();
                loaderLayout.setVisibility(View.GONE);
                // This is called only after transaction is deemed successful.
                // Retrieve the transaction, and send its reference to your server
                // for verification.

                ref_no = transaction.getReference();
                //Log.d("TRANZAKT",transaction.getReference());
                Toast.makeText(PayActivity.this,"Your payment was successful",Toast.LENGTH_LONG).show();
                String check = ref_no + " " + userId + " " + storeId + " " + amount + " " + days + " " + storeUid;
                if(pay_for.equals("store_renew")){
                    notifyNistores();
                }else{

                    notifyNistoresDO();
                }


            }

            @Override
            public void beforeValidate(Transaction transaction) {
                // This is called only before requesting OTP.
                // Save reference so you may send to server. If
                // error occurs with OTP, you should still verify on server.
            }

            @Override
            public void onError(Throwable error, Transaction transaction) {
                enableUserInteraction();
                loaderLayout.setVisibility(View.GONE);
                //handle error here
                if (transaction.getReference() != null) {
                    Toast.makeText(PayActivity.this, transaction.getReference() + " concluded with error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    mTextError.setText(String.format("%s  concluded with error: %s %s", transaction.getReference(), error.getClass().getSimpleName(), error.getMessage()));
                } else {
                    Toast.makeText(PayActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                    mTextError.setText(String.format("Error: %s %s", error.getClass().getSimpleName(), error.getMessage()));
                }

            }

        });
    }

    private void notifyNistores(){
        preventInteraction();
        Toast.makeText(PayActivity.this,"Taking note ot your payment. Please wait...",Toast.LENGTH_SHORT).show();
        loaderLayout.setVisibility(View.VISIBLE);
        StringRequest request = new StringRequest(Request.Method.POST, postURL, new Response.Listener<String>(){
            @Override
            public void onResponse(String s) {
                loaderLayout.setVisibility(View.GONE);
                //uploading.dismiss();
                enableUserInteraction();
                //Log.d("DFILE",s);


                toSuccessPage1();

            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                enableUserInteraction();
                loaderLayout.setVisibility(View.GONE);

                //Log.d("ERRARR",volleyError.toString());
                toSuccessPage1();


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
                parameters.put("request", "make_payment");
                parameters.put("ref_no", ref_no);
                parameters.put("pay_user", userId);
                parameters.put("pay_store", storeId);
                parameters.put("pay_amount", ""+amount);
                parameters.put("days", days);
                parameters.put("store_uid", storeUid);

                return parameters;
            }
        };

        //RequestQueue rQueue = Volley.newRequestQueue(getContext());
        request.setShouldCache(false);
        InitiateVolley.getInstance().addToRequestQueue(request);
    }

    private void notifyNistoresDO(){
        preventInteraction();
        Toast.makeText(PayActivity.this,"Taking note ot your payment. Please wait...",Toast.LENGTH_SHORT).show();
        loaderLayout.setVisibility(View.VISIBLE);
        StringRequest request = new StringRequest(Request.Method.POST, postURL, new Response.Listener<String>(){
            @Override
            public void onResponse(String s) {
                loaderLayout.setVisibility(View.GONE);
                //uploading.dismiss();
                enableUserInteraction();
                //Log.d("DFILE",s);

                toSuccessPage2();


            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                enableUserInteraction();
                loaderLayout.setVisibility(View.GONE);

                //Log.d("ERRARR",volleyError.toString());
                toSuccessPage2();


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
                parameters.put("request", "make_delivery_payment");
                parameters.put("ref_no", ref_no);
                parameters.put("pay_user", userId);
                parameters.put("pay_data", storeId);
                parameters.put("pay_amount", ""+amount);

                return parameters;
            }
        };

        //RequestQueue rQueue = Volley.newRequestQueue(getContext());
        request.setShouldCache(false);
        InitiateVolley.getInstance().addToRequestQueue(request);
    }

    private void toSuccessPage1(){
        Bundle bundle = new Bundle();
        bundle.putString("message",String.format("Your store [%s] payment has been updated with N%s - %s days",storeUid,amount,days)); //
        Intent intent = new Intent(getApplicationContext(),SuccessPaymentActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }

    private void toSuccessPage2(){
        Bundle bundle = new Bundle();
        bundle.putString("message",String.format("Your payment for the delivery order %s has been noted",storeId)); //
        Intent intent = new Intent(getApplicationContext(),SuccessPaymentActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }

    public void preventInteraction(){
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    public void enableUserInteraction(){
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

}
