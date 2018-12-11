package com.threeteam.shiyu.appNotify;

import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class NetworkThread extends Thread {
    private DoParse webAnalyzer;//html解析类
    private web webName;    //网站类别
    private String dstUrl;  //番剧详情页面url
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
            webAnalyzer.parseLatestInfo(webName, dstUrl);
        }catch (Exception e) {
            // TODO: thread exception
            Log.i("print_netWorkThread","error");
        }
    }
}
class DoParse{
    private String latestInfo;

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