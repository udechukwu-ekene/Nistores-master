package com.nistores.awesomeurch.nistores.folders.pages;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.View;

import com.nistores.awesomeurch.nistores.R;

public class OwnStoreActivity extends AppCompatActivity {
    Intent intent;
    AppCompatButton proceedBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_own_store);

        proceedBtn = findViewById(R.id.btn_proceed);
        proceedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(getApplicationContext(),CreateStoreActivity.class);
                startActivity(intent);
            }
        });
    }
}
