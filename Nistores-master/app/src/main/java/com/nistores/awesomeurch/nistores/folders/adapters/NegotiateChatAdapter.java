package com.nistores.awesomeurch.nistores.folders.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nistores.awesomeurch.nistores.folders.helpers.Chat;
import com.nistores.awesomeurch.nistores.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Awesome Urch on 30/08/2018.
 * Adapter Class for
 */

public class NegotiateChatAdapter extends RecyclerView.Adapter<NegotiateChatAdapter.MyViewHolder> {
    private Context context;
    private List<Chat> chatList;
    SharedPreferences preferences;
    String user;

    public NegotiateChatAdapter(Context context, List<Chat> chatList){
        this.context = context;
        this.chatList = chatList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView id, message, uid, date;
        LinearLayout container;

        public MyViewHolder(View view) {
            super(view);
            id = view.findViewById(R.id.id);
            message = view.findViewById(R.id.message);
            uid = view.findViewById(R.id.user);
            date = view.findViewById(R.id.date);
            container = view.findViewById(R.id.container);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.negotiate_item_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final Chat chat = chatList.get(position);
        holder.id.setText(chat.getComment_id());
        holder.message.setText(chat.getComment());
        holder.uid.setText(chat.getComment_user());
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        user = preferences.getString("user", null);
        if(!user.equals(chat.getComment_user())){
            holder.container.setBackgroundResource(R.drawable.receive_chat_style);
            holder.container.setGravity(Gravity.START);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
            params.weight = 1.0f;
            params.gravity = Gravity.START;

            holder.container.setLayoutParams(params);

            holder.message.setTextColor(Color.BLACK);
        }

        String nDate = chat.getComment_date();

        DateFormat inputFormatter1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.UK);
        Date date1 = null;
        try {
            date1 = inputFormatter1.parse(nDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        DateFormat outputFormatter1 = new SimpleDateFormat("EEE, MMM d, ''yy", Locale.UK);
        String output1 = outputFormatter1.format(date1);
        holder.date.setText(output1);

        //holder.date.setText(chat.getComment_date());


    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
