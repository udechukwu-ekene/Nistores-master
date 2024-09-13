package com.nistores.awesomeurch.nistores.folders.pages;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.Toast;

import com.nistores.awesomeurch.nistores.R;

public class SuccessActivity extends AppCompatActivity {
    Intent intent;
    AppCompatButton homeButton, displayPortBtn;
    public static int PORT = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success);
        homeButton = findViewById(R.id.btn_back);
        displayPortBtn = findViewById(R.id.btn_port);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toHome();
            }
        });
        displayPortBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toDisplayPort();
            }
        });

    }

    @Override
    public void onBackPressed(){
        Toast.makeText(getApplication(),"back_pressed",Toast.LENGTH_SHORT).show();
        intent = new Intent(getApplicationContext(),HomeActivity.class);
        startActivity(intent);
        super.onBackPressed();
    }

    public void toHome(){
        intent = new Intent(this,HomeActivity.class);
        startActivity(intent);
    }

    public void toDisplayPort(){
        Bundle bundle = new Bundle();
        bundle.putInt ("fragment", PORT);
        intent = new Intent(this,HomeActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

}
