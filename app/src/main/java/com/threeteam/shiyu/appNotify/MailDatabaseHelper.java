package com.threeteam.shiyu.appNotify;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MailDatabaseHelper extends SQLiteOpenHelper {
    public static final String CREATE_MAIL_BOX = "create table mailbox ("
            + "id integer primary key autoincrement, "
            + "title text, "
            + "content text, "
            + "read_flag integer, "
            + "star integer)";
    private Context mailContext;

    public MailDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context, name, factory, version);
        mailContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(CREATE_MAIL_BOX);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
    }
}
