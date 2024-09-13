package com.nistores.awesomeurch.nistores.folders.pages;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nistores.awesomeurch.nistores.folders.adapters.ChatContactsAdapter;
import com.nistores.awesomeurch.nistores.folders.helpers.ApiUrls;
import com.nistores.awesomeurch.nistores.folders.helpers.ChatContacts;
import com.nistores.awesomeurch.nistores.folders.helpers.VolleyRequest;
import com.nistores.awesomeurch.nistores.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class ChatContactActivity extends AppCompatActivity {
    Toolbar toolbar;
    ConstraintLayout networkErrorLayout, loaderLayout, messageLayout;
    ImageView backBtn, userImage;
    TextView userName;
    RecyclerView recyclerView;
    EditText messageArea;
    AppCompatButton sendMessageBtn, retryBtn;
    List<ChatContacts> chats;
    ChatContactsAdapter mAdapter;
    String URL, memberId, userId, chatString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_contact);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        View.OnClickListener sendMsg = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        };

        View.OnClickListener sendBack = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendBack();
            }
        };

        View.OnClickListener retry = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fetchItems();
            }
        };

        networkErrorLayout = findViewById(R.id.network_error_layout);
        loaderLayout = findViewById(R.id.loader_layout);
        messageLayout = findViewById(R.id.message_layout);
        backBtn = findViewById(R.id.btn_back);
        retryBtn = findViewById(R.id.btn_retry);
        userImage = findViewById(R.id.image);
        userName = findViewById(R.id.username);
        messageArea = findViewById(R.id.msg_area);
        sendMessageBtn = findViewById(R.id.btn_send);

        backBtn.setOnClickListener(sendBack);
        sendMessageBtn.setOnClickListener(sendMsg);
        retryBtn.setOnClickListener(retry);

        recyclerView = findViewById(R.id.recycler_view);
        chats = new ArrayList<>();
        mAdapter = new ChatContactsAdapter(getApplicationContext(), chats);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,1);
        gridLayoutManager.setOrientation(LinearLayout.VERTICAL);
        recyclerView.setLayoutManager(gridLayoutManager);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        userId = prefs.getString("user",null);
        URL = new ApiUrls().getApiUrl();

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            memberId = bundle.getString("member_id");
            String memberName = bundle.getString("member_name");
            String memberPic = bundle.getString("member_pic");
            userName.setText(memberName);
            Picasso.with(getApplicationContext()).load(memberPic).placeholder(R.drawable.ic_person_white).into(userImage);

        }

        if(savedInstanceState != null){
             chatString = savedInstanceState.getString("all_chats");
            if(chatString != null){
                try {
                    JSONArray allChatsArray = new JSONArray(chatString);
                    fillInItems(allChatsArray);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else{
                fetchItems();
            }
        }else{
            fetchItems();
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("all_chats",chatString);
    }

    private void fetchItems(){

        String newURL = URL + "request=all_chats&sender=" + memberId + "&receiver=" + userId;
        VolleyRequest volleyRequest = new VolleyRequest(getApplicationContext(), newURL) {
            @Override
            public void onProcess() {
                //do nothing while processing
                networkErrorLayout.setVisibility(View.GONE);
                loaderLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onSuccess(JSONObject response) {
                loaderLayout.setVisibility(View.GONE);
                networkErrorLayout.setVisibility(View.GONE);
                try {

                    Integer err = response.getInt("error");
                    if(err==0){

                        JSONArray data = response.getJSONArray("data");
                        //Log.d("MPUTA",data.toString());
                        chatString = data.toString();
                        fillInItems(data);

                    }else{
                        if(err!=1){
                            networkErrorLayout.setVisibility(View.VISIBLE);
                        }

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

    private void fillInItems(JSONArray data){
        messageLayout.setVisibility(View.VISIBLE);
        List<ChatContacts> items = new Gson().fromJson(data.toString(), new TypeToken<List<ChatContacts>>() {
        }.getType());
        chats.clear();
        chats.addAll(items);
        // refreshing recycler view
        mAdapter.notifyDataSetChanged();
    }

    public void sendMessage(){
        String chat = messageArea.getText().toString();
        if(!chat.isEmpty()){
            pushMessage(chat);
        }

    }

    public void pushMessage(final String typed){
        //disable button
        sendMessageBtn.setEnabled(false);
        sendMessageBtn.setText(getResources().getString(R.string.sending));
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(ChatContactActivity.this);
        //this is the url where you want to send the request

        String encodedMsg = "";
        try {
            encodedMsg = URLEncoder.encode(typed,"utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String url = URL+"request=send_chat&message="+encodedMsg+"&mfrom="+userId+"&mto="+memberId;
        //Log.d("RTN",url);
        // Request a string response from the provided URL.
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Display the response string.
                        sendMessageBtn.setText(getResources().getString(R.string.send));
                        sendMessageBtn.setEnabled(true);
                        //Log.d("RTN",""+response);
                        try {

                            Integer err = response.getInt("error");
                            JSONArray data = response.getJSONArray("data");
                            if(err==0){
                                appendComment(data);

                            }else{
                                Toast.makeText(getApplicationContext(),"Sorry an error occurred",Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            //Log.e("ERR",e.toString());
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //_response.setText("That didn't work!");
                Toast.makeText(getApplicationContext(),"Sorry an error occurred. Try again",Toast.LENGTH_SHORT).show();
                //Log.d("VOLLEY",error.toString());
                sendMessageBtn.setText(getResources().getString(R.string.send));
                sendMessageBtn.setEnabled(true);

            }
        });
        // Add the request to the RequestQueue.
        jsonObjectRequest.setShouldCache(false);
        queue.add(jsonObjectRequest);
    }

    public void appendComment(JSONArray commentsArray){
        List<ChatContacts> comments = new Gson().fromJson(commentsArray.toString(), new TypeToken<List<ChatContacts>>() {
        }.getType());

        messageArea.setText("");

        //final int currSize = chatAdapter.getItemCount();
        chats.addAll(comments);
        recyclerView.post(new Runnable() {
            @Override
            public void run() {
                mAdapter.notifyItemInserted(chats.size() - 1);
                //chatAdapter.notifyItemRangeChanged(currSize,chats.size() - 1);
            }
        });
    }

    private void sendBack(){
        super.onBackPressed();
    }

}
