package com.nistores.awesomeurch.nistores.folders.pages;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spannable;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import com.nistores.awesomeurch.nistores.folders.adapters.NegotiateChatAdapter;
import com.nistores.awesomeurch.nistores.folders.helpers.ApiUrls;
import com.nistores.awesomeurch.nistores.folders.helpers.Chat;
import com.nistores.awesomeurch.nistores.folders.helpers.InitiateVolley;
import com.nistores.awesomeurch.nistores.folders.helpers.PicassoImageGetter;
import com.nistores.awesomeurch.nistores.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class NegotiateActivity extends AppCompatActivity {
    String name,pic,likes,views,type,price,id,sid,URL,user;
    Intent intent;
    TextView pname, plikes, pviews, pdesc, ownerName, ownerAddress, ownerID;
    EditText messageArea;
    ImageView ppic, ownerPic;
    RecyclerView chatsRecyclerView;
    AppCompatButton btn_price, negotiateButton, viewProfileBtn, viewStoreBtn;
    ApiUrls apiUrls;
    ProgressBar progressBar;
    SharedPreferences preferences;
    NegotiateChatAdapter chatAdapter;
    List<Chat> chats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_negotiate);

        pname = findViewById(R.id.name);
        pdesc = findViewById(R.id.desc);
        plikes = findViewById(R.id.likes);
        pviews = findViewById(R.id.views);
        ppic = findViewById(R.id.pic);
        btn_price = findViewById(R.id.btn_cost);
        progressBar = findViewById(R.id.progress);
        negotiateButton = findViewById(R.id.btn_negotiate);
        negotiateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });
        messageArea = findViewById(R.id.msg_area);

        ownerID = findViewById(R.id.ownerID);
        ownerName = findViewById(R.id.ownerName);
        ownerAddress = findViewById(R.id.ownerAddress);
        ownerPic = findViewById(R.id.owner_picture);
        viewProfileBtn = findViewById(R.id.btn_view_profile);
        viewStoreBtn = findViewById(R.id.btn_store);

        View.OnClickListener viewProfile = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewProfile();
            }
        };
        viewProfileBtn.setOnClickListener(viewProfile);

        View.OnClickListener viewStore = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewStore();
            }
        };
        viewStoreBtn.setOnClickListener(viewStore);

        apiUrls = new ApiUrls();
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        user = preferences.getString("user", null);

        chats = new ArrayList<>();
        chatAdapter = new NegotiateChatAdapter(this,chats);

        chatsRecyclerView = findViewById(R.id.recycler_negotiate);

        RecyclerView.LayoutManager memberLayoutManager = new LinearLayoutManager(this);
        chatsRecyclerView.setLayoutManager(memberLayoutManager);
        chatsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        chatsRecyclerView.setAdapter(chatAdapter);

        intent = getIntent();

        Bundle bundle = intent.getExtras();
        if(bundle!=null){
            id = intent.getStringExtra("id");
            sid = intent.getStringExtra("sid");
            name = intent.getStringExtra("name");
            pic = intent.getStringExtra("pic");
            likes = intent.getStringExtra("likes");
            views = intent.getStringExtra("views");
            type = intent.getStringExtra("from");
            price = intent.getStringExtra("price");

            pname.setText(name);
            plikes.setText(likes);
            pviews.setText(views);
            btn_price.setText(price);

            Picasso.with(getApplicationContext()).load(pic).placeholder(R.drawable.ic_crop_image).into(ppic);

            fetchData();

        }
    }



    public void fetchData(){
        //?request=view_product&pid=633&uid=172&sid=10
        String URL = apiUrls.getApiUrl();
        String originURL = URL+"request=view_product&pid="+id+"&uid="+user+"&sid="+sid;
        //Log.d("RTN",originURL);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, originURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        //Log.d("RTN",response.toString());
                        progressBar.setVisibility(View.GONE);
                        try {

                            Integer err = response.getInt("error");
                            JSONObject data = response.getJSONObject("data");
                            if(err==0){
                                fillInItems(data);

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
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(),"Sorry an error occurred. Try again",Toast.LENGTH_SHORT).show();
                //Log.d("VOLLEY",error.toString());

            }
        });

        jsonObjectRequest.setShouldCache(false);
        InitiateVolley.getInstance().addToRequestQueue(jsonObjectRequest);
    }

    public void fillInItems(JSONObject data){
        try{
            Spannable html;

            String desc = data.getString("pdesc");

            Spanned cdesc = Html.fromHtml(desc);

            PicassoImageGetter imageGetter = new PicassoImageGetter(pdesc, this);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                html = (Spannable) Html.fromHtml(String.valueOf(cdesc), Html.FROM_HTML_MODE_LEGACY, imageGetter, null);
            } else {
                html = (Spannable) Html.fromHtml(String.valueOf(cdesc), imageGetter, null);
            }
            //Spanned ccdesc = Html.fromHtml(String.valueOf(cdesc));

            pdesc.setText(html);

            JSONArray commentsArray = data.getJSONArray("comments");
            List<Chat> comments = new Gson().fromJson(commentsArray.toString(), new TypeToken<List<Chat>>() {
            }.getType());

            chats.clear();
            chats.addAll(comments);
            chatAdapter.notifyDataSetChanged();

            JSONObject ownerDetails = data.getJSONObject("owner");
            String surname = ownerDetails.getString("surname");
            String firstname = ownerDetails.getString("firstname");
            String fullname = surname+" "+firstname;
            String id = ownerDetails.getString("merchant_id");
            String pic = apiUrls.getBaseURL() + ownerDetails.getString("picture");
            String loc = ownerDetails.getString("location");

            ownerName.setText(fullname);
            ownerAddress.setText(loc);
            ownerID.setText(id);
            Picasso.with(getApplicationContext()).load(pic).placeholder(R.drawable.ic_person_black_24dp).into(ownerPic);

        }catch(Exception e){
            Toast.makeText(getApplicationContext(),""+e,Toast.LENGTH_SHORT).show();
        }
    }

    public void sendMessage(){
        //Log.d("INFO","clicked!");
        String chat = messageArea.getText().toString();
        if(chat.isEmpty()){
            messageArea.setError("Type your message first");
        }else{

            pushMessage(chat);
            /*JSONArray commentsArray = new JSONArray();
            JSONObject messageObj = new JSONObject();

            String formattedDate = "2018-07-13 16:34:45";
            try{
                messageObj.put("comment_id","20");
                messageObj.put("comment",chat);
                messageObj.put("comment_user",user);
                messageObj.put("comment_date",formattedDate);

                commentsArray.put(messageObj);
                List<Chat> comments = new Gson().fromJson(commentsArray.toString(), new TypeToken<List<Chat>>() {
                }.getType());

                //final int currSize = chatAdapter.getItemCount();

                chats.addAll(comments);
                //Log.d("INFO",""+commentsArray);
                //chatAdapter.notifyItemRangeChanged(currSize,chats.size() - 1);
                chatsRecyclerView.post(new Runnable() {
                    @Override
                    public void run() {
                        //chatAdapter.notifyItemRangeChanged(currSize,chats.size() - 1);
                        chatAdapter.notifyItemInserted(chats.size() - 1);
                    }
                });

            }catch(Exception e){
                //Log.d("ERR",""+e);
            }*/

        }

    }

    public void pushMessage(final String typed){
        //disable button
        negotiateButton.setEnabled(false);
        negotiateButton.setText(R.string.sending);
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(NegotiateActivity.this);
        //this is the url where you want to send the request
        String URL = apiUrls.getApiUrl();
        String encodedMsg = "";
        try {
            encodedMsg = URLEncoder.encode(typed,"utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String ownerId = ownerID.getText().toString();
        String url = URL+"request=add_comment&comment_user="+user+"&comment="+encodedMsg+"&target_id="+id+"&owner_id="+ownerId;
        //Log.d("RTN",url);
        // Request a string response from the provided URL.
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Display the response string.
                        negotiateButton.setText(R.string.send);
                        negotiateButton.setEnabled(true);
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
                negotiateButton.setText(R.string.send);
                negotiateButton.setEnabled(true);

            }
        });
        // Add the request to the RequestQueue.
        jsonObjectRequest.setShouldCache(false);
        queue.add(jsonObjectRequest);
    }

    public void appendComment(JSONArray commentsArray){
        List<Chat> comments = new Gson().fromJson(commentsArray.toString(), new TypeToken<List<Chat>>() {
        }.getType());

        messageArea.setText("");

        //final int currSize = chatAdapter.getItemCount();
        chats.addAll(comments);
        chatsRecyclerView.post(new Runnable() {
            @Override
            public void run() {
                chatAdapter.notifyItemInserted(chats.size() - 1);
                //chatAdapter.notifyItemRangeChanged(currSize,chats.size() - 1);
            }
        });
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();

    }

    private void viewProfile(){
        String ownerId = ownerID.getText().toString();
        Bundle bundle = new Bundle();
        bundle.putString("id",ownerId);
        intent = new Intent(this,ProfileActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void viewStore(){
        Bundle bundle = new Bundle();
        bundle.putString("sName","Store Details");
        bundle.putString("id",sid);
        Intent intent = new Intent(this, StoreActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

}
