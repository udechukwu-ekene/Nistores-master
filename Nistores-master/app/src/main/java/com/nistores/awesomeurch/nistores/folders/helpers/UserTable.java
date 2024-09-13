package com.nistores.awesomeurch.nistores.folders.helpers;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Awesome Urch on 31/07/2018.
 * User Table
 */
@DatabaseTable
public class UserTable {

    @DatabaseField(generatedId = true)
    private Integer id;

    @DatabaseField
    private String all;

    @DatabaseField
    private String merchant_id;


    public Integer getId() {
        return id;
    }

    public void setId(Integer orderId) {
        this.id = orderId;
    }

    public String getAll() {
        return all;
    }

    public void setAll(String all) {
        this.all = all;
    }

    public String getMerchant_id(){return merchant_id; }

    public void setMerchant_id(String mid){ this.merchant_id = mid; }

}
