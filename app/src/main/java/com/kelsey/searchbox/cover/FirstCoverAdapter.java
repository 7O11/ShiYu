package com.kelsey.searchbox.cover;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kelsey.searchbox.search.ImageTools;
import com.kelsey.searchbox.search.R;

import java.util.List;

class ViewHolder{
    TextView vedioTitle;
    TextView vedioInfo;
    ImageView vedioPicInfo;
    TextView vedioScore;
    TextView vedioOrigin;
}

public class FirstCoverAdapter extends BaseAdapter {
    private View view;
    private ViewHolder viewHolder;
    private Context mContext;
    private Fragment fragment;

    public FirstCoverAdapter(Context mContext) {
        this.mContext= mContext;
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.search_style, null);
//            view =
//            view = LayoutInflater.from(mContext).inflate(R.layout.result_detail, null);
//            viewHolder = new ViewHolder();
//            viewHolder.vedioTitle = (TextView) view.findViewById(R.id.vedio_title);
//            viewHolder.vedioInfo = (TextView)view.findViewById(R.id.vedio_info);
//            viewHolder.vedioPicInfo = (ImageView)view.findViewById(R.id.vedio_pic);
//            viewHolder.vedioScore = (TextView)view.findViewById(R.id.vedio_socre);
//            viewHolder.vedioOrigin = (TextView)view.findViewById(R.id.vedio_origin);
//            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        return view;
    }
}
