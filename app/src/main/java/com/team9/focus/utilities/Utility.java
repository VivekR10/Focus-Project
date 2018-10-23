package com.team9.focus.utilities;


import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.team9.focus.BackgroundServices.ScheduleListener;
import com.team9.focus.R;
import com.team9.focus.activities.MainActivity;
import com.team9.focus.exceptions.AlreadyExistsException;
import com.team9.focus.exceptions.InvalidDateException;
import com.team9.focus.fragments.ScheduleFragment;
import com.team9.focus.models.DBHelper;
import com.team9.focus.models.objects.App;
import com.team9.focus.models.objects.Badge;
import com.team9.focus.models.objects.BlockIntent;
import com.team9.focus.models.objects.BlockedNotification;
import com.team9.focus.models.objects.Profile;
import com.team9.focus.models.objects.Schedule;
import com.team9.focus.models.objects.Timeslot;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * Created by maya on 10/13/17.
 */

public class Utility {

    private static int UPPER_BOUND = 10000000;
    private static int LOWER_BOUND = 0;

    /**
     *
     * @param num 1 is Sunday...7 is Saturday
     * @return The day of the week as a string
     * @throws InvalidDateException if num < 1 or num > 7
     */
    public static String getDayOfWeekFromInt(int num) throws InvalidDateException {
        String dayOfWeek;
        switch (num) {
            case 1:
                dayOfWeek = "Su";
                break;
            case 2:
                dayOfWeek = "M";
                break;
            case 3:
                dayOfWeek = "Tu";
                break;
            case 4:
                dayOfWeek = "W";
                break;
            case 5:
                dayOfWeek = "Th";
                break;
            case 6:
                dayOfWeek = "F";
                break;
            case 7:
                dayOfWeek = "Sa";
                break;
            default:
                throw new InvalidDateException("Day of week must be between 1 and 7");
        }
        return dayOfWeek;
    }

    /**
     *
     * @param day The day of the week as a string
     * @return 1 is Sunday...7 is Saturday
     */
    public static int getDayOfWeekFromString(String day) throws InvalidDateException {
        if (day != null) {
            if(day.equals("Su")) return 1;
            if(day.equals("M")) return 2;
            if(day.equals("Tu")) return 3;
            if(day.equals("W")) return 4;
            if(day.equals("Th")) return 5;
            if(day.equals("F")) return 6;
            if(day.equals("Sa")) return 7;
        }
        throw new InvalidDateException("Day is invalid.");
    }


    /**
     *
     * @param time The time as a double (i.e. 23.75)
     * @return just the hour (i.e. 23)
     */
    public static int getHourFromTime(double time) throws InvalidDateException {
        if (time < 0 || time >= 24) {
            throw new InvalidDateException("Hour must be between 0 and 23");
        }
        return (int)time;
    }

    /**
     *
     * @param time The time as a double (i.e. 10.3833)
     * @return just the minutes (i.e. 23)
     */
    public static int getMinFromTime(double time) throws InvalidDateException {
        if (time < 0 || time >= 24) {
            throw new InvalidDateException("Hour must be between 0 and 23");
        }
        double decimal = time - Math.floor(time);
        return (int)(decimal*60);
    }

    /**
     *
     * @param timeslots A list of timeslot objects
     * @return A string representation of all timeslots separated by commas
     */
    public static String timeslotsToString(List<Timeslot> timeslots) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < timeslots.size(); i++) {
            Log.d("U_formatAsString", timeslots.get(i).formatAsString());
            sb.append(timeslots.get(i).formatAsString());
            // Add commas to all but the last one
            if (i != timeslots.size() - 1) {
                sb.append('\n');
            }
        }

        return sb.toString();
    }

    /**
     *
     * @param packageName
     * @return A boolean value representing whether the given package should be blocked
     */
    public static boolean isBlockedApp(String packageName) {
        for(App a : DBHelper.getAllBlockedApps()) {
            if(packageName.equals(a.packageName)) {
                return true;
            }
        }
        return false;

    }

    public static App getAppFromPackageName (String packageName)
    {
        if (packageName != null && !packageName.equals("")) {
            for (App a : DBHelper.getAllApps()) {
                if (packageName.equals(a.packageName)) {
                    return a;
                }
            }
        }
        return null;
    }

    /**
     *
     * @return a random integer that is not currently in the database
     */
    public static int getUniqueRequestCode() {

        int requestCode = (int) (Math.random() * (UPPER_BOUND - LOWER_BOUND)) + LOWER_BOUND;
        while(DBHelper.getBlockIntentByRequestCode(requestCode) != null) {
            requestCode = (int) (Math.random() * (UPPER_BOUND - LOWER_BOUND)) + LOWER_BOUND;
        }

        return requestCode;

    }

    /**
     *
     * @param context
     * Uses the given application context to get all of the installed apps on the phone
     * Filters them by system apps and non-system apps
     * Adds all of the non-system apps to the DB
     */
    public static void setAllInstalledApps(Context context)
    {
        //TODO: Drop the old App table at the beginning of this.


        PackageManager pm = context.getPackageManager();
        ArrayList<App> installedApps = new ArrayList<App>();
        List<ApplicationInfo> apps = pm.getInstalledApplications(0);

        List<ApplicationInfo> installedAppInfo = new ArrayList<ApplicationInfo>();

        for(ApplicationInfo app : apps) {
            //checks for flags; if flagged, check if updated system app
            if((app.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
                // installedAppInfo.add(app);
                Log.d("UTIL_APP", ("Updated System App "+ app.packageName));


            } else if ((app.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {

                //Log.d("UTIL_APP", ("System App "+ app.packageName));
                //Discard this one
                //in this case, it should be a user-installed app
            } else {
                if(!app.packageName.contains("com.team9.focus")) {
                    installedAppInfo.add(app);
                }
            }


        }

        for(ApplicationInfo app: installedAppInfo)
        {
            /*Log.d("UTIL_APP_SAVED", app.toString());
            Log.d("UTIL_Process_Name", app.processName);


            Log.d("UTIL_Package_Name", app.packageName);
            Log.d("UTIL_Label_Name", app.loadLabel(pm).toString());*/

            String appName = app.loadLabel(pm).toString();
            String packageName = app.packageName;

            try {
                DBHelper.createApp(appName, packageName);
            } catch (AlreadyExistsException e) {
                Log.e("UTIL_SAVE_APP", e.toString());
            }
        }

    }

    public static void deactivateSchedule(Schedule schedule, Context context) {
        Log.d("DEACT", "beginning of func");

        //set isActive = false
        DBHelper.toggleScheduleActiveStatus(schedule, true);
        //For each timeslot in the schedule
        List<Timeslot> timeslots = DBHelper.getTimeslotsBySchedule(schedule);
        Log.d("DEACT timeslots", Integer.toString(timeslots.size()));
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        for (Timeslot timeslot : timeslots) {
            DBHelper.turnOffTimeslot(timeslot);
            Log.d("DEACT timeslots", timeslot.formatAsString());
            //get all intents
            List<BlockIntent> blockIntents = DBHelper.getBlockIntentsByTimeslot(timeslot);
            for (BlockIntent blockIntent : blockIntents) {
                Log.d("DEACT blockIntents", blockIntent.timeslot.toString());
                int requestCode = blockIntent.requestCode;
                Intent cancelStart = new Intent(context, ScheduleFragment.class);
                PendingIntent cancelPI = PendingIntent.getBroadcast(context, requestCode, cancelStart, 0);
                manager.cancel(cancelPI);
                cancelPI.cancel();

            }
        }
    }

    public static void sendEmail(final Context context)

    {
        List<App> apps =  DBHelper.getAllApps();
        try {
            JSONObject obj = new JSONObject();

            SharedPreferences mPrefs;
            String mEmail = "";
            mPrefs = context.getSharedPreferences("email",Context.MODE_PRIVATE);
            if(mPrefs.contains("email")) {
                mEmail = mPrefs.getString("email", "");
            }
            else
            {
                Toast.makeText(context, "Email Not Set", Toast.LENGTH_SHORT).show();
            }



            obj.put("email", mEmail);
            JSONObject appListJSON = new JSONObject();
            for (App app : apps) {
                JSONObject appJSON = new JSONObject();
                appJSON.put("blockCount", app.blockCount);
                appJSON.put("launchBlockCount", app.launchBlockCount);
                appJSON.put("minutesBlocked", app.minutesBlocked);
                appListJSON.put(app.appName, appJSON);
            }
            obj.put("apps", appListJSON);
            try {
                sendPost("http://138.68.240.16:5001/api/email/weekly", obj.toString(), new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.v("asdf", e.getMessage());
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.isSuccessful()) {
                            String responseStr = response.toString();
                            System.out.println(responseStr);
                        } else {
//                            Toast.makeText(context, "Error Sending Email", Toast.LENGTH_SHORT).show();
                        }

                    }

                });
            }
            catch (IOException e)
            {
                Log.e("MainErr", e.getMessage());
            }
        } catch (JSONException e){
            Log.v("Mainactivity", e.getMessage());
        }
    }


    public static void sendBadgeEmail(final Context context, Badge badge)

    {
        try {
            JSONObject obj = new JSONObject();

            SharedPreferences mPrefs;
            String mEmail = "";
            mPrefs = context.getSharedPreferences("email",Context.MODE_PRIVATE);
            if(mPrefs.contains("email")) {
                mEmail = mPrefs.getString("email", "");
            }
            else
            {
                Toast.makeText(context, "Email Not Set", Toast.LENGTH_SHORT).show();
            }

            obj.put("email", mEmail);
            obj.put("dateEarned", badge.dataEarned);
            obj.put("badgeName", badge.badgeName);
            try {
                sendPost("http://138.68.240.16:5001/api/email/badges", obj.toString(), new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.v("Badge Email Send Failed", e.getMessage());
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.isSuccessful()) {
                            String responseStr = response.toString();
                            Log.d("Response", responseStr);
                        } else {
                            Log.d("Response failed", response.toString());

//                            Toast.makeText(context, "Error Sending Email", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
            catch (IOException e)
            {
                Log.e("MainErr", e.getMessage());
            }
        } catch (JSONException e){
            Log.v("Mainactivity", e.getMessage());
        }
    }

    static Call sendPost(final String url, final String data, Callback callback) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Log.d("mainactivity", url);
        Log.d("mainactivity", data);
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, data);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }



    public static void configureResetTimer(Context context)
    {
        Log.d("UTIL_RESET", "beginning of func");
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        java.util.Calendar cal = java.util.Calendar.getInstance();
        //Sets the calendar
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);

        Intent i = new Intent(context, ScheduleListener.class);
        i.putExtra("type", "reset");
        PendingIntent pi = PendingIntent.getBroadcast(context, getUniqueRequestCode(), i, 0);
        manager.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), AlarmManager.INTERVAL_DAY * 7, pi);
    }

    public static void activateSchedule(String scheduleName, Context context) {
        Log.d("UTIL_ACT", "beginning of func");
        Schedule schedule = DBHelper.getScheduleByName(scheduleName);
        Log.d("UTIL_ACT_Sch", scheduleName);
        //set isActive = false
        DBHelper.toggleScheduleActiveStatus(schedule, false);
        //For each timeslot in the schedule
        List<Timeslot> timeslots = DBHelper.getTimeslotsBySchedule(schedule);
        Log.d("UTIL_ACT_timeslots", Integer.toString(timeslots.size()));
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        for (Timeslot timeslot : timeslots) {
            Log.d("UTIL_ACT_timeslot", timeslot.formatAsString());
            //get all intents
            List<BlockIntent> blockIntents = DBHelper.getBlockIntentsByTimeslot(timeslot);
            Log.d("UTIL_ACT_BI", Integer.toString(blockIntents.size()));
            for (BlockIntent blockIntent : blockIntents) {
                int hour = -1;
                int minute = -1;
                //checks the type
                if (blockIntent.type.equals("start")) {
                    hour = blockIntent.timeslot.startHour;
                    minute = blockIntent.timeslot.startMinute;
                }
                else if (blockIntent.type.equals("end")) {
                    hour = blockIntent.timeslot.endHour;
                    minute = blockIntent.timeslot.endMinute;

                }
                int requestCode = blockIntent.requestCode;

                //logs everything from the BlockIntent
                java.util.Calendar cal = java.util.Calendar.getInstance();
                //Sets the calendar
                cal.set(Calendar.HOUR_OF_DAY, hour);
                cal.set(Calendar.MINUTE, minute);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                cal.set(Calendar.DAY_OF_WEEK, blockIntent.dayOfWeek);
                Log.d("UTIL_ACT_Cal_DOW1", Integer.toString(cal.get(Calendar.DAY_OF_WEEK)));
                if (cal.getTimeInMillis() < System.currentTimeMillis()) {
                   //Adjusts if we are setting for a time of the week that is behind is
                    //(i.e. if it is 4PM but you are setting something for 2PM)
                    Log.d("UTIL_ACT", "In if");
                    cal.add(Calendar.DAY_OF_YEAR, 7);
                }
                //Logs summary
               // Log.d("UTIL_ACT_Cal_DOW2", Integer.toString(cal.get(Calendar.DAY_OF_WEEK)));
              //  Log.d("UTIL_ACT_summary", cal.toString());

                //Creates intent
                Intent i = new Intent(context, ScheduleListener.class);
                i.putExtra("type", blockIntent.type);
                i.putExtra("timeslotID", blockIntent.timeslot.getId());
                Log.d("UTIL_ACT_BI_type", blockIntent.type);
                Log.d("UTIL_ACT_BI_tID", Long.toString(blockIntent.timeslot.getId()));
                //Creates and schedules pending intent
                PendingIntent pi = PendingIntent.getBroadcast(context, requestCode, i, 0);
                manager.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), AlarmManager.INTERVAL_DAY * 7, pi);

            }
        }


    }

    public static void sendNotification(Context context, StringBuilder sb, String title) {
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent blankIntent = new Intent ();
        PendingIntent blankPending = PendingIntent.getBroadcast(context, 0, blankIntent, 0);

        //create id for notification
        Date now = new Date();
        int id = Integer.parseInt(new SimpleDateFormat("ddHHmmss",  Locale.US).format(now));

        NotificationCompat.Builder mHeadsUp =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_notification)
                        .setContentTitle(title)
                        .setContentText(sb.toString())
                        .setPriority(0)
                        .setFullScreenIntent(blankPending, true)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setAutoCancel(true);
        mNotificationManager.notify(id, mHeadsUp.build());
    }

    public static void showBlockedNotifications (Context context)
    {


        StringBuilder sb = new StringBuilder();
        for (BlockedNotification b: DBHelper.getAllBlockedNotifications()) {
            if (!Utility.isBlockedApp(b.packageName)) {
                App blockedApp = getAppFromPackageName(b.packageName);
                sb.append("Times Blocked This Session:" + blockedApp.appName + ": " + b.count + "\n");
                DBHelper.updateAppCount(blockedApp, b.count);
                sb.append("Total Times Blocked:" + blockedApp.appName + ": " + blockedApp.blockCount + "\n");
                b.delete();

            }
            else {
                Log.d("UTIL_SHOW_BLOCK", b.packageName);
                Log.d("UTIL_BLOCK_COUNT", String.valueOf(b.count));
            }

        }

        if (sb.toString().isEmpty()) {
            sb.append("You missed no notifications or all apps are still blocked");
        }
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent blankIntent = new Intent ();
        PendingIntent blankPending = PendingIntent.getBroadcast(context, 0, blankIntent, 0);
        NotificationCompat.Builder mHeadsUp =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_missed_notifications)
                        .setContentTitle("Missed Notifications")
                        .setContentText(sb.toString())
                        .setPriority(Notification.PRIORITY_DEFAULT)
                        .setFullScreenIntent(blankPending, true)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setAutoCancel(true);
        mNotificationManager.notify(12345, mHeadsUp.build());


        Log.d("UTIL_show", sb.toString());
    }

    /**
     *
     * @param context - the activity
     * @param title - the title of the dialog box
     * @param message - the message of the dialog box
     */
    public static void createErrorDialog(Context context, String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Nothing happens
                    }
                })
                .show();
    }

    public static List<Profile> getAllProfiles()
    {
        return DBHelper.getAllProfiles();
    }

    public static void createDefaultObjects() {
        if (DBHelper.getCalendarByName("Default") == null) {
            new com.team9.focus.models.objects.Calendar("Default").save();
        }
        if (DBHelper.getScheduleByName("Default") == null) {
            com.team9.focus.models.objects.Calendar defaultCalendar = DBHelper.getCalendarByName("Default");
            try {
                DBHelper.saveSchedule("Default", new ArrayList<Timeslot>(), new ArrayList<Profile>(),
                        new ArrayList<com.team9.focus.models.objects.Calendar>(Arrays.asList(defaultCalendar)));
            } catch (AlreadyExistsException e) {
                System.out.println(e.getMessage());
            }

        }
    }
}
