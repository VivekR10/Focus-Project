package com.team9.focus.tests;

import android.content.Context;
import android.os.Build;

import com.team9.focus.BuildConfig;
import com.team9.focus.CustomRobolectricRunner;
import com.team9.focus.TestApplication;
import com.team9.focus.TestUtils;
import com.team9.focus.exceptions.AlreadyExistsException;
import com.team9.focus.models.DBHelper;
import com.team9.focus.models.objects.App;
import com.team9.focus.models.objects.Calendar;
import com.team9.focus.models.objects.Profile;
import com.team9.focus.models.objects.Schedule;
import com.team9.focus.models.objects.Timeslot;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Created by jtsui on 10/25/17.
 */

@RunWith(CustomRobolectricRunner.class)
@Config(
        constants = BuildConfig.class,
        application = TestApplication.class,
        sdk = Build.VERSION_CODES.LOLLIPOP,
        packageName = "com.team9.focus")
public class DBHelperTest {
    private static Context mContext;

    private final String TEST_APPLICATION_NAME = "Test Application";
    private final String TEST_PACKAGE_NAME = "Test Package Name";
    private final String TEST_SCHEDULE_NAME = "Test Schedule";
    private final String TEST_PROFILE_1_NAME = "Profile 1";
    private final String TEST_PROFILE_2_NAME = "Profile 2";
    private final String TEST_PROFILE_3_NAME = "Profile 3";
    private final String TEST_CALENDAR_1_NAME = "Test Calendar 1";
    private final String TEST_CALENDAR_2_NAME = "Test Calendar 2";
    private final String TEST_CALENDAR_3_NAME = "Test Calendar 3";

    @Before
    public void createDefaultCalendar() {
        TestUtils.createDefaultCalendar();
    }

    /** Tests for Apps **/

    @Test(expected = AlreadyExistsException.class)
    public void createApp() throws AlreadyExistsException {
        // Normal flow of creating an app
        App app = DBHelper.createApp(TEST_APPLICATION_NAME, TEST_PACKAGE_NAME);
        assertEquals(app.appName, TEST_APPLICATION_NAME);
        assertEquals(app.packageName, TEST_PACKAGE_NAME);

        // Creating an app with null/empty name should return null
        assertNull(DBHelper.createApp(null, TEST_PACKAGE_NAME));
        assertNull(DBHelper.createApp("", TEST_PACKAGE_NAME));
        assertNull(DBHelper.createApp(TEST_APPLICATION_NAME, null));
        assertNull(DBHelper.createApp(TEST_APPLICATION_NAME, ""));

        // Creating an app with an already existing app should throw exception
        App duplicateApp = DBHelper.createApp(TEST_APPLICATION_NAME, TEST_PACKAGE_NAME);
    }


    @Test
    public void getAllApps() throws AlreadyExistsException {
        // Test for no applications
        List<App> apps = DBHelper.getAllApps();
        assertNotNull(apps);
        assertTrue(apps.isEmpty());

        // Adding 10 test applications to the Database
        for (int i = 0; i < 10; ++i) {
            DBHelper.createApp("Application_" + i, "Package_" + i);
        }
        apps = DBHelper.getAllApps();
        assertEquals(apps.size(), 10);
        for (int i = 0; i < 10; ++i) {
            assertEquals(apps.get(i).appName, "Application_" + i);
            assertEquals(apps.get(i).packageName, "Package_" + i);
        }

    }

    @Test
    public void getAppByName() throws AlreadyExistsException {
        DBHelper.createApp(TEST_APPLICATION_NAME, TEST_PACKAGE_NAME);
        App app = DBHelper.getAppByName(TEST_APPLICATION_NAME);
        assertEquals(app.appName, TEST_APPLICATION_NAME);
        assertEquals(app.packageName, TEST_PACKAGE_NAME);
        // getAppByName should return null if the app doesn't exist
        App nullApp = DBHelper.getAppByName("Nonexistent Application");
        assertNull(nullApp);
    }

    /** Test for Profiles **/
    /**
     * Creates test apps and attempts to save a profile in the database
     * It verifies that the apps that were saved are correctly associated with the profile
     * @throws AlreadyExistsException
     */
    @Test
    public void saveProfile() throws AlreadyExistsException {
        // Valid parameters
        List<App> testApps = TestUtils.createTestApplications();
        boolean result = DBHelper.saveProfile(TEST_PROFILE_1_NAME, testApps);
        assertTrue(result);

        Profile profile_1 = DBHelper.getProfileByName(TEST_PROFILE_1_NAME);
        Assert.assertEquals(profile_1.profileName, TEST_PROFILE_1_NAME);

        List<App> profileApps = DBHelper.getAppsByProfile(profile_1);
        assertNotNull(profileApps);
        Assert.assertEquals(profileApps, testApps);

        // Invalid parameters
        // profilename cannot be null
        assertFalse(DBHelper.saveProfile(null, testApps));
        // profilename cannot be empty
        assertFalse(DBHelper.saveProfile("", testApps));
        // list cannot be null
        assertFalse(DBHelper.saveProfile(TEST_PROFILE_2_NAME, null));
        // list cannot be empty
        assertFalse(DBHelper.saveProfile(TEST_PROFILE_2_NAME, new ArrayList<App>()));
        // Cannot create a profile with the same name
        assertFalse(DBHelper.saveProfile(TEST_PROFILE_1_NAME, testApps));
    }

    @Test
    public void updateProfile() throws AlreadyExistsException {
        /** Normal flow w/ valid parameters **/
        List<App> testApps = TestUtils.createTestApplications();
        DBHelper.saveProfile(TEST_PROFILE_1_NAME, testApps);
        Profile profile = DBHelper.getProfileByName(TEST_PROFILE_1_NAME);

        // newApps contains the list of apps to be associated with the profile
        List<App> newApps = new ArrayList<>();
        for (int i = 0; i < 10; ++i) {
            newApps.add(DBHelper.createApp("NewApplication_" + i, "NewPackage_" + i));
        }

        // Attempt to update the profile, setting name to the value of TEST_PROFILE_1_NAME
        // and the apps to newApps
        boolean result = DBHelper.updateProfile(profile, TEST_PROFILE_2_NAME, newApps);
        assertTrue(result);
        profile = DBHelper.getProfileByName(TEST_PROFILE_2_NAME);
        assertNotNull(profile);
        Assert.assertEquals(profile.profileName, TEST_PROFILE_2_NAME);

        // Check that the list of apps were updated
        List<App> profileApps = DBHelper.getAppsByProfile(profile);
        assertNotNull(profileApps);
        assertEquals(profileApps, newApps);

        /** Invalid or null parameters **/
        // Profile cannot be null
        assertFalse(DBHelper.updateProfile(null, TEST_PROFILE_3_NAME, testApps));
        // name cannot be null
        assertFalse(DBHelper.updateProfile(profile, null, testApps));
        // name cannot be empty
        assertFalse(DBHelper.updateProfile(profile, "", testApps));
        // list of apps cannot be empty
        assertFalse(DBHelper.updateProfile(profile, TEST_PROFILE_3_NAME, null));
    }

    @Test
    public void deleteProfile() throws AlreadyExistsException {
        // Cannot delete a null profile
        assertFalse(DBHelper.deleteProfile(null));

        // Create test profile to be deleted
        List<App> testApps = TestUtils.createTestApplications();
        DBHelper.saveProfile(TEST_PROFILE_1_NAME, testApps);
        Profile profile = DBHelper.getProfileByName(TEST_PROFILE_1_NAME);
        // Add it to a schedule to test if deleting a profile deletes it from a schedule
        List<Timeslot> timeslots = TestUtils.createTestTimeslots();
        List<Calendar> calendars = new ArrayList<>(Arrays.asList(DBHelper.getCalendarByName("Default")));
        List<Profile> profiles = new ArrayList<>(Arrays.asList(profile));
        DBHelper.saveSchedule(TEST_SCHEDULE_NAME, timeslots, profiles, calendars);
        Schedule schedule = DBHelper.getScheduleByName(TEST_SCHEDULE_NAME);

        assertTrue(DBHelper.deleteProfile(profile));
        assertTrue(DBHelper.getAppsByProfile(profile).isEmpty());
        // Profile-to-Schedule association should be removed when Profile is deleted
        assertTrue(DBHelper.getProfilesBySchedule(schedule).isEmpty());
    }

    /** Schedule Tests **/

    /**
     * Tests the saving schedule functionality. Tests the edge cases, invalid parameters, and also
     * the valid flow.
     * @throws AlreadyExistsException
     */
    @Test
    public void saveSchedule() throws AlreadyExistsException {

        /** Testing for invalid parameters **/
        // Null schedule name not allowed
        assertFalse(DBHelper.saveSchedule(null, new ArrayList<Timeslot>(),
                new ArrayList<Profile>(), new ArrayList<Calendar>()));
        // Empty schedule name not allowed
        assertFalse(DBHelper.saveSchedule("", new ArrayList<Timeslot>(),
                new ArrayList<Profile>(), new ArrayList<Calendar>()));
        // Null timeslot list not allowed
        assertFalse(DBHelper.saveSchedule(TEST_SCHEDULE_NAME, null,
                new ArrayList<Profile>(), new ArrayList<Calendar>()));
        // Null profile list not allowed
        assertFalse(DBHelper.saveSchedule(null, new ArrayList<Timeslot>(),
                null, new ArrayList<Calendar>()));
        // Null calendar list not allowed
        assertFalse(DBHelper.saveSchedule(null, new ArrayList<Timeslot>(),
                new ArrayList<Profile>(), null));

        /** Testing a valid schedule **/
        List<Profile> profiles = TestUtils.createTestProfiles();
        List<Timeslot> timeslots = TestUtils.createTestTimeslots();
        Calendar defaultCalendar = DBHelper.getCalendarByName("Default");
        assertTrue(DBHelper.saveSchedule(TEST_SCHEDULE_NAME, timeslots, profiles,
                new ArrayList(Arrays.asList(defaultCalendar))));
        Schedule schedule = DBHelper.getScheduleByName(TEST_SCHEDULE_NAME);
        assertEquals(TEST_SCHEDULE_NAME, schedule.scheduleName);
        assertEquals(timeslots, DBHelper.getTimeslotsBySchedule(schedule));
        assertEquals(profiles, DBHelper.getProfilesBySchedule(schedule));
        assertEquals(1, DBHelper.getCalendarsBySchedule(schedule).size());
        assertEquals(defaultCalendar, DBHelper.getCalendarsBySchedule(schedule).get(0));
    }

    /**
     * Tests deleting schedule and the relations between profiles/timeslots/calendars after deletion
     * Also tests for invalid parameters/edge cases
     * @throws AlreadyExistsException
     */
    @Test
    public void deleteSchedule() throws AlreadyExistsException {
        /** Cannot delete null schedule **/
        assertFalse(DBHelper.deleteSchedule(null));

        /** Create a schedule to be deleted **/
        List<Profile> profiles = TestUtils.createTestProfiles();
        List<Timeslot> timeslots = TestUtils.createTestTimeslots();
        Calendar defaultCalendar = DBHelper.getCalendarByName("Default");
        DBHelper.saveSchedule(TEST_SCHEDULE_NAME, timeslots, profiles,
                new ArrayList(Arrays.asList(defaultCalendar)));
        Schedule schedule = DBHelper.getScheduleByName(TEST_SCHEDULE_NAME);
        /** Delete schedule and check for all relations **/
        assertTrue(DBHelper.deleteSchedule(schedule));
        assertNull(DBHelper.getScheduleByName(TEST_SCHEDULE_NAME));
        assertTrue(DBHelper.getTimeslotsBySchedule(schedule).isEmpty());
        assertTrue(DBHelper.getProfilesBySchedule(schedule).isEmpty());
        assertTrue(DBHelper.getCalendarsBySchedule(schedule).isEmpty());
        // Assert that the schedule is removed from the default calendar
        assertTrue(DBHelper.getSchedulesByCalendar(defaultCalendar).isEmpty());
    }

    /** Timeslot Tests **/

    @Test
    public void createTimeslot() {
        /** Testing for invalid or null parameters **/
        // Null name not allowed
        assertNull(DBHelper.createTimeslot(null, 1, 30, 2, 30, false));
        // Empty name not allowed
        assertNull(DBHelper.createTimeslot("", 1, 30, 2, 30, false));
        // Negative hours are not allowed
        assertNull(DBHelper.createTimeslot("1", -1, 30, 2, 30, false));
        assertNull(DBHelper.createTimeslot("1", 1, -30, 2, 30, false));
        assertNull(DBHelper.createTimeslot("1", 1, 30, -2, 30, false));
        assertNull(DBHelper.createTimeslot("1", -1, 30, 2, -30, false));
        // Minutes >= 60 are not allowed
        assertNull(DBHelper.createTimeslot("1", 2, 60, 3, 30, false));
        // Ending hours >= 24 not allowed
        assertNull(DBHelper.createTimeslot("1", 1, 30, 24, 00, false));

        /** Valid Timeslot insertion **/
        long timeslotID = DBHelper.createTimeslot("1", 1, 30, 2, 30, false).getId();
        Timeslot timeslot = DBHelper.getTimeslotsByID(timeslotID).get(0);
        assertEquals("1", timeslot.dayOfWeek);
        assertEquals(1, timeslot.startHour);
        assertEquals(30, timeslot.startMinute);
        assertEquals(2, timeslot.endHour);
        assertEquals(30, timeslot.endMinute);
        assertFalse(timeslot.isOn);
    }

    @Test
    public void toggleTimeslot() {
        // Passing in null Timeslot will return false for both turning on and off
        assertFalse(DBHelper.turnOnTimeslot(null));
        assertFalse(DBHelper.turnOffTimeslot(null));
        // Create a timeslot that is initially off
        Timeslot timeslot = DBHelper.createTimeslot("1", 1, 30, 2, 30, false);
        // Turn on the timeslot
        assertTrue(DBHelper.turnOnTimeslot(timeslot));
        assertTrue(DBHelper.getTimeslotsByID(timeslot.getId()).get(0).isOn);
        // Turn back off the timeslot
        assertTrue(DBHelper.turnOffTimeslot(timeslot));
        assertFalse(DBHelper.getTimeslotsByID(timeslot.getId()).get(0).isOn);
    }

    /** Calendar Tests **/
    @Test
    public void saveCalendar() throws AlreadyExistsException {
        /** Testing for invalid or null parameters **/
        assertFalse(DBHelper.saveCalendar(null, new ArrayList<Schedule>()));
        // Empty name not allowed
        assertFalse(DBHelper.saveCalendar("", new ArrayList<Schedule>()));

        /** Creating valid calendar objects **/
        // Calendar with empty ArrayList of schedules
        assertTrue(DBHelper.saveCalendar(TEST_CALENDAR_1_NAME, new ArrayList<Schedule>()));
        Calendar calendar_1 = DBHelper.getCalendarByName(TEST_CALENDAR_1_NAME);
        assertEquals(calendar_1.calendarName, TEST_CALENDAR_1_NAME);
        assertTrue(DBHelper.getSchedulesByCalendar(calendar_1).isEmpty());
        // Calendar with null schedules (allowed, same behavior as above)
        assertTrue(DBHelper.saveCalendar(TEST_CALENDAR_2_NAME, null));
        Calendar calendar_2 = DBHelper.getCalendarByName(TEST_CALENDAR_2_NAME);
        assertEquals(calendar_2.calendarName, TEST_CALENDAR_2_NAME);
        assertTrue(DBHelper.getSchedulesByCalendar(calendar_2).isEmpty());
        // Calendar with list of ten schedules
        List<Schedule> testSchedules = TestUtils.createTestSchedules();
        assertTrue(DBHelper.saveCalendar(TEST_CALENDAR_3_NAME, testSchedules));
        Calendar calendar_3 = DBHelper.getCalendarByName(TEST_CALENDAR_3_NAME);
        assertEquals(testSchedules, DBHelper.getSchedulesByCalendar(calendar_3));

        /** Test for Duplicate calendar names are not allowed **/
        assertFalse(DBHelper.saveCalendar(TEST_CALENDAR_1_NAME, new ArrayList<Schedule>()));
    }

    @Test
    public void deleteCalendar() throws AlreadyExistsException {
        /** Testing for invalid parameters **/
        // Cannot delete null calendar
        assertFalse(DBHelper.deleteCalendar(null));
        // Cannot delete the Default calendar
        Calendar defaultCalendar = DBHelper.getCalendarByName("Default");
        assertFalse(DBHelper.deleteCalendar(defaultCalendar));

        /** Test for deleting a valid calendar **/
        List<Schedule> testSchedules = TestUtils.createTestSchedules();
        DBHelper.saveCalendar(TEST_CALENDAR_1_NAME, testSchedules);
        Calendar calendar = DBHelper.getCalendarByName(TEST_CALENDAR_1_NAME);
        // Check for all schedule-to-calendar relations being deleted
        assertTrue(DBHelper.deleteCalendar(calendar));
        assertNull(DBHelper.getCalendarByName(TEST_CALENDAR_1_NAME));
        assertTrue(DBHelper.getSchedulesByCalendar(calendar).isEmpty());
        // Check that all of these scheduels have been moved to the default calendar
        defaultCalendar = DBHelper.getCalendarByName("Default");
        assertEquals(testSchedules, DBHelper.getSchedulesByCalendar(defaultCalendar));
    }

}
