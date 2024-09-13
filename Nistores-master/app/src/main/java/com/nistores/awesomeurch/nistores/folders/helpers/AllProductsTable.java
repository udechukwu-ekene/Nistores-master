package com.nistores.awesomeurch.nistores.folders.helpers;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;


/**
 * Created by Awesome Urch on 31/07/2018.
 * All Products table (home)
 */

@DatabaseTable
public class AllProductsTable {
    @DatabaseField(generatedId = true)
    private Integer id;

    @DatabaseField
    private String productId;

    @DatabaseField
    private String name;

    @DatabaseField
    private String photo;

    @DatabaseField
    private String price;

    @DatabaseField
    private String views;

    @DatabaseField
    private String store_uid;

    @DatabaseField
    private String likes;

    @DatabaseField
    private String store_id;

    @DatabaseField
    private String featured;


    public Integer getId() {
        return id;
    }

    public void setId(Integer orderId) {
        this.id = orderId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getViews() {
        return views;
    }

    public void setViews(String views) {
        this.views = views;
    }

    public String getStore_uid() {
        return store_uid;
    }

    public void setStore_uid(String store_uid) {
        this.store_uid = store_uid;
    }

    public String getStore_id() {
        return store_id;
    }

    public void setStore_id(String store_id) {
        this.store_id = store_id;
    }

    public String getLikes() {
        return likes;
    }

    public void setLikes(String likes) {
        this.likes = likes;
    }

    public String getFeatured() {
        return featured;
    }

    public void setFeatured(String featured) {
        this.featured = featured;
    }

}
