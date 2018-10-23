package com.team9.focus.BackgroundServices;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.service.notification.NotificationListenerService;
import android.util.Log;

import java.util.List;
import java.util.TreeMap;

/**
 * Created by maya on 10/7/17.
 */

public class AppLaunchListener extends NotificationListenerService {

    @Override
    public void onCreate()
    {
        super.onCreate();
        Log.d("ALL_Created", "Yes");
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
