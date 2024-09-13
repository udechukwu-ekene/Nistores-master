package com.nistores.awesomeurch.nistores.folders.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nistores.awesomeurch.nistores.folders.helpers.ApiUrls;
import com.nistores.awesomeurch.nistores.folders.helpers.MyStore;
import com.nistores.awesomeurch.nistores.folders.pages.SelectPaymentActivity;
import com.nistores.awesomeurch.nistores.folders.pages.StoreActivity;
import com.nistores.awesomeurch.nistores.R;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MyStoreAdapter extends RecyclerView.Adapter<MyStoreAdapter.MyViewHolder> {
    private Context context;
    private List<MyStore> myStores;

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView idView, uidView, nameView, addressView, expiryView, renewView;
        ImageView thumbNail;
        AppCompatButton deleteBtn, editBtn;

        public MyViewHolder(View view) {
            super(view);
            nameView = view.findViewById(R.id.name);
            idView = view.findViewById(R.id.store_id);
            uidView = view.findViewById(R.id.store_uid);
            addressView = view.findViewById(R.id.address);
            expiryView = view.findViewById(R.id.expiry);
            renewView = view.findViewById(R.id.renew);
            thumbNail = view.findViewById(R.id.thumbnail);
            deleteBtn = view.findViewById(R.id.btn_delete);
            editBtn = view.findViewById(R.id.btn_edit);

            nameView.setOnClickListener(this);
            thumbNail.setOnClickListener(this);
            renewView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view){
            Bundle bundle = new Bundle();
            String storeName = nameView.getText().toString();
            String id = idView.getText().toString();
            String uid = uidView.getText().toString();
            Context mcontext = view.getContext();
            Intent intent;

            switch (view.getId()){
                case R.id.thumbnail:
                case R.id.name:
                    bundle.putString("sName",storeName);
                    bundle.putString("id",id);
                    intent = new Intent(mcontext, StoreActivity.class);
                    intent.putExtras(bundle);
                    mcontext.startActivity(intent);
                    break;
                case R.id.renew:
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mcontext);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("current_store_id",id).apply();
                    editor.putString("current_store_uid",uid).apply();
                    bundle.putString("uid",uid);
                    intent = new Intent(mcontext, SelectPaymentActivity.class);
                    intent.putExtras(bundle);
                    mcontext.startActivity(intent);
                    break;
            }
        }
    }

    public MyStoreAdapter(Context context, List<MyStore> myStores) {
        this.context = context;
        this.myStores = myStores;
    }

    @NonNull
    @Override
    public MyStoreAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_store_item_row, parent, false);

        return new MyStoreAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyStoreAdapter.MyViewHolder holder, final int position) {
        final MyStore myStore = myStores.get(position);
        holder.idView.setText(myStore.getStore_id());
        holder.uidView.setText(myStore.getStore_uid());
        holder.nameView.setText(myStore.getSname());
        holder.addressView.setText(myStore.getSaddress());

        String orderDate = myStore.getExpires();
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(orderDate);
            String dateString = new SimpleDateFormat("EEE, MMM d, ''yy", Locale.ENGLISH).format(date);

            Date currDate = Calendar.getInstance().getTime();
            String exp = "Expires";
            if(currDate.getTime() > date.getTime()){
                holder.expiryView.setTextColor(Color.RED);
                holder.renewView.setTextColor(Color.RED);
                exp = "Expired";
            }

            holder.expiryView.setText(String.format("%s on %s",exp,dateString));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        final String STRING_BASE_URL = new ApiUrls().getBaseURL();
        String pic = myStore.getSlogo();

        Picasso.with(context).load(STRING_BASE_URL + pic).placeholder(R.drawable.ic_store_grey).into(holder.thumbNail);

    }

    @Override
    public int getItemCount() {
        return myStores.size();
    }
}