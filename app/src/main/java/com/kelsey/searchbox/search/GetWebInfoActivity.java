package com.kelsey.searchbox.search;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.kelsey.searchbox.webinfo.ListElement;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GetWebInfoActivity extends AppCompatActivity {

    private List<Item> vedioList;
    private VedioAdapter adapter;
    private Handler handler;
    private ListView lv;
    private String search_content;
    Message msg = new Message();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_result);

        vedioList = new ArrayList<>();
        lv = (ListView) findViewById(R.id.vedio_item);
        Bundle bundle = getIntent().getExtras();

        if (bundle != null){
            //芒果tv
            search_content = bundle.getString("data");
        }
        getNews();
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.arg1 == 5){
                    adapter = new VedioAdapter(GetWebInfoActivity.this,vedioList);
                    lv.setAdapter(adapter);
                    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Item vedio = vedioList.get(position);
                            Intent intent = new Intent(GetWebInfoActivity.this,VedioDisplayActivity.class);
                            intent.putExtra("vedio_url",vedio.getVedioUrl());
                            startActivity(intent);
                        }
                    });
                }
            }
        };

    }

    private void getNews(){
        msg.arg1 = 0;

        //芒果tv
        new Thread(new Runnable() {
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
                    for (int j = 0; j < urlLinks.size(); j++) {
                        boolean isImago = false;
                        String title, url, desc, pic, score;
                        title = urlLinks.get(j).select("div.vari_pho").select("img").attr("alt");
                        if (title.equals("")) {
                            title = urlLinks.get(j).select("p.result-pic.result-pic-imgo").select("img").attr("alt");
                            if (title != "") isImago = true;
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
                        if (isImago) {
                            Item vedio = new Item(title, url, desc, pic, score, origin);
                            ListElement.addElement(vedioList, vedio);
                        }
                    }
                    msgCount();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
         }).start();

        //Bilibili
        new Thread(new Runnable() {
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
                        ListElement.addElement(vedioList,vedio);
                    }
                    msgCount();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        //Tencent
        new Thread(new Runnable() {
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
                            ListElement.addElement(vedioList,vedio);
                        }
                    }
                    msgCount();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        //Youku
        new Thread(new Runnable() {
            @Override
            public void run() {
                Document doc;
                Elements urlLinks;
                String icon_text, web_info, title, url, desc, pic, score;
                //Youku
                String YoukuUrl = "https://so.youku.com/search_video/q_" + search_content;

                try {
                    doc = Jsoup.connect(YoukuUrl).get();
                    web_info = doc.toString();
                    int is_find_item = web_info.indexOf("<div class=\\\"sk-mod\\\">");
                    while(is_find_item!=-1){
                        int title_start = web_info.indexOf("<a  target=\\\"_blank\\\"  data-spm=\\\"dtitle\\\" title=\\", is_find_item) + "<a  target=\\\"_blank\\\"  data-spm=\\\"dtitle\\\" title=\\".length() + 1;
                        int test0 = "<a  target=\\\"_blank\\\"  data-spm=\\\"dtitle\\\" title=\\".length();
                        int test1 = web_info.indexOf("<a  target=\\\"_blank\\\"  data-spm=\\\"dtitle\\\" title=\\", is_find_item);
                        int title_end = web_info.indexOf("\\", title_start);
                        title = web_info.substring(title_start, title_end);
                        int url_start = web_info.indexOf("data-spm=\\\"ddetail\\\"  class=\\\"row-end\\\" href=\\\"", is_find_item) + "data-spm=\\\"ddetail\\\"  class=\\\"row-end\\\" href=\\\"".length();
                        int url_end = web_info.indexOf("\"", url_start) - 1;
                        url = web_info.substring(url_start, url_end);
                        Document doc0 = Jsoup.connect(url).get();
                        desc = doc0.select("li.p-row.p-intro").select("span.text").text();
                        pic = doc0.select(".mod.mod-new").select(".p-thumb").select("img").attr("src");
                        int icon_start = web_info.indexOf("播放源:</label> 优酷", is_find_item);
                        score = doc0.select("li.p-score").select("span.star-num").text();
                        if(icon_start!=-1){
                            String origin = "优酷";
                            Item vedio = new Item(title,url,desc,pic,score,origin);
                            ListElement.addElement(vedioList,vedio);
                        }
                        is_find_item = web_info.indexOf("class=\"sk-mod\"", is_find_item);
                    }
                    msgCount();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        //Iqiyi
        new Thread(new Runnable() {
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
                            ListElement.addElement(vedioList,vedio);
                        }
                    }
                    msgCount();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    public void msgCount(){
        synchronized (msg){
            msg.arg1++;
            if(msg.arg1==5){
                handler.sendMessage(msg);
            }
        }
    }
}
