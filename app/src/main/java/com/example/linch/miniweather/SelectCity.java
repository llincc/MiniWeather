package com.example.linch.miniweather;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.linch.app.MyApplication;

import java.util.List;

/**
 * Created by linch on 2017/10/18.
 */

public class SelectCity extends Activity implements View.OnClickListener,AdapterView.OnItemClickListener{
    private ImageView mBackBtn;
    private MyApplication mApplication;
    private ListView cityListView;
    private List<String> cityList;

    @Override
    protected  void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.select_city);
        cityListView = (ListView)findViewById(R.id.city_list);
        mBackBtn = (ImageView)findViewById(R.id.title_back);
        mBackBtn.setOnClickListener(this);
        cityListView.setOnItemClickListener(this);

        //初始化城市列表
        initCityListView();



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


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l){

        Log.d("Select:",cityList.get(i));
        Intent intent = new Intent(this,MainActivity.class);
        intent.putExtra("cityName",cityList.get(i));

        Toast.makeText(SelectCity.this,"选择城市："+cityList.get(i),Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK,intent);
        finish();
    }
    //初始化城市列表
    private void initCityListView(){
        System.out.println("初始化列表");
        mApplication = MyApplication.getInstance();
        //MyApplication mApplication= (MyApplication) this.getApplication();
        cityList =  mApplication.getCityList();

        //System.out.println("当前线程"+Thread.currentThread().getName());
       // System.out.println("城市数量"+String.valueOf(cityList.size()));
        //Adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(SelectCity.this,R.layout.list_item_text,cityList);

        cityListView.setAdapter(adapter);
    }
}
