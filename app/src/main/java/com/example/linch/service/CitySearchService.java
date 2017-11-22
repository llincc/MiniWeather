package com.example.linch.service;

import com.example.linch.app.MyApplication;
import com.example.linch.bean.City;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by linch on 2017/11/10.
 */

/**
 * 城市搜索服务
 */
public class CitySearchService implements Callable<List<String>>{
    private List<City> mCityList;
    private String preChar;
    public CitySearchService(String preChar,List<City> mCityList){
        this.preChar = preChar;
        this.mCityList = mCityList;
    }
    //搜索
    private List<String> getCityList(String preChar, List<City> mCityList){
        if("".equals(preChar)) //输入为空，则直接返回所有城市列表
            return MyApplication.getInstance().getCityList();
        //System.out.println(TAG+Thread.currentThread().getName());
        List<String> SearchResult =new LinkedList<String>();
        for(City city: mCityList){
            if(city.getCity().startsWith(preChar)){
                SearchResult.add(city.getCity()+" "+city.getProvince());
                continue;
            }
            else{
                preChar = preChar.toUpperCase(); //转为大写
            }
            if(city.getAllfirstPY().startsWith(preChar)){
                SearchResult.add(city.getCity()+" "+city.getProvince());
            }
            else if(city.getAllPY().startsWith(preChar)){
                SearchResult.add(city.getCity()+" "+city.getProvince());
            }
            else if(city.getFirstPY().startsWith(preChar)) {
                SearchResult.add(city.getCity()+" "+city.getProvince());
            }
        }
        return SearchResult;
    }

    @Override
    public List<String> call() throws Exception {
        return getCityList(preChar,mCityList);
    }
}
