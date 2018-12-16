package com.kelsey.shiyu.search;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.kelsey.shiyu.library.cache.HistoryCache;
import com.kelsey.shiyu.library.callback.onSearchCallBackListener;
import com.kelsey.shiyu.library.widget.SearchListLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private SearchListLayout searchLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_style);

        searchLayout = (SearchListLayout)findViewById(R.id.searchlayout);
        initData();
    }

    private void initData() {
        List<String> skills = HistoryCache.toArray(getApplicationContext());
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
                finish();
            }

            @Override
            public void ClearOldData() {
                //清除历史搜索记录  更新记录原始数据
                HistoryCache.clear(getApplicationContext());
                Log.e("点击","清除数据");
            }
            @Override
            public void SaveOldData(ArrayList<String> AlloldDataList) {
                //保存所有的搜索记录
                HistoryCache.saveHistory(getApplicationContext(),HistoryCache.toJsonArray(AlloldDataList));
                Log.e("点击","保存数据");
            }
        },1);
    }


    public void startActivity(Class<?> openClass, Bundle bundle) {
        Intent intent = new Intent(this, openClass);
        if (null != bundle)
            intent.putExtras(bundle);
        startActivity(intent);
    }

}
