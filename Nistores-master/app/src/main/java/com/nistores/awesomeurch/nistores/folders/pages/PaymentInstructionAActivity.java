package com.nistores.awesomeurch.nistores.folders.pages;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nistores.awesomeurch.nistores.folders.adapters.PaymentPlanAdapter;
import com.nistores.awesomeurch.nistores.folders.helpers.ApiUrls;
import com.nistores.awesomeurch.nistores.folders.helpers.PaymentPlan;
import com.nistores.awesomeurch.nistores.folders.helpers.VolleyRequest;
import com.nistores.awesomeurch.nistores.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PaymentInstructionAActivity extends AppCompatActivity {

    LinearLayout inBankLayout, agentLayout, mobileTransferLayout, cardLayout, networkErrorLayout, plansALayout, plansBLayout, amountALayout, amountBLayout;
    RecyclerView planRecycler;
    ProgressBar progressBar;
    List<PaymentPlan> paymentPlanList;
    PaymentPlanAdapter planAdapter;
    AppCompatButton callBtn, retryBtn;
    TextView agentPhoneView, titleCardView, depositorAView, depositorBView, assureAView, assureBView;
    String payMethod, URL, planString, amount, payment_for;
    static String ATM = "atm";
    static String IN_BANK = "in_bank";
    static String WITH_BANK = "with_bank";
    static String MOBILE = "mobile";
    String AGENT = "agent";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_instruction_a);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            payMethod = bundle.getString("method");
            payment_for = bundle.getString("pay_for");
            amount = bundle.getString("amount");

            if(payment_for!=null){
                //Log.d("KONFAM",payment_for);
                if(payment_for.equals("delivery_order")){
                    setContentView(R.layout.activity_payment_instruction_b);

                    /*if(amount != null){
                        amountViewA.setText(amount);
                        amountViewB.setText(amount);
                    }*/
                }
            }else{
                payment_for = "store_renew";
            }

        }else{
            payment_for = "store_renew";
        }

        View.OnClickListener onPlaceCall = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callAgent();
            }
        };
        URL = new ApiUrls().getApiUrl();

        agentPhoneView = findViewById(R.id.agent_phone);
        titleCardView = findViewById(R.id.title_card);
        /*amountViewA = findViewById(R.id.pay_amount_a);
        amountViewB = findViewById(R.id.pay_amount_b);
        depositorAView = findViewById(R.id.depositor_a);
        depositorBView = findViewById(R.id.depositor_b);*/

        progressBar = findViewById(R.id.loader);
        cardLayout = findViewById(R.id.card);
        networkErrorLayout = findViewById(R.id.network_error);
        inBankLayout = findViewById(R.id.in_bank);
        agentLayout = findViewById(R.id.agent);
        mobileTransferLayout = findViewById(R.id.mobile_transfer);
        /*plansALayout = findViewById(R.id.plans_a);
        plansBLayout = findViewById(R.id.plans_b);
        amountALayout = findViewById(R.id.amount_a);
        amountBLayout = findViewById(R.id.amount_b);
        assureAView = findViewById(R.id.assure_a);
        assureBView = findViewById(R.id.assure_b);*/
        callBtn = findViewById(R.id.btn_call);
        callBtn.setOnClickListener(onPlaceCall);

        retryBtn = findViewById(R.id.btn_retry);

        planRecycler = findViewById(R.id.payment_recycler);
        paymentPlanList = new ArrayList<>();
        planAdapter = new PaymentPlanAdapter(getApplicationContext(), paymentPlanList);

        //RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,1);
        gridLayoutManager.setOrientation(LinearLayout.HORIZONTAL);
        planRecycler.setLayoutManager(gridLayoutManager);

        planRecycler.setItemAnimator(new DefaultItemAnimator());
        planRecycler.setAdapter(planAdapter);

        setUI();

    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();

    }

    private void setUI() {
        /*if(payment_for != null){
            //Log.d("KONFAM",payment_for);
            if(payment_for.equals("delivery_order")){
                plansALayout.setVisibility(View.GONE);
                plansBLayout.setVisibility(View.GONE);
                amountBLayout.setVisibility(View.VISIBLE);
                amountALayout.setVisibility(View.VISIBLE);
                depositorAView.setText(getResources().getString(R.string.depositor_name2));
                depositorBView.setText(getResources().getString(R.string.depositor_name2));
                assureAView.setText(getResources().getString(R.string.when_you_make_payment_short));
                assureBView.setText(getResources().getString(R.string.when_you_make_payment_short));
            }
        }else{
            //Log.d("KONFAM","null");
            plansALayout.setVisibility(View.VISIBLE);
            plansBLayout.setVisibility(View.VISIBLE);
            amountBLayout.setVisibility(View.GONE);
            amountALayout.setVisibility(View.GONE);
            depositorAView.setText(getResources().getString(R.string.depositor_name));
            depositorBView.setText(getResources().getString(R.string.narration_description));
            assureAView.setText(getResources().getString(R.string.when_you_make_payment));
            assureBView.setText(getResources().getString(R.string.when_you_make_payment));
        }*/
        if(payment_for.equals("delivery_order")){
            TextView amountViewA = findViewById(R.id.pay_amount_a);
            TextView amountViewB = findViewById(R.id.pay_amount_b);
            amountViewA.setText(amount);
            amountViewB.setText(amount);
        }

        switch (payMethod) {
            case "agent":
                agentLayout.setVisibility(View.VISIBLE);
                break;
            case "in_bank":
                inBankLayout.setVisibility(View.VISIBLE);
                break;
            case "mobile":
                mobileTransferLayout.setVisibility(View.VISIBLE);
                break;
            case "with_bank":
                if(payment_for.equals("delivery_order")){
                    Bundle bundle = new Bundle();
                    bundle.putInt("amount",Integer.valueOf(amount));
                    bundle.putString("pay_for","delivery_order");
                    Intent intent = new Intent(getApplicationContext(),PayActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }else{
                    cardLayout.setVisibility(View.VISIBLE);
                    titleCardView.setText(getResources().getString(R.string.pay_with_bank));
                    fetchPlans();
                }


                break;
            case "atm":
                if(payment_for.equals("delivery_order")){
                    Bundle bundle = new Bundle();
                    bundle.putInt("amount",Integer.valueOf(amount));
                    bundle.putString("pay_for","delivery_order");
                    Intent intent = new Intent(getApplicationContext(),PayActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }else{
                    cardLayout.setVisibility(View.VISIBLE);
                    titleCardView.setText(getResources().getString(R.string.pay_with_atm));
                    fetchPlans();
                }

                break;
        }
    }

    private void callAgent() {
        String number = agentPhoneView.getText().toString();
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + number));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {

            startActivity(callIntent);
        }else{
            Toast.makeText(this,"Permission to make calls not granted",Toast.LENGTH_SHORT).show();
        }

    }

    private void fetchPlans(){

        String newURL = URL + "request=pay_plans";
        VolleyRequest volleyRequest = new VolleyRequest(getApplicationContext(), newURL) {
            @Override
            public void onProcess() {
                //do nothing while processing
                networkErrorLayout.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onSuccess(JSONObject response) {
                progressBar.setVisibility(View.GONE);
                networkErrorLayout.setVisibility(View.GONE);
                try {

                    Integer err = response.getInt("error");
                    if(err==0){

                        JSONArray data = response.getJSONArray("data");
                        //Log.d("MPUTA",data.toString());
                        planString = data.toString();
                        fillInItems(data);

                    }else{

                        networkErrorLayout.setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
                    //Log.e("V_ERROR",e.toString());
                    e.printStackTrace();
                }
            }

            @Override
            public void onNetworkError() {
                progressBar.setVisibility(View.GONE);
                networkErrorLayout.setVisibility(View.VISIBLE);
            }
        };
        volleyRequest.fetchResources();
    }

    private void fillInItems(JSONArray data){
        List<PaymentPlan> items = new Gson().fromJson(data.toString(), new TypeToken<List<PaymentPlan>>() {
        }.getType());
        paymentPlanList.clear();
        paymentPlanList.addAll(items);
        // refreshing recycler view
        planAdapter.notifyDataSetChanged();
    }

}
