package com.example.linch.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.linch.activity.MainActivity;
import com.example.linch.controller.ThreadPoolController;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by linch on 2017/12/6.
 */

public class AutoUpdateService extends Service{
    private static final  String  TAG = "AutoUpdate";
    static final int UPDATE_INTERVAL = 60 * 60 * 1000;//更新时间间隔 30分钟自动更新一次
    private Timer timer = new Timer();//计时器
    private LocalBinder binder = new LocalBinder();
    private Context context;

    /**
     * Binder类
     */
    public class LocalBinder extends Binder {
        //声明一个方法，提供给客户端使用
        public AutoUpdateService getSerVice(){
             return AutoUpdateService.this;
        }
    }

    /**
     * Binder类返回给客户端
     * @param intent
     * @return binder
     */
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "自动更新服务开始");
        fixedTimeUpdate();
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "自动更新服务停止");
        super.onDestroy();
    }

    public void setContext(Context context) {
        this.context = context;
    }
    private void fixedTimeUpdate(){
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                queryWeatherCode();
            }
        },UPDATE_INTERVAL, UPDATE_INTERVAL);
    }
    /**
     * 获取cityCode所指的城市的天气信息
     */
   private  void queryWeatherCode(){
       Log.d(TAG, "自动发送天气更新请求");

       String citycode;
       if(!(citycode =getSharedPreferences("config",MODE_PRIVATE).getString("main_city_code","")).equals("")){
           String address = String.format("%s%s","http://wthrcdn.etouch.cn/WeatherApi?citykey=",citycode);
           ThreadPoolExecutor executor = ThreadPoolController.getInstance().getExecutor();
           executor.submit(new FetchTodayWeatherService(address, (MainActivity) context));
       }
    }
}
