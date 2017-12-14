package com.example.linch.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.example.linch.activity.MainActivity;
import com.example.linch.app.MyApplication;

/**
 * Created by linch on 2017/12/12.
 */

public class BaiDuLocationService extends BDAbstractLocationListener {
    private static final String TAG = "BaiDuLocation";
    private MainActivity context;
    public BaiDuLocationService(MainActivity context){
        this.context = context;
    }

    @Override
    public void onReceiveLocation(BDLocation bdLocation) {
        if(bdLocation == null){
            sendMassage("");
            return;
        }
        Log.d(TAG, String.valueOf(bdLocation.getLongitude()));
        Log.d(TAG, String.valueOf(bdLocation.getLatitude()));
        Log.d(TAG,String.valueOf(bdLocation.getLocType()));
        if(bdLocation.getAddrStr() == null){
            Log.d(TAG,"获取失败");
            sendMassage("");
            return;
        }
       // Log.d(TAG,bdLocation.getAddrStr());
        String province = postfixFilter(bdLocation.getProvince().trim());  //去除省，市，县，区后缀
        String city = postfixFilter(bdLocation.getCity().trim());
        String district = postfixFilter(bdLocation.getDistrict().trim());
        String cityname = "";
        Log.d(TAG, province);
        Log.d(TAG, city);
        Log.d(TAG, district);
        if(cityexist(district+" "+province)){
            cityname = district;
        }
        else if(cityexist(city+ " "+ province)){

            cityname = city;
        }
        else{
            sendMassage(""); //数据库中找不到合适的城市，定位失败，发送空字符串
            return;
        }

        Toast.makeText(context,"当前定位: "+cityname, Toast.LENGTH_SHORT).show();
        String citycode = MyApplication.getInstance().getCityCode(cityname, province);
        sendMassage(citycode); //发送消息给Activity
    }
    private boolean cityexist(String cityname){
         Log.d(TAG,cityname);
         return MyApplication.getInstance().getCityList().contains(cityname);
    }
    private void sendMassage(String citycode){
        Message message = new Message();
        message.what = MainActivity.LOCATION_SERVICE;
        message.obj = citycode;
        context.getmHandler().sendMessage(message);
    }

    private String postfixFilter(String location){  //市、区、县 后缀过滤
        return location.replaceAll("(省|市|区|县)$","");
    }

}
