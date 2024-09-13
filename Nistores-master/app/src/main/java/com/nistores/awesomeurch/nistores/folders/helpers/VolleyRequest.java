package com.nistores.awesomeurch.nistores.folders.helpers;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

public abstract class VolleyRequest {
    public Context context;
    public String URL;
    private boolean cache;
    private String format;

    public VolleyRequest(Context context, String URL){
        this.context = context;
        this.URL = URL;
        this.cache = true;
        this.format = "array";
    }

    private boolean isCache() {
        return cache;
    }

    public void setCache(boolean cache) {
        this.cache = cache;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public void fetchResources(){
        onProcess();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //onResponse
                        //Log.d("RTN",response.toString());
                        onSuccess(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                onNetworkError();

                //Log.d("V_ERROR",error.toString());

            }
        });
        if(!isCache()){
            jsonObjectRequest.setShouldCache(false);
        }
        InitiateVolley.getInstance().addToRequestQueue(jsonObjectRequest);
    }

    public abstract void onProcess();
    public abstract void onSuccess(JSONObject data);

    public abstract void onNetworkError();

}
