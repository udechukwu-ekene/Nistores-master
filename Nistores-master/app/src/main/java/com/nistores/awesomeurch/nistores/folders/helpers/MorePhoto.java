package com.nistores.awesomeurch.nistores.folders.helpers;

import android.graphics.Bitmap;

public class MorePhoto {
    private String name;
    private String image;
    private Bitmap imageBitmap;

    public String getImage() {
        return image;
    }

    public String getName() {
        return name;
    }

    public Bitmap getImageBitmap() {
        return imageBitmap;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImageBitmap(Bitmap imageBitmap) {
        this.imageBitmap = imageBitmap;
    }
}
