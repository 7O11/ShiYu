package com.kelsey.shiyu.search;

public class Item {
    private String vedioTitle;   //视频标题
    private String vedioUrl;     //视频链接地址
    private String vedioInfo;    //视频概要
    private String vedioPicInfo;    //视频宣传图
    private String vedioScore;  //视频评分
    private String vedioOrigin; //视频来源

    public Item(String title, String url, String info, String picInfo, String score, String origin) {
        this.vedioTitle = title;
        this.vedioUrl = url;
        this.vedioInfo = info;
        this.vedioPicInfo = picInfo;
        this.vedioScore = score;
        this.vedioOrigin = origin;
    }

    public String getVedioInfo() {
        return vedioInfo;
    }

    public void setVedioInfo(String info) {
        this.vedioInfo = info;
    }

    public String getVedioTitle() {
        return vedioTitle;
    }

    public void setVedioTitle(String title) {
        this.vedioTitle = title;
    }

    public String getVedioUrl() {
        return vedioUrl;
    }

    public String getVedioPicInfo(){
        return this.vedioPicInfo;
    }

    public String getVedioScore(){
        return this.vedioScore;
    }

    public String getVedioOrigin(){
        return this.vedioOrigin;
    }

    public void setVedioUrl(String url) {
        this.vedioUrl = url;
    }


}
