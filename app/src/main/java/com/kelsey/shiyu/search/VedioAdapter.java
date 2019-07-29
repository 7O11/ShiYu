package com.kelsey.shiyu.search;

import android.content.Context;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Picture;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
    ImageView collectIcon;
}

public class VedioAdapter extends BaseAdapter {

    private List<Item> vedioList;
    private View view;
    private Context mContext;
    private ViewHolder viewHolder;
//    private ImageView collectIcon;

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
    public View getView(final int position, View convertView, ViewGroup parent) {
        ImageView imageView = null;
        if (convertView == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.result_detail, null);
            viewHolder = new ViewHolder();
            viewHolder.vedioTitle = (TextView) view.findViewById(R.id.vedio_title);
            viewHolder.vedioInfo = (TextView)view.findViewById(R.id.vedio_info);
            viewHolder.vedioPicInfo = (ImageView)view.findViewById(R.id.vedio_pic);
            viewHolder.vedioScore = (TextView)view.findViewById(R.id.vedio_socre);
            viewHolder.vedioOrigin = (TextView)view.findViewById(R.id.vedio_origin);
//            viewHolder.collectIcon = (ImageView) view.findViewById(R.id.collect_icon);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        imageView = (ImageView)view.findViewById(R.id.uncollect_icon);
        imageView.setVisibility(View.VISIBLE);
        view.findViewById(R.id.collect_icon).setVisibility(View.GONE);
        viewHolder.vedioTitle.setText(vedioList.get(position).getVedioTitle());
        viewHolder.vedioInfo.setText(vedioList.get(position).getVedioInfo());
        ImageTools imageTools = new ImageTools();
        imageTools.getIcon(vedioList.get(position).getVedioPicInfo(), viewHolder);
        viewHolder.vedioScore.setText(vedioList.get(position).getVedioScore());
        viewHolder.vedioOrigin.setText(vedioList.get(position).getVedioOrigin());
//        viewHolder.collectIcon.setColorFilter(vedioList.get(position).getInitColor());
        final ImageView finalImageView = imageView;
//        finalImageView.setColorFilter(Color.rgb(179,174,174));
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if(viewHolder.collectIcon.getTag().equals(position)){
//                    viewHolder.collectIcon.setColorFilter(Color.RED);
//                }
//                int color;
                int icon_id = finalImageView.getId();
                if(icon_id==R.id.uncollect_icon){
//                    finalImageView.setVisibility(View.GONE);
                    finalImageView.setId(R.id.collect_icon);
                    finalImageView.setColorFilter(Color.RED);
//                    finalImageView.setVisibility(View.VISIBLE);
                } else {
                    finalImageView.setId(R.id.uncollect_icon);
                    finalImageView.setColorFilter(Color.rgb(179,174,174));
//                    finalImageView.setVisibility(View.VISIBLE);
                }
//                if(finalImageView.getColorFilter()==Color.RED){
//                    color = Color.rgb(179,174,174);
//                } else {
//                    color = Color.RED;
//                }
//                finalImageView.setColorFilter(color);
            }
        });
//        viewHolder.collectIcon.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v){
////                viewHolder.collectIcon.setImageResource(R.mipmap.pick);
////                viewHolder.collectIcon.setColorFilter(viewHolder.collectIcon.getColorFilter().equals(Color.RED)?Color.rgb(179,174,174):Color.RED);
////                int color = viewHolder.collectIcon.getColorFilter().equals(Color.RED)?Color.rgb(179,174,174):Color.RED;
////                iconClickListener.onReceiveCollectionData(position, viewHolder);
//                int color = vedioList.get(position).getNewColor(viewHolder.collectIcon.getColorFilter());
//                viewHolder.collectIcon.setColorFilter(color);
//            }
//        });

        return view;
    }

    private onIconClickListener iconClickListener;
    public interface onIconClickListener{
        void onReceiveCollectionData(int position, ViewHolder viewHolder);
    }

    public void setOnIconClickListener(onIconClickListener iconClickListener){
        this.iconClickListener = iconClickListener;
    }

}


