package com.example.linch.miniweather;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.linch.bean.TodayWeather;
import com.example.linch.util.NetUtil;

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
import java.util.HashMap;

/**
 * Created by linch on 2017/9/20.
 */

public class MainActivity extends Activity implements View.OnClickListener{
    private static final  int UPDATE_TODAY_WEATHER = 1;

    private ImageView mUpdateBtn;
    private ImageView mCityBtn;

    private TextView  cityTv, timeTv, humidityTv, weekTv, pmDataTv, pmQualityTv, temperatureTv,
                       climateTv, windTv, city_name_Tv, currenttemp;
    private ImageView weatherImg, pmImg;

    private HashMap<String ,Integer> ImgHash;
    private Handler mHandler = new Handler(){
        public void handleMessage(android.os.Message msg){
            switch (msg.what){
                case UPDATE_TODAY_WEATHER:
                    updateTodayWeather((TodayWeather)msg.obj);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState ){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_info);

        System.out.println(Thread.currentThread().getName());
        mUpdateBtn = (ImageView)findViewById(R.id.title_update_btn); //连接title_update_btn按钮
        mCityBtn = (ImageView)findViewById(R.id.title_city_manager);
        mUpdateBtn.setOnClickListener(this);
        mCityBtn.setOnClickListener(this);
        //网咯测试
        if(NetUtil.getNetworkState(this) != NetUtil.NETWORN_NONE){
            Log.d("myWeather","网络OK");

            Toast.makeText(MainActivity.this,"网络OK！",Toast.LENGTH_LONG).show();
        }
        else{
            Log.d("myWeather","网络炸了");

            Toast.makeText(MainActivity.this,"网络炸了！",Toast.LENGTH_LONG).show();
        }

        initView();
        initImage();
    }
    @Override
    public void onClick(View view){
        if(view.getId() == R.id.title_city_manager){
            Intent i = new Intent(this,SelectCity.class);
           // startActivity(i);
            startActivityForResult(i,1);
        }
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

    protected  void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == 1 && resultCode == RESULT_OK){
            String newCityCode = data.getStringExtra("cityCode");
            Log.d("myWeather","选择城市代码为"+newCityCode);

            if(NetUtil.getNetworkState(this) != NetUtil.NETWORN_NONE){
                Log.d("myWeather","网络OK");
                queryWeatherCode(newCityCode);
            }
            else{
                Log.d("myWeather","网络炸了");

            }
        }
    }
    /**
     * 初始化天气信息
     */
    private  void initView(){
        city_name_Tv = (TextView) findViewById(R.id.title_city_name);
        cityTv = (TextView) findViewById(R.id.city);
        timeTv = (TextView) findViewById(R.id.time);
        humidityTv = (TextView) findViewById(R.id.humidity);
        currenttemp = (TextView) findViewById(R.id.currenttemp);
        weekTv = (TextView) findViewById(R.id.week_today);
        pmDataTv = (TextView) findViewById(R.id.pm_data);
        pmQualityTv = (TextView) findViewById(R.id.pm2_5_quality);
        pmImg = (ImageView) findViewById(R.id.pm2_5_img);
        temperatureTv = (TextView) findViewById(R.id.temperature);
        climateTv = (TextView) findViewById(R.id.climate);
        windTv = (TextView) findViewById(R.id.wind);
        weatherImg = (ImageView) findViewById(R.id.weather_img);

        city_name_Tv.setText("N/A");
        cityTv.setText("N/A");
        timeTv.setText("N/A");
        humidityTv.setText("N/A");
        currenttemp.setText("N/A");
        pmDataTv.setText("N/A");
        pmQualityTv.setText("N/A");
        weekTv.setText("N/A");
        temperatureTv.setText("N/A");
        climateTv.setText("N/A");
        windTv.setText("N/A");
    }
    private void initImage(){
        ImgHash = new HashMap<String ,Integer>();
        ImgHash.put("晴",R.drawable.biz_plugin_weather_qing);
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
    /**
     * 更新UI天气信息
     * @param todayWeather
     */
    private void updateTodayWeather(TodayWeather todayWeather){
        int pm25 = Integer.parseInt(todayWeather.getPm25());
        String type = todayWeather.getType();
        String []high = todayWeather.getHigh().split(" ");
        String []low  = todayWeather.getLow().split(" ");
        String highdata = high.length == 2? high[1] : high[0];
        String lowdata = low.length == 2? low[1] : low[0];


        city_name_Tv.setText(todayWeather.getCity()+"天气");
        cityTv.setText(todayWeather.getCity());
        timeTv.setText(todayWeather.getUpdatetime()+"发布");
        humidityTv.setText("湿度：" + todayWeather.getShidu());
        currenttemp.setText("温度："+ todayWeather.getWendu()+ "℃");
        pmDataTv.setText(todayWeather.getPm25());
        pmQualityTv.setText(todayWeather.getQuality());
        weekTv.setText(todayWeather.getDate());
        temperatureTv.setText(highdata+"~"+ lowdata);
        climateTv.setText(todayWeather.getType());
        windTv.setText("风力："+todayWeather.getFengli());
        //没有风向
        if(pm25<=50){
            /*String fileName = "/main/java/res/drawble-xhdpi/biz_plugin_weather_0_50.png";
            Bitmap bmp = BitmapFactory.decodeFile(fileName);
            pmImg.setImageBitmap(bmp);*/
            pmImg.setImageDrawable(getResources().getDrawable(R.drawable.biz_plugin_weather_0_50));
        }
        else if(pm25<=100){
            pmImg.setImageDrawable(getResources().getDrawable(R.drawable.biz_plugin_weather_51_100));
        }
        else if(pm25<=150){
            pmImg.setImageDrawable(getResources().getDrawable(R.drawable.biz_plugin_weather_101_150));
        }
        else if(pm25<=200){
            pmImg.setImageDrawable(getResources().getDrawable(R.drawable.biz_plugin_weather_151_200));
        }
        else if(pm25<=300){
            pmImg.setImageDrawable(getResources().getDrawable(R.drawable.biz_plugin_weather_201_300));
        }
        else{
            pmImg.setImageDrawable(getResources().getDrawable(R.drawable.biz_plugin_weather_greater_300));
        }
        //更新天气图片
        if(ImgHash.containsKey(type)){
            weatherImg.setImageDrawable(getResources().getDrawable(ImgHash.get(type)));
        }

        Toast.makeText(MainActivity.this, "更新成功!", Toast.LENGTH_SHORT).show();
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
                TodayWeather todayWeather = null;
                System.out.println(Thread.currentThread().getName());
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
                    todayWeather = parseXML(responseStr);
                    if(todayWeather != null){
                        Log.d("myWeather", todayWeather.toString());

                        Message msg = new Message();
                        msg.what = UPDATE_TODAY_WEATHER;
                        msg.obj = todayWeather;
                        mHandler.sendMessage(msg);
                    }
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

    /**
     * 解析XML
     * @param xmldata
     */
    private TodayWeather parseXML(String xmldata){
        TodayWeather todayWeather = null;

        int fengxiangCount = 0;
        int fengliCount = 0;
        int dateCount = 0;
        int highCount = 0;
        int lowCount = 0;
        int typeCount = 0;
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
                        if(xmlPullParser.getName().equals("resp")){
                            todayWeather = new TodayWeather();
                        }
                        if(todayWeather!=null){
                            if(xmlPullParser.getName().equals("city")){
                                eventType = xmlPullParser.next();
                                //Log.d("myWeather","city     "+xmlPullParser.getText());
                                todayWeather.setCity(xmlPullParser.getText());
                            }
                            else if(xmlPullParser.getName().equals("updatetime")){
                                eventType = xmlPullParser.next();
                                todayWeather.setUpdatetime(xmlPullParser.getText());
//                            Log.d("myWeather","updatetime    "+xmlPullParser.getText());
                            }
                            else if(xmlPullParser.getName().equals("shidu")){
                                eventType = xmlPullParser.next();
//                            Log.d("myWeather","shidu    "+xmlPullParser.getText());
                                todayWeather.setShidu(xmlPullParser.getText());
                            }
                            else if(xmlPullParser.getName().equals("wendu")){
                                eventType = xmlPullParser.next();
//                            Log.d("myWeather","wendu    "+xmlPullParser.getText());
                                todayWeather.setWendu(xmlPullParser.getText());
                            }
                            else if(xmlPullParser.getName().equals("pm25")){
                                eventType = xmlPullParser.next();
//                            Log.d("myWeather","pm25    "+xmlPullParser.getText());
                                todayWeather.setPm25(xmlPullParser.getText());
                            }
                            else if(xmlPullParser.getName().equals("quality")){
                                eventType = xmlPullParser.next();
//                            Log.d("myWeather","quality    "+xmlPullParser.getText());
                                todayWeather.setQuality(xmlPullParser.getText());
                            }
                            else if(xmlPullParser.getName().equals("fengxiang" ) && fengxiangCount==0){
                                eventType = xmlPullParser.next();
//                            Log.d("myWeather","fengxiang    "+xmlPullParser.getText());
                                todayWeather.setFengxiang(xmlPullParser.getText());
                                fengxiangCount++;
                            }
                            else if(xmlPullParser.getName().equals("fengli" ) && fengliCount==0){
                                eventType = xmlPullParser.next();
//                            Log.d("myWeather","fengli    "+xmlPullParser.getText());
                                todayWeather.setFengli(xmlPullParser.getText());
                                fengliCount++;
                            }
                            else if(xmlPullParser.getName().equals("date" ) && dateCount==0){
                                eventType = xmlPullParser.next();
//                            Log.d("myWeather","date    "+xmlPullParser.getText());
                                todayWeather.setDate(xmlPullParser.getText());
                                dateCount++;
                            }
                            else if(xmlPullParser.getName().equals("high" ) && highCount==0){
                                eventType = xmlPullParser.next();
//                            Log.d("myWeather","high    "+xmlPullParser.getText());
                                todayWeather.setHigh(xmlPullParser.getText());
                                highCount++;
                            }
                            else if(xmlPullParser.getName().equals("low") && lowCount==0){
                                eventType = xmlPullParser.next();
//                            Log.d("myWeather","low    "+xmlPullParser.getText());
                                todayWeather.setLow(xmlPullParser.getText());
                                lowCount++;
                            }
                            else if(xmlPullParser.getName().equals("type" ) && typeCount==0){
                                eventType = xmlPullParser.next();
//                            Log.d("myWeather","type    "+xmlPullParser.getText());
                                todayWeather.setType(xmlPullParser.getText());
                                typeCount++;
                            }
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                }
                //进入下一个元素
                eventType = xmlPullParser.next();


            }


        }
        catch (XmlPullParserException e){
            e.printStackTrace();
        }
        catch (IOException e){
            e.printStackTrace();
        }
        return  todayWeather;
    }


}
