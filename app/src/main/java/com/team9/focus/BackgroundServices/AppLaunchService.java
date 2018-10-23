package com.team9.focus.BackgroundServices;

import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vivek on 10/7/2017.
 */

public class AppLaunchService extends Service {

    private Handler handler;
    Runnable runnable;
    ArrayList<String> whitelist;
    @Override
    public void onCreate() {
        Log.d("ALS_Created", "created");
        // TODO Auto-generated method stub
        super.onCreate();
        handler = new Handler();
        whitelist = new ArrayList<String>();
        runnable = new Runnable() {
            @Override
            public void run() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

                        List<ActivityManager.RunningTaskInfo> taskInfo = am
                                .getRunningTasks(1);

                        ComponentName componentInfo = taskInfo.get(0).topActivity;
                        String currentActivityName = componentInfo.getClassName();
                        String packageName = componentInfo.getPackageName();
                        if (whitelist.contains(currentActivityName)) {

                            Intent launchIntent = new Intent();
                            launchIntent.setComponent(new ComponentName("com.facebook.orca",
                                    "Messenger"));
                            launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(launchIntent);

                        }
                        Log.d("ALS_Package", componentInfo.getPackageName());
                        Log.d("ALS_Class", componentInfo.getClassName());
                    }
                }).start();
                handler.postDelayed(this, 1000);

            }
        };
        handler.postDelayed(runnable, 1000);
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Intent intent = new Intent(this, AppLaunchService.class);
        startService(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }
}
