package com.threeteam.shiyu.appNotify;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.widget.Toast;

public class NotifyService extends Service {
    final static int checkGap = 10*60*1000;
    boolean flag = false;
    //
    public void setFlag(boolean flag) {
        this.flag = flag;
    }
    @Override
    public IBinder onBind(Intent intent){
        return null;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        //匿名类 新开线程执行查询操作
        new Thread(new Runnable() {
            @Override
            public void run() {
                //TODO:change 必须改为收藏数据库！！！
                Notifier notifier = new Notifier(null);
                notifier.checkUpdate();
                if(notifier.isUpdate()){
                    MailBoxActivity.updateMailList();
                    Toast.makeText(ApplicationForContext.getContext(),"有新的更新", Toast.LENGTH_SHORT).show();
                }
            }
        }).start();
        AlarmManager manager = (AlarmManager)getSystemService(ALARM_SERVICE);
        long triggerAtTime = SystemClock.elapsedRealtime()+checkGap;
        Intent i = new Intent(this, AlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
        return super.onStartCommand(intent, flags, startId);
    }
}
