package com.nistores.awesomeurch.nistores.folders.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nistores.awesomeurch.nistores.folders.helpers.ApiUrls;
import com.nistores.awesomeurch.nistores.folders.helpers.MorePhoto;
import com.nistores.awesomeurch.nistores.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MorePhotoAdapter extends RecyclerView.Adapter<MorePhotoAdapter.MyViewHolder> {
    private Context context;
    private List<MorePhoto> morePhotos;
    private Boolean server = true;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView thumbnail;

        public MyViewHolder(View view) {
            super(view);
            thumbnail = view.findViewById(R.id.thumbnail);
        }
    }

    public MorePhotoAdapter(Context context, List<MorePhoto> morePhotos) {
        this.context = context;
        this.morePhotos = morePhotos;
    }

    @Override
    public MorePhotoAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.more_photo_item, parent, false);

        return new MorePhotoAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MorePhotoAdapter.MyViewHolder holder, final int position) {
        final MorePhoto photo = morePhotos.get(position);
        //holder.thumbnail.setImageBitmap(photo.getImageBitmap());

        if(!server){
            //For testing purpose. Not yet sending image to server
            byte[] decodedString = Base64.decode(photo.getImage(), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            holder.thumbnail.setImageBitmap(decodedByte);
        }else{
            //The real implementation, sending to server and getting the image path from API
            String base = new ApiUrls().getBaseURL();
            String pic = base + photo.getImage();
            Picasso.with(context).load(pic).placeholder(R.drawable.ic_crop_image).into(holder.thumbnail);
        }


    }

    @Override
    public int getItemCount() {
        return morePhotos.size();
    }

    public void setServer(Boolean server) {
        this.server = server;
    }
}