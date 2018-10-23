package com.team9.focus.BackgroundServices;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Toast;

import com.team9.focus.R;
import com.team9.focus.models.DBHelper;
import com.team9.focus.models.objects.App;
import com.team9.focus.utilities.Utility;

/**
 * Created by maya on 10/7/17.
 */

public class WindowChangeDetectingService extends AccessibilityService {

    @Override
    protected void onServiceConnected() {
        Log.d("WCDS", "onService");
        super.onServiceConnected();

        //Configure these here for compatibility with API 13 and below.
        AccessibilityServiceInfo config = new AccessibilityServiceInfo();
        config.eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED;
        config.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;

        if (Build.VERSION.SDK_INT >= 16)
            //Just in case this helps
            config.flags = AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS;

        setServiceInfo(config);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            if (event.getPackageName() != null && event.getClassName() != null) {
                ComponentName componentName = new ComponentName(
                        event.getPackageName().toString(),
                        event.getClassName().toString()
                );

                ActivityInfo activityInfo = tryGetActivity(componentName);
                boolean isActivity = activityInfo != null;
                if (isActivity) {
                    Log.d("WCDS_CA Name", componentName.flattenToShortString());
                    Log.d("WCDS_CA Package", componentName.getPackageName());
                    Log.d("WCDS_CA Class", componentName.getClassName());

                    if(Utility.isBlockedApp(componentName.getPackageName())) {
                        StringBuilder sb = new StringBuilder();
                        sb.append(componentName.getPackageName() + " is blocked and you cannot access it");

                        App app = Utility.getAppFromPackageName(componentName.getPackageName());
                        DBHelper.incrementAppLaunchCount(app);
                        Toast.makeText(this, sb.toString(), Toast.LENGTH_LONG).show();
                        Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.team9.focus");
                        startActivity(launchIntent);
                        //sendLaunchNotification(componentName.getPackageName());

                    }
                }
            }
        }
    }

    private ActivityInfo tryGetActivity(ComponentName componentName) {
        try {
            return getPackageManager().getActivityInfo(componentName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }

    private void sendLaunchNotification(String appName) {
        Log.d("WCDS", "sendLaunchNotification");

        StringBuilder sb = new StringBuilder();
        sb.append(appName + " is blocked and you cannot access it");
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.notification_icon)
                        .setContentTitle("Accessing Blocked App")
                        .setContentText(sb.toString());

        NotificationManager mNotificationManager =(NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        // When you issue multiple notifications about the same type of event, it’s best practice for your app to try to update an existing notification with this new information, rather than immediately creating a new notification. If you want to update this notification at a later date, you need to assign it an ID. You can then use this ID whenever you issue a subsequent notification. If the previous notification is still visible, the system will update this existing notification, rather than create a new one. In this example, the notification’s ID is 001//
        mNotificationManager.notify(100, mBuilder.build());

    }

    @Override
    public void onInterrupt() {}
}