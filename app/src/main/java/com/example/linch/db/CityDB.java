package com.example.linch.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.linch.bean.City;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by linch on 2017/10/18.
 */

public class CityDB implements Callable<List<City>>{
    public   static final  String CITY_DB_NAME = "city.db";
    private  static final  String CITY_TABLE_NAME = "city";
    private SQLiteDatabase db;

    public CityDB(Context context, String path){
        db = context.openOrCreateDatabase(path,Context.MODE_PRIVATE,null);
    }
    //城市搜索

    public List<City> getAllCity() {
        List<City> list = new ArrayList<City>();
        Cursor c = db.rawQuery("SELECT * FROM " + CITY_TABLE_NAME,null);
        while(c.moveToNext()){
            String province = c.getString(c.getColumnIndex("province"));
            String city = c.getString(c.getColumnIndex("city"));
            String number = c.getString(c.getColumnIndex("number"));
            String allPY = c.getString(c.getColumnIndex("allpy"));
            String allFirstPY = c.getString(c.getColumnIndex("allfirstpy"));
            String firstPY = c.getString(c.getColumnIndex("firstpy"));
            City item = new City(province,city,number,allPY,allFirstPY,firstPY);
            list.add(item);
        }
        c.close();
        db.close();
        return list;
    }

    @Override
    public List<City> call() throws Exception {
        return getAllCity();
    }
}
