package com.nistores.awesomeurch.nistores.folders.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nistores.awesomeurch.nistores.folders.helpers.InitiatedOrder;
import com.nistores.awesomeurch.nistores.folders.pages.deliveredOrderActivity;
import com.nistores.awesomeurch.nistores.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class InitiatedOrderAdapter extends RecyclerView.Adapter<InitiatedOrderAdapter.MyViewHolder> {
    private Context context;
    private List<InitiatedOrder> orders;

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView numberView, dateView, idView;
        Intent intent;

        public MyViewHolder(View view) {
            super(view);
            numberView = view.findViewById(R.id.order_number);
            dateView = view.findViewById(R.id.date);
            idView = view.findViewById(R.id.order_id);
            numberView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            String orderNo = numberView.getText().toString();
            String id = idView.getText().toString();
            Context viewContext = view.getContext();
            switch (view.getId()){
                case R.id.order_number:
                        Bundle bundle = new Bundle();
                        bundle.putString("number",orderNo);
                        bundle.putString("id",id);
                        intent = new Intent(viewContext,deliveredOrderActivity.class);
                        intent.putExtras(bundle);
                        viewContext.startActivity(intent);
                    break;
            }
        }

    }

    public InitiatedOrderAdapter(Context context, List<InitiatedOrder> orders) {
        this.context = context;
        this.orders = orders;
    }

    @Override
    public InitiatedOrderAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.initiated_order_item, parent, false);

        return new InitiatedOrderAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(InitiatedOrderAdapter.MyViewHolder holder, final int position) {
        final InitiatedOrder myOrder = orders.get(position);
        holder.idView.setText(myOrder.getId());
        holder.numberView.setText(myOrder.getOrder_id());
        String orderDate = myOrder.getInitiated_date();
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(orderDate);
            String dateString = new SimpleDateFormat("EEE, MMM d, ''yy", Locale.ENGLISH).format(date);
            holder.dateView.setText(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return orders.size();
    }
}