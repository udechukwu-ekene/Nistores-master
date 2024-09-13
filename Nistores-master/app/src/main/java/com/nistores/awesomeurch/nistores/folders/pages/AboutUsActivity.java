package com.nistores.awesomeurch.nistores.folders.pages;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.nistores.awesomeurch.nistores.folders.helpers.ApiUrls;
import com.nistores.awesomeurch.nistores.folders.helpers.VolleyRequest;
import com.nistores.awesomeurch.nistores.R;

import org.json.JSONException;
import org.json.JSONObject;

public class AboutUsActivity extends AppCompatActivity {
    TextView contentView;
    ProgressBar progressBar;
    LinearLayout networkErrorLayout;
    AppCompatButton retryBtn;
    String URL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        contentView = findViewById(R.id.content);
        progressBar = findViewById(R.id.loader);
        networkErrorLayout = findViewById(R.id.network_error);
        retryBtn = findViewById(R.id.btn_retry);

        retryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getInfo();
            }
        });

        URL = new ApiUrls().getApiUrl();
        getInfo();
    }

    private void getInfo(){

        String pURL = URL + "request=page&id=5";
        //Log.d("myURL",pURL);
        VolleyRequest volleyRequest = new VolleyRequest(getApplicationContext(), pURL) {
            @Override
            public void onProcess() {
                networkErrorLayout.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onSuccess(JSONObject response) {
                //Log.d("MPUTA",response.toString());
                progressBar.setVisibility(View.GONE);
                try {

                    Integer err = response.getInt("error");
                    if(err==0){
                        JSONObject data = response.getJSONObject("data");
                        String content = data.getString("page_content");
                        contentView.setVisibility(View.VISIBLE);
                        contentView.setText(Html.fromHtml(content));

                    }else{
                        Toast.makeText(getApplicationContext(),"Network Error Occurred",Toast.LENGTH_SHORT).show();
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
        volleyRequest.setCache(false);
        volleyRequest.fetchResources();

    }

}
