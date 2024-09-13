package com.nistores.awesomeurch.nistores.folders.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nistores.awesomeurch.nistores.folders.helpers.Polls;
import com.nistores.awesomeurch.nistores.R;

import java.util.List;

public class PollAdapter extends RecyclerView.Adapter<PollAdapter.MyViewHolder> {
    private Context context;
    private List<Polls> polls;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView titleView, idView;

        public MyViewHolder(View view) {
            super(view);
            titleView = view.findViewById(R.id.title);
            idView = view.findViewById(R.id.id);
        }
    }

    public PollAdapter(Context context, List<Polls> polls) {
        this.context = context;
        this.polls = polls;
    }

    @Override
    public PollAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.polls_item_row, parent, false);

        return new PollAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PollAdapter.MyViewHolder holder, final int position) {
        final Polls poll = polls.get(position);
        holder.idView.setText(poll.getPoll_id());
        holder.titleView.setText(Html.fromHtml(poll.getPoll_title()));

    }

    @Override
    public int getItemCount() {
        return polls.size();
    }
}