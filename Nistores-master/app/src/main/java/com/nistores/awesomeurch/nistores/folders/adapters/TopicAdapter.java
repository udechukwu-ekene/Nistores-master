package com.nistores.awesomeurch.nistores.folders.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nistores.awesomeurch.nistores.folders.helpers.ApiUrls;
import com.nistores.awesomeurch.nistores.folders.helpers.Topic;
import com.nistores.awesomeurch.nistores.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class TopicAdapter extends RecyclerView.Adapter<TopicAdapter.MyViewHolder> {
    private Context context;
    private List<Topic> topics;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView thumbnail;
        public TextView titleView, idView;

        public MyViewHolder(View view) {
            super(view);
            titleView = view.findViewById(R.id.title);
            idView = view.findViewById(R.id.topic_id);
            thumbnail = view.findViewById(R.id.authorPic);
        }
    }

    public TopicAdapter(Context context, List<Topic> topics) {
        this.context = context;
        this.topics = topics;
    }

    @Override
    public TopicAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.topic_item_row, parent, false);

        return new TopicAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(TopicAdapter.MyViewHolder holder, final int position) {
        final Topic topic = topics.get(position);
        holder.idView.setText(topic.getMt_id());
        holder.titleView.setText(Html.fromHtml(topic.getMt_title()));

        String pic = new ApiUrls().getBaseURL() + topic.getPicture();

        Picasso.with(context).load(pic).placeholder(R.drawable.ic_crop_image).into(holder.thumbnail);

    }

    @Override
    public int getItemCount() {
        return topics.size();
    }
}