package com.nistores.awesomeurch.nistores.folders.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nistores.awesomeurch.nistores.folders.helpers.PaymentPlan;
import com.nistores.awesomeurch.nistores.folders.pages.PayActivity;
import com.nistores.awesomeurch.nistores.R;

import java.util.List;

public class PaymentPlanAdapter extends RecyclerView.Adapter<PaymentPlanAdapter.MyViewHolder> {
    private Context context;
    private List<PaymentPlan> paymentPlans;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView priceView, idView, durationView, timeView, discountView;

        public MyViewHolder(View view) {
            super(view);
            priceView = view.findViewById(R.id.price);
            idView = view.findViewById(R.id.id);
            durationView = view.findViewById(R.id.duration);
            timeView = view.findViewById(R.id.time);
            discountView = view.findViewById(R.id.discount);
            View.OnClickListener pay = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    toPayment(view);
                }
            };

            priceView.setOnClickListener(pay);
            durationView.setOnClickListener(pay);

        }

        private void toPayment(View view){
            Context mcontext = view.getContext();
            String sAmount = priceView.getText().toString().trim();
            String days = timeView.getText().toString();
            int amount = 0;
            try {
                amount = Integer.parseInt(sAmount);
            } catch (Exception ignored) {
            }
            String id = idView.getText().toString();
            Bundle bundle = new Bundle();
            bundle.putString("id",id);
            bundle.putInt("amount",amount);
            bundle.putString("days",days);

            Intent intent = new Intent(mcontext,PayActivity.class);
            intent.putExtras(bundle);
            mcontext.startActivity(intent);
        }


    }

    public PaymentPlanAdapter(Context context, List<PaymentPlan> paymentPlans) {
        this.context = context;
        this.paymentPlans = paymentPlans;
    }

    @Override
    public PaymentPlanAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.payplan_item_row, parent, false);

        return new PaymentPlanAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PaymentPlanAdapter.MyViewHolder holder, final int position) {
        final PaymentPlan plan = paymentPlans.get(position);
        holder.idView.setText(plan.getPlan_id());
        holder.priceView.setText(plan.getPlan_amount());
        holder.durationView.setText(plan.getPlan_name());
        holder.timeView.setText(plan.getPlan_time());
        holder.discountView.setText(plan.getPlan_discount());

    }

    @Override
    public int getItemCount() {
        return paymentPlans.size();
    }
}