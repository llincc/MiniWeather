package com.example.linch.app;

import android.app.Application;
import android.os.Environment;
import android.util.Log;

import com.example.linch.bean.City;
import com.example.linch.controller.ThreadPoolController;
import com.example.linch.db.CityDB;
import com.example.linch.miniweather.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by linch on 2017/10/18.
 */

public class MyApplication extends Application {
    private  static  final String TAG = "MyAPP";
    private  static MyApplication myApplication;
    private CityDB mCityDB;

    private List<City> mCityList;
    private List<String> cityList;

    private HashMap<String ,Integer> ImgHash;
    @Override
    public void onCreate(){
        super.onCreate();
        Log.d(TAG,"MyApplication->onCreate");
        //创建Application实例对象
        myApplication = this;
        initCityList();
        initImage();
    }
    private void initCityList(){
        cityList  = new LinkedList<String>();
        //CityDB  mCityDB
        //初始化数据库操作任务
        CityDB mCityDB = openCityDB();
        FutureTask<List<City>> fetchTask = new FutureTask<List<City>>(mCityDB);
        //将任务提交到线程池
        ThreadPoolExecutor executor = ThreadPoolController.getInstance().getExecutor();
        executor.submit(fetchTask);

        try{
            mCityList = fetchTask.get();
            prepareCityList();
        }
        catch (InterruptedException e){
            e.printStackTrace();
        }
        catch (ExecutionException e){
            e.printStackTrace();
        }
    }
    private boolean prepareCityList(){
        int i = 0;
        //从mCityList复制城市名和省份到cityList
        for(City city : mCityList){
            i++;
            cityList.add(city.getCity()+" "+city.getProvince());
        }
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

    /**
     * 初始化图片索引
     */
    private void initImage(){
        ImgHash = new HashMap<String ,Integer>();
        ImgHash.put("晴", R.drawable.biz_plugin_weather_qing);
        ImgHash.put("阴",R.drawable.biz_plugin_weather_yin);
        ImgHash.put("多云",R.drawable.biz_plugin_weather_duoyun);
        ImgHash.put("沙尘暴",R.drawable.biz_plugin_weather_shachenbao);
        ImgHash.put("雾",R.drawable.biz_plugin_weather_wu);
        ImgHash.put("小雨",R.drawable.biz_plugin_weather_xiaoyu);
        ImgHash.put("中雨",R.drawable.biz_plugin_weather_zhongyu);
        ImgHash.put("大雨",R.drawable.biz_plugin_weather_dayu);
        ImgHash.put("暴雨",R.drawable.biz_plugin_weather_baoyu);
        ImgHash.put("大暴雨",R.drawable.biz_plugin_weather_tedabaoyu);
        ImgHash.put("特大暴雨",R.drawable.biz_plugin_weather_tedabaoyu);
        ImgHash.put("阵雨",R.drawable.biz_plugin_weather_zhenyu);
        ImgHash.put("雷阵雨",R.drawable.biz_plugin_weather_leizhenyu);
        ImgHash.put("雷阵雨冰雹",R.drawable.biz_plugin_weather_leizhenyubingbao);
        ImgHash.put("雨夹雪",R.drawable.biz_plugin_weather_yujiaxue);
        ImgHash.put("小雪",R.drawable.biz_plugin_weather_xiaoxue);
        ImgHash.put("中雪",R.drawable.biz_plugin_weather_zhongxue);
        ImgHash.put("阵雪",R.drawable.biz_plugin_weather_zhenxue);
        ImgHash.put("大雪",R.drawable.biz_plugin_weather_daxue);
        ImgHash.put("暴雪",R.drawable.biz_plugin_weather_baoxue);
    }

    //单例模式，返回MyApplication对象
    public static MyApplication getInstance(){
        return myApplication;
    }

    public HashMap<String, Integer> getImgHash() {
        return ImgHash;
    }
}
