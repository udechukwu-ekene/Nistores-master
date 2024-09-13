package com.nistores.awesomeurch.nistores.folders.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nistores.awesomeurch.nistores.folders.helpers.StoreReview;
import com.nistores.awesomeurch.nistores.R;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class StoreReviewAdapter extends RecyclerView.Adapter<StoreReviewAdapter.MyViewHolder> {
    private Context context;
    private List<StoreReview> reviewList;
    SharedPreferences preferences;
    String user;

    public StoreReviewAdapter(Context context, List<StoreReview> reviewList){
        this.context = context;
        this.reviewList = reviewList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView merchantIdView, nameView, messageView, dateView;
        ImageView merchantLogoView;
        LinearLayout rateContainer;

        public MyViewHolder(View view) {
            super(view);
            merchantIdView = view.findViewById(R.id.merchant_id);
            nameView = view.findViewById(R.id.name);
            messageView = view.findViewById(R.id.message);
            dateView = view.findViewById(R.id.date);
            merchantLogoView = view.findViewById(R.id.merchantLogo);
            rateContainer = view.findViewById(R.id.rate_container);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.review_item_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        user = preferences.getString("user", null);
        final StoreReview review = reviewList.get(position);
        holder.merchantIdView.setText(review.getMerchant_id());
        String fullName = review.getSurname() + " " + review.getFirstname();
        holder.nameView.setText(fullName);

        if(user.equals(review.getMerchant_id())){
            holder.nameView.setText(context.getResources().getString(R.string.you));
        }

        holder.messageView.setText(review.getReview());

        final String STRING_BASE_URL = "https://www.nistores.com.ng/";
        String pic = review.getPicture();

        Picasso.with(context).load(STRING_BASE_URL + pic).placeholder(R.drawable.ic_person_default).into(holder.merchantLogoView);

        String nDate = review.getRdate();

        DateFormat inputFormatter1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.UK);
        Date date1 = null;
        try {
            date1 = inputFormatter1.parse(nDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        DateFormat outputFormatter1 = new SimpleDateFormat("EEE, MMM d, ''yy", Locale.UK);
        String output1 = outputFormatter1.format(date1);
        holder.dateView.setText(output1);

        ViewGroup viewGroup = holder.rateContainer;
        int rating = Integer.parseInt(review.getStars());
        if(rating < 6){
            for(int i = 0; i < rating; i++){
                View child = viewGroup.getChildAt(i);
                if(child instanceof ImageView){
                    ((ImageView) child).setImageResource(R.drawable.ic_rating_good);
                }
            }
        }


    }

    @Override
    public int getItemCount() {
        return reviewList.size();
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