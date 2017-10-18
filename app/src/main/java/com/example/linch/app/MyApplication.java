package com.example.linch.app;

import android.app.Application;
import android.util.Log;

/**
 * Created by linch on 2017/10/18.
 */

public class MyApplication extends Application {
    private  static  final String TAG = "MyAPP";
    private  static MyApplication myApplication;

    @Override
    public void onCreate(){
        super.onCreate();
        Log.d(TAG,"MyApplication->onCreate");
        myApplication = this;
    }
    public static MyApplication getInstance(){
        return myApplication;
    }
}
