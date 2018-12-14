package com.threeteam.shiyu.appNotify;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.support.v7.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import static com.threeteam.shiyu.appNotify.MailDatabaseHelper.MAILBOX;
import static com.threeteam.shiyu.appNotify.web.ALL;

enum web{
    MANGGUOTV,
    TENGXUN,
    BILIBILI,
    YOUKU,
    AIQIYI,
    ALL
}
public class MailBoxActivity extends AppCompatActivity implements View.OnClickListener{
    //helper
    private MailDatabaseHelper mailHelper;
    public static SQLiteDatabase maildb;//设为public以便notifier可向其添加更新信息
    public static SimpleDateFormat sDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static List<MailItem> mails = new ArrayList<MailItem>();
    private static MailItemAdapter adapter;
    private static int WEB_ID;
    //view
    private Button add_mail;
    private ImageView backfromMail;
    private ImageView mailGarbage;
    private ListView listView;
    //handler
    private static Handler updateHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            fetchMail(WEB_ID);
            adapter.notifyDataSetChanged();
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mail_box);
        getSupportActionBar().hide();//隐藏标题栏，必须放到setContentView后
        mailHelper = new MailDatabaseHelper(MailBoxActivity.this, MAILBOX, null, 2);
        maildb = mailHelper.getWritableDatabase();
        //读取显示所有站内信
        maildb.delete(MAILBOX,null,null);
        initView();
        //TODO: 在主线程中开启service
        Intent intent = new Intent(this, NotifyService.class);
        startService(intent);
        //adapter
    }
    @Override
    public void onClick(View v)
    {
        Log.i("print_","a click");
        switch (v.getId()){
            //TODO: delete
            case R.id.add_mail:{
                addMail("这是测试,aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
                fetchMail(WEB_ID);
                adapter.notifyDataSetChanged();
                break;
            }
            //返回主界面
            case R.id.back_from_mail:{
                finish();
                break;
            }
            //清空站内信
            case R.id.mail_garbage:{
                AlertDialog dialog = new AlertDialog.Builder(MailBoxActivity.this)
                        .setMessage(R.string.clear_all_mail_dialog)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(WEB_ID == ALL.ordinal())
                                    maildb.delete(MAILBOX, null,null);
                                else
                                    maildb.delete(MAILBOX, "web_id = ?",new String[]{WEB_ID+""});
                                fetchMail(ALL.ordinal());
                                adapter.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("取消", null)
                        .create();
                dialog.show();
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLUE);
                break;
            }
            default:
                break;
            }
    }
    private void initView(){
        //get instance
        add_mail = (Button)findViewById(R.id.add_mail);
        backfromMail = (ImageView)findViewById(R.id.back_from_mail);
        mailGarbage = (ImageView)findViewById(R.id.mail_garbage) ;
        //initial
        fetchMail(ALL.ordinal());
        adapter = new MailItemAdapter(
                MailBoxActivity.this, R.layout.mail_item_layout, mails);
        listView = (ListView)findViewById(R.id.mail_list);
        listView.setAdapter(adapter);
        //set on click listener
        add_mail.setOnClickListener(this);
        backfromMail.setOnClickListener(this);
        mailGarbage.setOnClickListener(this);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                popMailMenu(view, position);
                return true;
            }
        });
        //实现控件mail的子事件：筛选消息 or 显示内容
        adapter.setOnMailItemClickListener(new MailItemAdapter.onMailItemListener(){
            //筛选列表并更新
            @Override
            public void onMailIconClick(View v, final int iconId){
                if(iconId == WEB_ID){
                    fetchMail(ALL.ordinal());
                }
                else{
                    fetchMail(iconId);
                }
                adapter.notifyDataSetChanged();
            }
            //显示内容
            @Override
            public void onMailSubjectClick(String title, String content){
                AlertDialog dialog = new AlertDialog.Builder(MailBoxActivity.this)
                    .setTitle(title)
                    .setMessage(content)
                    .create();
                    dialog.show();
                try {
                    Field mAlert = AlertDialog.class.getDeclaredField("mAlert");
                    mAlert.setAccessible(true);
                    Object mAlertController = mAlert.get(dialog);
                    Field mTitle = mAlertController.getClass().getDeclaredField("mTitleView");
                    mTitle.setAccessible(true);
                    TextView mTitleView = (TextView)mTitle.get(mAlertController) ;
                    mTitleView.setTextColor(Color.parseColor("#383838"));
                    Field mMessage = mAlertController.getClass().getDeclaredField("mMessageView");
                    mMessage.setAccessible(true);
                    TextView mMessageView = (TextView) mMessage.get(mAlertController);
                    mMessageView.setTextColor(Color.parseColor("#ADADAD"));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                }
                ContentValues values = new ContentValues();
                values.put("read_flag",0);
                maildb.update(MAILBOX,values,"title = ? and content=?",new String[]{title,content});
                fetchMail(WEB_ID);
                adapter.notifyDataSetChanged();
            }
            @Override
            public boolean onItemLongClick(View v, int position){
                popMailMenu(v, position);
                return true;
            }
        });
    }
    static int count = 0;
    public void addMail(String str){
        ContentValues mail = new ContentValues();
        mail.put("content",str);
        mail.put("title","标题"+String .valueOf(count++));
        mail.put("receive_time", sDateFormat.format(new Date()));
        mail.put("web_id", (int)(Math.random()*5));
        maildb.insert(MAILBOX, null, mail);
    }
    private static void fetchMail(int webid){
        WEB_ID = webid;
        mails.clear();
        MailItem mail;
        Cursor cursor = maildb.query(MAILBOX, null, null, null, null, null, null);
        try {
            if (cursor.moveToLast()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndex("web_id"));
                    if(webid == ALL.ordinal() || webid == id) {
                        String title = cursor.getString(cursor.getColumnIndex("title"));
                        String content = cursor.getString(cursor.getColumnIndex("content"));
                        int unread = cursor.getInt(cursor.getColumnIndex("read_flag"));
                        String time = cursor.getString(cursor.getColumnIndex("receive_time"));
                        //截取时间段
                        Date date = sDateFormat.parse(time);
                        long timelong = date.getTime();
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(date);
                        long minus = System.currentTimeMillis() - timelong;
                        //24h
                        if (minus < 24 * 60 * 60 * 1000) {
                            if(cal.get(Calendar.MINUTE)<10){
                                time = String.valueOf(cal.get(Calendar.HOUR_OF_DAY)) + ":0" + String.valueOf(cal.get(Calendar.MINUTE));
                            }
                            else{
                                time = String.valueOf(cal.get(Calendar.HOUR_OF_DAY)) + ":" + String.valueOf(cal.get(Calendar.MINUTE));
                            }
                            if(cal.get(Calendar.HOUR_OF_DAY)==0){
                                time = "0"+time;
                            }
                        }
                        //一年以内
                        else if (minus < 365 * 24 * 60 * 60 * 1000) {
                            time = String.valueOf(cal.get(Calendar.MONTH)) + "-" + String.valueOf(cal.get(Calendar.DATE));
                        } else {
                            time = String.valueOf(cal.get(Calendar.YEAR)) + "-" + String.valueOf(cal.get(Calendar.MONTH)) + "-" + String.valueOf(cal.get(Calendar.DATE));

                        }
                        mails.add(new MailItem(id, title, content, (unread == 1), time));
                    }
                } while (cursor.moveToPrevious());

            }
        }catch(Exception e){
            //TODO fetch mail
        }
        cursor.close();
        Log.i("print_",String.valueOf(mails.size()));
    }
    private void popMailMenu(View view, final int position){
        PopupMenu popupMenu = new PopupMenu(MailBoxActivity.this, view);
        popupMenu.getMenuInflater().inflate(R.menu.mail_popup_menu, popupMenu.getMenu());
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                // 控件每一个item的点击事件
                switch (item.getItemId()) {
                    case R.id.mail_renew: {
                        MailItem mItem = adapter.getItem(position);
                        //            Toast.makeText(MailBoxActivity.this, "设为未读", Toast.LENGTH_SHORT).show();
                        ContentValues values = new ContentValues();
                        values.put("read_flag",1);
                        maildb.update(MAILBOX,values,"title = ? and content=?",new String[]{mItem.getTitle(),mItem.getContent()});
                        fetchMail(WEB_ID);
                        adapter.notifyDataSetChanged();
                        break;
                    }
                    case R.id.mail_delete: {
                        MailItem mItem = adapter.getItem(position);
                        Toast.makeText(MailBoxActivity.this, "已删除", Toast.LENGTH_SHORT).show();
                        maildb.delete(MAILBOX, "title=? and content=?",new String[]{mItem.getTitle(),mItem.getContent()});
                        fetchMail(WEB_ID);
                        adapter.notifyDataSetChanged();
                        break;
                    }
                    default: {
                        break;
                    }
                }
                return true;
            }
        });
    }

    //实现静态方法，若查阅到新消息 && 当前activity为站内信，则及时更新列表
    public static void updateMailList(){
        Message message = new Message();
        updateHandler.sendMessage(message);
    }
}
