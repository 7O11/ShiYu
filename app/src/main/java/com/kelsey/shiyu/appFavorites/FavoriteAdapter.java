package com.kelsey.shiyu.appFavorites;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kelsey.shiyu.appNotify.R;

import java.util.List;

public class FavoriteAdapter extends ArrayAdapter {
    private int resourceId;
    public FavoriteAdapter(Context context, int textViewResourceId, List<FavItem> objects){
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
        Log.i("print_adapter", "create adapter");
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent){
        final FavItem fItem = (FavItem)getItem(position);
        Log.i("print_adapter", "get item"+position+" done");
        final View view;
        final ViewHolder viewHolder;

        if(convertView == null){
            view = LayoutInflater.from(getContext()).inflate(resourceId, null);
            viewHolder = new ViewHolder();
            viewHolder.dramaGraphic = (ImageView) view.findViewById(R.id.drama_view);
            viewHolder.name = (TextView)view.findViewById(R.id.drama_title);
            viewHolder.content = (TextView)view.findViewById(R.id.drama_content);
            viewHolder.from = (TextView)view.findViewById(R.id.drama_from) ;
            viewHolder.btnDelete = (Button)view.findViewById(R.id.btnDelete);
            viewHolder.favItem = (RelativeLayout)view.findViewById(R.id.fav_item);
            view.setTag(viewHolder);
        }else{
            view = convertView;
            viewHolder = (ViewHolder)view.getTag();
        }
        new ImageTools().getIcon("http://r1.ykimg.com/05160000594B5E6BADBA1F1B5C0C6DAA", viewHolder);
  /*      viewHolder.name.setText(fItem.getName());
        viewHolder.content.setText(fItem.getContent());
        viewHolder.from.setText(fItem.getFrom());*/
        //事件点击
        viewHolder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                favItemListener.onDeleteFavoriteClick(position);
            }
        });
        viewHolder.favItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                favItemListener.onFavSubjectClick(fItem.getUrl());
            }
        });
        return view;
    }
    //内部textView的监听接口
    public interface onFavItemListener{
        void onDeleteFavoriteClick(int position);
        void onFavSubjectClick(String url);
    }
    private onFavItemListener favItemListener;
    public void setOnFavItemClickListener(onFavItemListener favItemListener){
        this.favItemListener = favItemListener;
    }
}
class ViewHolder{
    ImageView dramaGraphic;
    TextView name;
    TextView content;
    TextView from;
    Button btnDelete;
    RelativeLayout favItem;
}
class FavItem {
    private String name;//番名
    private String content;//简介
    private String from;    //来源
    private String graphic;//图片url
    private String url; //播放地址（详情页）
    public FavItem(String name, String c, String f, String g, String u){
        this.name = name;
        this.content = c;
        this.from = f;
        this.graphic = g;
        this.url = u;
    }
    public String getName() {
        return name;
    }
    public String getFrom() {
        return from;
    }
    public String getGraphic() {
        return graphic;
    }
    public String getUrl() {
        return url;
    }
    public String getContent(){
        return this.content;
    }
}