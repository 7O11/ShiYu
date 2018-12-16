package com.kelsey.shiyu.appNotify;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MailDatabaseHelper extends SQLiteOpenHelper {
    public static final String MAILBOX = "mailbox";
    public static final String CREATE_MAIL_BOX = "create table "+MAILBOX+" ("
            + "title text, "
            + "content text , "  //标题和内容组成key
            + "read_flag integer, " //未读：1
            + "receive_time text, "    //消息接收时间,格式：2018-12-29 13:00
            + "web_id integer,"    //更新来源
            + "url text, "  //视频链接
            + "primary key(url, receive_time, content))";
    private Context mailContext;

    public MailDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context, name, factory, version);
        mailContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        //创建站内信数据库
        db.execSQL(CREATE_MAIL_BOX);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("drop table if exists mailbox");
        onCreate(db);
    }
}
