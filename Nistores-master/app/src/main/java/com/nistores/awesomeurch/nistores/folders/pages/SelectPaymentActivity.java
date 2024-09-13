package com.nistores.awesomeurch.nistores.folders.pages;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.nistores.awesomeurch.nistores.R;

public class SelectPaymentActivity extends AppCompatActivity {
    TextView uidView, amountView;
    String uid, selectedMethod, amount, payment_for;
    RadioGroup paymentGroup;
    AppCompatButton proceedBtn;
    TextView firstPhraseView;
    static String ATM = "atm";
    static String IN_BANK = "in_bank";
    static String WITH_BANK = "with_bank";
    static String MOBILE = "mobile";
    static String AGENT = "agent";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_payment);

        View.OnClickListener onRadioButtonClicked = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSelectPayMethod(view);
            }
        };

        View.OnClickListener onProceedButtonClicked = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onProceed();
            }
        };

        payment_for = "store_renew";

        proceedBtn = findViewById(R.id.btn_proceed);
        proceedBtn.setOnClickListener(onProceedButtonClicked);

        paymentGroup = findViewById(R.id.paymentGroup);
        for(int x = 0; x < paymentGroup.getChildCount(); x++){
            View child = paymentGroup.getChildAt(x);
            child.setOnClickListener(onRadioButtonClicked);
        }

        firstPhraseView = findViewById(R.id.first_phrase);
        uidView = findViewById(R.id.store_number);
        amountView = findViewById(R.id.amount);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            uid = bundle.getString("uid");
            uidView.setText(uid);
            payment_for = bundle.getString("pay_for");
            amount = bundle.getString("amount");
            if(payment_for != null){
                personalizeUI();
            }
            if(amount != null){
                amountView.setText(amount);
            }
        }

    }

    private void onSelectPayMethod(View view){
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.atm:
                if (checked)
                    selectedMethod = ATM;
                    break;
            case R.id.with_bank:
                if (checked)
                    selectedMethod = WITH_BANK;
                    break;
            case R.id.in_bank:
                if (checked)
                    selectedMethod = IN_BANK;
                    break;
            case R.id.mobile:
                if (checked)
                    selectedMethod = MOBILE;
                    break;
            case R.id.agent:
                if (checked)
                    selectedMethod = AGENT;
                    break;
        }
        //Toast.makeText(this,selectedMethod,Toast.LENGTH_SHORT).show();
    }

    private void onProceed(){
        if(selectedMethod != null){
            Bundle bundle = new Bundle();
            bundle.putString("method",selectedMethod);
            bundle.putString("pay_for",payment_for);
            bundle.putString("amount",amountView.getText().toString());
            Intent intent = new Intent(this,PaymentInstructionAActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }

    private void personalizeUI(){
        if(payment_for.equals("delivery_order")){
            firstPhraseView.setText(getResources().getString(R.string.your_delivery_order_number));
        }
    }

}
