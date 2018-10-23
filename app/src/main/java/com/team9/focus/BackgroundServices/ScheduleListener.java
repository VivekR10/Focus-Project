package com.team9.focus.BackgroundServices;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.team9.focus.models.DBHelper;
import com.team9.focus.models.objects.App;
import com.team9.focus.models.objects.Badge;
import com.team9.focus.models.objects.BlockedNotification;
import com.team9.focus.models.objects.Profile;
import com.team9.focus.models.objects.Schedule;
import com.team9.focus.models.objects.Timeslot;
import com.team9.focus.utilities.BadgeComparator;
import com.team9.focus.utilities.Utility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Vivek (and Maya but not really) on 9/30/2017.
 * @TODO:
 * A new BroadcastReceiver is created every time an intent is broadcasted --> data won't persist, must connect to database.
 * 1. Query and write to the database on addBlockedNotification
 * 2. Query from the database on showNotifications()
 * In the database we need to store:
 * Name of Package --> # of notifications received
 * 3. Figure out how and when to store intents in DB
 * 3.1. What if we override? Don't have that functionality yet.
 *
 */

public class ScheduleListener extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent)
    {
        Log.d("SL_OnReceive", "Here");
        Bundle extras = intent.getExtras();

        String type = (String) extras.get("type");
        long timeslotID = intent.getLongExtra("timeslotID", 0);

        if(type.equals("start")) {
            Log.d("SL_If", "start");
            List<String> appNames = new ArrayList<>();
            List<Timeslot> timeslots = DBHelper.getTimeslotsByID(timeslotID);
            Log.d("SL_Timeslot_size", String.valueOf(timeslots.size()));
            for(Timeslot t : timeslots) {
                DBHelper.turnOnTimeslot(t);
                Log.d("SL_On", t.toString());
                Schedule schedule = DBHelper.getScheduleByTimeslot(t);
                List<Profile> profiles = DBHelper.getProfilesBySchedule(schedule);
                for (Profile p : profiles) {
                    List<App> apps = DBHelper.getAppsByProfile(p);
                    for (App a : apps) {
                        appNames.add(a.appName);
                    }
                }
            }

            StringBuilder sb = new StringBuilder();
            int last = appNames.size()-1;
            sb.append("Now blocking apps: ");
            for (String name : appNames) {
                if (name != appNames.get(last)) {
                    sb.append(name + ", ");
                } else {
                    sb.append(name);
                }
            }

            Utility.sendNotification(context.getApplicationContext(), sb, "Schedule On");
            //Toast.makeText(context, sb.toString(), Toast.LENGTH_LONG).show();
        }
        else if(type.equals("end")) {
            Log.d("SL_If", "end");
            List<Timeslot> timeslots = DBHelper.getTimeslotsByID(timeslotID);

            // If it was overridden, don't show notifications again
            boolean wasOverridden = false;

            for(Timeslot t : timeslots) {
                Schedule schedule = DBHelper.getScheduleByTimeslot(t);
                List<Profile> profiles = DBHelper.getProfilesBySchedule(schedule);

                // Calculate the duration
                int startMinutes = (t.startHour*60) + t.startMinute;
                int endMinutes = (t.endHour*60) + t.endMinute;
                int duration = endMinutes-startMinutes;
                int durationInHours = duration / 60;
                for(Profile p: profiles)
                {
                    List<App> apps = DBHelper.getAppsByProfile(p);
                    for(App a: apps)
                    {
                        DBHelper.updateAppBlockMinutes(a, duration);
                        Log.d("SL_Duration", String.valueOf(duration));
                    }
                }
                // Update hours focused for the timeslot
                DBHelper.getScheduleByTimeslot(t).addHoursFocused(duration);
                // Unlock badges
                List<Badge> badges = DBHelper.getBadgesByCategory("hoursFocused");
                Collections.sort(badges, new BadgeComparator());
                for(Badge badge : badges) {
                    if(!badge.isEarned) {
                        DBHelper.updateBadge(badge, durationInHours, context.getApplicationContext());
                    }
                }

                DBHelper.turnOffTimeslot(t);

                if(t.isOverridden){
                    wasOverridden = true;
                }

                Log.d("SL_Off", t.toString());

            }

            // Only show if not overridden
            if(!wasOverridden){
                showNotifications(context);
            }
            if(timeslots.size() > 0) {
                if(DBHelper.getScheduleByTimeslot(timeslots.get(0)).scheduleName.equals("Default")) {
                    DBHelper.deleteScheduleAssociations(DBHelper.getScheduleByTimeslot(timeslots.get(0)));
                }
            }

        } else if(type.equals("notification")) {
            Log.d("SL_If", "notification");

            DBHelper.addBlockedNotification((String) extras.get("packageName"));

            BlockedNotification block = DBHelper.getBlockedNotification((String) extras.get("packageName"));
            Log.d("SL_Count", String.valueOf(block.count));
            Log.d("SL_Package", block.packageName);


        }
        else if (type.equals("reset"))
        {
            Utility.sendEmail(context);
            Log.d("SL_If", "reset");
            DBHelper.resetAppCount();
        }

    }

    private void showNotifications(Context context) {
        Log.d("SL_showNotifications", "beginning of function");
        Utility.showBlockedNotifications(context.getApplicationContext());
    }




}
