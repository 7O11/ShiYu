package com.kelsey.searchbox.webinfo;

import android.os.Handler;
import android.os.Message;

import com.kelsey.searchbox.search.Item;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.List;

public class MangguotvInfo extends Thread{

    public Message message;
    private String search_content;
    private List<Item> vList;
    private Handler vhandler;

    public MangguotvInfo(String content, List<Item> list, Handler handler){
        this.search_content = content;
        this.vList = list;
        this.vhandler = handler;
    }

    @Override
    public void run() {
        //芒果tv
        String MangguoUrl = "https://so.mgtv.com/so/k-" + search_content;

        //获取网页首页数据
        Document doc = null;
        Elements urlLinks;
        try {
            doc = Jsoup.connect(MangguoUrl).get();
            urlLinks = doc.select("div.so-result-info.search-television.clearfix");    //解析来获取标题与链接地址

            //for循环遍历获取首页所有视频
            for(int j = 0;j < urlLinks.size();j++){
                boolean isImago = false;
                String title, url, desc, pic, score;
                title = urlLinks.get(j).select("div.vari_pho").select("img").attr("alt");
                if(title.equals("")){
                    title = urlLinks.get(j).select("p.result-pic.result-pic-imgo").select("img").attr("alt");
                    if(title!="")   isImago = true;
                    url = "https:" + urlLinks.get(j).select("p.result-til").select("a").attr("href");
                    desc = urlLinks.get(j).select("div.desc_text").text();
                    pic = "https:" + urlLinks.get(j).select("p.result-pic.result-pic-imgo").select("img").attr("src");
                    score = urlLinks.get(j).select("p.result-til").select("span.score").text();
                } else {
                    url = "https:" + urlLinks.get(j).select("div.tele_hd").select("a").attr("href");
                    desc = urlLinks.get(j).select("div.tele_item_des").select("span.desc_text").text();  //获取简介
                    pic = "https:" + urlLinks.get(j).select("div.vari_pho").select("img").attr("src");
                    score = urlLinks.get(j).select("div.tele_hd").select("span.score").text();   //解析来获取评分
                }
                String origin = "芒果TV";
                if(isImago){
                    Item vedio = new Item(title,url,desc,pic,score,origin);
//                vedioList.add(vedio);
                    ListElement.addElement(vList,vedio);
                }
            }
            message.arg1++;
            vhandler.sendMessage(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
