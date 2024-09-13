package com.nistores.awesomeurch.nistores.folders.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nistores.awesomeurch.nistores.folders.helpers.ApiUrls;
import com.nistores.awesomeurch.nistores.folders.helpers.Product;
import com.nistores.awesomeurch.nistores.folders.pages.NegotiateActivity;
import com.nistores.awesomeurch.nistores.R;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Awesome Urch on 29/07/2018.
 * My Adapter for All Products (Home) Fragment
 */

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.MyViewHolder> {
    private Context context;
    private List<Product> productList;
    private int design = 2;

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView name, price, store, views, likesView, storeIdView, productIdView, featuredView, negotiate, url_view;
        public ImageView thumbnail;

        public MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.title);
            price = view.findViewById(R.id.price);
            store = view.findViewById(R.id.store);
            views = view.findViewById(R.id.views);
            likesView = view.findViewById(R.id.likes);
            storeIdView = view.findViewById(R.id.store_id);
            productIdView = view.findViewById(R.id.product_id);
            featuredView = view.findViewById(R.id.featured);
            thumbnail = view.findViewById(R.id.thumbnail);
            negotiate = view.findViewById(R.id.negotiate);
            url_view = view.findViewById(R.id.image_url);

            negotiate.setOnClickListener(this);

        }

        @Override
        public void onClick(View view){
            Bundle bundle;
            String id = productIdView.getText().toString();
            String pname = name.getText().toString();
            String pviews = views.getText().toString();
            String plikes = likesView.getText().toString();
            String pic = url_view.getText().toString();
            String sid = storeIdView.getText().toString();
            String pprice = price.getText().toString();

            String type = (design == 2)?"products":"favourites";
            Context mcontext = view.getContext();

            switch (view.getId()){
                case R.id.negotiate:
                    bundle = new Bundle();
                    bundle.putString("id",id);
                    bundle.putString("name",pname);
                    bundle.putString("views",pviews);
                    bundle.putString("likes",plikes);
                    bundle.putString("from",type);
                    bundle.putString("pic",pic);
                    bundle.putString("sid",sid);
                    bundle.putString("price",pprice);
                    Intent intent = new Intent(mcontext, NegotiateActivity.class);
                    intent.putExtras(bundle);
                    mcontext.startActivity(intent);

                    break;
            }
        }

    }

    public ProductAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;

    }

    @Override
    public ProductAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.product_item_row, parent, false);
        if(design == 1){
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.favourite_item_row, parent, false);
        }


        return new ProductAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ProductAdapter.MyViewHolder holder, final int position) {
        final Product product = productList.get(position);
        holder.name.setText(Html.fromHtml(product.getTitle()));
        holder.price.setText(product.getPrice());
        holder.store.setText(product.getStore_uid());
        holder.views.setText(product.getViews());
        holder.likesView.setText(product.getLikes());
        holder.storeIdView.setText(product.getStore_id());
        holder.productIdView.setText(product.getProduct_id());

        //Log.d("Feat",product.getFeatured());
        holder.featuredView.setVisibility(View.GONE);
        if(product.getFeatured().equals("1")){
            holder.featuredView.setVisibility(View.VISIBLE);
        }

        final String STRING_BASE_URL = new ApiUrls().getBaseURL();
        String pic = product.getImage();

        String full_pic = STRING_BASE_URL + pic;
        //String img = "https://www.nistores.com.ng/"+product.getImage();
        holder.url_view.setText(full_pic);

        Picasso.with(context).load(full_pic).placeholder(R.mipmap.ic_launcher).into(holder.thumbnail);

            /*Glide.with(context)
                    .load(str.toString())
                    .into(holder.thumbnail);*/
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public void setDesign(int design) {
        this.design = design;
    }
}
