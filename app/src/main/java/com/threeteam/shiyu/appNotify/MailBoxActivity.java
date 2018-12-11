package com.threeteam.shiyu.appNotify;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

enum web{
    MANGGUOTV,
    TENGXUN,
    BILIBILI,
    YOUKU,
    AIQIYI
}
public class MailBoxActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
    }
}
