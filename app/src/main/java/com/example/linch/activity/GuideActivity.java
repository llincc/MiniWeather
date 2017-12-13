package com.example.linch.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.example.linch.adapter.GuidePageAdapter;
import com.example.linch.miniweather.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by linch on 2017/11/22.
 */

public class GuideActivity extends Activity implements View.OnClickListener{
    private List<View> guideViews;
    private GuidePageAdapter guidePagerAdapter;
    private ViewPager guideViewPager;
    private ImageView guidebutton;
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //判断是否是第一次启动
        if(!isFirst()){
            directToMain();
        }
        else{
            //将第一次启动属性设为false
            SharedPreferences.Editor editor = getSharedPreferences("config",MODE_PRIVATE).edit();
            editor.putBoolean("first",false);
            editor.commit();
        }

        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT){
            //透明状态栏
             getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
             //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            //去掉标题栏
             requestWindowFeature(Window.FEATURE_NO_TITLE);
        }
        setContentView(R.layout.guide);
        initViews();

        guidebutton = (ImageView)guideViews.get(3).findViewById(R.id.guidebutton);
        guidebutton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.guidebutton:
                directToMain();
                break;
            default:
                break;
        }
    }

    private void initViews(){


        LayoutInflater inflater = LayoutInflater.from(this);
        guideViews = new ArrayList<View>();

        guideViews.add(inflater.inflate(R.layout.guidepage1, null));
        guideViews.add(inflater.inflate(R.layout.guidepage2, null));
        guideViews.add(inflater.inflate(R.layout.guidepage3, null));
        guideViews.add(inflater.inflate(R.layout.guidepage4, null));

        guidePagerAdapter = new GuidePageAdapter(guideViews, this);
        guideViewPager = (ViewPager) findViewById(R.id.guideViewPager);
        guideViewPager.setAdapter(guidePagerAdapter);

        TabLayout tabLayout = (TabLayout)findViewById(R.id.guide_tabDots);
        tabLayout.setupWithViewPager(guideViewPager, true);

    }

    private void directToMain(){
        //跳转到MainActivity
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
        finish();
    }

    private boolean isFirst(){
        boolean first = getSharedPreferences("config",MODE_PRIVATE).getBoolean("first", true);
        return first;
    }

}
