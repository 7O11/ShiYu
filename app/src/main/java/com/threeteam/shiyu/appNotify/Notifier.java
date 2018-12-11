package com.threeteam.shiyu.appNotify;

import android.app.Activity;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import static com.threeteam.shiyu.appNotify.MailDatabaseHelper.MAILBOX;
import static com.threeteam.shiyu.appNotify.web.*;

public class Notifier {
    //mail format
    private static final String UPDATE_TITLE = "更新通知";
    private static final String UPDATE_CONTENT_LEFT = "哇塞！您追的《";
    private static final String UPDATE_CONTENT_MID = "》又有更新啦，最新状态：【";
    private static final String UPDATE_CONTENT_RIGHT = "】，快去看看吧~";
    //检查更新
    private MailDatabaseHelper mailHelper;
    public Notifier(Activity activity){
        mailHelper = new MailDatabaseHelper(activity, MAILBOX, null, 1);
    }

    public void checkUpdate(SQLiteDatabase collectdb){
        //站内信数据库
        SQLiteDatabase maildb = mailHelper.getWritableDatabase();
        //查询线程
        NetworkThread thread;
        String latestInfo;
        //TODO: needs replace
        String[] urls = {"https://www.iqiyi.com/a_19rrh4i9vt.html"};
        web[] webs = {AIQIYI};
        String[] infos = {""};
        String[] names = {"番名"};
        for(int i = 0; i<urls.length; i++){
            try{
                //check all updating drama
                thread = new NetworkThread(webs[i], urls[i]);
                thread.start();
                thread.join();
                if((latestInfo = thread.get_latestEpiInfo()) != infos[i]){
                    if(latestInfo.matches("\\d+\\-\\d+")){
                        latestInfo = "No."+String.valueOf(pickData(latestInfo));
                    }
                    ContentValues mail = new ContentValues();
                    mail.put("title", UPDATE_TITLE);
                    mail.put("content", UPDATE_CONTENT_LEFT+names[i]+UPDATE_CONTENT_MID
                            + latestInfo + UPDATE_CONTENT_RIGHT);
                    mail.put("read_flag", 0);
                    mail.put("star", 0);
                    maildb.insert(MAILBOX , null, mail);
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
