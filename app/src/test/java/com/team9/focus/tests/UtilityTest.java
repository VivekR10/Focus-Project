package com.team9.focus.tests;

import android.os.Build;

import com.team9.focus.BuildConfig;
import com.team9.focus.CustomRobolectricRunner;
import com.team9.focus.TestApplication;
import com.team9.focus.TestUtils;
import com.team9.focus.exceptions.AlreadyExistsException;
import com.team9.focus.exceptions.InvalidDateException;
import com.team9.focus.models.objects.App;
import com.team9.focus.utilities.Utility;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

/**
 * Created by jtsui on 10/25/17.
 */

@RunWith(CustomRobolectricRunner.class)
@Config(
        constants = BuildConfig.class,
        application = TestApplication.class,
        sdk = Build.VERSION_CODES.LOLLIPOP,
        packageName = "com.team9.focus")
public class UtilityTest {

    /** Utility Tests **/
    @Test(expected = InvalidDateException.class)
    public void getDayOfWeekFromInt() throws InvalidDateException {
        // Should translate valid days (int) into strings
        int days[] = {1, 2, 3, 4, 5, 6, 7};
        String daysOfWeek[] = {"Su", "M", "Tu", "W", "Th", "F", "Sa"};
        for (int i = 0; i < 7; ++i) {
            assertEquals(Utility.getDayOfWeekFromInt(days[i]), daysOfWeek[i]);
        }

        // Should throw an error if anything else is given
        Utility.getDayOfWeekFromInt(-1);
    }

    @Test(expected = InvalidDateException.class)
    public void getDayOfWeekFromString() throws InvalidDateException {
        // Should translate valid string dates into ints
        int days[] = {1, 2, 3, 4, 5, 6, 7};
        String daysOfWeek[] = {"Su", "M", "Tu", "W", "Th", "F", "Sa"};
        for (int i = 0; i < 7; ++i) {
            assertEquals(Utility.getDayOfWeekFromString(daysOfWeek[i]), days[i]);
        }

        // Should throw an error if any other string is given
        Utility.getDayOfWeekFromString("z");
        // Should throw an error if null is passed in
        Utility.getDayOfWeekFromString(null);
    }


    @Test(expected = InvalidDateException.class)
    public void getHourFromTime() throws InvalidDateException {
        // Should translate valid doubles between 0 and 24 non-inclusive
        assertEquals(Utility.getHourFromTime(23.75), 23);
        assertEquals(Utility.getHourFromTime(0.00), 0);
        assertEquals(Utility.getHourFromTime(12.99), 12);
        assertEquals(Utility.getHourFromTime(0.01), 0);

        // Should throw an InvalidDateException if out of bounds
        Utility.getHourFromTime(-1.00);
        Utility.getHourFromTime(24.00);
        Utility.getHourFromTime(99);
    }

    @Test(expected = InvalidDateException.class)
    public void getMinFromTime() throws InvalidDateException {
        // Should translate valid doubles between 0 and 24 non-inclusive
        assertEquals(Utility.getMinFromTime(0.75), 45);
        assertEquals(Utility.getMinFromTime(0.00), 0);
        assertEquals(Utility.getMinFromTime(23.50), 30);

        // Should throw an InvalidDateException if out of bounds
        Utility.getMinFromTime(-23.00);
        Utility.getMinFromTime(-0.01);
        Utility.getMinFromTime(150);
    }

    @Test
    public void getAppFromPackageName() throws AlreadyExistsException {
        // Null or empty packagename should return null
        assertNull(Utility.getAppFromPackageName(null));
        assertNull(Utility.getAppFromPackageName(""));

        // Create test applications and save them to the database.
        // Check if getAppFromPackageName returns the app whose packagename we passed in
        // as a parameter
        List<App> apps = TestUtils.createTestApplications();
        for (App app : apps) {
            assertEquals(app, Utility.getAppFromPackageName(app.packageName));
        }
    }
}
