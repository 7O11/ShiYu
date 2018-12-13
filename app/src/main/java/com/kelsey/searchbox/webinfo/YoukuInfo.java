package com.kelsey.searchbox.webinfo;

import android.os.Handler;
import android.os.Message;

import com.kelsey.searchbox.search.Item;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.List;

public class YoukuInfo extends Thread {
    public Message message;
    private String search_content;
    private List<Item> vList;
    private Handler vhandler;

    public YoukuInfo(String content, List<Item> list, Handler handler){
        this.search_content = content;
        this.vList = list;
        this.vhandler = handler;
    }

    @Override
    public void run() {
        Document doc;
        Elements urlLinks;
        String icon_text;
        //Youku
        String YoukuUrl = "https://so.youku.com/search_video/q_" + search_content;

        try {
            doc = Jsoup.connect(YoukuUrl).get();
            urlLinks = doc.select(".arrow-up.mod-filter");

            //for循环遍历获取首页所有视频
            for(int j = 0;j < urlLinks.size();j++){
                String title = urlLinks.get(j).select("h2.spc-lv-1").select("a").attr("title");
                String url = urlLinks.get(j).select("p.row-ellipsis").select("a").attr("href");
                Document doc0 = Jsoup.connect(url).get();
                String desc = doc0.select("li.p-row.p-intro").select("span.text").text();
//                        String desc = urlLinks.get(j).select("div.info_item.info_item_desc").select("span.desc_text").text().replace("\n","").replace(" ","");  //获取简介
                //获取图片
                String pic = "https:" + urlLinks.get(j).select("div.pack-cover").select("img").attr("src");
                String score = doc0.select("li.p-score").select("span.star-num").text();
                icon_text = urlLinks.get(j).select("span.source").text();
                if(icon_text.equals("播放源: 优酷")){
                    String origin = "优酷";
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
