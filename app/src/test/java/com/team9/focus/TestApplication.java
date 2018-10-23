package com.team9.focus;

import android.app.Application;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Configuration;
import com.team9.focus.models.mappers.AppProfile;
import com.team9.focus.models.mappers.CalendarSchedule;
import com.team9.focus.models.mappers.ScheduleProfile;
import com.team9.focus.models.mappers.ScheduleTimeslot;
import com.team9.focus.models.objects.App;
import com.team9.focus.models.objects.BlockIntent;
import com.team9.focus.models.objects.BlockedNotification;
import com.team9.focus.models.objects.Calendar;
import com.team9.focus.models.objects.Profile;
import com.team9.focus.models.objects.Schedule;
import com.team9.focus.models.objects.Timeslot;

/**
 * Created by jtsui on 10/23/17.
 */

public class TestApplication extends Application {
    /*
        We need this custom Test Application because we want to initialize our Database with Active Android
     */
    @Override
    public void onCreate() {
        super.onCreate();
        Configuration dbConfig = new Configuration.Builder(this)
                .setDatabaseName("Focus.db")
                .setDatabaseVersion(1)
                .addModelClasses(
                        App.class,
                        Profile.class,
                        Timeslot.class,
                        Schedule.class,
                        AppProfile.class,
                        ScheduleProfile.class,
                        ScheduleTimeslot.class,
                        BlockedNotification.class,
                        BlockIntent.class,
                        Calendar.class,
                        CalendarSchedule.class
                )
                .create();
        ActiveAndroid.initialize(dbConfig);
    }

    @Override
    public void onTerminate() {
        ActiveAndroid.dispose();
        super.onTerminate();
    }
}
