package com.example.linch.miniweather;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by linch on 2017/9/20.
 */

public class MainActivity extends Activity implements View.OnClickListener{
    private ImageView mUpdateBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState ){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_info);

        mUpdateBtn = (ImageView)findViewById(R.id.title_update_btn); //连接title_update_btn按钮
        mUpdateBtn.setOnClickListener(this);
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
    @Override
    public void onClick(View view){
        if(view.getId() == R.id.title_update_btn){
            //通过SharedPreferences读取城市id，如果没有定义则缺省为101010100（北京城市）
            SharedPreferences sharedPreferences = getSharedPreferences("config",MODE_PRIVATE);
            String cityCode = sharedPreferences.getString("main_city_code","101010100");
            Log.d("myWeather",cityCode);

            if(NetUtil.getNetworkState(this)!=NetUtil.NETWORN_NONE){  //确定网络可访问
                Log.d("myWeather","网络OK");
                queryWeatherCode(cityCode);
            }
            else{
                Log.d("myWeather","网络炸了");
                Toast.makeText(MainActivity.this,"网络炸了！",Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * 获取cityCode所指的城市的天气信息
     * @param cityCode   城市id
     */
    private void queryWeatherCode(String cityCode){
        final String address = "http://wthrcdn.etouch.cn/WeatherApi?citykey=" + cityCode;
        Log.d("myWeather",address);
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection con = null;
                try{
                    URL url = new URL(address);
                    con = (HttpURLConnection)url.openConnection();
                    con.setRequestMethod("GET");
                    con.setConnectTimeout(8000);
                    con.setReadTimeout(8000);
                    InputStream in = con.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String str;
                    while((str=reader.readLine())!=null){
                        response.append(str);
                        Log.d("myWeather",str);
                    }
                    String responseStr = response.toString();
                    Log.d("myWeather",responseStr);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
                finally {
                    if(con != null){
                        con.disconnect();
                    }
                }
            }
        }).start();
    }

    private void parseXML(String xmldata){
        try{
            XmlPullParserFactory fac = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = fac.newPullParser();
            xmlPullParser.setInput(new StringReader(xmldata));
            int eventType = xmlPullParser.getEventType();
            Log.d("myWeather","paserXML");
            while(eventType != XmlPullParser.END_DOCUMENT){
                switch (eventType){
                    //判断是否为文档开始
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    //判断是否为标签元素开始
                    case XmlPullParser.START_TAG:
                        if(xmlPullParser.getName().equals("city")){
                            
                        }
                }


            }


        }
        catch (XmlPullParserException e){
            e.printStackTrace();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }


}
