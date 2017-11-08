package com.example.linch.app;

import android.app.Application;
import android.nfc.Tag;
import android.os.Environment;
import android.renderscript.ScriptGroup;
import android.util.Log;

import com.example.linch.bean.City;
import com.example.linch.db.CityDB;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by linch on 2017/10/18.
 */

public class MyApplication extends Application {
    private  static  final String TAG = "MyAPP";
    private  static MyApplication myApplication;
    private CityDB mCityDB;

    private List<City> mCityList;
    private List<String> cityList;
    @Override
    public void onCreate(){
        super.onCreate();
        Log.d(TAG,"MyApplication->onCreate");
        myApplication = this;

        //CityDB  mCityDB
        mCityDB = openCityDB();
        initCityList();
    }

    private void initCityList(){
        //mCityList = new LinkedList<City>();
        cityList  = new LinkedList<String>();

        new Thread(new Runnable() {
            @Override
            public void run() {
                prepareCityList();
            }
        }).start();
    }
    private boolean prepareCityList(){
        mCityList = mCityDB.getAllCity();
        int i = 0;
        for(City city : mCityList){
            i++;
            //String cityCode = city.getNumber();
            cityList.add(city.getCity()+" "+city.getProvince());
           // Log.d(TAG,cityCode+":"+cityName);
        }
        //System.out.println("城市数量"+cityList.size());
        Log.d(TAG,"i="+i);
        return true;
    }
    public List<City> getmCityList(){
        return mCityList;
    }
    //返回所有城市列表
    public List<String> getCityList(){
        return cityList;
    }
    //对比城市名和省名返回cityCode
    public String getCityCode(String cityname, String province){
         for(City city: mCityList){
             if(city.getProvince().equals(province)&&city.getCity().equals(cityname)){
                 return city.getNumber();
             }
         }
         return "";
    }
    //搜索

    public List<String> getCityList(String preChar){
        //System.out.println(TAG+Thread.currentThread().getName());
        List<String> SearchResult =new LinkedList<String>();
        for(City city: mCityList){
             if(city.getCity().startsWith(preChar)){
                 SearchResult.add(city.getCity()+" "+city.getProvince());
                 continue;
             }
             else{
                 preChar = preChar.toUpperCase();
             }
             if(city.getAllfirstPY().startsWith(preChar)){
                 SearchResult.add(city.getCity()+" "+city.getProvince());
             }
             else if(city.getAllPY().startsWith(preChar)){
                 SearchResult.add(city.getCity()+" "+city.getProvince());
             }
             else if(city.getFirstPY().startsWith(preChar)) {
                 SearchResult.add(city.getCity()+" "+city.getProvince());
             }
        }
        return SearchResult;
    }
    //单例模式，返回MyApplication对象
    public static MyApplication getInstance(){
        return myApplication;
    }
    //判断路径是否存在，不存在则创建路径并保存复制数据库文件,返回CityDB对象
    private CityDB openCityDB(){
        String path = "/data"
                + Environment.getDataDirectory().getAbsolutePath()
                + File.separator + getPackageName()
                + File.separator + "database1"
                + File.separator
                + CityDB.CITY_DB_NAME;
        File db = new File(path);
        Log.d(TAG,path);
        if(!db.exists()){

            String pathfolder = "/data"
                    + Environment.getDataDirectory().getAbsolutePath()
                    + File.separator + getPackageName()
                    + File.separator + "database1"
                    + File.separator;
            File dirFirstFolder = new File(pathfolder);
            if(!dirFirstFolder.exists()){
                dirFirstFolder.mkdirs();   //创建目录
                Log.i("MyApp","mkdirs");
            }
            Log.i("MyApp","db is not exists");
            try{
                InputStream is = getAssets().open("city.db");
                FileOutputStream fos = new FileOutputStream(db);
                int len = -1;
                byte[] buffer = new byte[1024];
                while((len=is.read(buffer))!=-1){
                    fos.write(buffer, 0, len);
                    fos.flush();
                }
                fos.close();
                is.close();
            }
            catch (IOException e){
                e.printStackTrace();
                System.exit(0);
            }
        }
        return new CityDB(this, path);
    }

}
