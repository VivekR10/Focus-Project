package com.team9.focus.models;

import android.content.Context;
import android.util.Log;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.activeandroid.query.Update;
import com.team9.focus.exceptions.AlreadyExistsException;
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
import com.team9.focus.utilities.HoursFocusedComparator;
import com.team9.focus.utilities.LaunchBlockComparator;
import com.team9.focus.utilities.MinBlockedComparator;
import com.team9.focus.utilities.NotifsBlockComparator;
import com.team9.focus.utilities.OverrideComparator;
import com.team9.focus.utilities.ProfileUseComparator;
import com.team9.focus.utilities.Utility;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by jtsui on 9/30/17.
 */

public class DBHelper {

    public static List<Timeslot> getTimeslotsBySchedule(Schedule schedule){
        List<Timeslot> timeslots = null;
        if (schedule != null) {
            timeslots = new Select()
                    .from(Timeslot.class)
                    .innerJoin(ScheduleTimeslot.class).on("schedules_timeslots.timeslot = timeslots.id")
                    .where("schedules_timeslots.schedule = ?", schedule.getId())
                    .execute();
        }
        return timeslots;
    }

    public static List<Timeslot> getTimeslotsByID(long timeslotID) {
        List<Timeslot> timeslots = null;
        if (timeslotID > -1) {
            timeslots = new Select()
                    .from(Timeslot.class)
                    .where("id = ?", timeslotID)
                    .execute();
        }
        return timeslots;
    }


    public static List<Profile> getProfilesBySchedule(Schedule schedule) {
        List<Profile> profiles = null;
        if (schedule != null) {
            profiles = new Select()
                    .from(Profile.class)
                    .innerJoin(ScheduleProfile.class).on("schedules_profiles.profile = profiles.id")
                    .where("schedules_profiles.schedule = ?", schedule.getId())
                    .execute();
        }
        return profiles;
    }

    public static List<App> getAppsByProfile(Profile profile) {
        List<App> apps = null;
        if (profile != null) {
            apps = new Select()
                    .from(App.class)
                    .innerJoin(AppProfile.class).on("apps_profiles.app = apps.id")
                    .where("apps_profiles.profile = ?", profile.getId())
                    .execute();
        }
        return apps;
    }

    public static List<Schedule> getSchedulesByCalendar(Calendar calendar) {
        List<Schedule> schedules = null;
        if (calendar != null) {
            schedules = new Select()
                    .from(Schedule.class)
                    .innerJoin(CalendarSchedule.class).on("calendars_schedules.schedule = schedules.id")
                    .where("calendars_schedules.calendar = ?", calendar.getId())
                    .execute();
        }
        return schedules;
    }

    public static List<Calendar> getCalendarsBySchedule(Schedule schedule){
        List<Calendar> calendars = null;
        if(schedule!=null){
            calendars = new Select()
                    .from(Calendar.class)
                    .innerJoin(CalendarSchedule.class).on("calendars_schedules.calendar = calendars.id")
                    .where("calendars_schedules.schedule = ?", schedule.getId())
                    .execute();
        }
        return calendars;
    }

    public static List<Schedule> getSchedulesByProfile(Profile profile){
        List<Schedule> schedules = null;
        if(profile!=null){
            schedules = new Select()
                    .from(Schedule.class)
                    .innerJoin(ScheduleProfile.class).on("schedules_profiles.schedule = schedules.id")
                    .where("schedules_profiles.profile = ?", profile.getId())
                    .execute();
        }
        return schedules;
    }

    /**
     *
     * @param timeslot Timeslot object
     * @return a list of intents associated with the timeslot object
     */
    public static List<BlockIntent> getBlockIntentsByTimeslot(Timeslot timeslot) {
        List<BlockIntent> intents = null;
        if (timeslot != null) {
            intents = new Select()
                    .from(BlockIntent.class)
                    .where("timeslot = ?", timeslot.getId())
                    .execute();
        }
        return intents;
    }

    public static BlockIntent getBlockIntentByRequestCode(int requestCode) {
        BlockIntent blockIntent = null;
        if (requestCode > -1) {
            blockIntent = new Select()
                    .from(BlockIntent.class)
                    .where("request_code = ?", requestCode)
                    .executeSingle();
        }
        return blockIntent;

    }

    public static Schedule getScheduleByName(String scheduleName) {
        Schedule schedule = null;
        if (scheduleName != null && !scheduleName.isEmpty()) {
            schedule = new Select().
                    from(Schedule.class)
                    .where("schedule_name = ?", scheduleName)
                    .executeSingle();
        }
        return schedule;
    }

    public static App getAppByName(String appName) {
        App app = null;
        if (appName != null && !appName.isEmpty()) {
            app = new Select()
                    .from(App.class)
                    .where("app_name = ?", appName)
                    .executeSingle();
        }
        return app;
    }

    public static Profile getProfileByName(String profileName) {
        Profile profile = null;
        if (profileName != null && !profileName.isEmpty()) {
            profile = new Select()
                    .from(Profile.class)
                    .where("profile_name = ?", profileName)
                    .executeSingle();
        }
        return profile;
    }

    public static Calendar getCalendarByName(String calendarName) {
        Calendar calendar = null;
        if (calendarName != null && !calendarName.isEmpty()) {
            calendar = new Select()
                    .from(Calendar.class)
                    .where("calendar_name = ?", calendarName)
                    .executeSingle();
        }
        return calendar;
    }

    /**
     * @param schedules for which you want to check if the timeslot is on
     * @return all the Schedules that have timeslots which are currently "on"
     */
    public static ArrayList<Schedule> getSchedulesWithOnTimeslots(ArrayList<Schedule> schedules,
                                                                  boolean canBeOverridden){
        // Schedules to return
        ArrayList<Schedule> onSchedules = new ArrayList<>();
        // Get all schedules
        //List<Schedule> allSchedules = getAllSchedules();

        for(Schedule schedule : schedules){
            // Get all the timeslots for that schedule
            List<Timeslot> allTimeslots = getTimeslotsBySchedule(schedule);

            // Check if each timeslot is on -> if it is, add schedule to the list
            for(Timeslot timeslot : allTimeslots){
                if(timeslot.isOn){
                    if(!canBeOverridden && timeslot.isOverridden){
                        break;
                    }
                    onSchedules.add(schedule);
                    break;
                }
            }
        }

        return onSchedules;
    }





    public static boolean associateScheduleAndTimeslot(Schedule schedule, Timeslot timeslot) {
        if (timeslot == null || schedule == null) {
            return false;
        }
        new ScheduleTimeslot(schedule, timeslot).save();
        return true;
    }

    public static boolean associateAppAndProfile(App app, Profile profile) {
        if (app == null || profile == null) {
            return false;
        }
        new AppProfile(app, profile).save();
        return true;
    }

    public static boolean associateScheduleAndProfile(Schedule schedule, Profile profile) {
        if (schedule == null || profile == null) {
            return false;
        }
        new ScheduleProfile(schedule, profile).save();
        return true;
    }

    public static boolean associateCalendarAndSchedule(Calendar calendar, Schedule schedule) {
        if (calendar == null || schedule == null) {
            return false;
        }
        new CalendarSchedule(calendar, schedule).save();
        return true;
    }

    /* The following two functions below deal with blocking notifications */

    public static boolean addBlockedNotification(String packageName) {
        if (packageName == null || packageName.isEmpty()) {
            return false;
        }

        boolean exists = new Select()
                .from(BlockedNotification.class)
                .where("package_name = ?", packageName)
                .exists();
        if (exists) {
            new Update(BlockedNotification.class)
                    .set("count = count + 1")
                    .where("package_name = ?", packageName)
                    .execute();
        } else {
            BlockedNotification blockedNotification = new BlockedNotification();
            blockedNotification.packageName = packageName;
            blockedNotification.save();
        }

        return true;
    }

    public static BlockedNotification getBlockedNotification(String packageName) {
        BlockedNotification blockedNotification = null;
        if (!(packageName == null || packageName.isEmpty())) {
            blockedNotification = new Select()
                    .from(BlockedNotification.class)
                    .where("package_name = ?", packageName)
                    .executeSingle();
        }

        return blockedNotification;
    }

    /**
     * @return A list of every single app
     */
    public static List<App> getAllApps() {
        return new Select().all().from(App.class).execute();
    }


    /**
     * @return A list of every single badge
     */
    public static List<Badge> getAllBadges() {
        return new Select().all().from(Badge.class).execute();
    }
    /**
     *
     * @return A list of every single profile
     */
    public static List<Profile> getAllProfiles() {
        return new Select().all().from(Profile.class).execute();
    }

    /**
     *
     * @return A list of every single schedule
     */
    public static List<Schedule> getAllSchedules() {
        return new Select().all().from(Schedule.class).execute();
    }

    public static List<Calendar> getAllCalendars() {
        return new Select().all().from(Calendar.class).execute();
    }

    /**
     *
     * @return A list of every single Blocked Apps
     */
    public static List<BlockedNotification> getAllBlockedNotifications() {
        return new Select().all().from(BlockedNotification.class).execute();
    }

    /**
     *
     * @param schedule - schedule to be deleted
     * @return - if the schedule was successfully deleted
     */
    public static boolean deleteScheduleAssociations(Schedule schedule) {

        if (schedule == null) {
            return false;
        }
        // If a profile with this name already exists
        boolean exists = new Select()
                .from(Schedule.class)
                .where("schedule_name = ?", schedule.scheduleName)
                .exists();

        if(!exists) {
            return false;
        }

        // Delete all intents related to the timeslots in the schedule
        List<Timeslot> timeslots = getTimeslotsBySchedule(schedule);
        for(Timeslot timeslot : timeslots) {
            deleteIntentsFromTimeslot(timeslot);
        }

        // Delete ScheduleTimeslot relationship
        new Delete().from(ScheduleTimeslot.class)
                .where("schedule = ?", schedule.getId())
                .execute();

        // Delete each timeslot
        for(Timeslot t: timeslots)
        {
            t.delete();
        }

        // Delete ScheduleProfile relationship
        new Delete().from(ScheduleProfile.class)
                .where("schedule = ?", schedule.getId())
                .execute();

        //Delete CalendarSchedule relationship if not the timer
        if(!schedule.scheduleName.equals("Default")) {
            new Delete().from(CalendarSchedule.class)
                    .where("schedule = ?", schedule.getId())
                    .execute();
        }

        return true;
    }

    public static boolean updateSchedule(Schedule schedule, String newName, List<Timeslot> timeslots,
                                         List<Profile> profiles, List<Calendar> calendars) throws AlreadyExistsException {
        if (schedule == null || newName == null || newName.isEmpty()
                || timeslots == null || profiles == null || calendars == null) {
            return false;
        }
        // If a schedule with this name already exists
        boolean exists = new Select()
                .from(Schedule.class)
                .where("schedule_name = ?", newName)
                .exists();
        // Allow the user to not change the profile name
        if (exists && !(schedule.scheduleName.equals(newName))) {
            throw new AlreadyExistsException("Profile name already exists!");
        }
        // Update the Schedule name
        new Update(Schedule.class)
                .set("schedule_name = ?", newName)
                .where("id = ?", schedule.getId())
                .execute();

//        schedule.save();
        Log.d("DBH_US_NewName", newName);
        for (Profile p : profiles) {
            associateScheduleAndProfile(schedule, p);
        }
        for (Timeslot t : timeslots) {
            Log.d("DBH_USTimeslots", t.formatAsString());
            associateScheduleAndTimeslot(schedule, t);
        }

        for(Calendar c : calendars) {
            associateCalendarAndSchedule(c, schedule);
        }
        return true;
    }

    /**
     *
     * @param scheduleName The schedule's name
     * @param timeslots A list of the pre-created timeslot objects to be saved and associated with the schedule
     * @param profiles A list of profile objects to be saved and associated with the schedule
     * @return true if save was successful, false if not.
     * @throws AlreadyExistsException if the scheduleName already exists
     */
    public static boolean saveSchedule(String scheduleName, List<Timeslot> timeslots,
                                       List<Profile> profiles, List<Calendar> calendars) throws AlreadyExistsException {
        // NOTE: We are allowing empty schedules and profiles!
        // Also checks if the schedule name already exists or not
        if (scheduleName == null || scheduleName.isEmpty() || timeslots == null
                || profiles == null) {
            return false;
        } else if (getScheduleByName(scheduleName) != null) {
            //throw new AlreadyExistsException("Schedule name already exists");
            return false;
        }
        Schedule schedule = new Schedule(scheduleName);
        schedule.save();
        for (Profile p : profiles) {
            associateScheduleAndProfile(schedule, p);
        }
        for (Timeslot t : timeslots) {
            associateScheduleAndTimeslot(schedule, t);
        }

        for(Calendar c : calendars) {
            associateCalendarAndSchedule(c, schedule);
        }
        return true;
    }

    /**
     *
     * @param daysOfWeek Comma separated string of days of week ("1,2,3") or ("3")
     * @param startHour, startMinute Cannot be negative
     * @param endHour, endMinute Cannot be negative
     * @return null if the parameters are invalid, else returns the Timeslot that was saved
     */
    public static Timeslot createTimeslot(String daysOfWeek, int startHour, int startMinute,
                                          int endHour, int endMinute, boolean isOn) {
        if (daysOfWeek == null || daysOfWeek.isEmpty() || startHour < 0 || startMinute < 0
                || endHour < 0 || endMinute < 0 || endHour >= 24 || endMinute >= 60
                || startMinute >= 60) {
            return null;
        }
        Timeslot t = new Timeslot();
        t.isOverridden = false;
        t.isOn = isOn;
        t.startHour = startHour;
        t.startMinute = startMinute;
        t.endHour = endHour;
        t.endMinute = endMinute;
        t.dayOfWeek = daysOfWeek;
        t.save();
        return t;
    }

    /**
     *
     * @param name Name of the app to be created
     * @return null if the name is empty or null, else it returns the app created.
     * @throws AlreadyExistsException if an app with the name already exists
     */
    public static App createApp(String name, String packageName) throws AlreadyExistsException {
        if (name == null || name.isEmpty() || packageName == null || packageName.isEmpty()) {
            return null;
        } else if (getAppByName(name) != null) {
            throw new AlreadyExistsException("App with name already exists.");
        }
        App app = new App(name, packageName);
        app.save();
        return app;
    }

    /**
     *
     * @param name The name of the profile to be created
     * @param apps The list of apps to be associated with the profile
     * @return true if the profile was created successfully, false if there were errors with the parameter
     * @return true if the profile was created successfully, false if there were errors with the parameter
     * @throws AlreadyExistsException if a profile with the name already exists
     */
    public static boolean saveProfile(String name, List<App> apps) throws AlreadyExistsException {
        if (name == null || name.isEmpty() || apps == null || apps.size() == 0) {
            return false;
        } else if (getProfileByName(name) != null) {
            return false;
            //throw new AlreadyExistsException("Profile with name already exists.");

        }
        Profile profile = new Profile();
        profile.profileName = name;
        profile.numUses = 0;
        profile.save();
        for (App app : apps) {
            associateAppAndProfile(app, profile);
        }
        return true;
    }

    /**
     *
     * @param name The name of the calendar to be saved
     * @param schedules If null, don't associate with any schedules. Else, associate with every
     *                  schedule in the list
     * @return True if saved successfully, false if invalid parameters
     */
    public static boolean saveCalendar(String name, List<Schedule> schedules) {
        if (name == null || name.isEmpty() || getCalendarByName(name) != null) {
            return false;
        }
        Calendar calendar = new Calendar(name);
        calendar.save();
        if (schedules != null) {
            for (Schedule schedule : schedules) {
                associateCalendarAndSchedule(calendar, schedule);
                System.out.println("associating: " + schedule.scheduleName + " with: " + calendar.calendarName);
            }
        }

        return true;
    }

    /**
     *
     * @param profile The profile to be updated
     * @param newName The new ame of the profile
     * @param apps List of apps to be associated with the profile
     * @return
     * @throws AlreadyExistsException
     */
    public static boolean updateProfile(Profile profile, String newName,
                                        List<App> apps) throws AlreadyExistsException {
        if (profile == null || newName == null || newName.isEmpty() || apps == null) {
            return false;
        }
        // If a profile with this name already exists
        boolean exists = new Select()
                .from(Profile.class)
                .where("profile_name = ?", newName)
                .exists();
        // Allow the user to not change the profile name
        if (exists && !(profile.profileName.equals(newName))) {
            throw new AlreadyExistsException("Profile name already exists!");
        }
        // Update the profile name
        new Update(Profile.class)
                .set("profile_name = ?", newName)
                .where("id = ?", profile.getId())
                .execute();
        // Delete from the junction table
        new Delete().from(AppProfile.class)
                .where("profile = ?", profile.getId())
                .execute();
        for (App app : apps) {
            associateAppAndProfile(app, profile);
        }
        return true;
    }

    /**
     *
     * @param app The app to be updated
     * @param count The count to add
     * @return success or failure
     */
    public static boolean updateAppCount(App app, int count)  {


        boolean exists = new Select()
                .from(App.class)
                .where("package_name = ?", app.packageName)
                .exists();

        if(!exists)
        {
            return false;
        }
        new Update(App.class)
                .set("block_count = ?", app.addBlockCount(count))
                .where("package_name = ?", app.packageName)
                .execute();

        return true;
    }

    /**
     *
     * @param app The app to be updated
     * @param minutes The minutes to add
     * @return success or failure
     */
    public static boolean updateAppBlockMinutes(App app, int minutes)  {


        boolean exists = new Select()
                .from(App.class)
                .where("package_name = ?", app.packageName)
                .exists();

        if(!exists)
        {
            return false;
        }

        new Update(App.class)
                .set("minutes_blocked = ?", app.addBlockAppMinutes(minutes))
                .where("package_name = ?", app.packageName)
                .execute();

        return true;
    }

    /**
     *
     * @param app The app to be updated
     * @return success or failure
     */
    public static boolean incrementAppLaunchCount(App app)  {


        boolean exists = new Select()
                .from(App.class)
                .where("package_name = ?", app.packageName)
                .exists();

        if(!exists)
        {
            return false;
        }

        new Update(App.class)
                .set("launch_block_count = ?", app.incrementLaunchBlockCount())
                .where("package_name = ?", app.packageName)
                .execute();

        return true;
    }

    /**
     *
     * @return A set of all of the current blocked applications.
     */
    public static Set<App> getAllBlockedApps() {
        Set<App> blockedApps = new HashSet<>();
        List<Schedule> allSchedules = getAllSchedules();
        for (Schedule schedule : allSchedules) {
            List<Timeslot> timeslots = getTimeslotsBySchedule(schedule);
            boolean isOn = false;

            for (Timeslot timeslot : timeslots) {
                if (timeslot.isOn && !timeslot.isOverridden) {
                    isOn = true;
                    break;
                }
            }

            if (isOn) {
                List<Profile> profiles = getProfilesBySchedule(schedule);
                for (Profile profile : profiles) {
                    blockedApps.addAll(getAppsByProfile(profile));
                }
            }
        }

        return blockedApps;
    }

    /**
     *
     * @param profile the profile to set the number of schedules the profile is in
     */
    public static void setNumProfileUses(Profile profile){
        profile.numUses = getSchedulesByProfile(profile).size();
    }

    public static Set<Profile> getAllBlockedProfiles() {
        Set<Profile> blockedProfiles = new HashSet<>();
        List<Schedule> allSchedules = getAllSchedules();
        for (Schedule schedule : allSchedules) {
            List<Timeslot> timeslots = getTimeslotsBySchedule(schedule);
            boolean isOn = false;

            for (Timeslot timeslot : timeslots) {
                if (timeslot.isOn && !timeslot.isOverridden) {
                    isOn = true;
                    break;
                }
            }

            if (isOn) {
                List<Profile> profiles = getProfilesBySchedule(schedule);
                blockedProfiles.addAll(profiles);
            }
        }

        return blockedProfiles;
    }


    /**
     *
     * @param timeslot
     * @return true if the timeslot was turned on
     */
    public static boolean turnOnTimeslot(Timeslot timeslot) {
        if (timeslot == null) {
            return false;
        }

        boolean exists = new Select()
                .from(Timeslot.class)
                .where("id = ?", timeslot.getId())
                .exists();
        if (exists) {
            new Update(Timeslot.class)
                    .set("is_on = 1")
                    .where("id = ?", timeslot.getId())
                    .execute();
            return true;
        } else {
            return false;
        }
    }

    public static boolean turnOffTimeslot(Timeslot timeslot) {
        if (timeslot == null) {
            return false;
        }

        boolean exists = new Select()
                .from(Timeslot.class)
                .where("id = ?", timeslot.getId())
                .exists();
        if (exists) {
            new Update(Timeslot.class)
                    .set("is_on = 0")
                    .where("id = ?", timeslot.getId())
                    .execute();
            new Update(Timeslot.class)
                    .set("is_overridden = 0")
                    .where("id = ?", timeslot.getId())
                    .execute();
            return true;
        } else {
            return false;
        }
    }

    /**
     *
     * @param profile the profile to be deleted
     * @return false if profile is null, else true
     */
    public static boolean deleteProfile(Profile profile) {
        if (profile == null) {
            return false;
        }
        // Delete the relationships
        new Delete().from(ScheduleProfile.class)
                .where("profile = ?", profile.getId())
                .execute();
        new Delete().from(AppProfile.class)
                .where("profile = ?", profile.getId())
                .execute();
        profile.delete();
        return true;
    }

    public static boolean deleteCalendar(Calendar calendar) {
        if (calendar == null || calendar.calendarName.equals("Default")) {
            return false;
        }

        //if any of the schedules are not in any other calendars, move it to the Default calendar
        for (Schedule s : getSchedulesByCalendar(calendar)) {
            if (getCalendarsBySchedule(s).size() == 1 ) {
                List<Calendar> c = new Select().from(Calendar.class)
                        .where("calendar_name = ?", "Default")
                        .execute();
                associateCalendarAndSchedule(c.get(0), s);
            }
        }

        //delete associations
        new Delete().from(CalendarSchedule.class)
                .where("calendar = ?", calendar.getId())
                .execute();
        calendar.delete();
        return true;
    }

    /**
     *
     * @param schedule - schedule you want to delete
     * @return - if the schedule was successfully deleted
     */
    public static boolean deleteSchedule(Schedule schedule){
        if(schedule==null){
            return false;
        }

        // Delete all associations to this schedule
        deleteScheduleAssociations(schedule);

        // Delete the schedule
        schedule.delete();

        return true;
    }

    /**
     *
     * @param timeslot - to be deleted
     * @return - if the timeslot was successfully deleted
     */
    public static boolean deleteTimeslot(Timeslot timeslot){
        if(timeslot==null){
            return false;
        }

        // Remove intents first
        deleteIntentsFromTimeslot(timeslot);

        // Delete the relationship with schedule
        new Delete().from(ScheduleTimeslot.class)
                .where("timeslot = ?", timeslot.getId())
                .execute();

        // Delete the timeslot
        timeslot.delete();

        return true;
    }

    /**
     *
     * @param timeslot - from which you want to delete the intents
     * @return if intents were successfully deleted
     */
    public static boolean deleteIntentsFromTimeslot(Timeslot timeslot){
        if(timeslot==null){
            return false;
        }

        // Loop through intents and delete
        List<BlockIntent> associatedIntents = DBHelper.getBlockIntentsByTimeslot(timeslot);
        for (BlockIntent bi : associatedIntents) {
            bi.delete();
        }
        return true;
    }



    /**
     *
     * @param schedule, deactivate
     * @return true if schedule was successfully deactivated and false otherwise
     */
    public static boolean toggleScheduleActiveStatus(Schedule schedule, boolean deactivate) {
        if (schedule == null) {
            return false;
        }

        boolean exists = new Select()
                .from(Schedule.class)
                .where("schedule_name = ?", schedule.scheduleName)
                .exists();
        if (exists) {
            if (deactivate) {
                new Update(Schedule.class)

                        .set("is_active = 0")

                        .where("schedule_name = ?", schedule.scheduleName)
                        .execute();
                return true;
            } else {
                new Update(Schedule.class)
                        .set("is_active = 1")

                        .where("schedule_name = ?", schedule.scheduleName)
                        .execute();
                return true;
            }

        } else {
            return false;
        }
    }

    public static boolean toggleCalendarActiveStatus(Calendar calendar, boolean toggle) {
        if (calendar == null) {
            return false;
        }
        boolean exists = new Select()
                .from(Calendar.class)
                .where("calendar_name = ?", calendar.calendarName)
                .exists();
        if (exists) {
            if (!toggle) {
                new Update(Calendar.class)
                        .set("is_active = 0")
                        .where("id = ?", calendar.getId())
                        .execute();
                return true;
            } else {
                new Update(Calendar.class)
                        .set("is_active = 1")
                        .where("id = ?", calendar.getId())
                        .execute();

                return true;
            }

        } else {
            return false;
        }

    }

    /**
     * Gets a list of all "on" timeslots, regardless of if they are overriden or not
     *
     * @return a list of the timeslots that are on
     */
    public static List<Timeslot> getAllOnTimeslots() {
        return new Select().from(Timeslot.class)
                .where("is_on = 1")
                .execute();
    }

    public static boolean toggleTimeslotOverride(Timeslot timeslot, boolean isOverridden) {
        if (timeslot == null) {
            return false;
        }
        new Update(Timeslot.class)
                .set("is_overridden = ?", isOverridden ? 1 : 0)
                .where("id = ?", timeslot.getId())
                .execute();

        if(isOverridden) {
            Log.d("DBH_isOverriden", "here");
            Set<App> apps = new HashSet<>();
            // Get the applicable schedules
            Schedule schedule = getScheduleByTimeslot(timeslot);
            // Get the profiles
            List<Profile> profiles = getProfilesBySchedule(schedule);
            // Get the apps and add to a set (to avoid duplicates)
            for(Profile profile : profiles) {
                List<App> tempApps = getAppsByProfile(profile);
                for(App a : tempApps) {
                    if(!apps.contains(a)) {
                        apps.add(a);
                        new Update(App.class)
                                .set("overrides = ?", a.incrementOverrides())
                                .where("package_name = ?", a.packageName)
                                .execute();

                    }
                }
            }

        }
        return true;
    }


    public static Schedule getScheduleByTimeslot(Timeslot timeslot) {
        Schedule schedule = null;
        if (timeslot != null) {
            schedule = new Select()
                    .from(Schedule.class)
                    .innerJoin(ScheduleTimeslot.class).on("schedules_timeslots.schedule = schedules.id")
                    .where("schedules_timeslots.timeslot = ?", timeslot.getId())
                    .executeSingle();
        }
        return schedule;
    }


    public static void resetAppCount() {
        List<App> apps = getAllApps();
        String updateSet = "block_count = 0, launch_block_count = 0, minutes_blocked = 0";

        for (App app : apps) {
            new Update(App.class)
                    .set(updateSet)
                    .where("id = ?", app.getId())
                    .execute();
        }
    }

    /**********************************************
     *               Statistics
     **********************************************/

    /**
     *
     * @param numSchedules - the number of top schedules you want returned
     * @return numSchedules with the top hours focused
     */
    public static List<Schedule> getTopHoursFocused(int numSchedules){
        List<Schedule> topSchedules = new ArrayList<>();

        // Get all schedules
        ArrayList<Schedule> allSchedules = new ArrayList<>(getAllSchedules());

        // Sort them in descending order of hours focused
        Collections.sort(allSchedules, new HoursFocusedComparator());

        // Determine how big the return list is
        int sizeOfSchedules = allSchedules.size();
        int lengthOfFinal = (sizeOfSchedules<numSchedules) ? sizeOfSchedules : numSchedules;

        for(int i=0; i<lengthOfFinal; i++){
            topSchedules.add(allSchedules.get(i));
        }

        return topSchedules;
    }

    public static List<App> getTopOverrides(int numApps) {
        List<App> topOverrides = new ArrayList<>();

        // Get all apps
        ArrayList<App> allApps = new ArrayList<>(getAllApps());

        // Sort them in descending order of # overrides
        Collections.sort(allApps, new OverrideComparator());

        // Determine how big the return list is
        int sizeOfApps = allApps.size();
        int lengthOfFinal = (sizeOfApps<numApps) ? sizeOfApps : numApps;

        for(int i=0; i<lengthOfFinal; i++){
            topOverrides.add(allApps.get(i));
        }

        return topOverrides;
    }

    public static List<App> getTopLaunchBlocks(int numApps) {
        List<App> topLaunchBlocks = new ArrayList<>();

        // Get all apps
        ArrayList<App> allApps = new ArrayList<>(getAllApps());

        // Sort them in descending order of # overrides
        Collections.sort(allApps, new LaunchBlockComparator());

        // Determine how big the return list is
        int sizeOfApps = allApps.size();
        int lengthOfFinal = (sizeOfApps<numApps) ? sizeOfApps : numApps;

        for(int i=0; i<lengthOfFinal; i++){
            topLaunchBlocks.add(allApps.get(i));
        }

        return topLaunchBlocks;
    }

    public static List<App> getTopNotifsBlocks(int numApps) {
        List<App> topNotifsBlocks = new ArrayList<>();

        // Get all apps
        ArrayList<App> allApps = new ArrayList<>(getAllApps());

        // Sort them in descending order of # overrides
        Collections.sort(allApps, new NotifsBlockComparator());

        // Determine how big the return list is
        int sizeOfApps = allApps.size();
        int lengthOfFinal = (sizeOfApps<numApps) ? sizeOfApps : numApps;

        for(int i=0; i<lengthOfFinal; i++){
            topNotifsBlocks.add(allApps.get(i));
        }

        return topNotifsBlocks;
    }

    public static List<App> getTopMinutesBlocks(int numApps) {
        List<App> topMinutesBlocked = new ArrayList<>();

        // Get all apps
        ArrayList<App> allApps = new ArrayList<>(getAllApps());

        // Sort them in descending order of # overrides
        Collections.sort(allApps, new MinBlockedComparator());

        // Determine how big the return list is
        int sizeOfApps = allApps.size();
        int lengthOfFinal = (sizeOfApps<numApps) ? sizeOfApps : numApps;

        for(int i=0; i<lengthOfFinal; i++){
            topMinutesBlocked.add(allApps.get(i));
        }

        return topMinutesBlocked;
    }



    public static List<Profile> getMaxProfileUses(int numProfiles) {
        List<Profile> topUses = new ArrayList<>();

        // Get all profiles
        ArrayList<Profile> allProfiles = new ArrayList<>(getAllProfiles());
        for(Profile profile : allProfiles) {
            setNumProfileUses(profile);
        }

        // Sort them in descending order of # overrides
        Collections.sort(allProfiles, new ProfileUseComparator());

        // Determine how big the return list is
        int sizeOfProfiles = allProfiles.size();
        int lengthOfFinal = (sizeOfProfiles < numProfiles) ? sizeOfProfiles : numProfiles;

        for(int i = 0; i < lengthOfFinal; i++) {
            topUses.add(allProfiles.get(i));
        }

        return topUses;
    }

    /***** BADGES *****/
    public static boolean updateBadge(Badge badge, double amount, Context context) {
        if (badge == null) {
            return false;
        }

        if(badge.isEarned) {
            return true;
        }

        String date = new SimpleDateFormat("MM/dd/yyyy").format(java.util.Calendar.getInstance().getTime());
        double dataEarned = badge.dataEarned;
        dataEarned += amount;
        new Update(Badge.class)
                .set("data_earned = ?", dataEarned)
                .where("badge_name = ?", badge.badgeName)
                .execute();

        if(dataEarned >= badge.dataGoal) {
            new Update(Badge.class)
                    .set("is_earned = 1")
                    .where("badge_name = ?", badge.badgeName)
                    .execute();

            new Update(Badge.class)
                    .set("date_earned = ?", date)
                    .where("badge_name = ?", badge.badgeName)
                    .execute();

            StringBuilder sb = new StringBuilder();
            sb.append("You have unlocked " + badge.badgeName + "!");
            Utility.sendNotification(context, sb, "Badge Unlocked!");
        }

        return true;
    }

    public static List<Badge> getBadgesByCategory(String category) {
        List<Badge> badges = null;
        badges = new Select()
                .from(Badge.class)
                .where("category = ?", category)
                .execute();
        return badges;


    }

    public static Badge getNextBadgeInCategory(String category, int level) {
        List<Badge> badges = null;
        int nextLevel = level + 1;
        if(nextLevel > 6) {
            return null;
        }
        badges = new Select()
                .from(Badge.class)
                .where("category = ? AND level = ?", category, nextLevel)
                .execute();

        return badges.get(0);
    }

}

