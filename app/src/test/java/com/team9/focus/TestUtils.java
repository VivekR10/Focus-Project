package com.team9.focus;

import com.team9.focus.exceptions.AlreadyExistsException;
import com.team9.focus.models.DBHelper;
import com.team9.focus.models.objects.App;
import com.team9.focus.models.objects.Calendar;
import com.team9.focus.models.objects.Profile;
import com.team9.focus.models.objects.Schedule;
import com.team9.focus.models.objects.Timeslot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by jtsui on 10/25/17.
 */

public class TestUtils {

    /**
     * Helper function to create 10 Test App objects
     * @return List of App objects created
     * @throws AlreadyExistsException
     */
    public static List<App> createTestApplications() throws AlreadyExistsException {
        List<App> apps = new ArrayList<>();
        for (int i = 0; i < 10; ++i) {
            apps.add(DBHelper.createApp("Application_" + i, "Package_" + i));
        }
        return apps;
    }

    /**
     * Helper function to create 10 Test Profile objects
     * Also associates each of the 10 Test Profile objects with one unique app.
     * @return List of Profile objects created
     * @throws AlreadyExistsException
     */
    public static List<Profile> createTestProfiles() throws AlreadyExistsException {
        List<Profile> profiles = new ArrayList<>();
        for (int i = 0; i < 10; ++i) {
            DBHelper.saveProfile("Profile_" + i,
                    new ArrayList<App>(Arrays.asList(DBHelper.createApp("App_" + i, "Package_" + i))));
            profiles.add(DBHelper.getProfileByName("Profile_" + i));
        }
        return profiles;
    }

    /**
     * Helper function to create a list of three timeslots that are all not on.
     * @return List of Timeslot objects created
     */
    public static List<Timeslot> createTestTimeslots() {
        List<Timeslot> timeslots = new ArrayList<>();
        timeslots.add(DBHelper.createTimeslot("1", 10, 10, 11, 11, false));
        timeslots.add(DBHelper.createTimeslot("2", 11, 11, 12, 12, false));
        timeslots.add(DBHelper.createTimeslot("3", 12, 12, 13, 13, false));
        return timeslots;
    }

    /**
     * Helper function to create a list of schedules. Each schedule will be associated
     * with all of the profiles created by createTestProfiles and will have all of the timeslots
     * created by createTestTimeslots.
     * @return List of Schedule objects created
     * @throws AlreadyExistsException
     */
    public static List<Schedule> createTestSchedules() throws AlreadyExistsException {
        List<Profile> profiles = createTestProfiles();
        List<Timeslot> timeslots = createTestTimeslots();
        List<Calendar> calendars = new ArrayList<>(Arrays.asList(createDefaultCalendar()));
        List<Schedule> schedules = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            String name = "Schedule_" + i;
            DBHelper.saveSchedule(name, timeslots, profiles, calendars);
            schedules.add(DBHelper.getScheduleByName(name));
        }
        return schedules;
    }

    /**
     * Helper function to create the default calendar
     * @return The default calendar
     */
    public static Calendar createDefaultCalendar() {
        DBHelper.saveCalendar("Default", null);
        return DBHelper.getCalendarByName("Default");
    }
}
