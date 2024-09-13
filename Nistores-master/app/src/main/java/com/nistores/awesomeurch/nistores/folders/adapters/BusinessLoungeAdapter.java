package com.nistores.awesomeurch.nistores.folders.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nistores.awesomeurch.nistores.folders.helpers.BusinessLounge;
import com.nistores.awesomeurch.nistores.folders.pages.StateOrdersActivity;
import com.nistores.awesomeurch.nistores.folders.pages.StateStoresActivity;
import com.nistores.awesomeurch.nistores.folders.pages.StateTopicsActivity;
import com.nistores.awesomeurch.nistores.R;

import java.util.List;

public class BusinessLoungeAdapter extends RecyclerView.Adapter<BusinessLoungeAdapter.MyViewHolder> {
    private Context context;
    private List<BusinessLounge> bizList;
    private String openActivity = "";

    public BusinessLoungeAdapter(Context context, List<BusinessLounge> bizList) {
        this.context = context;
        this.bizList = bizList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView name, id, link ;
        public Intent intent;


        public MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.name);
            id = view.findViewById(R.id.id);
            link = view.findViewById(R.id.link);
            name.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            String state = link.getText().toString();
            String state_id = id.getText().toString();
            String stateName = name.getText().toString();
            Context viewContext = view.getContext();
            switch (view.getId()){
                case R.id.name:
                        //Toast.makeText(viewContext,state,Toast.LENGTH_SHORT).show();
                        Bundle bundle = new Bundle();
                        bundle.putString("state",state);
                        bundle.putString("stateName",stateName);
                    switch (openActivity) {
                        case "state_orders":
                            intent = new Intent(viewContext, StateOrdersActivity.class);
                            intent.putExtras(bundle);
                            viewContext.startActivity(intent);
                            break;
                        case "state_stores":
                            intent = new Intent(viewContext, StateStoresActivity.class);
                            intent.putExtras(bundle);
                            viewContext.startActivity(intent);
                            break;
                        case "state_topics":
                            bundle.putString("id", state_id);
                            intent = new Intent(viewContext, StateTopicsActivity.class);
                            intent.putExtras(bundle);
                            viewContext.startActivity(intent);
                            break;
                    }
                    break;
            }
        }
    }

    @Override
    public BusinessLoungeAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.businesslounge_item_row, parent, false);

        return new BusinessLoungeAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(BusinessLoungeAdapter.MyViewHolder holder, final int position) {
        final BusinessLounge business = bizList.get(position);
        holder.name.setText(business.getMcat_title());
        holder.id.setText(business.getMcat_id());
        holder.link.setText(business.getMcat_link());


    }

    @Override
    public int getItemCount() {
        return bizList.size();
    }

    public void setOpenActivity(String openActivity) {
        this.openActivity = openActivity;
    }
}