package com.example.linch.app;

import android.app.Application;
import android.os.Environment;
import android.util.Log;

import com.example.linch.bean.City;
import com.example.linch.controller.ThreadPoolController;
import com.example.linch.db.CityDB;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
    @Override
    public void onCreate(){
        super.onCreate();
        Log.d(TAG,"MyApplication->onCreate");
        //创建Application实例对象
        myApplication = this;
        initCityList();
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
