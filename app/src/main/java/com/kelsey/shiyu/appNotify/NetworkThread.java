package com.kelsey.shiyu.appNotify;

import android.util.Log;

import com.kelsey.shiyu.web;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class NetworkThread extends Thread {
    private DoParse webAnalyzer;//html解析类
    private web webName;    //网站类别
    private String dstUrl;  //番剧详情页面url
    private boolean eventType;  //操作类型。false代表检查更新，true代表收藏番剧。

    public void setEventType(boolean eventType) {
        this.eventType = eventType;
    }

    public NetworkThread(web webName, String dstUrl){
        webAnalyzer = new DoParse();
        this.webName = webName;
        this.dstUrl = dstUrl;
    }
    public String get_latestEpiInfo(){
        return webAnalyzer.getLatestInfo();
    }
    @Override
    public void run(){
        try{
            if(eventType){
                webAnalyzer.parseUpdateInfo(webName,dstUrl);
            }
            else webAnalyzer.parseLatestInfo(webName, dstUrl);
        }catch (Exception e) {
            // TODO: thread exception
            Log.i("print_netWorkThread","error");
        }
    }
}
class DoParse{
    private String latestInfo;
    private String updateInfo;
    int[] daynumVIP = new int[10];
    int[] daynum = new int[10];
    String specTime;
    String VIPInfo = "";
    String normalInfo = "";
    private boolean vipFlag = false;

    public DoParse(){
        this.latestInfo = "";
    }
    //get latest episode information
    public String getLatestInfo(){
        return this.latestInfo;
    }
    //get latest number or episode name
    public void parseLatestInfo(web webName, String dstUrl){
        latestInfo = "";
        String key = "";
        try{
            Document doc = Jsoup.connect(dstUrl).get();
            Element epiInfo;
            String html = doc.toString();
            switch(webName){
                case MANGGUOTV:{
                    epiInfo = doc.getElementsByClass("status").first();
                    if(epiInfo != null){
                        latestInfo = epiInfo.text();
                    }
                    break;
                 }
                case TENGXUN:{
                    if(html.contains("range")){
                        key = "range";
                        latestInfo = pickRangeInfo(html, html.indexOf(key), key, true);
                    }
                    else{
                        key = "Range";
                        latestInfo = pickRangeInfo(html, html.indexOf(key), key, true);
                    }
                    //<!--[if lte IE 6 ]> <html class="ie ie6 lte_ie7 lte_ie8 lte_ie9" lang="zh-CN"> <![endif]-->
                    if(latestInfo.charAt(0) == 'i'){
                        epiInfo = doc.getElementsByClass("figure").first();
                        if(epiInfo != null){
                            latestInfo = epiInfo.attr("title");
                        }
                        else latestInfo = "";
                    }
                    break;
                }
                case BILIBILI:{
                    if(html.contains("\"index\"")){
                        key = "\"index\"";
                        latestInfo = "["+pickRangeInfo(html, html.lastIndexOf(key), key, false) + "]";
                    }
                    if(html.contains("\"index_title\"")){
                        key = "\"index_title\"";
                        latestInfo += pickRangeInfo(html, html.lastIndexOf(key), key, false);
                    }
                    break;
                }
                case YOUKU:{
                    epiInfo = doc.getElementsByClass("p-row p-renew").first();
                    if(epiInfo != null){
                        latestInfo = epiInfo.text();
                    }
                    break;
                }
                case AIQIYI:{
                    epiInfo = doc.getElementsByClass("title-update-progress").first();
                    if(epiInfo == null){
                        epiInfo = doc.getElementsByClass("item no").first();
                    }
                    if(epiInfo != null){
                        latestInfo = epiInfo.text();
                    }
                    break;
                }
                default:{
                    //TODO: not a legal website
                    break;
                }
            }
        }catch (Exception e){
            //TODO: addName error
            latestInfo = "";
        }
    }
    public void parseUpdateInfo(web webName, String dstUrl){
        Log.i("print_star", "parse update "+webName.ordinal());
        parseLatestInfo(webName,dstUrl);
        updateInfo = "";
        vipFlag = false;
        daynum = new int[10];
        try{
            switch (webName){
                case MANGGUOTV:{
                    break;
                }
                case TENGXUN:{
                    Document doc = Jsoup.connect(dstUrl).get();
                    Element type_item = doc.getElementsByClass("type_txt").last();
                    if(type_item != null){
                        parseAIQIYI(type_item.text());
                    }
                    break;
                }
                case BILIBILI:{
                    Document doc = Jsoup.connect(dstUrl).get();
                    Element media_info = doc.getElementsByClass("media-info-time").first();
             //       Log.i("print_star", media_info.text());
                    if(media_info!=null){
                        parseAIQIYI(media_info.text());
                    }
                    break;
                }
                case YOUKU:{
                    Log.i("print_star", latestInfo);
                    if(latestInfo.contains("（")) {
                        updateInfo = latestInfo.substring(latestInfo.indexOf('（'));
                        parseYOUKU(updateInfo);
                    }
                    break;
                }
                case AIQIYI:{
                    Document doc = Jsoup.connect(dstUrl).get();
                    Element way = doc.getElementsByClass("update-way").first();
                    if(way == null) way = doc.getElementsByClass("episodeIntro-update").first();
                    if(way != null){
                        parseAIQIYI(way.text());
                    }
                    break;
                }
                default:{
                    break;
                }
             }
        }catch (Exception e){
            //TODO:
        }
        for(int i = 1;i<=7;i++){
    //        Log.i("print_star","day["+i+"] "+daynumVIP[i]+" "+daynum[i]);
            if(vipFlag) VIPInfo += (daynumVIP[i]);
            normalInfo += (daynum[i]);
        }
    }
    private void parseAIQIYI(String str){
        //eg: 每日24:00更新三集 //每周六22:00更新一集;VIP会员抢先看一周 //周日至周四24点2集,周五至周六24点1集
        //每周日00:30更新1集，会员抢先看1集 //每周一至周四24:00更新2集，非VIP次日转免
        vipFlag = false;
        Log.i("print_star","parse AIQIYI:"+ str);
        String[] strs = str.split(";|，|,|；");
        for(int i = 0;i<strs.length;i++){
            itemParse(strs[i], 1);
        }
        pickSpecTime(str);
    }
    private int findNum(String str){
        int num = 0;
        if(str.matches(".*?[一二三四五六七八九123456789][集期版]")){
           if(str.contains("集")) num = character2int(str.charAt(str.indexOf('集')-1));
           else if(str.contains("期")) num = character2int(str.charAt(str.indexOf('期')-1));
           else num = character2int(str.charAt(str.indexOf('版')-1));
        }
        else num = 1;
        return num;
    }
    private void itemParse(String str, int type){
        int[] dntmp = new int[10];
        if(str.matches(".*?周[一二三四五六日][至到]周[一二三四五六日].*")){
            Log.i("print_star","aaaaaaa");
            char c1,c2;
            int num = findNum(str);
            c1 = str.charAt(str.indexOf('周')+1);
            c2 = str.charAt(str.lastIndexOf('周')+1);
            for(int i = character2int(c1); i!=character2int(c2);){
                dntmp[i] = num;
                Log.i("print_star", i+"");
                i = ((i+1)%7==0?7:(i+1)%7);
            }
            dntmp[character2int(c2)] = num;
        }
        else if(str.matches(".*?周[一二三四五六日]((、周)?[一二三四五六日])*.*")){
            Log.i("print_star","bbbbbbbb");
            int num = findNum(str);
            for(int i = str.indexOf('周'); i<str.length();i++){
                if(str.charAt(i) == '一' || str.charAt(i) == '二' || str.charAt(i) == '三' || str.charAt(i) == '四' || str.charAt(i) == '五' || str.charAt(i) == '六' || str.charAt(i) == '日' || str.charAt(i) == '一'){
                    int tmp = character2int(str.charAt(i));
                    dntmp[tmp] = num;
                }
            }
        }
        else if(str.matches(".*?(周[一二三四五六日].*?((更新?)|播出)\\d集)+")){
            Log.i("print_star","cccccc");
            int num = findNum(str);
            for(int i = str.indexOf('周'); i<str.length();i++){
                if(str.charAt(i) == '周'){
                    int tmp = character2int(str.charAt(i+1));
                    dntmp[tmp] = num;
                    i+=3;
                }
            }
        }
        else if(str.contains("每天") || str.contains("每日")){
            Log.i("print_star","dddddd");
            int num = findNum(str);
            for(int i = 1;i<=7;i++){
                dntmp[i] = num;
            }
        }
        if(type == 0){
            for(int i = 1; i<=7;i++){
                if(dntmp[i]!=0) daynumVIP[i] = dntmp[i];
            }
        }
        else{
            for(int i = 1; i<=7;i++) {
                if (dntmp[i] != 0) daynum[i] = dntmp[i];
            }
        }

    }
    private void parseYOUKU(String str){
        if(str.contains("会员")){
            vipFlag = true;
            String[] strs = str.split("，");
            //会员 eg:VIP会员每周一到周五20：30同步TVB更新1集//会员12点周一更4集周五更2集，周一至周六免费1集//会员周二、周五20点更4集，非会员周一至周五更1集
            // 12月3日会员每周一至周五21：30各更新1集//每天24点同步卫视更新//12月11日起，每天0：00上线2集
            //会员↓
            itemParse(strs[0], 0);
            itemParse(strs[1], 1);
        }
        else itemParse(str, 1);
        Log.i("print_star","star youku:"+str);
        //具体时间段
        pickSpecTime(str);
    }
    private void pickSpecTime(String str){
        int index;
        if(str.contains("：") || str.contains("点") || str.contains(":")){
            int after;
            if(str.contains("：")){ after = 3;index = str.indexOf('：');}
            else if(str.contains(":")){ after = 3;index = str.indexOf(':');}
            else {after = 1;index = str.indexOf('点');}

            Log.i("print_star","okkkkk:"+index+" "+after);
            if(Character.isDigit(str.charAt(index-2))){
                specTime = str.substring(index-2, index+after);
            }
            else if(Character.isDigit(str.charAt(index-1))){
                specTime = str.substring(index-1, index+after);
            }
        }
    }
    private int character2int(char c){
        if(c == '一') return 1;
        if(c == '二') return 2;
        if(c == '三') return 3;
        if(c == '四') return 4;
        if(c == '五') return 5;
        if(c == '六') return 6;
        if(c == '日') return 7;
        if(Character.isDigit(c)) return c-'0';
        return 0;
    }
    private String pickRangeInfo(String html, int index, String key, boolean rec){
        String info = "";
        int len = html.length();
        for(int i = index+key.length(); i<len; i++){
            if(html.charAt(i) == '\''){
                do{
                    i++;
                    info += html.charAt(i);
                }while(i != '\'' && i < len);
                break;
            }
            else if(html.charAt(i) == '\"'){
                do{
                    i ++;
                    info += html.charAt(i);
                }while(i != '\"' && i < len);
                break;
            }
        }
        if(!rec) return info;
        int nextIndex;
        if((nextIndex = html.indexOf(key, index)) != -1){
            String tmpInfo = pickRangeInfo(html, nextIndex, key, rec);
            if(Character.isDigit(tmpInfo.charAt(1))){
                info = tmpInfo;
            }
        }
        return info;
    }
}