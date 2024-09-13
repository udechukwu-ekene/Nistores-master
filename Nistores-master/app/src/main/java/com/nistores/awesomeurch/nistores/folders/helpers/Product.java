package com.nistores.awesomeurch.nistores.folders.helpers;

/**
 * Created by Awesome Urch on 29/07/2018.
 * POJO class for passing my product JSON
 */

public class Product {
    private String pname;
    private String pphoto;
    private String pprice;
    private String store_uid;
    private String views;
    private String featured;
    private String likes;
    private String store_id;
    private String product_id;

    public String getTitle() {
        return pname;
    }

    public void setTitle(String title) {
        this.pname = title;
    }

    public String getImage() {
        return pphoto;
    }

    public void setImage(String image) {
        this.pphoto = image;
    }

    public String getPrice() {
        return pprice;
    }

    public void setPrice(String price) {
        this.pprice = price;
    }

    public String getStore_uid() {
        return store_uid;
    }

    public void setStore_uid(String price) {
        this.store_uid = price;
    }

    public String getViews(){ return views; }

    public void setViews(String views){
        this.views = views;
    }

    public String getLikes(){ return likes; }

    public void setLikes(String likes){
        this.likes = likes;
    }

    public String getStore_id(){ return store_id; }

    public void setStore_id(String store_id){
        this.store_id = store_id;
    }

    public String getFeatured(){ return featured; }

    public void setFeatured(String featured){
        this.featured = featured;
    }

    public String getProduct_id(){ return product_id; }

    public void setProduct_id(String product_id){
        this.product_id = product_id;
    }
}
