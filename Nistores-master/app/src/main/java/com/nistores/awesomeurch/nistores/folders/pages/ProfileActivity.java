package com.nistores.awesomeurch.nistores.folders.pages;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nistores.awesomeurch.nistores.folders.helpers.ApiUrls;
import com.nistores.awesomeurch.nistores.folders.helpers.VolleyRequest;
import com.nistores.awesomeurch.nistores.R;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ProfileActivity extends AppCompatActivity {
    Intent intent;
    ConstraintLayout loaderLayout,networkErrorLayout, infoLayout;
    ImageView profilePicture;
    TextView nameView, addressView, regDateView, viewsView, followersView, followingView,
            contactsView, topicsView, emailView,statusView;
    String URL, userId, profileId;
    ApiUrls apiUrls;
    AppCompatButton retryBtn, editBtn, addContactBtn, followBtn, messageBtn;
    String surname, firstname, address, email, status, picture, interests;
    boolean me = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        infoLayout = findViewById(R.id.info_layout);
        loaderLayout = findViewById(R.id.loader_layout);
        networkErrorLayout = findViewById(R.id.network_error_layout);
        profilePicture = findViewById(R.id.profile_picture);
        nameView = findViewById(R.id.full_name);
        addressView = findViewById(R.id.address);
        regDateView = findViewById(R.id.reg_date);
        viewsView = findViewById(R.id.views);
        followersView = findViewById(R.id.followers);
        followingView = findViewById(R.id.following);
        contactsView = findViewById(R.id.contacts);
        topicsView = findViewById(R.id.topics);
        emailView = findViewById(R.id.email);
        statusView = findViewById(R.id.status);

        apiUrls = new ApiUrls();
        URL = apiUrls.getApiUrl();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        userId = prefs.getString("user",null);

        editBtn = findViewById(R.id.edit_btn);
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editProfile();
            }
        });
        retryBtn = findViewById(R.id.btn_retry);
        retryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getInfo();
            }
        });
        addContactBtn = findViewById(R.id.add_contact_btn);
        followBtn = findViewById(R.id.follow_btn);
        messageBtn = findViewById(R.id.message_btn);


        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            profileId = bundle.getString("id");
            if(profileId != null){
                if(!profileId.equals(userId)){
                    userId = profileId;
                    me = false;
                }

            }
        }

        getInfo();
    }

    private void editProfile(){
        Bundle bundle = new Bundle();
        bundle.putString("firstname",firstname);
        bundle.putString("lastname",surname);
        bundle.putString("email",email);
        bundle.putString("picture",picture);
        bundle.putString("interests",interests);
        intent = new Intent(this,EditProfileActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void getInfo(){
        String pURL = URL + "request=info&id=" + userId;
        //Log.d("myURL",pURL);
        VolleyRequest volleyRequest = new VolleyRequest(getApplicationContext(), pURL) {
            @Override
            public void onProcess() {
                networkErrorLayout.setVisibility(View.GONE);
                loaderLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onSuccess(JSONObject response) {
                //Log.d("MPUTA",response.toString());
                loaderLayout.setVisibility(View.GONE);
                try {

                    Integer err = response.getInt("error");
                    if(err==0){
                        JSONObject data = response.getJSONObject("data");
                        fillInItems(data);

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
                loaderLayout.setVisibility(View.GONE);
                networkErrorLayout.setVisibility(View.VISIBLE);

            }
        };
        volleyRequest.setCache(false);
        volleyRequest.fetchResources();

    }

    public void fillInItems(JSONObject data){
        infoLayout.setVisibility(View.VISIBLE);

        if(me){
            addContactBtn.setVisibility(View.GONE);
            followBtn.setVisibility(View.GONE);
            messageBtn.setVisibility(View.GONE);
        }else{
            editBtn.setVisibility(View.GONE);
        }

        try {
            surname = data.getString("surname");
            firstname = data.getString("firstname");
            address = data.getString("location");
            email = data.getString("email");
            picture = data.getString("picture");
            interests = data.getString("interest");
            String regDate = data.getString("udate");
            String views = data.getString("views");
            status = data.getString("vstatus");
            String following = String.valueOf(data.getInt("following"));
            String followers = String.valueOf(data.getInt("followers"));

            String fullname = firstname + " " + surname;
            try {
                Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(regDate);
                String dateString = new SimpleDateFormat("EEE, MMM d, ''yy", Locale.ENGLISH).format(date);
                regDateView.setText(dateString);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            nameView.setText(fullname);
            addressView.setText(address);
            emailView.setText(email);
            viewsView.setText(views);
            statusView.setText(status);
            followingView.setText(following);
            followersView.setText(followers);

            final String STRING_BASE_URL = "https://www.nistores.com.ng/";
            Picasso.with(getApplicationContext()).load(STRING_BASE_URL + picture).placeholder(R.drawable.ic_person_default).into(profilePicture);


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }



}
