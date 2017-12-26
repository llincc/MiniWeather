package com.example.linch.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;

import com.example.linch.app.MyApplication;
import com.example.linch.controller.ThreadPoolController;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by linch on 2017/12/20.
 */

public class WidgetUpateService extends Service {

    private static final long interval = 5000; //更新间隔

    private Timer timer;

    private Handler handler =  new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    @Override
    public void onCreate() {
        timer = new Timer();
        timer.schedule(new TimerTask(){
            @Override
            public void run() {
                String citycode = getSharedPreferences("config", Context.MODE_PRIVATE).getString("main_city_code_current", "101010100");
                queryWeatherCode(citycode);
            }
        },1, interval);
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        if(timer != null){
            timer.cancel();
            timer = null;
        }
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public int onStartCommand(Intent intent,int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 发送天气查询请求
     * @param cityCode
     */
    private  void queryWeatherCode(String cityCode){
        String address = String.format("%s%s","http://wthrcdn.etouch.cn/WeatherApi?citykey=",cityCode);
        ThreadPoolExecutor executor = ThreadPoolController.getInstance().getExecutor();
        executor.submit(new FetchTodayWeatherService(address, this, FetchTodayWeatherService.TO_MINI_WIDGET));
    }
    public Handler getmHandler(){
        return handler;
    }
}
