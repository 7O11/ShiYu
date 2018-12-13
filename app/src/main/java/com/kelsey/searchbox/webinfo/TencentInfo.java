package com.kelsey.searchbox.webinfo;

import android.os.Handler;
import android.os.Message;

import com.kelsey.searchbox.search.Item;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.List;

public class TencentInfo extends Thread {

    public Message message;
    private String search_content;
    private List<Item> vList;
    private Handler vhandler;

    public TencentInfo(String content, List<Item> list, Handler handler){
        this.search_content = content;
        this.vList = list;
        this.vhandler = handler;
    }

    @Override
    public void run() {
        Document doc;
        Elements urlLinks;
        //Tencent
        String TencentUrl = "https://v.qq.com/x/search/?q=" + search_content + "&stag=0&smartbox_ab=";

        try {
            doc = Jsoup.connect(TencentUrl).get();
            urlLinks = doc.select("div.result_item.result_item_v");
            String icon_text;

            //for循环遍历获取首页所有视频
            for(int j = 0;j < urlLinks.size();j++){
                String title = urlLinks.get(j).select("h2.result_title").select("em.hl").text();
                String url = urlLinks.get(j).select("h2.result_title").select("a").attr("href");
                Document doc0 = Jsoup.connect(url).get();
                String url0 = doc0.select("._playsrc").select(".video_btn").select("a").attr("href");
                if(!url0.equals("")){
                    url = url0;
                }
                String desc = "简介: " + doc0.select("div.video_desc").select("span.txt._desc_txt_lineHight").text();
                if(desc.equals("简介: ")){
                    desc += "无。";
                }
                //获取图片
                String pic = "https:" + urlLinks.get(j).select("a.figure.result_figure").select("img").attr("src");
                String score = urlLinks.get(j).select("span.result_score").text();   //解析来获取评分
                icon_text = urlLinks.get(j).select("span._cur_playsrc").select("span.icon_text").text();
                if(icon_text.equals("")||icon_text.equals("腾讯视频")){
                    String origin = "腾讯视频";
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
