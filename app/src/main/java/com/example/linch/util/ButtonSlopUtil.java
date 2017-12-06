package com.example.linch.util;

/**
 * Created by linch on 2017/11/15.
 */

import java.util.HashMap;
import java.util.Map;

/**
 * final类禁止被继承
 * 限制按钮点击频率
 */
public final class ButtonSlopUtil {
    //记录所有按钮最后点击的时间
    private final static Map<String,Long> SLOPS_MAP = new HashMap<String ,Long>();
    private static int MIN_SLOP = 500;

    /**
     * 500ms内不能再点击
     * @param buttonID
     * @return true
     */
    public  static boolean check(int buttonID){
        return check(buttonID,MIN_SLOP);
    }
    public static boolean check(int buttonID, int holdTimeMills){
        return check(String.valueOf(buttonID), holdTimeMills);
    }
    public static boolean waitInfinte(int buttonID){
        return waitInfinte(String.valueOf(buttonID));
    }
    public static void cancel(int buttonID){
        cancel(String.valueOf(buttonID));
    }
    /**
     * 检查点击间隔是否符合要求
     * @param buttonID
     * @param holdTimeMills
     * @return true不能被点击，false表示可以被点击
     */
    public static boolean check(String buttonID, int holdTimeMills) {
        if (buttonID == null || buttonID.length() <= .0) {
            return true;
        }
        if (holdTimeMills < 100) {
            holdTimeMills = 100; //限制最小间隔为100ms
        }
        //同步块控制
        synchronized (SLOPS_MAP) {
            Long lastTipLong = SLOPS_MAP.get(buttonID);
            if (lastTipLong == null || System.currentTimeMillis() - lastTipLong >= holdTimeMills) {
                SLOPS_MAP.put(buttonID, System.currentTimeMillis());
                return false;//表示可以被点击
            } else {
                return true;//表示不能被点击
            }

        }
    }

    /**
     * 业务控制
     * @param buttonID
     * @return
     */
    public static boolean waitInfinte(String buttonID){
        synchronized (SLOPS_MAP){
            Long lastTipLong = SLOPS_MAP.put(buttonID,System.currentTimeMillis());
            return lastTipLong != null; //说明已经存储过这次单击事件
        }
    }
    public static  void cancel(String buttonID){
        synchronized (SLOPS_MAP){
            SLOPS_MAP.remove(buttonID);
        }
    }
}
