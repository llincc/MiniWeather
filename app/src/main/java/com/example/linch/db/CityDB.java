package com.example.linch.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.linch.bean.City;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by linch on 2017/10/18.
 */

public class CityDB {
    private  static  final  String CITY_DB_NAME = "city.db";
    private  static final  String CITY_TABLE_NAME = "city";
    private SQLiteDatabase db;

    public CityDB(Context context, String path){
        db = context.openOrCreateDatabase(path,Context.MODE_PRIVATE,null);
    }

    public List<City> getAllCity() {
        List<City> list = new ArrayList<City>();
        //Cursor c = db.query("SELECT * FROM " + CITY_TABLE_NAME );
    }
}
