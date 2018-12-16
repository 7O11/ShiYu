package com.kelsey.searchbox.bottombar;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kelsey.searchbox.library.cache.HistoryCache;
import com.kelsey.searchbox.library.callback.onSearchCallBackListener;
import com.kelsey.searchbox.library.widget.SearchListLayout;
import com.kelsey.searchbox.search.GetWebInfoActivity;
import com.kelsey.searchbox.search.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SearchFragment extends Fragment {
    private SearchListLayout searchLayout;

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        searchLayout = (SearchListLayout)getActivity().findViewById(R.id.searchlayout);
        initData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.search_style, container, false);
    }

    private void initData() {
        List<String> skills = HistoryCache.toArray(getActivity().getApplicationContext());
        String shareHotData ="熊出没之探险日记,三月的狮子,蜂蜜与四叶草,明星大侦探,妖神记,熊鼠一家,我是江小白,百变小樱";
        List<String> skillHots = Arrays.asList(shareHotData.split(","));
        searchLayout.initData(skills, skillHots, new onSearchCallBackListener() {
            @Override
            public void Search(String str) {
                //进行或联网搜索
                Log.e("点击",str);
                Bundle bundle = new Bundle();
                bundle.putString("data",str);
                startActivity(GetWebInfoActivity.class, bundle);
//                startActivity(CalendarActivity.class, bundle);
            }
            @Override
            public void Back() {
                getActivity().finish();
            }

            @Override
            public void ClearOldData() {
                //清除历史搜索记录  更新记录原始数据
                HistoryCache.clear(getActivity().getApplicationContext());
                Log.e("点击","清除数据");
            }
            @Override
            public void SaveOldData(ArrayList<String> AlloldDataList) {
                //保存所有的搜索记录
                HistoryCache.saveHistory(getActivity().getApplicationContext(),HistoryCache.toJsonArray(AlloldDataList));
                Log.e("点击","保存数据");
            }
        },1);
    }

    public void startActivity(Class<?> openClass, Bundle bundle) {
        Intent intent = new Intent(getActivity(), openClass);
        if (null != bundle)
            intent.putExtras(bundle);
        startActivity(intent);
    }
}
