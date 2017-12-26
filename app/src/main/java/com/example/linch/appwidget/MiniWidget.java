package com.example.linch.appwidget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.RemoteViews;

import com.example.linch.app.MyApplication;
import com.example.linch.bean.TodayWeather;
import com.example.linch.controller.ThreadPoolController;
import com.example.linch.miniweather.R;
import com.example.linch.service.FetchTodayWeatherService;
import com.example.linch.util.NetUtil;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Implementation of App Widget functionality.
 */
public class MiniWidget extends AppWidgetProvider {
    private static final String TAG = "MiniWidget";
    public static final String ACTION_UPDATE_WEATHER = "ACTION_UPDATE_WEATHER";
    private static int interval = 5 * 1000;
    private Timer timer;
    private void updateWidget(Context context, TodayWeather todayWeather){
        if(todayWeather == null)
            return;
        String date = todayWeather.getDate();
        String temp = todayWeather.getWendu();
        String high = todayWeather.getHigh();
        String low = todayWeather.getLow();
        String type = todayWeather.getType();

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.mini_widget);
        ComponentName thisWidget = new ComponentName(context, MiniWidget.class);
        views.setTextViewText(R.id.widget_temp, temp+"°");
        views.setTextViewText(R.id.widget_range, high+"/"+low);
        views.setTextViewText(R.id.widget_time, date);
        views.setImageViewResource(R.id.widget_weather_img, MyApplication.getInstance().getImgHash().get(type));

        appWidgetManager.updateAppWidget(thisWidget,views);

    }


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
//        for (int appWidgetId : appWidgetIds) {
//            updateAppWidget(context, appWidgetManager, appWidgetId);
//        }
        super.onUpdate(context,appWidgetManager,appWidgetIds);
    }

    @Override
    public void onEnabled(Context context) {
        Log.d(TAG, "创建窗口部件");
        if(timer == null){
            Log.d(TAG, "创建线程");
            timer = new Timer();
        }
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(NetUtil.isConnectNet(MyApplication.getInstance())){
                    String citycode = MyApplication.getInstance().getSharedPreferences("config", Context.MODE_PRIVATE).getString("main_city_code_current", "101010100");
                    queryWeatherCode(citycode); //查询天气
                }
            }
        }, 1, interval); //固定时间更新
        // Enter relevant functionality for when the first widget is created
    }
    private  void queryWeatherCode(String cityCode){
        String address = String.format("%s%s","http://wthrcdn.etouch.cn/WeatherApi?citykey=",cityCode);
        ThreadPoolExecutor executor = ThreadPoolController.getInstance().getExecutor();
        executor.submit(new FetchTodayWeatherService(address, null, FetchTodayWeatherService.TO_MINI_WIDGET));
    }
    @Override
    public void onDisabled(Context context) {
        if(timer != null){
            timer.cancel();
            timer = null;
        }
        // Enter relevant functionality for when the last widget is disabled
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.d(TAG, action);
        if(ACTION_UPDATE_WEATHER.equals(action)){
            Log.d(TAG, "接收广播");
            TodayWeather todayWeather = (TodayWeather) intent.getSerializableExtra("todayweather");
            updateWidget(context, todayWeather);
        }
        super.onReceive(context, intent);
    }

}

