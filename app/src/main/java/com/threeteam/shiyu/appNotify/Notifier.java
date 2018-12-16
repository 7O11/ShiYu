package com.threeteam.shiyu.appNotify;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.threeteam.shiyu.web;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.threeteam.shiyu.web.*;

public class Notifier{
    //update flag
    private boolean flag = false;
    private boolean eventType;
    //mail format
    private static final String UPDATE_TITLE = "更新: ";
    private static final String UPDATE_CONTENT_LEFT = "最新状态：";
    private static final String UPDATE_CONTENT_RIGHT = "";
    //collect db
    private SQLiteDatabase collectdb;
    Notifier(SQLiteDatabase c){
        this.collectdb = c;
    }

    public void setEventType(boolean eventType) {
        this.eventType = eventType;
    }

    public boolean isUpdate() {
        return flag;
    }

    //
    //检查更新
    public void checkUpdate(){
        //站内信数据库 MailBoxActivity.maildb
        //查询线程
        Log.i("print_noti","check_update");
        NetworkThread thread;
        String latestInfo;
        //TODO: needs replace
        String[] urls = {"https://list.youku.com/show/id_z0f532f6fc8e34beb852e.html?spm=a2h0j.11185381.bpmodule-playpage-righttitle.5~H2~A"};
        web[] webs = {YOUKU};
        String[] infos = {""};
        String[] names = {"番名"};
        for(int i = 0; i<urls.length; i++){
            try{
                //check all updating drama
                thread = new NetworkThread(webs[i], urls[i]);
                thread.setEventType(eventType);
                thread.start();
                thread.join();
                latestInfo = thread.get_latestEpiInfo();
                if(!latestInfo.equals(infos[i])){
                    flag = true;
                    if(latestInfo.matches("\\d+\\-\\d+")){
                        latestInfo = "No."+String.valueOf(pickData(latestInfo));
                    }
                    ContentValues mail = new ContentValues();
                    mail.put("title", UPDATE_TITLE+"《"+names[i]+"》");
                    mail.put("content", UPDATE_CONTENT_LEFT+ latestInfo);
                    mail.put("read_flag", 1);
                    mail.put("receive_time", MailBoxActivity.sDateFormat.format(new Date()));
                    mail.put("web_id", webs[i].ordinal());
                    mail.put("url", urls[i]);
                    MailBoxActivity.maildb.insert(MailDatabaseHelper.MAILBOX, null, mail);
                }
            }catch (Exception e){
                //TODO: thread error , or db error
                //try again
            }
        }

    }
    private int pickData(String str){
        int len = str.length(),num = 0;
        char ch;
        for(int i = 0; i<len; i++){
            if(str.charAt(i)=='-'){
                do{
                    i++;
                    ch = str.charAt(i);
                    num = num*10+(ch - '0');
                }while(i < len);
                return num;
            }
        }
        return -1;
    }
}
