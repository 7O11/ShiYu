package com.kelsey.searchbox.webinfo;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.kelsey.searchbox.search.GetWebInfoActivity;
import com.kelsey.searchbox.search.Item;
import com.kelsey.searchbox.search.R;
import com.kelsey.searchbox.search.VedioAdapter;
import com.kelsey.searchbox.search.VedioDisplayActivity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.List;

import static android.support.v4.content.ContextCompat.startActivity;

public class BilibiliInfo extends Thread{
    public Message message;
    private String search_content;
    private List<Item> vList;
    private Handler vhandler;

    public BilibiliInfo(String content, List<Item> list, Handler handler){
        this.search_content = content;
        this.vList = list;
        this.vhandler = handler;
    }

    @Override
    public void run() {
        Document doc;
        Elements urlLinks;
        //bilibili
        String BilibiliUrl = "https://search.bilibili.com/all?keyword=" + search_content + "&from_source=banner_search";

        try {
            doc = Jsoup.connect(BilibiliUrl).get();
            urlLinks = doc.select("li.synthetical");    //解析来获取标题与链接地址

            //for循环遍历获取首页所有视频
            for(int j = 0;j < urlLinks.size();j++){
                String title = urlLinks.get(j).select("a").attr("title");
                String url = "https:" + urlLinks.get(j).select("div.headline").select("a").attr("href");
                String desc = "简介: " + urlLinks.get(j).select("div.des.info").attr("title").replace("\n","").replace(" ","");  //获取简介
                String vedio_url = "https:" + urlLinks.get(j).select("a").attr("href");
                Document doc0 = Jsoup.connect(vedio_url).get();
                //获取图片
                String pic = "https:" + doc0.select("div.bangumi-info-wrapper.report-wrap-module.report-scroll-module").select("div.info-cover").select("img").attr("src");
                String score = urlLinks.get(j).select("div.score-num").text();   //解析来获取评分
                String origin = "Bilibili";
                Item vedio = new Item(title,url,desc,pic,score,origin);
//            vedioList.add(vedio);
                ListElement.addElement(vList,vedio);
            }
            message.arg1++;
            vhandler.sendMessage(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
