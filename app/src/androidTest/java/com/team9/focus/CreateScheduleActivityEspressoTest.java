package com.team9.focus;

import android.os.Build;
import android.os.Environment;
import android.os.SystemClock;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.BySelector;

import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;
import android.util.Log;

import com.team9.focus.activities.CreateScheduleActivity;
import com.team9.focus.activities.MainActivity;
import com.team9.focus.models.DBHelper;
import com.team9.focus.models.objects.Calendar;
import com.team9.focus.models.objects.Profile;
import com.team9.focus.models.objects.Schedule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.scrollTo;

import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;

import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.RootMatchers.isDialog;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.instanceOf;

/**
 * Created by maya on 10/24/17.
 */

@RunWith(AndroidJUnit4.class)
public class CreateScheduleActivityEspressoTest {
    //rule for CreateScheduleActivity
    @Rule
    public ActivityTestRule<CreateScheduleActivity> mActivityRule =
            new ActivityTestRule<CreateScheduleActivity>(CreateScheduleActivity.class, true, false);
    public ActivityTestRule<MainActivity> mMainRule =
            new ActivityTestRule<MainActivity>(MainActivity.class, true, false);

    /**
     * Dismiss the Accessibility window without allowing WindowChangeDetectingService
     */
    private static void allowPermissionsIfNeeded() {
        if (Build.VERSION.SDK_INT >= 23) {
            UiDevice device = UiDevice.getInstance(getInstrumentation());
            UiObject allowPermissions = device.findObject(new UiSelector().text("Accessibility"));
            if (allowPermissions.exists()) {
                device.pressBack();
            }
        }
    }

    @Test
    /**
     * Checks whether there is an element where the user can update the schedule name.
     * @STATUS: passes
     */
    public void userCanEnterScheduleName() {
        //start our activity
        mActivityRule.launchActivity(null);

        SystemClock.sleep(2000);
        String scheduleName = "New Schedule Name";
        //Checks if view exists
        onView(withId(R.id.scheduleName)).check(matches(isDisplayed()));
        //Tries to type in a new schedule name
        onView(withId(R.id.scheduleName))
                .perform(replaceText(scheduleName), closeSoftKeyboard());

        // Check that the text was changed.
        onView(withId(R.id.scheduleName)).check(matches(withText(scheduleName)));
    }

    @Test
    /**
     * Checks whether the user can submit an empty schedule name.
     * Checks for a toast error message.
     * Checks to see if the page proceeds.
     * @STATUS: success
     */
    public void scheduleNameCannotBeEmpty() {
        mMainRule.launchActivity(null);
        allowPermissionsIfNeeded();
        mActivityRule.launchActivity(null);
        //SystemClock.sleep(2000);
        onView(withId(R.id.scheduleName)).perform(clearText(), closeSoftKeyboard());

        // Select a Profile
        onData(instanceOf(Profile.class))
                .inAdapterView(withId(R.id.profileListView))
                .atPosition(0).onChildView(withId(R.id.cbProfileName))
                .perform(scrollTo()).perform(click());

        // Select a Calendar
        onData(instanceOf(Calendar.class))
                .inAdapterView(withId(R.id.calendarListView))
                .atPosition(0).onChildView(withId(R.id.cbCalendarName))
                .perform(scrollTo()).perform(click());

        // Click submit
        onView(withId(R.id.saveButton)).perform(scrollTo()).perform(click());

        // Check if the dialog box is up
        String dialogueTitle = "Invalid Name";
        onView(withText(dialogueTitle))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));

        // Click ok on dialog
        onView(withId(android.R.id.button1)).perform(click());

        // Make sure it didn't navigate away
        // @TODO: Uncomment this to check that the page doesn't change
        // onView(withId(R.id.createTimeslotButton)).check(matches(isDisplayed()));
    }

    @Test
    /**
     * Checks whether the user can submit a schedule with no calendars.
     * Checks for a dialog error message
     * Checks to see if the page proceeds.
     * @STATUS: passes
     */
    public void calendarCannotBeEmpty() {
        mMainRule.launchActivity(null);
        allowPermissionsIfNeeded();
        mActivityRule.launchActivity(null);

        // Allow time for the DB to populate
        SystemClock.sleep(2000);

        // Set a schedule name
        onView(withId(R.id.scheduleName)).perform(replaceText("Test Schedule"), closeSoftKeyboard());

        //Select a Profile
        onData(instanceOf(Profile.class))
                .inAdapterView(withId(R.id.profileListView))
                .atPosition(0).onChildView(withId(R.id.cbProfileName))
                .perform(scrollTo()).perform(click());

        // Click submit
        onView(withId(R.id.saveButton)).perform(scrollTo()).perform(click());

        // Check if the dialog box is correct
        String dialogueTitle = "No Calendars";
        onView(withText(dialogueTitle))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));

        // Click ok on dialog
        onView(withId(android.R.id.button1)).perform(click());

        // Make sure it didn't navigate away
        // @TODO: Uncomment this to check that the page doesn't change
        // onView(withId(R.id.createTimeslotButton)).check(matches(isDisplayed()));
    }

    @Test
    /**
     * Checks whether the user can submit a non-unique schedule name.
     * Checks for a dialog box error message
     * Checks to see if the page proceeds.
     * @STATUS: passes
     */
    public void scheduleNameMustBeUnique() {
        mMainRule.launchActivity(null);
        allowPermissionsIfNeeded();
        mActivityRule.launchActivity(null);

        // Allow time for DB to populate
        //SystemClock.sleep(2000);

        // Get a name from the DB
        List<Schedule> allSchedules = DBHelper.getAllSchedules();
        String existingScheduleName = allSchedules.get(0).scheduleName;
        onView(withId(R.id.scheduleName)).perform(replaceText(existingScheduleName), closeSoftKeyboard());

        // Select a Profile
        onData(instanceOf(Profile.class))
                .inAdapterView(withId(R.id.profileListView))
                .atPosition(0).onChildView(withId(R.id.cbProfileName))
                .perform(scrollTo()).perform(click());

        // Select a Calendar
        onData(instanceOf(Calendar.class))
                .inAdapterView(withId(R.id.calendarListView))
                .atPosition(0).onChildView(withId(R.id.cbCalendarName))
                .perform(scrollTo()).perform(click());

        // Click submit
        onView(withId(R.id.saveButton)).perform(scrollTo()).perform(click());

        // Check if the dialog box is up
        String dialogueTitle = "Invalid Name";
        onView(withText(dialogueTitle))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));

        // Click ok on dialog
        onView(withId(android.R.id.button1)).perform(click());

        // Make sure it didn't navigate away
        // @TODO: Uncomment this to check that the page doesn't change
        // onView(withId(R.id.createTimeslotButton)).check(matches(isDisplayed()));
    }

    @Test
    /**
     * Checks whether there is a profile list to choose from
     * @STATUS: passes
     */
    public void scheduleIncludesExistingProfiles() {
        mMainRule.launchActivity(null);
        allowPermissionsIfNeeded();
        mActivityRule.launchActivity(null);

        // Check that there's a profile list view
        onView(withId(R.id.profileListView)).check(matches(isDisplayed()));
    }
}
