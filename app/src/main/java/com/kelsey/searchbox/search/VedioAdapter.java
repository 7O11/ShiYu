package com.kelsey.searchbox.search;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

class ViewHolder{
    TextView vedioTitle;
    TextView vedioInfo;
    ImageView vedioPicInfo;
    TextView vedioScore;
    TextView vedioOrigin;
}

public class VedioAdapter extends BaseAdapter {

    private List<Item> vedioList;
    private View view;
    private Context mContext;
    private ViewHolder viewHolder;

    public VedioAdapter(Context mContext, List<Item> newsList) {
        this.vedioList = newsList;
        this.mContext= mContext;
    }

    @Override
    public int getCount() {
        return vedioList.size();
    }

    @Override
    public Object getItem(int position) {
        return vedioList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.result_detail, null);
            viewHolder = new ViewHolder();
            viewHolder.vedioTitle = (TextView) view.findViewById(R.id.vedio_title);
            viewHolder.vedioInfo = (TextView)view.findViewById(R.id.vedio_info);
            viewHolder.vedioPicInfo = (ImageView)view.findViewById(R.id.vedio_pic);
            viewHolder.vedioScore = (TextView)view.findViewById(R.id.vedio_socre);
            viewHolder.vedioOrigin = (TextView)view.findViewById(R.id.vedio_origin);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.vedioTitle.setText(vedioList.get(position).getVedioTitle());
        viewHolder.vedioInfo.setText(vedioList.get(position).getVedioInfo());
//        Bitmap bitmap =ImageTools.getIcon(vedioList.get(position).getVedioPicInfo());
//        viewHolder.vedioPicInfo.setImageBitmap(bitmap);
        ImageTools imageTools = new ImageTools();
        imageTools.getIcon(vedioList.get(position).getVedioPicInfo(), viewHolder);
        viewHolder.vedioScore.setText(vedioList.get(position).getVedioScore());
        viewHolder.vedioOrigin.setText(vedioList.get(position).getVedioOrigin());

        return view;
    }


}


