package com.team9.focus;

import android.content.Intent;

import android.os.Build;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.intent.Intents;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiSelector;
import android.support.v4.app.FragmentTransaction;

import com.team9.focus.R;
import com.team9.focus.activities.CreateCalendarActivity;
import com.team9.focus.activities.DayViewActivity;
import com.team9.focus.activities.MainActivity;
import com.team9.focus.activities.WeekViewActivity;
import com.team9.focus.fragments.CalendarFragment;
import com.team9.focus.models.DBHelper;
import com.team9.focus.models.objects.Calendar;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.RootMatchers.isDialog;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.not;

/**
 * Created by sophi on 10/28/2017.
 */

@RunWith(AndroidJUnit4.class)
public class CalendarFragmentEspressoTest {
    private final String DELETING_DEFAULT = "You cannot delete the default calendar.";
    private final String DELETING_ACTIVE = "Please deactivate this calendar before deleting.";
    @Rule
    public ActivityTestRule<MainActivity> mMainActivityRule =
            new ActivityTestRule<MainActivity>(MainActivity.class, true, false);

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
    /*
     * Checks that a user cannot delete an active schedule
     * If the user deactivates it, check that it deletes
     * @STATUS: passes
     */
    public void userMustDeactivateToDelete() {
        mMainActivityRule.launchActivity(null);
        allowPermissionsIfNeeded();
        String name = "new calendar";

        //create a new calendar
        DBHelper.saveCalendar(name, DBHelper.getAllSchedules());

        mMainActivityRule.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                CalendarFragment calendarFragment = startCalendarFragment();
            }
        });

        //delete new calendar
        onData(instanceOf(Calendar.class))
                .inAdapterView(withId(R.id.lvCalendars))
                .atPosition(1).onChildView(withId(R.id.deleteButton))
                .perform(click());

        //check dialogue box
        onView(withText(DELETING_ACTIVE))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));

        //press ok
        onView(withId(android.R.id.button1)).perform(click());

        //toggle to inactive
        onData(instanceOf(Calendar.class))
                .inAdapterView(withId(R.id.lvCalendars))
                .atPosition(1).onChildView(withId(R.id.swToggle))
                .perform(click());

        //try to delete calendar again
        onData(instanceOf(Calendar.class))
                .inAdapterView(withId(R.id.lvCalendars))
                .atPosition(1).onChildView(withId(R.id.deleteButton))
                .perform(click());

        //check that the calendar is deleted from the list
        onView(withId(R.id.lvCalendars))
                .check(matches(not(hasDescendant(withText(name)))));
    }

    @Test
    /*
     * Checks that a user cannot delete the default schedule
     * @STATUS: passes
     */
    public void userCannotDeleteDefault() {
        mMainActivityRule.launchActivity(null);
        allowPermissionsIfNeeded();

        mMainActivityRule.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                CalendarFragment calendarFragment = startCalendarFragment();
            }
        });

        //try to delete default calendar
        onData(instanceOf(Calendar.class))
                .inAdapterView(withId(R.id.lvCalendars))
                .atPosition(0).onChildView(withId(R.id.deleteButton))
                .perform(click());

        //check dialogue box
        onView(withText(DELETING_DEFAULT))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));

    }

    @Test
    /*
     * Checks that the create new calendar launches new activity
     * @STATUS: passes
     */
    public void createCalendar() {
        Intents.init();

        mMainActivityRule.launchActivity(null);
        allowPermissionsIfNeeded();

        mMainActivityRule.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                CalendarFragment calendarFragment = startCalendarFragment();
            }
        });

        onView(withId(R.id.floatingActionButton)).perform(click());
        intended(hasComponent(CreateCalendarActivity.class.getName()));
        Intents.release();
    }

    @Test
    /*
     * Checks that the day view button launches new activity
     * @STATUS: passes
     */
    public void pressDayView() {
        Intents.init();

        mMainActivityRule.launchActivity(null);
        allowPermissionsIfNeeded();

        mMainActivityRule.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                CalendarFragment calendarFragment = startCalendarFragment();
            }
        });

        onView(withId(R.id.dayViewButton)).perform(click());
        intended(hasComponent(DayViewActivity.class.getName()));
        Intents.release();
    }

    @Test
    /*
     * Checks that the week view button launches new activity
     * @STATUS: passes
     */
    public void pressWeekView() {
        Intents.init();

        mMainActivityRule.launchActivity(null);
        allowPermissionsIfNeeded();

        mMainActivityRule.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                CalendarFragment calendarFragment = startCalendarFragment();
            }
        });

        onView(withId(R.id.weekViewButton)).perform(click());
        intended(hasComponent(WeekViewActivity.class.getName()));
        Intents.release();
    }


    private CalendarFragment startCalendarFragment() {
        //Start on the timer fragment
        MainActivity mainActivity = (MainActivity) mMainActivityRule.getActivity();
        FragmentTransaction transaction = mainActivity.getSupportFragmentManager().beginTransaction();
        CalendarFragment calendarFragment = new CalendarFragment();
        transaction.replace(R.id.content_frame, calendarFragment);
        transaction.commitAllowingStateLoss();
        return calendarFragment;
    }
}
