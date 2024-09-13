package com.nistores.awesomeurch.nistores.folders.helpers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.nistores.awesomeurch.nistores.R;

import java.sql.SQLException;

/**
 * Created by Awesome Urch on 31/07/2018.
 * Ormlite database helper
 */

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    private static final String DATABASE_NAME = "laundry.db";
    private static final int DATABASE_VERSION = 3;

    private Dao<AllProductsTable, Integer> AllProductsDao;
    private Dao<UserTable, Integer> UserDao;

    public DatabaseHelper(Context context){
        super(context, DATABASE_NAME,null,DATABASE_VERSION, R.raw.ormlite_config);
    }

    public DatabaseHelper getInstance(Context context){return  new DatabaseHelper(context);}

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource) {
        try{
            TableUtils.createTableIfNotExists(connectionSource, AllProductsTable.class);
            TableUtils.createTableIfNotExists(connectionSource, UserTable.class);
        }catch(Exception e)
        {
            //Log.e("ERR","Could not create DB",e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            TableUtils.dropTable(connectionSource, AllProductsTable.class,true);
            TableUtils.dropTable(connectionSource, UserTable.class,true);
            onCreate(sqLiteDatabase);
        }catch (Exception e){
            e.printStackTrace();
            //Log.e("ERR","Could not upgrade DB",e);
        }
    }

    public Dao<AllProductsTable, Integer> getAllProductsDao() throws SQLException {
        if (AllProductsDao == null) {
            AllProductsDao = getDao(AllProductsTable.class);
        }
        return AllProductsDao;
    }

    public Dao<UserTable, Integer> getUserDao() throws SQLException {
        if (UserDao == null) {
            UserDao = getDao(UserTable.class);
        }
        return UserDao;
    }

}
