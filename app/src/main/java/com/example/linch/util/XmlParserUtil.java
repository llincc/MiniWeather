package com.example.linch.util;

/**
 * Created by linch on 2017/11/10.
 */
import android.util.Xml;

import com.example.linch.bean.TodayWeather ;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.StringReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


class Action {
    private List<String> texts = new ArrayList<String>();
    public void execute(String text){
        //System.out.println(texts.size());
        this.texts.add(text);
    }
    public String getText(int index)
    {
        if(index >= getSize()){
            //pm2.5和quality很可能没有
            return null;
        }
        //System.out.println(texts.get(index));
        return texts.get(index);
    }
    public int getSize(){
        return texts.size();
    }
}

public class XmlParserUtil {
    private Map<String, Action> map;

    public XmlParserUtil() {
        this.map = new HashMap<>();
    }

    private void initMap() {
        map.put("city", new Action());
        map.put("updatetime", new Action());
        map.put("shidu", new Action());
        map.put("wendu", new Action());
        map.put("pm25", new Action());
        map.put("quality", new Action());
        map.put("fengxiang", new Action());
        map.put("fengli", new Action());
        map.put("date", new Action());
        map.put("high", new Action());
        map.put("low", new Action());
        map.put("type", new Action());

        map.put("date_1", new Action());
        map.put("high_1", new Action());
        map.put("low_1", new Action());
        map.put("type_1", new Action());
        map.put("fx_1",new Action());
        map.put("fl_1",new Action());
    }

    private TodayWeather getTodayWeather() {
        TodayWeather todayWeather = new TodayWeather();

        if(map.get("city").getSize() <= 0){
            //存在获取信息失败的情况，则返回初始化todayWeather
            return todayWeather;
        }

        int typeindex = 0; //type分为白天晚上，所以需要根据当前判断要去白天还是晚上的type
        if(Calendar.getInstance().get(Calendar.HOUR_OF_DAY) > 16)
            typeindex = 1;
        //今天天气
        todayWeather.setCity(map.get("city").getText(0));
        todayWeather.setUpdatetime(map.get("updatetime").getText(0));
        todayWeather.setShidu(map.get("shidu").getText(0));
        todayWeather.setWendu(map.get("wendu").getText(0));
        todayWeather.setPm25(map.get("pm25").getText(0));
        todayWeather.setQuality(map.get("quality").getText(0));
        todayWeather.setFengxiang(map.get("fengxiang").getText(0));
        todayWeather.setFengli(map.get("fengli").getText(0));
        todayWeather.setDate(map.get("date").getText(0));
        todayWeather.setHigh(map.get("high").getText(0));
        todayWeather.setLow(map.get("low").getText(0));
        todayWeather.setType(map.get("type").getText(typeindex));
        //昨天天气
        todayWeather.setFengxiang0(map.get("fx_1").getText(0));
        todayWeather.setFengli0(map.get("fl_1").getText(0));
        todayWeather.setDate0(map.get("date_1").getText(0));
        todayWeather.setHigh0(map.get("high_1").getText(0));
        todayWeather.setLow0(map.get("low_1").getText(0));
        todayWeather.setType0(map.get("type_1").getText(0));

        todayWeather.setFengxiang1(map.get("fengxiang").getText(3));
        todayWeather.setFengli1(map.get("fengli").getText(3));
        todayWeather.setDate1(map.get("date").getText(1));
        todayWeather.setHigh1(map.get("high").getText(1));
        todayWeather.setLow1(map.get("low").getText(1));
        todayWeather.setType1(map.get("type").getText(2));

        todayWeather.setFengxiang2(map.get("fengxiang").getText(5));
        todayWeather.setFengli2(map.get("fengli").getText(5));
        todayWeather.setDate2(map.get("date").getText(2));
        todayWeather.setHigh2(map.get("high").getText(2));
        todayWeather.setLow2(map.get("low").getText(2));
        todayWeather.setType2(map.get("type").getText(4));

        todayWeather.setFengxiang3(map.get("fengxiang").getText(7));
        todayWeather.setFengli3(map.get("fengli").getText(7));
        todayWeather.setDate3(map.get("date").getText(3));
        todayWeather.setHigh3(map.get("high").getText(3));
        todayWeather.setLow3(map.get("low").getText(3));
        todayWeather.setType3(map.get("type").getText(6));

        todayWeather.setFengxiang4(map.get("fengxiang").getText(9));
        todayWeather.setFengli4(map.get("fengli").getText(9));
        todayWeather.setDate4(map.get("date").getText(4));
        todayWeather.setHigh4(map.get("high").getText(4));
        todayWeather.setLow4(map.get("low").getText(4));
        todayWeather.setType4(map.get("type").getText(8));

        return todayWeather;
    }

    public TodayWeather parser(String xmlData) {
        this.initMap();
        try
        {
            XmlPullParser xmlPullParser = Xml.newPullParser();
            xmlPullParser.setInput(new StringReader(xmlData));

            int eventType = xmlPullParser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT)
            {
                switch (eventType)
                {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                    {
                        String name = xmlPullParser.getName();
                        if (map.containsKey(name))
                        {
                            xmlPullParser.next();
                            String text = xmlPullParser.getText();
                            map.get(name).execute(text);
                        }

                    }
                    case XmlPullParser.END_TAG:
                        break;
                }
                eventType = xmlPullParser.next();
            }
        }
        catch (XmlPullParserException | IOException e)
        {
            e.printStackTrace();
        }
        return this.getTodayWeather();
    }
}