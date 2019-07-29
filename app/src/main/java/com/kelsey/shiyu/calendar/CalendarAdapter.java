package com.kelsey.shiyu.calendar;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kelsey.shiyu.search.R;


public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.ViewHolder> {

    private final LayoutInflater layoutInflater;
    private final Context context;
    private String[] titles;

    public CalendarAdapter(Context context) {
        titles = context.getResources().getStringArray(R.array.titles);
        titles[0] = "无";
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public CalendarAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(layoutInflater.inflate(R.layout.item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.textView.setText(titles[position]);
    }

    @Override
    public int getItemCount() {
        return titles == null ? 0 : titles.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        ViewHolder(View view) {
            super(view);
            textView = (TextView) view.findViewById(R.id.text_view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("ViewHolder", "onClick--> position = " + getPosition());
                }
            });
        }
    }
}