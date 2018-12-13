package com.threeteam.shiyu.appNotify;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
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
import java.util.ArrayList;
import java.util.List;

import static com.threeteam.shiyu.appNotify.MailDatabaseHelper.MAILBOX;

enum web{
    MANGGUOTV,
    TENGXUN,
    BILIBILI,
    YOUKU,
    AIQIYI
}
public class MailBoxActivity extends AppCompatActivity implements View.OnClickListener{
    private MailDatabaseHelper mailHelper;
    private SQLiteDatabase maildb;
    private List<MailItem> mails = new ArrayList<MailItem>();
    MailItemAdapter adapter;
    //view
    private Button add_mail;
    private ImageView backfromMail;
    private ImageView mailGarbage;
    private ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mail_box);
        getSupportActionBar().hide();
        mailHelper = new MailDatabaseHelper(MailBoxActivity.this, MAILBOX, null, 1);
        maildb = mailHelper.getWritableDatabase();
        //读取显示所有站内信
        initView();
        //TODO: data
        //adapter
    }
    @Override
    public void onClick(View v){
        Log.i("print_","a click");
        switch (v.getId()){
            case R.id.add_mail:{
                addMail("这是测试,aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
                fetchMail();
                adapter.notifyDataSetChanged();
                break;
            }
            case R.id.back_from_mail:{
                finish();
                break;
            }
            case R.id.mail_garbage:{
                AlertDialog dialog = new AlertDialog.Builder(MailBoxActivity.this)
                        .setMessage(R.string.clear_all_mail_dialog)
                        .setPositiveButton("确定",null)
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
        maildb.delete(MAILBOX, null,null);
        //get instance
        add_mail = (Button)findViewById(R.id.add_mail);
        backfromMail = (ImageView)findViewById(R.id.back_from_mail);
        mailGarbage = (ImageView)findViewById(R.id.mail_garbage) ;
        //initial
        fetchMail();
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
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                PopupMenu popupMenu = new PopupMenu(MailBoxActivity.this, view);
                popupMenu.getMenuInflater().inflate(R.menu.mail_popup_menu, popupMenu.getMenu());
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        // 控件每一个item的点击事件
                        switch (item.getItemId()) {
                            case R.id.mail_renew: {
                                Toast.makeText(MailBoxActivity.this, "设为未读", Toast.LENGTH_SHORT).show();
                        /*        ContentValues values = new ContentValues();
                                values.put("read_flag",1);
                                maildb.update(MAILBOX,values,"id = ?",new String[]{String.valueOf(position)});
                                fetchMail();
                                adapter.notifyDataSetChanged();*/
                                break;
                            }
                            case R.id.mail_delete: {
                                Toast.makeText(MailBoxActivity.this, "已删除", Toast.LENGTH_SHORT).show();
                                break;
                            }
                            default: {
                                break;
                            }
                        }
                        return true;
                    }
                });
                return true;
            }
        });
        //重写控件mail的子事件
        adapter.setOnMailItemClickListener(new MailItemAdapter.onMailItemListener(){
            @Override
            public void onMailIconClick(View v, final int position){
                Log.i("print_pop", "ok");
                Toast.makeText(MailBoxActivity.this, "web icon", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onMailSubjectClick(String title, String content){
                Toast.makeText(MailBoxActivity.this, "content",Toast.LENGTH_SHORT).show();
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
            }
        });
    }
    public void addMail(String str){
        ContentValues mail = new ContentValues();
        mail.put("content",str);
        mail.put("title","标题");
        maildb.insert(MAILBOX, null, mail);
    }
    private void fetchMail(){
        mails.clear();
        MailItem mail;
        Cursor cursor = maildb.query(MAILBOX, null, null, null, null, null, null);
        if(cursor.moveToLast()){
            do{
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                String title = cursor.getString(cursor.getColumnIndex("title"));
                String content = cursor.getString(cursor.getColumnIndex("content"));
                int unread = cursor.getInt(cursor.getColumnIndex("read_flag"));
                Log.i("print_up",id+" "+unread);
                mails.add(new MailItem(id, title, content, (unread==1)));
            }while(cursor.moveToPrevious());

        }
        cursor.close();
        Log.i("print_",String.valueOf(mails.size()));
    }
}
