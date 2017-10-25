package com.example.linch.miniweather;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.linch.app.MyApplication;
import com.example.linch.bean.City;

import java.util.List;

/**
 * Created by linch on 2017/10/18.
 */

public class SelectCity extends Activity implements View.OnClickListener{
    private ImageView mBackBtn;
    private MyApplication mApplication;
    private ListView cityListView;
    private List<City> cityList;

    @Override
    protected  void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.select_city);

        mBackBtn = (ImageView)findViewById(R.id.title_back);
        mBackBtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v){
         switch (v.getId()){
             case R.id.title_back:
                 Intent i = new Intent();
                 i.putExtra("cityCode","101160101");
                 setResult(RESULT_OK,i);
                 finish();
                 break;
             default:
                 break;
         }
    }
    //初始化城市列表
    private void initCityListView(){
        mApplication = MyApplication.getInstance();
        cityListView = (ListView)findViewById(R.id.city_list);
        cityList =  mApplication.getCityList();

       // ArrayAdapter<String> adapter = new ArrayAdapter<String>(SelectCity.this,android.R.layout.simple_list_item_1,cityList);

    }
}
