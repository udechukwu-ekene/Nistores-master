package com.nistores.awesomeurch.nistores.folders.pages;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.TextView;

import com.nistores.awesomeurch.nistores.R;

public class SuccessPaymentActivity extends AppCompatActivity {
    AppCompatButton backBtn;
    TextView messageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success_payment);

        backBtn = findViewById(R.id.btn_back);
        messageView = findViewById(R.id.message);

        final View.OnClickListener toHome = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toHome();
            }
        };

        backBtn.setOnClickListener(toHome);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            String message = bundle.getString("message");
            messageView.setText(message);
        }

    }

    public void toHome(){
        Intent intent = new Intent(this,HomeActivity.class);
        startActivity(intent);
    }
}
