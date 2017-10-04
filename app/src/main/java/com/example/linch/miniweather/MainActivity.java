package com.example.linch.miniweather;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by linch on 2017/9/20.
 */

public class MainActivity extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState ){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_info);

        //网咯测试
        if(NetUtil.getNetworkState(this) != NetUtil.NETWORN_NONE){
            Log.d("myWeather","网络OK");

            Toast.makeText(MainActivity.this,"网络OK！",Toast.LENGTH_LONG).show();
        }
        else{
            Log.d("myWeather","网络炸了");

            Toast.makeText(MainActivity.this,"网络炸了！",Toast.LENGTH_LONG).show();
        }
    }
}
