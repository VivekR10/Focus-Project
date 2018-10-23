package com.team9.focus;

import android.content.Intent;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.intent.Intents;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.team9.focus.activities.CreateCalendarActivity;
import com.team9.focus.activities.MainActivity;
import com.team9.focus.fragments.CalendarFragment;
import com.team9.focus.models.DBHelper;
import com.team9.focus.models.objects.Calendar;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.RootMatchers.isDialog;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

/**
 * Created by sophi on 10/25/2017.
 */

@RunWith(AndroidJUnit4.class)
public class CreateCalendarActivityEspressoTest {
    private final String NO_NAME = "You must name your calendar.";
    private final String NOT_UNIQUE = "A calendar with this name already exists.";

    @Rule
    public ActivityTestRule<CreateCalendarActivity> mActivityRule =
            new ActivityTestRule<CreateCalendarActivity>(CreateCalendarActivity.class, true, false);
    public ActivityTestRule<MainActivity> mMainRule =
            new ActivityTestRule<MainActivity>(MainActivity.class, true, false);

    @Test
    /**
     * Checks whether there is an element where the user can update the calendar name.
     * @STATUS: passes
     */
    public void userCanEnterCalendarName() {
        //start our activity
        mActivityRule.launchActivity(null);
        String calendarName = "New Calendar Name";
        //Checks if view exists
        onView(withId(R.id.etCalendarName)).check(matches(isDisplayed()));
        //Tries to type in a new schedule name
        onView(withId(R.id.etCalendarName))
                .perform(replaceText(calendarName), closeSoftKeyboard());

        // Check that the text was changed.
        onView(withId(R.id.etCalendarName)).check(matches(withText(calendarName)));
    }

    @Test
    /**
     * Checks whether the user can submit an empty calendar name.
     * Checks for an alert dialogue.
     * @STATUS: passes
     */
    public void calendarNameCannotBeEmpty() {
        mActivityRule.launchActivity(null);
        onView(withId(R.id.etCalendarName)).perform(clearText(), closeSoftKeyboard());
        onView(withId(R.id.saveButton)).perform(click());
        onView(withText(NO_NAME)).check(matches(isDisplayed()));
    }

    @Test
    /*
     * Checks whether user can submit a duplicate calendar name.
     * Checks for alert dialogue
     * @STATUS: passes
     */
    public void calendarNameMustBeUnique() {
        mMainRule.launchActivity(null);
        //allowPermissionsIfNeeded();
        mActivityRule.launchActivity(null);

        // Get a name from the DB
        List<Calendar> calendars = DBHelper.getAllCalendars();
        String existingName = calendars.get(0).calendarName;
        onView(withId(R.id.etCalendarName)).perform(replaceText(existingName), closeSoftKeyboard());

        // Click save
        onView(withId(R.id.saveButton)).perform(click());

        // Check if the dialog box is up
        onView(withText(NOT_UNIQUE))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));
    }

    /*
     * Checks if save button brings user back to calendar fragment, given calendar is valid
     * Not a test for the database
     * @STATUS: fails
     */
   /* public void validCalendarSave() {
        Intents.init();
        //launch fragment?
        mActivityRule.launchActivity(new Intent());
        String name = "New Calendar Name";
        onView(withId(R.id.etCalendarName)).perform(replaceText(name), closeSoftKeyboard());
        onView(withId(R.id.saveButton)).perform(click());
        intended(hasComponent(CalendarFragment.class.getName()));
        Intents.release();
    }*/

}
