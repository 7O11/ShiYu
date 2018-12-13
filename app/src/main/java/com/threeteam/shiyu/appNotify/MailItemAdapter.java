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

public class MailItemAdapter extends ArrayAdapter<MailItem> {
    private int resourceId;
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
            view.setTag(viewHolder);
        }else{
            view = convertView;
            viewHolder = (ViewHolder)view.getTag();
        }
        viewHolder.mailTitle.setText(mItem.getTitle());
        viewHolder.mailContent.setText(mItem.getContent());
        if(mItem.getUnread()){
            view.setBackgroundResource(R.mipmap.mail_unread_bg);
            viewHolder.webIcon.setBackgroundResource(R.mipmap.mail_id_unread_bg);
        }
        else{
            viewHolder.webIcon.setBackgroundResource(R.mipmap.mail_id_read_bg);
            view.setBackgroundResource(R.mipmap.mail_read_bg);

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
                mailItemListener.onMailSubjectClick(mItem.getTitle(),mItem.getContent());
            }
        });
        viewHolder.mailTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mailItemListener.onMailSubjectClick(mItem.getTitle(),mItem.getContent());
            }
        });
        return view;
    }
    //内部textView的监听接口
    public interface onMailItemListener{
        void onMailIconClick(View v, int position);
        void onMailSubjectClick(String title, String content);
    }
    private onMailItemListener mailItemListener;
    public void setOnMailItemClickListener(onMailItemListener mailItemListener){
        this.mailItemListener = mailItemListener;
    }
    class ViewHolder{
        ImageView webIcon;
        TextView mailTitle;
        TextView mailContent;
    }
}
class MailItem{
    private int icon;
    private String title;
    private String content;
    boolean unread;
    public MailItem(int id, String t, String c, boolean unread){
        this.icon = id;
        this.title = t;
        this.content = c;
        this.unread = unread;
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
}