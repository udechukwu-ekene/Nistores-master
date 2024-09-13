package com.nistores.awesomeurch.nistores.folders.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nistores.awesomeurch.nistores.folders.helpers.Notification;
import com.nistores.awesomeurch.nistores.R;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.MyViewHolder> {
    private Context context;
    private List<Notification> notifications;
    private String openActivity = "";

    public NotificationAdapter(Context context, List<Notification> notifications) {
        this.context = context;
        this.notifications = notifications;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView linkView, idView, iconView, dateView, messageView, eventView;
        ImageView merchantImage, iconImage;

        public Intent intent;


        public MyViewHolder(View view) {
            super(view);
            linkView = view.findViewById(R.id.nlink);
            idView = view.findViewById(R.id.notif_id);
            iconView = view.findViewById(R.id.nicon);
            eventView = view.findViewById(R.id.nevent);
            dateView = view.findViewById(R.id.date);
            messageView = view.findViewById(R.id.message);
            merchantImage = view.findViewById(R.id.merchant);
            iconImage = view.findViewById(R.id.action_icon);
        }

        @Override
        public void onClick(View view) {

        }
    }

    @Override
    public NotificationAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notification_item_row, parent, false);

        return new NotificationAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(NotificationAdapter.MyViewHolder holder, final int position) {
        final Notification notification = notifications.get(position);
        holder.linkView.setText(notification.getNlink());
        holder.idView.setText(notification.getNotify_id());
        holder.iconView.setText(notification.getNicon());
        holder.dateView.setText(notification.getNdate());
        holder.messageView.setText(notification.getNcontent());

        final String STRING_BASE_URL = "https://www.nistores.com.ng/";
        String pic = notification.getPicture();
        //String img = "https://www.nistores.com.ng/"+product.getImage();

        Picasso.with(context).load(STRING_BASE_URL + pic).placeholder(R.drawable.ic_person_default).into(holder.merchantImage);

        String orderDate = notification.getNdate();
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(orderDate);
            String dateString = new SimpleDateFormat("EEE, MMM d, ''yy", Locale.ENGLISH).format(date);
            holder.dateView.setText(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        switch (notification.getNicon()){
            case "icon-confirm-order":
                holder.iconImage.setImageResource(R.drawable.ic_thumb_up_color);
                break;
            case "icon-wallet":
                holder.iconImage.setImageResource(R.drawable.ic_wallet);
                break;
            case "icon-bubble":
                holder.iconImage.setImageResource(R.drawable.ic_bubble_color);
                break;
            case "icon-like":
                holder.iconImage.setImageResource(R.drawable.ic_thumb_up_color);
                break;
            case "icon-credit-card":
                holder.iconImage.setImageResource(R.drawable.ic_credit_card_color);
                break;
            case "icon-heart":
                holder.iconImage.setImageResource(R.drawable.ic_favorite_border_color);
                break;
            case "icon-star":
                holder.iconImage.setImageResource(R.drawable.ic_star_color);
                break;
            case "icon-envelope":
                holder.iconImage.setImageResource(R.drawable.ic_email_color);
                break;
            case "icon-action-undo":
                holder.iconImage.setImageResource(R.drawable.ic_notification_color);
                break;
            case "icon-present":
                holder.iconImage.setImageResource(R.drawable.ic_lens_colored);
                break;
            default:
        }

    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public void setOpenActivity(String openActivity) {
        this.openActivity = openActivity;
    }
}