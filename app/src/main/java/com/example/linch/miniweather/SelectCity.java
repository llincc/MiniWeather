package com.example.linch.miniweather;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.linch.app.MyApplication;
import com.example.linch.util.NetUtil;

import java.util.List;

/**
 * Created by linch on 2017/10/18.
 */

public class SelectCity extends Activity implements View.OnClickListener,AdapterView.OnItemClickListener,TextWatcher{
    private ImageView mBackBtn;
    private MyApplication mApplication;
    private ListView cityListView;
    private List<String> cityList;
    private EditText searchbox;
    private TextView titleCity;

    private String currentCity;

    @Override
    protected  void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.select_city);
        cityListView = (ListView)findViewById(R.id.city_list);
        mBackBtn = (ImageView)findViewById(R.id.title_back);
        searchbox = (EditText)findViewById(R.id.city_search);
        titleCity = (TextView)findViewById(R.id.title_name);

        mBackBtn.setOnClickListener(this);
        cityListView.setOnItemClickListener(this);
        searchbox.addTextChangedListener(this);

        //接受当前城市名
        Intent it_get = getIntent();
        currentCity = it_get.getStringExtra("currentCity");
        currentCity = currentCity.equals("N/A") ? "无" : currentCity;
        titleCity.setText("当前天气:"+currentCity);
        //初始化城市列表
        cityList = MyApplication.getInstance().getCityList();
        initCityListView(cityList);



    }

    @Override
    public void onClick(View v){
         switch (v.getId()){
             case R.id.title_back:
                 Intent i = new Intent(this,MainActivity.class);
                 //i.putExtra("cityCode","101160101");
                 //setResult(RESULT_OK,i);
                 finish();
                 break;
             default:
                 break;
         }
    }

    protected  void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == 1 && resultCode == RESULT_OK){

        }
    }
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        //返回城市列表
        cityList = MyApplication.getInstance().getCityList(s.toString());
        initCityListView(cityList);
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l){

        Log.d("Select:",cityList.get(i));
        String []city_province = cityList.get(i).split(" ");
        String citycode = MyApplication.getInstance().getCityCode(city_province[0],city_province[1]);
        //cityCode保存到SharedPreference
        SharedPreferences sharedPreferences = getSharedPreferences("config",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("main_city_code",citycode);
        editor.commit();
        //发送Intent
        Intent intent = new Intent(this,MainActivity.class);
        intent.putExtra("cityCode",citycode);
        Toast.makeText(SelectCity.this,"选择城市："+cityList.get(i),Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK,intent);
        finish();
    }
    //更新城市列表
    private void initCityListView(List<String> city_province){
        //System.out.println("初始化列表");
        //cityList =  MyApplication.getInstance().getCityList();
        //Adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(SelectCity.this,R.layout.list_item_text,city_province);
        //更新ListView
        cityListView.setAdapter(adapter);
    }
}
