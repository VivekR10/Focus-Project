package com.team9.focus.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.team9.focus.BackgroundServices.BlockedNotificationListener;
import com.team9.focus.BackgroundServices.WindowChangeDetectingService;
import com.team9.focus.Main;
import com.team9.focus.R;
import com.team9.focus.adapters.DrawerItemAdapter;
import com.team9.focus.exceptions.AlreadyExistsException;
import com.team9.focus.fragments.CalendarFragment;
import com.team9.focus.fragments.HomeFragment;
import com.team9.focus.fragments.ProfileFragment;
import com.team9.focus.fragments.ScheduleFragment;
import com.team9.focus.fragments.TimerFragment;
import com.team9.focus.models.DBHelper;
import com.team9.focus.models.objects.App;
import com.team9.focus.models.objects.Badge;
import com.team9.focus.models.objects.Calendar;
import com.team9.focus.models.objects.Profile;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private String[] mNavigationDrawerItemTitles;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    Toolbar toolbar;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private FragmentManager mFragmentManager;
    android.support.v7.app.ActionBarDrawerToggle mDrawerToggle;

    GsonBuilder builder = new GsonBuilder();
    Gson gson;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        builder.excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC);
        gson = builder.create();
        super.onCreate(savedInstanceState);
        Set<String> result = NotificationManagerCompat.getEnabledListenerPackages(this);
        for (String r : result) {
            Log.d("PERM", r);

        }
        try {
            PackageInfo info = getPackageManager().getPackageInfo(this.getPackageName(), PackageManager.GET_PERMISSIONS);
            if (info.requestedPermissions != null) {
                for (String p : info.requestedPermissions) {
                    Log.d("PERM", p);
                }
            }
        } catch (Exception e) {

        }

        if (!isAccessibilitySettingsOn(getApplicationContext())) {
            Log.d("PERM", "Accessiblity Not found");
            startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
        }

        ComponentName notificationComponent = new ComponentName(getApplicationContext(), BlockedNotificationListener.class);
        String notificationServices = Settings.Secure.getString(getApplicationContext().getContentResolver(), "enabled_notification_listeners");
        final boolean notificationOn = notificationServices != null && notificationServices.contains(notificationComponent.flattenToString());

        if (!notificationOn) {
            Log.d("PERM", "Notif Not found");
            Intent allowsNotifAccess = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
            startActivity(allowsNotifAccess);
        }


        setContentView(R.layout.activity_main);
        mTitle = mDrawerTitle = getTitle();
        mNavigationDrawerItemTitles = getResources().getStringArray(R.array.navigation_drawer_items_array);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mDrawerList = findViewById(R.id.left_drawer);

        setupToolbar();

        String[] drawerItem = new String[5];

        drawerItem[0] = "Home";
        drawerItem[1] = "Profile";
        drawerItem[2] = "Schedule";
        drawerItem[3] = "Timer";
        drawerItem[4] = "Calendar";

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(true);

        DrawerItemAdapter adapter = new DrawerItemAdapter(this, R.layout.list_view_item_row, drawerItem);
        mDrawerList.setAdapter(adapter);
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        setupDrawerToggle();


        if (!((Main) getApplicationContext()).isDBInitialized) {
            fillDB();
        }

        // Testing the getAllBlockedApps functionality
        Set<App> apps = DBHelper.getAllBlockedApps();
        for (App app : apps) {
            Log.d("Blocked App:", app.appName);
        }

        // Set home as the default
        selectItem(0);

    }

    // Fills the database with junk data for testing purposes
    private void fillDB() {
        // Create our apps
        App[] apps = new App[10];
        for (int i = 0; i < apps.length; ++i) {
            try {
                apps[i] = DBHelper.createApp("app_" + i, "package_" + i);
            } catch (AlreadyExistsException e) {
                System.out.println(e.getMessage());
            }
        }

        Profile profile1 = new Profile();
        profile1.profileName = "profile_1";
        profile1.save();

        // Associate apps 1-5 with profile1
        for (int i = 0; i < 5; ++i) {
            DBHelper.associateAppAndProfile(apps[i], profile1);
        }

        Profile profile2 = new Profile();
        profile2.profileName = "profile_2";
        profile2.save();

        for (int i = 5; i < apps.length; ++i) {
            DBHelper.associateAppAndProfile(apps[i], profile2);
        }

        /* Create Badges */
        /*for (int i = 0; i < 5; i++) {
            if (i < 4) {
                Badge badge = new Badge(false, "", "@drawable/focus_icon", ("Badge" + Integer.toString(i)), (i * 100));
                badge.save();
            }

            if (i == 4) {
                String date = new SimpleDateFormat("MM/dd/yyyy").format(java.util.Calendar.getInstance().getTime());

                Badge badge = new Badge(true, date, "android.resource://com.team9.focus/drawable/focus_icon", ("Badge" + Integer.toString(i)), (i * 100));
                badge.save();
            }
        }*/
        //viewing all badge icons
        String date = new SimpleDateFormat("MM/dd/yyyy").format(java.util.Calendar.getInstance().getTime());
        //profile
        Badge badge1 = new Badge(false, date, "android.resource://com.team9.focus/drawable/badge1", "profile", "Profile Newbie", 1);
        badge1.save();
        Badge badge2 = new Badge(false, date, "android.resource://com.team9.focus/drawable/badge2", "profile", "Profile Novice", 5);
        badge2.save();
        Badge badge3 = new Badge(false, date, "android.resource://com.team9.focus/drawable/badge3", "profile", "Profile Advanced", 10);
        badge3.save();
        Badge badge4 = new Badge(false, date, "android.resource://com.team9.focus/drawable/badge4", "profile", "Profile Expert", 15);
        badge4.save();
        Badge badge5 = new Badge(false, date, "android.resource://com.team9.focus/drawable/badge5", "profile", "Profile Master", 20);
        badge5.save();
        Badge badge6 = new Badge(false, date, "android.resource://com.team9.focus/drawable/badge6", "profile", "Profile Champion", 25);
        badge6.save();

        //timer
        Badge badge7 = new Badge(false, date, "android.resource://com.team9.focus/drawable/badge1", "timer", "Timer Newbie", 1);
        badge7.save();
        Badge badge8 = new Badge(false, date, "android.resource://com.team9.focus/drawable/badge2", "timer", "Timer Novice", 2);
        badge8.save();
        Badge badge9 = new Badge(false, date, "android.resource://com.team9.focus/drawable/badge3", "timer", "Timer Advanced", 5);
        badge9.save();
        Badge badge10 = new Badge(false, date, "android.resource://com.team9.focus/drawable/badge4", "timer", "Timer Expert", 10);
        badge10.save();

        //hoursFocused
        Badge badge11 = new Badge(false, date, "android.resource://com.team9.focus/drawable/badge1", "hoursFocused", "Hours Focused Newbie", 5);
        badge11.save();
        Badge badge12 = new Badge(false, date, "android.resource://com.team9.focus/drawable/badge2", "hoursFocused", "Hours Focused Novice", 10);
        badge12.save();
        Badge badge13 = new Badge(false, date, "android.resource://com.team9.focus/drawable/badge3", "hoursFocused", "Hours Focused Advanced", 15);
        badge13.save();
        Badge badge14 = new Badge(false, date, "android.resource://com.team9.focus/drawable/badge4", "hoursFocused", "Hours Focused Expert", 50);
        badge14.save();
        Badge badge15 = new Badge(false, date, "android.resource://com.team9.focus/drawable/badge5", "hoursFocused", "Hours Focused Master", 75);
        badge15.save();
        Badge badge16 = new Badge(false, date, "android.resource://com.team9.focus/drawable/badge6", "hoursFocused", "Hours Focused Champion", 100);
        badge16.save();





        List<Profile> profiles = new ArrayList<Profile>();
        profiles.add(profile1);
        profiles.add(profile2);

        List<Calendar> calendars = new ArrayList<>();
        Calendar c = new Calendar();
        c.calendarName = "Default";
        c.isActive = true;
        c.save();
        calendars.add(c);

        ((Main) getApplicationContext()).isDBInitialized = true;
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }

    }

    private void selectItem(int position) {

        Fragment fragment = null;

        switch (position) {
            case 0:
                fragment = new HomeFragment();
                break;
            case 1:
                fragment = new ProfileFragment();
                break;
            case 2:
                fragment = new ScheduleFragment();
                break;
            case 3:
                fragment = new TimerFragment();
                break;
            case 4:
                fragment = new CalendarFragment();
                break;
            default:
                break;
        }

        if (fragment != null) {
            mFragmentManager = getSupportFragmentManager();
            mFragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

            mDrawerList.setItemChecked(position, true);
            mDrawerList.setSelection(position);
            setTitle(mNavigationDrawerItemTitles[position]);
            mDrawerLayout.closeDrawer(mDrawerList);

        } else {
            Log.e("MainActivity", "Error in creating fragment");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    void setupToolbar()  {
        toolbar = findViewById(R.id.toolbar);
        ImageView ivUserIcon = findViewById(R.id.ivUserIcon);
        ivUserIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)  {

                Intent userProfileIntent = new Intent(MainActivity.this, UserProfileActivity.class);
                startActivity(userProfileIntent);
            }
        });
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }



    void setupDrawerToggle() {
        mDrawerToggle = new android.support.v7.app.ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.app_name, R.string.app_name);
        //This is necessary to change the icon of the Drawer Toggle upon state change.
        mDrawerToggle.syncState();
    }

    private boolean isAccessibilitySettingsOn(Context mContext) {
        int accessibilityEnabled = 0;
        final String service = getPackageName() + "/" + WindowChangeDetectingService.class.getCanonicalName();
        try {
            accessibilityEnabled = Settings.Secure.getInt(
                    mContext.getApplicationContext().getContentResolver(),
                    android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
            Log.v("PERM", "accessibilityEnabled = " + accessibilityEnabled);
        } catch (Settings.SettingNotFoundException e) {
            Log.e("PERM", "Error finding setting, default accessibility to not found: "
                    + e.getMessage());
        }
        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled == 1) {
            Log.v("PERM", "***ACCESSIBILITY IS ENABLED*** -----------------");
            String settingValue = Settings.Secure.getString(
                    mContext.getApplicationContext().getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue);
                while (mStringColonSplitter.hasNext()) {
                    String accessibilityService = mStringColonSplitter.next();

                    Log.v("PERM", "-------------- > accessibilityService :: " + accessibilityService + " " + service);
                    if (accessibilityService.equalsIgnoreCase(service)) {
                        Log.v("PERM", "We've found the correct setting - accessibility is switched on!");
                        return true;
                    }
                }
            }
        } else {
            Log.v("PERM", "***ACCESSIBILITY IS DISABLED***");
        }

        return false;
    }
}
