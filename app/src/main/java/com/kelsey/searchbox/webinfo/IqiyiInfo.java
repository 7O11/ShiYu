package com.kelsey.searchbox.webinfo;

import android.os.Handler;
import android.os.Message;

import com.kelsey.searchbox.search.Item;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.List;

public class IqiyiInfo extends Thread {
    public Message message;
    private String search_content;
    private List<Item> vList;
    private Handler vhandler;

    public IqiyiInfo(String content, List<Item> list, Handler handler){
        this.search_content = content;
        this.vList = list;
        this.vhandler = handler;
    }

    @Override
    public void run() {
        Document doc;
        Elements urlLinks;
        String icon_text;
        //爱奇艺
        String AiqiyiUrl = "https://so.iqiyi.com/so/q_" + search_content;

        try {
            doc = Jsoup.connect(AiqiyiUrl).get();

            urlLinks = doc.select("li.list_item");

            //for循环遍历获取首页所有视频
            for(int j = 0;j < urlLinks.size();j++){
                String title = urlLinks.get(j).select("h3.result_title").select("a").attr("title");
                String url = urlLinks.get(j).select("h3.result_title").select("a").attr("href");
                Document doc0 = Jsoup.connect(url).get();
                String desc = "简介: " + doc0.select("div.shortWordIntro-brief").select("span.briefIntroTxt").text();
                if(desc.equals("简介: ")){
                    desc = "简介: 无。";
                }
                //获取图片
                String pic = "https:" + urlLinks.get(j).select("a.figure.figure-180236").select("img").attr("src");
                String score = doc0.select("div.info-intro").select("span.effect-score").val();
                if(score.equals("")){
                    score = "无";
                }
                icon_text = urlLinks.get(j).select("span.source_detail").select("em").text();
                if(icon_text.contains("爱奇艺")){
                    String origin = "爱奇艺";
                    Item vedio = new Item(title,url,desc,pic,score,origin);
//                    vedioList.add(vedio);
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
