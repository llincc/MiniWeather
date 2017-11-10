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
import java.util.HashMap;
import java.util.Map;


abstract class Action
{
    String text;
    public abstract void execute(String text);

    String getText()
    {
        return text;
    }
}

class Action1 extends Action
{
    @Override
    public void execute(String text)
    {
        this.text = text;
    }
}

class Action2 extends Action
{
    private int count;

    Action2(int count)
    {
        this.count = count;
    }

    @Override
    public void execute(String text)
    {
        if (this.count == 0)
        {
            this.text = text;
            this.count++;
        }
    }
}

public class XmlParserUtil {
    private Map<String, Action> map;

    public XmlParserUtil() {
        this.map = new HashMap<>();
    }

    private void initMap() {
        map.put("city", new Action1());
        map.put("updatetime", new Action1());
        map.put("shidu", new Action1());
        map.put("wendu", new Action1());
        map.put("pm25", new Action1());
        map.put("quality", new Action1());
        map.put("fengxiang", new Action2(0));
        map.put("fengli", new Action2(0));
        map.put("date", new Action2(0));
        map.put("high", new Action2(0));
        map.put("low", new Action2(0));
        map.put("type", new Action2(0));
    }

    private TodayWeather getTodayWeather() {
        TodayWeather todayWeather = new TodayWeather();
        todayWeather.setCity(map.get("city").getText());
        todayWeather.setUpdatetime(map.get("updatetime").getText());
        todayWeather.setShidu(map.get("shidu").getText());
        todayWeather.setWendu(map.get("wendu").getText());
        todayWeather.setPm25(map.get("pm25").getText());
        todayWeather.setQuality(map.get("quality").getText());
        todayWeather.setFengxiang(map.get("fengxiang").getText());
        todayWeather.setFengli(map.get("fengli").getText());
        todayWeather.setDate(map.get("date").getText());
        todayWeather.setHigh(map.get("high").getText());
        todayWeather.setLow(map.get("low").getText());
        todayWeather.setType(map.get("type").getText());
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