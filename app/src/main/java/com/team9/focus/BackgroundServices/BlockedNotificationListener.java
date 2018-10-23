package com.team9.focus.BackgroundServices;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.team9.focus.R;
import com.team9.focus.utilities.Utility;

import java.util.List;
import java.util.TreeMap;

/**
 * Created by Vivek on 10/1/2017.
 * Right now this class cancels the notification.
 * @TODO:
 * 1. Write queries for the database for the blocked apps
 *
 */


public class BlockedNotificationListener extends NotificationListenerService {
    @Override
    public void onCreate()
    {
        super.onCreate();
        Log.d("BNL_Created", "Yes");
    }
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        Log.d("BNL, OnPosted", "First");
        Log.d("BNL_foreground", getForegroundApp());
        Log.d("BNL_Notification1", sbn.getKey().toString());
        Log.d("BNL_sbn", sbn.toString());

        if(Utility.isBlockedApp(sbn.getPackageName())) {
            Log.d("BNL_isBlocked", "success");

            cancelNotification(sbn.getKey());
            Intent i = new Intent(this, ScheduleListener.class);
            i.putExtra("type", "notification");
            i.putExtra("packageName", sbn.getPackageName());
            sendBroadcast(i);
        }
    }

    public void createAndCancelHeadsUpNotification() {
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Intent blankIntent = new Intent ();
        PendingIntent blankPending = PendingIntent.getBroadcast(this, 0, blankIntent, 0);
        NotificationCompat.Builder mHeadsUp =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.notification_icon)
                        .setContentTitle("")
                        .setContentText("")
                        .setPriority(Notification.PRIORITY_DEFAULT)
                        .setFullScreenIntent(blankPending, true)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setAutoCancel(true);
        mNotificationManager.notify(12345, mHeadsUp.build());
        mNotificationManager.cancel(12345);


    }

    public String getForegroundApp() {
        Log.d("ALL", "getForegroundApp");

        String currentApp = "NULL";

        UsageStatsManager usm = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
        long time = System.currentTimeMillis();
        List<UsageStats> appList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000 * 1000, time);
        if (appList != null && appList.size() > 0) {
            TreeMap<Long, UsageStats> mySortedMap = new TreeMap<Long, UsageStats>();
            for (UsageStats usageStats : appList) {
                mySortedMap.put(usageStats.getLastTimeUsed(), usageStats);
            }
            if (mySortedMap != null && !mySortedMap.isEmpty()) {
                currentApp = mySortedMap.get(mySortedMap.lastKey()).getPackageName();
            }
        }

        return currentApp;
    }
}
