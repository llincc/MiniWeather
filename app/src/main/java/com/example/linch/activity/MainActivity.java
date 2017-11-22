package com.example.linch.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.linch.adapter.ViewPagerAdapter;
import com.example.linch.app.MyApplication;
import com.example.linch.bean.TodayWeather;
import com.example.linch.controller.ThreadPoolController;
import com.example.linch.miniweather.R;
import com.example.linch.service.FetchTodayWeatherService;
import com.example.linch.util.ImageRotateUtil;
import com.example.linch.util.ButtonSlopUtil;
import com.example.linch.util.NetUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by linch on 2017/9/20.
 */

public class MainActivity extends Activity implements View.OnClickListener{
    public static final  int UPDATE_TODAY_WEATHER = 1;

    private ImageView mUpdateBtn;
    private ImageView mCityBtn;
    private ImageView weatherImg, pmImg;

    private TextView  cityTv, timeTv, humidityTv, weekTv, pmDataTv, pmQualityTv, temperatureTv,
                       climateTv, windTv, city_name_Tv, currenttemp;
    //viewAdapter

    private ViewPagerAdapter viewPagerAdapter;
    private ViewPager viewPager;
    private List<View> views;

    @Override
    protected void onCreate(Bundle savedInstanceState ){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_info);
        //System.out.println(Thread.currentThread().getName());
        mUpdateBtn = (ImageView)findViewById(R.id.title_update_btn); //title_update_btn按钮
        mCityBtn =   (ImageView)findViewById(R.id.title_city_manager); //title_city_manager按钮
        mUpdateBtn.setOnClickListener(this);
        mCityBtn.setOnClickListener(this);
        //网咯测试
        if(NetUtil.getNetworkState(this) != NetUtil.NETWORN_NONE){
            Log.d("myWeather","网络OK");
            Toast.makeText(MainActivity.this,"网络OK！",Toast.LENGTH_SHORT).show();
        }
        else{
            Log.d("myWeather","网络连接失败");
            Toast.makeText(MainActivity.this,"网络连接失败！",Toast.LENGTH_SHORT).show();
        }

        initAllViews();
    }
    /**
     * Handler负责接收子线程发送的消息并更新UI
     */
    private Handler mHandler = new Handler(){
        public void handleMessage(android.os.Message msg){
            switch (msg.what){
                case UPDATE_TODAY_WEATHER:
                    //更新界面
                    TodayWeather todayWeather = (TodayWeather)msg.obj;
                    if(todayWeather.getCity().equals("N/A")){
                        Toast.makeText(MainActivity.this,"抱歉，该城市天气信息不存在",Toast.LENGTH_SHORT).show();
                        break;
                    }
                    updateTodayWeather(todayWeather);
                    viewPagerAdapter.setInfoList(todayWeather);
                    viewPagerAdapter.updatePageItems();
                    break;
                default:
                    break;
            }
        }
    };
    @Override
    public void onClick(View view){
        if(view.getId() == R.id.title_city_manager){
            Intent intent = new Intent(this,SelectCityActivity.class);
            //传递当前城市信息
            intent.putExtra("currentCity",cityTv.getText());
            startActivityForResult(intent,1);
        }
        if(view.getId() == R.id.title_update_btn){
            //通过SharedPreferences读取城市id，如果没有定义则缺省为101010100（北京城市）
            SharedPreferences sharedPreferences = getSharedPreferences("config",MODE_PRIVATE);
            String cityCode = sharedPreferences.getString("main_city_code","101010100");
            Log.d("myWeather",cityCode);

            if(NetUtil.getNetworkState(this)!=NetUtil.NETWORN_NONE){  //确定网络可访问
                Log.d("myWeather","网络OK");
               // queryWeatherCode(cityCode);
                if(ButtonSlopUtil.check(R.id.title_update_btn, 1000)){ //设置点击间隔，防止按钮被频繁点击
                    //Toast.makeText(this, "亲爱的，您点太快了", Toast.LENGTH_SHORT).show();
                }
                else{
                    fetchWeather(cityCode);
                }

            }
            else{
                Log.d("myWeather","网络连接失败");
                Toast.makeText(MainActivity.this,"网络连接失败！",Toast.LENGTH_SHORT).show();
            }
        }
    }
    //接收Intent
    protected  void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == 1 && resultCode == RESULT_OK){ //接收城市代码
            String newCityCode = data.getStringExtra("cityCode");
            if(NetUtil.getNetworkState(this) != NetUtil.NETWORN_NONE){ //网络测试
                Log.d("myWeather","网络OK");
                //if(city)
                queryWeatherCode(newCityCode);
            }
            else{
                Log.d("myWeather","网络连接失败");

            }
        }
    }
    private void initAllViews(){
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



        LayoutInflater inflater = LayoutInflater.from(this);
        View one_page = inflater.inflate(R.layout.page1viewer,null);
        View two_page = inflater.inflate(R.layout.page2viewer,null);
        views = new ArrayList<View>();
        views.add(one_page);
        views.add(two_page);
        viewPagerAdapter = new ViewPagerAdapter(views,this);
        viewPager = (ViewPager)findViewById(R.id.viewpager);
        viewPager.setAdapter(viewPagerAdapter);


        String citycode;
        if(!(citycode =getSharedPreferences("config",MODE_PRIVATE).getString("main_city_code","")).equals("")){
            queryWeatherCode(citycode);
        }
        else{
            initView();
        }
    }
    /**
     * 初始化天气信息
     */
    private  void initView(){

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

    /**
     * 更新UI天气信息
     * @param todayWeather
     */
    private void updateTodayWeather(TodayWeather todayWeather){
        //System.out.println(todayWeather.toString());
        int pm25 = Integer.parseInt(todayWeather.getPm25());
        String type = todayWeather.getType();

        city_name_Tv.setText(todayWeather.getCity()+"天气");
        cityTv.setText(todayWeather.getCity());
        timeTv.setText(todayWeather.getUpdatetime()+"发布");
        humidityTv.setText("湿度：" + todayWeather.getShidu());
        currenttemp.setText("温度："+ todayWeather.getWendu()+ "℃");
        pmDataTv.setText(todayWeather.getPm25());
        pmQualityTv.setText(todayWeather.getQuality());
        weekTv.setText(todayWeather.getDate());
        temperatureTv.setText(todayWeather.getHigh()+"~"+ todayWeather.getLow());
        climateTv.setText(todayWeather.getType());
        windTv.setText("风力："+todayWeather.getFengli());
        //没有风向
        if(pm25<=50){
           // pmImg.setImageResource();
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
        if(MyApplication.getInstance().getImgHash().containsKey(type)){
            weatherImg.setImageDrawable(getResources().getDrawable(MyApplication.getInstance().getImgHash().get(type)));
        }

        Toast.makeText(MainActivity.this, "更新成功!", Toast.LENGTH_SHORT).show();
    }
    /**
     * 获取cityCode所指的城市的天气信息
     * @param cityCode   城市id
     */
    private  void queryWeatherCode(String cityCode){
        String address = String.format("%s%s","http://wthrcdn.etouch.cn/WeatherApi?citykey=",cityCode);
        ThreadPoolExecutor executor = ThreadPoolController.getInstance().getExecutor();
        executor.submit(new FetchTodayWeatherService(address,this));

    }

    /**
     * 控制图标转动，获取天气信息
     * @param cityCode
     */
    private void fetchWeather(String cityCode){
        ImageRotateUtil updateImageRotate = new ImageRotateUtil(this.mUpdateBtn,this);
        updateImageRotate.startRotate();
        queryWeatherCode(cityCode);
        //updateImageRotate.stopRotate();
    }
    public Handler getmHandler(){
        return mHandler;
    }


}
