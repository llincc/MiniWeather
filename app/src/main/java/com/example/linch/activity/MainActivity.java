package com.example.linch.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.example.linch.adapter.ViewPagerAdapter;
import com.example.linch.app.MyApplication;
import com.example.linch.appwidget.MiniWidget;
import com.example.linch.bean.TodayWeather;
import com.example.linch.controller.ThreadPoolController;
import com.example.linch.miniweather.R;
import com.example.linch.service.AutoUpdateService;
import com.example.linch.service.BaiDuLocationService;
import com.example.linch.service.FetchTodayWeatherService;
import com.example.linch.util.ButtonSlopUtil;
import com.example.linch.util.ImageRotateUtil;
import com.example.linch.util.NetUtil;
import com.example.linch.util.PermissionUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by linch on 2017/9/20.
 */

public class MainActivity extends Activity implements View.OnClickListener{
    public static final  int UPDATE_TODAY_WEATHER = 1;  //更新天气标志
    public static final  int LOCATION_SERVICE = 2;

    private static final int SDK_PERMISSION_REQUEST = 100;

    public static final String TAG = "MainActivity";

    private ImageView mUpdateBtn;
    private ImageView mLocationBtn;
    private ImageView mCityBtn;
    private ImageView weatherImg, pmImg;

    private TextView  cityTv, timeTv, humidityTv, weekTv, pmDataTv, pmQualityTv, temperatureTv,
                       climateTv, windTv, city_name_Tv, currenttemp;
    //viewAdapter

    private ViewPagerAdapter viewPagerAdapter;

    private ViewPager viewPager;
    private List<View> views;
    private List<ImageView> idotList;

    private ServiceConnection connection;
    private AutoUpdateService autoUpdateService;

    private LocationClient locationClient;
    private BaiDuLocationService baiDuLocationService;

    private String[] requestPermissions = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION};
    private PermissionUtil.PermissionTool permissionTool;
    @Override
    protected void onCreate(Bundle savedInstanceState ){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_info);

        //System.out.println(Thread.currentThread().getName());
        mUpdateBtn = (ImageView)findViewById(R.id.title_update_btn);   //title_update_btn按钮
        mCityBtn =   (ImageView)findViewById(R.id.title_city_manager); //title_city_manager按钮
        mLocationBtn = (ImageView)findViewById(R.id.title_location);   //title_location按钮
        mUpdateBtn.setOnClickListener(this);
        mCityBtn.setOnClickListener(this);
        mLocationBtn.setOnClickListener(this);

        locationClient = new LocationClient(this);                      //声明LocationClient类
        baiDuLocationService = new BaiDuLocationService(this);         //注册监听函数
        locationClient.registerLocationListener(baiDuLocationService);
        //网咯测试
        if(NetUtil.isConnectNet(this)){
            Log.d(TAG,"网络OK");
            Toast.makeText(MainActivity.this,"网络OK！",Toast.LENGTH_SHORT).show();
        }
        else{
            Log.d(TAG,"网络连接失败");
            Toast.makeText(MainActivity.this,"网络连接失败！",Toast.LENGTH_SHORT).show();
        }

        initAllViews();
        initService();  //初始化服务
        initLocation(); //初始化地址设置


        //permissionCheck(); //权限检查与申请
        locationClient.start(); //初始化时定位一次
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
                        recoverCityCode("main_city_code_current", "main_city_code_previous"); //天气信息不存在，说明当前城市代码无效，恢复上一个有效的城市代码
                    }
                    else{
                        updateTodayWeather(todayWeather);
                        viewPagerAdapter.setInfoList(todayWeather); //更新ViewPager
                        viewPagerAdapter.updatePageItems();
                        recoverCityCode( "main_city_code_previous", "main_city_code_current"); //保存当前有效的citycode
                    }

                    break;
                case LOCATION_SERVICE:   //接受定位回传的城市代码
                    String citycode = (String)msg.obj;
                    if("".equals(citycode)){
                        Toast.makeText(MainActivity.this,"定位失败，请检查网络设置",Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Log.d(TAG, "定位城市代码："+citycode);
                        queryWeatherCode(citycode);  //发送天气信息请求
                    }
                    if(locationClient.isStarted()){
                        locationClient.stop();// 暂停定位服务
                    }
                    break;
                default:
                    break;
            }
        }
    };
    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.title_city_manager:
                Intent intent = new Intent(this,SelectCityActivity.class);
                //传递当前城市信息
                intent.putExtra("current_city", cityTv.getText());
                startActivityForResult(intent, 1);
                break;
            case R.id.title_update_btn:
                //通过SharedPreferences读取城市id，如果没有定义则缺省为101010100（北京城市）
                SharedPreferences sharedPreferences = getSharedPreferences("config",MODE_PRIVATE);
                String cityCode = sharedPreferences.getString("main_city_code_current","101010100");
                Log.d(TAG,cityCode);

                if(NetUtil.isConnectNet(this)){  //确定网络可访问
                    Log.d(TAG,"网络OK");
                    // queryWeatherCode(cityCode);
                    if(ButtonSlopUtil.check(R.id.title_update_btn, 1000)){ //设置点击间隔，防止按钮被频繁点击
                        //Toast.makeText(this, "亲爱的，您点太快了", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        fetchWeather(cityCode);
                    }
                }
                else{
                    Log.d(TAG,"网络连接失败");
                    Toast.makeText(MainActivity.this,"网络连接失败！",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.title_location:
                locationClient.start();
                break;
            default:
                break;
        }
    }
    //接收Intent
    protected  void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == 1 && resultCode == RESULT_OK){ //接收城市代码
            String newCityCode = data.getStringExtra("cityCode");
            if(NetUtil.isConnectNet(this)){ //网络测试
                Log.d(TAG,"网络OK");
                //if(city)
                queryWeatherCode(newCityCode);
            }
            else{
                Log.d(TAG,"网络连接失败");
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

        TabLayout tabLayout = (TabLayout)findViewById(R.id.tabDots);
        tabLayout.setupWithViewPager(viewPager, true);  //TabLayout绑定ViewPage

        String citycode;
        if(!(citycode =getSharedPreferences("config",MODE_PRIVATE).getString("main_city_code_current","")).equals("")){
            queryWeatherCode(citycode);
        }
        else{
            initView();
        }
    }
    private void initLocation(){
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);
        option.setIsNeedAddress(true); //是否获取地址信息
        option.setCoorType("bd09ll");  //百度经纬度坐标系
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy); //定位模式
        option.setScanSpan(5000);  //设置检查周期
        ;
        locationClient.setLocOption(option);
    }

    /**
     * 初始化服务
     */
    private void initService(){
        //初始化链接
        connection = new ServiceConnection() {
            /**
             * 与服务器端交互的接口方法 绑定服务的时候被回调，在这个方法获取绑定Service传递过来的IBinder对象，
             * 通过这个IBinder对象，实现宿主和Service的交互。
             */
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Log.d(TAG,"绑定自动更新服务");
                //获取绑定的Service实例
                AutoUpdateService.LocalBinder binder = (AutoUpdateService.LocalBinder)service;
                autoUpdateService = binder.getSerVice();
                autoUpdateService.setContext(MainActivity.this);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                autoUpdateService = null;
            }
        };
        Intent intent = new Intent(MainActivity.this, AutoUpdateService.class);
        bindService(intent, connection, Service.BIND_AUTO_CREATE);  //绑定服务
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
        //cityCode保存到SharedPreference
        setSharePreference("main_city_code_current", cityCode);

        String address = String.format("%s%s","http://wthrcdn.etouch.cn/WeatherApi?citykey=",cityCode);
        ThreadPoolExecutor executor = ThreadPoolController.getInstance().getExecutor();
        executor.submit(new FetchTodayWeatherService(address,this));
    }

    /**
     * 恢复citycode, 因为城市代码中可能存在无效的citycode(得不到天气信息)，所以用之前有效的citycode覆盖当前无效的citycode
     */
    private void recoverCityCode(String key_cur, String key_pre){
        String old_citycode = getSharedPreferences("config",MODE_PRIVATE).getString(key_pre,"101010100"); //之前的有效的citycode
        setSharePreference(key_cur, old_citycode);
    }
    private void setSharePreference(String key, String value){
        SharedPreferences.Editor editor = getSharedPreferences("config",MODE_PRIVATE).edit();
        editor.putString(key,value);
        editor.commit();
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

    /**
     * 定位相关权限检查
     */
    @TargetApi(23)
    private void startLocation(){
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.M){ //安卓6.0 SDK23才需要申请权限
            permissionTool = new PermissionUtil.PermissionTool(new PermissionUtil.PermissionListener() {
                @Override
                public void allGranted() {
                    Log.d(TAG, "已有权限");
                    locationClient.start();
                }
            });
            permissionTool.checkAndRequestPermission(MainActivity.this, requestPermissions);
        }
    }


}
