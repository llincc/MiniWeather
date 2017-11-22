package com.example.linch.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.app.Activity;

import com.example.linch.app.MyApplication;
import com.example.linch.bean.TodayWeather;
import com.example.linch.miniweather.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by linch on 2017/11/1.
 */

public class ViewPagerAdapter extends PagerAdapter {
    private List<View> views;
    private int position;
    private Context context;
    private List<String> weatherInfoList;
    //TextView id列表
    private final int pageViewIdList[] = {
            R.id.date1, R.id.climate1,R.id.temperature1 , R.id.wind1,
            R.id.date2, R.id.climate2,R.id.temperature2 , R.id.wind2,
            R.id.date3, R.id.climate3,R.id.temperature3 , R.id.wind3,
            R.id.date4, R.id.climate4,R.id.temperature4 , R.id.wind4,
            R.id.date5, R.id.climate5,R.id.temperature5 , R.id.wind5,
            R.id.date6, R.id.climate6,R.id.temperature6 , R.id.wind6};
    //ImageView id 列表
    private final  int pageImageIdList[] = {
            R.id.weather_img1, R.id.weather_img2, R.id.weather_img3,
            R.id.weather_img4, R.id.weather_img5, R.id.weather_img6};

    private boolean positionValue[] = {false, false}; //控制pageView更新频率
    public ViewPagerAdapter(List<View> views, Context context){
        this.views = views;
        this.context = context;
        this.position = 0;
       // this.todayWeather = todayWeather;
    }
    @Override
    public int getCount() {
        if(views!=null)
            return views.size();
        return 0;
    }

    /**
     * 初始化View
     * @param container
     * @param position
     * @return
     */
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        //System.out.println("********************"+ position);
        if(weatherInfoList == null)
            initPageItems(views.get(position),position);
        else
            updatePageItems(views.get(position),position);
        container.addView(views.get(position));
        return views.get(position);
    }

    /**
     * 更新PageView
     * @param container
     * @param position
     * @param object
     */
    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
         this.position = position;
         if(positionValue[position] == false){
             updatePageItems(views.get(position),position);
             positionValue[position] = true; //只更新一次，避免频繁更新
         }

    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(views.get(position));
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view == object);
    }
    //初始化界面
    private void initPageItems(View view,int position){
        TextView textview;
        int offset = position * 12;
        for(int i=0; i<3; i++){
            for(int j=0; j<4; j++){
                textview = (TextView)view.findViewById(pageViewIdList[offset + i*4 + j]);
                textview.setText("N/A");
            }
        }
    }
    public void updatePageItems(){
        updatePageItems(views.get(0),0);
        updatePageItems(views.get(1),1);
    }

    /**
     * pageVIew更新主模块
     * 同步机制防止出现错乱
     * @param view
     * @param position
     */
    private  synchronized void updatePageItems(View view, int position){
        System.out.println("position"+position);
        if(weatherInfoList==null){
            return;
        }
        TextView textview;
        int offset = position * 12;
        //更新文本
        for(int i=0; i<3; i++){
            for(int j=0; j<4; j++){
                int index = offset + i*4 + j;
                textview = (TextView)view.findViewById(pageViewIdList[index]);
                textview.setText(weatherInfoList.get(index));
            }
        }
        //更新图片
        ImageView imageview;
        HashMap<String,Integer> ImgHash = MyApplication.getInstance().getImgHash();
        for(int i=0; i<3; i++){
            int index = offset + i*4 + 2;
            String type = weatherInfoList.get(index); //获取气候信息
            if("N/A".equals(type))
                continue;
            imageview = (ImageView)view.findViewById(pageImageIdList[position*3 +i]);
            System.out.println("***"+type+"***");
            imageview.setImageDrawable(view.getResources().getDrawable(ImgHash.get(type)));
            //imageview.setImageResource(ImgHash.get(type));
        }
    }

    /**
     * 复制天气信息到weatherInfoList中
     * @param todayWeather
     */
    public void setInfoList(TodayWeather todayWeather) {
        if (todayWeather == null) {
            return;
        }
        positionValue[0] = false; //设置为需要更新
        positionValue[1] = false; //设置为需要更新

        weatherInfoList = new ArrayList<String>();
        //  weatherInfoList.add(todayWeather.getFengxiang0());
        //昨天
        weatherInfoList.add(todayWeather.getDate0().replaceFirst("星期[一二三四五六日]","")+"昨天");
        weatherInfoList.add(todayWeather.getHigh0() + "~" + todayWeather.getLow0());
        weatherInfoList.add(todayWeather.getType0());
        weatherInfoList.add(todayWeather.getFengxiang0());
        //今天
        weatherInfoList.add(todayWeather.getDate().replaceFirst("星期[一二三四五六日]","")+"今天");
        weatherInfoList.add(todayWeather.getHigh() + "~" + todayWeather.getLow());
        weatherInfoList.add(todayWeather.getType());
        weatherInfoList.add(todayWeather.getFengxiang());
        //后天
        weatherInfoList.add(todayWeather.getDate1());
        weatherInfoList.add(todayWeather.getHigh1() + "~" + todayWeather.getLow1());
        weatherInfoList.add(todayWeather.getType1());
        weatherInfoList.add(todayWeather.getFengxiang1());

        weatherInfoList.add(todayWeather.getDate2());
        weatherInfoList.add(todayWeather.getHigh2() + "~" + todayWeather.getLow2());
        weatherInfoList.add(todayWeather.getType2());
        weatherInfoList.add(todayWeather.getFengxiang2());

        weatherInfoList.add(todayWeather.getDate3());
        weatherInfoList.add(todayWeather.getHigh3() + "~" + todayWeather.getLow3());
        weatherInfoList.add(todayWeather.getType3());
        weatherInfoList.add(todayWeather.getFengxiang3());

        weatherInfoList.add(todayWeather.getDate4());
        weatherInfoList.add(todayWeather.getHigh4() + "~" + todayWeather.getLow4());
        weatherInfoList.add(todayWeather.getType4());
        weatherInfoList.add(todayWeather.getFengxiang4());

    }
}
