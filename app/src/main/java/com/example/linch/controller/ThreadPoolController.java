package com.example.linch.controller;

/**
 * Created by linch on 2017/11/10.
 */
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.Executors;

/**
 * 线程池
 */
public class ThreadPoolController {
    private static final ThreadPoolController threadpoolinstance = new ThreadPoolController(); //单例模式
    private ThreadPoolExecutor executor;

    private ThreadPoolController(){
        this.executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
    }

    public static ThreadPoolController getInstance(){
        return threadpoolinstance;
    }
    public ThreadPoolExecutor getExecutor(){
        return executor;
    }

}
