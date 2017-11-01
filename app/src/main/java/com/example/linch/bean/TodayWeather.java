package com.example.linch.bean;

/**
 * Created by linch on 2017/10/12.
 */

public class TodayWeather {

    private String city;
    private String updatetime;
    private String wendu;
    private String shidu;
    private String pm25;
    private String quality;
    private String fengxiang;
    private String fengli;
    private String date;
    private String high;
    private String low;
    private String type;

    public TodayWeather(){
        this.city = "N/A";
        this.updatetime = "N/A";
        this.wendu = "N/A";
        this.shidu = "N/A";
        this.pm25 = "0";
        this.quality = "N/A";
        this.fengxiang = "N/A";
        this.fengli = "N/A";
        this.date = "N/A";
        this.high = "N/A N/A";
        this.low = "N/A N/A";
        this.type = "N/A";
    }
    public String getCity() {
        return city;
    }

    public void setCity(String city) {

        this.city = city != null ? city : "N/A" ;
    }


    public String getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(String updatetime) {
        this.updatetime = updatetime  != null ? updatetime : "N/A";
    }


    public String getWendu() {
        return wendu;
    }

    public void setWendu(String wendu) {

        this.wendu = wendu != null ? wendu : "N/A";
    }
    public String getShidu() {
        return shidu;
    }

    public void setShidu(String shidu) {

        this.shidu = shidu != null ? shidu : "N/A";
    }


    public String getPm25() {
        return pm25;
    }

    public void setPm25(String pm25) {

        this.pm25 = pm25 != null ? pm25 : "N/A";
    }


    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {

        this.quality = quality != null ? quality : "无";
    }


    public String getFengxiang() {
        return fengxiang;
    }

    public void setFengxiang(String fengxiang) {

        this.fengxiang = fengxiang != null ? fengxiang : "N/A";
    }


    public String getFengli() {
        return fengli;
    }

    public void setFengli(String fengli) {

        this.fengli = fengli != null ? fengli : "N/A";
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {

        this.date = date != null ? date : "N/A";
    }


    public String getHigh() {
        return high;
    }

    public void setHigh(String high) {

        this.high = high != null ? high : "N/A N/A";
    }


    public String getLow() {
        return low;
    }

    public void setLow(String low) {

        this.low = low  != null ? low : "N/A";
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {

        this.type = type != null ? type : "N/A";
    }

    @Override
    public String toString(){
        return "TodayWeather{"+
                "city='" + city +  '\'' +
                "updatetime='" + updatetime +  '\'' +
                "wendu='" + wendu +  '\'' +
                "shidu='" + shidu +  '\'' +
                "pm25='" + pm25 +  '\'' +
                "quality='" + quality +  '\'' +
                "fengxiang='" + fengxiang +  '\'' +
                "fengli='" + fengli +  '\'' +
                "date='" + date +  '\'' +
                "high='" + high +  '\'' +
                "low='" + low +  '\'' +
                "type='" + type +  '\'' ;
    }

}
