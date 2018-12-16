package com.threeteam.shiyu.appNotify;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import static com.threeteam.shiyu.web.*;

public class MailItemAdapter extends ArrayAdapter<MailItem> {private int resourceId;
    public MailItemAdapter(Context context, int textViewResourceId,
                           List<MailItem> objects){
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;

        Log.i("print_adapter", "create adapter");
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent){
        final MailItem mItem = getItem(position);
        Log.i("print_adapter", "get item"+position+" done");
        final View view;
        ViewHolder viewHolder;

        if(convertView == null){
            view = LayoutInflater.from(getContext()).inflate(resourceId, null);
            viewHolder = new ViewHolder();
            viewHolder.webIcon = (ImageView) view.findViewById(R.id.web_icon);
            viewHolder.mailTitle = (TextView)view.findViewById(R.id.mail_title);
            viewHolder.mailContent = (TextView)view.findViewById(R.id.mail_content);
            viewHolder.mailTime = (TextView)view.findViewById(R.id.mail_receive_time) ;
            view.setTag(viewHolder);
        }else{
            view = convertView;
            viewHolder = (ViewHolder)view.getTag();
        }
        viewHolder.mailTitle.setText(mItem.getTitle());
        viewHolder.mailContent.setText(mItem.getContent());
        viewHolder.mailTime.setText(mItem.getTime());
        if(mItem.getUnread()){
            viewHolder.webIcon.setBackgroundResource(id2web(mItem.getIconId(), true));
        }
        else{
            viewHolder.webIcon.setBackgroundResource(id2web(mItem.getIconId(), false));
        }
        //点击icon，触发列表筛选更新操作
        viewHolder.webIcon.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                mailItemListener.onMailIconClick(v, mItem.getIconId());
            }
        });
        //点击信息，呈现消息窗口
        viewHolder.mailContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mailItemListener.onMailSubjectClick(mItem.getTitle(),mItem.getContent(), mItem.getUrl());
            }
        });
        viewHolder.mailTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mailItemListener.onMailSubjectClick(mItem.getTitle(),mItem.getContent(), mItem.getUrl());
            }
        });
        //长按响应
        viewHolder.webIcon.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View v){
                return mailItemListener.onItemLongClick(v, position);
            }
        });
        viewHolder.mailTitle.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View v){
                mailItemListener.onItemLongClick(v, position);
                return true;
            }
        });
        viewHolder.mailContent.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View v){
                mailItemListener.onItemLongClick(v, position);
                return true;
            }
        });
        return view;
    }
    //内部textView的监听接口
    public interface onMailItemListener{
        void onMailIconClick(View v, int iconId);
        void onMailSubjectClick(String title, String content, String url);
        boolean onItemLongClick(View v, int position);
    }
    private onMailItemListener mailItemListener;
    public void setOnMailItemClickListener(onMailItemListener mailItemListener){
        this.mailItemListener = mailItemListener;
    }
    private int id2web(int id, boolean unread){
        if(unread){
            if(id == MANGGUOTV.ordinal()) return R.mipmap.mangguotv_icon_;
            else if(id == YOUKU.ordinal()) return R.mipmap.youku_icon_;
            else if(id == TENGXUN.ordinal()) return R.mipmap.tengxun_icon_;
            else if(id == BILIBILI.ordinal()) return R.mipmap.bilibili_icon_;
            else return R.mipmap.aiqiyi_icon_;
        }
        if(id == MANGGUOTV.ordinal()) return R.mipmap.mangguotv_icon;
        else if(id == YOUKU.ordinal()) return R.mipmap.youku_icon;
        else if(id == TENGXUN.ordinal()) return R.mipmap.tengxun_icon;
        else if(id == BILIBILI.ordinal()) return R.mipmap.bilibili_icon;
        else return R.mipmap.aiqiyi_icon;
    }
    class ViewHolder{
        ImageView webIcon;
        TextView mailTitle;
        TextView mailContent;
        TextView mailTime;
    }
}
class MailItem{
    private int icon;
    private String title;
    private String content;
    private boolean unread;
    private String time;
    private String url;
    public MailItem(int id, String t, String c, boolean unread, String time, String url){
        this.icon = id;
        this.title = t;
        this.content = c;
        this.unread = unread;
        this.time = time;
        this.url = url;
    }
    public int getIconId(){
        return this.icon;
    }
    public String getTitle(){
        return this.title;
    }
    public String getContent(){
        return this.content;
    }
    public boolean getUnread(){
        return this.unread;
    }
    public String getTime(){
        return this.time;
    }

    public String getUrl() {
        return url;
    }
}