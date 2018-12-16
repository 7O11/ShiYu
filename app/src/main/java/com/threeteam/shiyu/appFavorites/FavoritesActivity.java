package com.threeteam.shiyu.appFavorites;

import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.michaldrabik.tapbarmenulib.TapBarMenu;
import com.threeteam.shiyu.appNotify.R;
import com.threeteam.shiyu.web;

import java.util.ArrayList;
import java.util.List;

import static com.threeteam.shiyu.web.AIQIYI;
import static com.threeteam.shiyu.web.ALL;
import static com.threeteam.shiyu.web.BILIBILI;
import static com.threeteam.shiyu.web.MANGGUOTV;
import static com.threeteam.shiyu.web.TENGXUN;
import static com.threeteam.shiyu.web.YOUKU;

public class FavoritesActivity extends AppCompatActivity  implements View.OnClickListener{
    private TapBarMenu iconMenu;
    private ImageView itemMg, itemYk,itemBili,itemAqy,itemTx;
    private ImageView favBack;
    private ImageView favTrash;
    private ListView mlistView;
    private List<FavItem> favorites;
    private FavoriteAdapter adapter;
    //helper
    private int WEB_ID=ALL.ordinal();
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);
        getSupportActionBar().hide();//隐藏标题栏，必须放到setContentView后
        initFav();
    }
    @Override
    public void onClick(View view){
        switch (view.getId()){
            //弹出菜单筛选番剧功能
            case R.id.tapBarMenu:{
                Log.i("print_fa","click pop menu");
                iconMenu.toggle();
                break;
            }
            case R.id.item_mg:{
     //           Toast.makeText(this,"芒果TV",Toast.LENGTH_SHORT).show();
                fetchFavorites(MANGGUOTV.ordinal());
                adapter.notifyDataSetChanged();
                iconMenu.toggle();
                break;
            }
            case R.id.item_aqy:{
                fetchFavorites(AIQIYI.ordinal());
                adapter.notifyDataSetChanged();
                iconMenu.toggle();
                break;
            }
            case R.id.item_tx:{
                fetchFavorites(TENGXUN.ordinal());
                adapter.notifyDataSetChanged();
                iconMenu.toggle();
                break;
            }
            case R.id.item_bili:{
                fetchFavorites(BILIBILI.ordinal());
                adapter.notifyDataSetChanged();
                iconMenu.toggle();
                break;
            }
            case R.id.item_yk:{
                fetchFavorites(YOUKU.ordinal());
                adapter.notifyDataSetChanged();
                iconMenu.toggle();
                break;
            }
            //返回
            case R.id.back_from_f:{
                finish();
                break;
            }
            //清空收藏列表
            case R.id.f_garbage:{
                AlertDialog dialog = new AlertDialog.Builder(FavoritesActivity.this)
                        .setMessage(R.string.clear_all_fav_dialog)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //TODO: 清空数据库操作
                                fetchFavorites(ALL.ordinal());
                                adapter.notifyDataSetChanged();
                            }
                        })
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
    private void initFav(){
        iconMenu = (TapBarMenu)findViewById(R.id.tapBarMenu);
        itemMg = (ImageView)findViewById(R.id.item_mg);
        itemAqy = (ImageView)findViewById(R.id.item_aqy);
        itemBili = (ImageView)findViewById(R.id.item_bili);
        itemTx = (ImageView)findViewById(R.id.item_tx) ;
        itemYk = (ImageView)findViewById(R.id.item_yk);
        favBack = (ImageView)findViewById(R.id.back_from_f) ;
        favTrash = (ImageView)findViewById(R.id.f_garbage);
        //
        favorites = new ArrayList<FavItem>();
        favorites.add(new FavItem("番名","aaaaa","f","no","u"));
        favorites.add(new FavItem("番名","aaaaa","f","no","u"));
        favorites.add(new FavItem("番名","aaaaa","f","no","u"));
        favorites.add(new FavItem("番名","aaaaa","f","no","u"));
        favorites.add(new FavItem("番名","aaaaa","f","no","u"));
        favorites.add(new FavItem("番名","aaaaa","f","no","u"));
        adapter = new FavoriteAdapter(FavoritesActivity.this, R.layout.favorite_and_slip_item_layout, favorites);
        Log.i("print_fa","get adapter");
        iconMenu.setOnClickListener(this);
        itemMg.setOnClickListener(this);
        itemAqy.setOnClickListener(this);
        itemBili.setOnClickListener(this);
        itemTx.setOnClickListener(this);
        itemYk.setOnClickListener(this);
        favBack.setOnClickListener(this);
        favTrash.setOnClickListener(this);
        //
        mlistView = (ListView)findViewById(R.id.list);
        View v = LayoutInflater.from(FavoritesActivity.this).inflate(R.layout.list_last_layout, null);
        mlistView.addFooterView(v);
        mlistView.setAdapter(adapter);
        //设置列表项点击事件
        adapter.setOnFavItemClickListener(new FavoriteAdapter.onFavItemListener() {
            @Override
            public void onDeleteFavoriteClick(int position) {
                //TODO: 根据getItem获取到的主键（url）删除数据库中的数据
                fetchFavorites(WEB_ID);
                adapter.notifyDataSetChanged();
                Toast.makeText(FavoritesActivity.this, "删除", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFavSubjectClick(String url) {
                //TODO:跳转url播放
                Toast.makeText(FavoritesActivity.this, "跳转播放", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void fetchFavorites(int webid){
        //筛选
        //二次点击某网站按钮，将显示全部
        if(webid==WEB_ID){
           WEB_ID = ALL.ordinal();
        }
        //否则仅显示该网站
        else WEB_ID = webid;
        //TODO:cursor 取数据,更新favorites列表
    }
}
