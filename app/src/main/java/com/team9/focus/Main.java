package com.team9.focus;

import android.app.Application;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Configuration;
import com.facebook.FacebookSdk;
import com.facebook.stetho.Stetho;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.team9.focus.models.mappers.AppProfile;
import com.team9.focus.models.mappers.CalendarSchedule;
import com.team9.focus.models.mappers.ScheduleProfile;
import com.team9.focus.models.mappers.ScheduleTimeslot;
import com.team9.focus.models.objects.App;
import com.team9.focus.models.objects.Badge;
import com.team9.focus.models.objects.BlockIntent;
import com.team9.focus.models.objects.BlockedNotification;
import com.team9.focus.models.objects.Calendar;
import com.team9.focus.models.objects.Profile;
import com.team9.focus.models.objects.Schedule;
import com.team9.focus.models.objects.Timeslot;
import com.team9.focus.utilities.Utility;

import okhttp3.OkHttpClient;

/**
 * Created by jtsui on 9/30/17.
 */

public class Main extends Application {

    // DELETE THIS LATER!!! Just so that fillDB isn't called more than once
    public boolean isDBInitialized = false;
    private final String TWITTER_CONSUMER_KEY = "221c1KeT9rtG7p4KP9KTv3isE";
    private final String TWITTER_CONSUMER_SECRET = "ttBvjZkoTYS8wE4DvMLFPn46zcZs5BHehF2PhiwFWEsMDKE4Fk";

    @Override
    public void onCreate() {
        super.onCreate();

        OkHttpClient client = new OkHttpClient.Builder()
                .addNetworkInterceptor(new StethoInterceptor())
                .build();
        Stetho.initializeWithDefaults(this);

        // Initialize FB
        FacebookSdk.sdkInitialize(getApplicationContext());

        // COMMENT THIS OUT IF YOU DONT WANNA DROP THE DB
        //deleteDatabase("Focus.db");

        // Sets up the database
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
                        CalendarSchedule.class,
                        Badge.class
                )
                .create();
        ActiveAndroid.initialize(dbConfig);

        Utility.setAllInstalledApps(this);
        Utility.createDefaultObjects();
        Utility.configureResetTimer(this);
    }
}
