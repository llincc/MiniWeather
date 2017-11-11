package com.example.linch.service;

/**
 * Created by linch on 2017/11/10.
 */


import android.os.Message;

import com.example.linch.activity.MainActivity;
import com.example.linch.bean.TodayWeather;
import com.example.linch.util.XmlParserUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class FetchTodayWeatherService implements Runnable
{
    private String address;
    private HttpURLConnection connection;
    private MainActivity context;

    public FetchTodayWeatherService(String address,MainActivity context)
    {
        this.address = address;
        this.connection = null;
        this.context = context;
    }

    /**
     * 创建网络连接
     * @throws IOException
     */
    private void initConnection() throws IOException
    {
        connection = (HttpURLConnection) new URL(address).openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(8000);
        connection.setReadTimeout(8000);
    }

    /**
     * 从网络获取数据
     * @return
     * @throws IOException
     */
    private String fetchData() throws IOException
    {
        InputStream in = connection.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder response = new StringBuilder();

        for (String str; (str = reader.readLine()) != null;)
        {
            response.append(str);
        }
        return response.toString();
    }

    /**
     * 关闭网络连接
     * @throws IOException
     */
    private void close() throws IOException
    {
        if (connection != null)
        {
            connection.disconnect();
        }
    }

    /**
     * xml解析
     * @param xmlData
     * @return TodayWeahter
     */
    private TodayWeather parseWeather(String xmlData)
    {
        XmlParserUtil xmlParser = new XmlParserUtil();
        return xmlParser.parser(xmlData);
    }

    private void sendMessage(TodayWeather todayWeather)
    {
        Message message = new Message();
        message.what = MainActivity.UPDATE_TODAY_WEATHER;
        message.obj = todayWeather;
        context.getmHandler().sendMessage(message);
    }
    /**
     * 主调用程序
     * @return TodayWeather
     * @throws IOException
     */
    private void runWithException() throws IOException
    {
        this.initConnection();
        String data = this.fetchData();
        this.close();
        TodayWeather todayWeather = this.parseWeather(data);
        this.sendMessage(todayWeather);
    }

    @Override
    public void run() {
        try
        {
            this.runWithException();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
