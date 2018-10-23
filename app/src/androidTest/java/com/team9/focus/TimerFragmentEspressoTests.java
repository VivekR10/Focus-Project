package com.team9.focus;

import android.os.Build;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.NumberPicker;

import com.team9.focus.activities.MainActivity;
import com.team9.focus.fragments.TimerFragment;

import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.isDialog;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anything;

/**
 * Created by maya on 10/25/17.
 */

@RunWith(AndroidJUnit4.class)
public class TimerFragmentEspressoTests {
    @Rule
    public ActivityTestRule<MainActivity> mMainActivityRule =
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

    /**
     * Checks to see if the timer view has a Choose Profiles title, a list of profiles,
     * a Set Timer Length title, and a number picker for the hour and a number picker
     * for the minute.
     * @STATUS: passed
     */
    @Test
    public void timerViewHasAllElements() {
        // Open the main page. NOTE: you must manually accept the permissions (for now)
        mMainActivityRule.launchActivity(null);

        // Dismiss the Accessibility Window
        allowPermissionsIfNeeded();

        //Navigate to the TimerFragment
        mMainActivityRule.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TimerFragment timerFragment = startTimerFragment();
            }
        });

        // We can see the Choose Profiles text
        onView(withId(R.id.textView5)).check(matches(withText("Choose Profiles")));
        // List of profiles exists
        onData(anything()).inAdapterView(withId(R.id.profileListView)).atPosition(0).check(matches(isDisplayed()));
        // We can select a profile
        onData(anything()).inAdapterView(withId(R.id.profileListView)).atPosition(0).onChildView(withId(R.id.cbProfileName)).perform(click());
        // We can see the Set Timer Length text
        onView(withId(R.id.textView6)).check(matches(withText("Set Timer Length")));
        // Hours picker exists
        onView(withId(R.id.hoursPicker)).check(matches(isDisplayed()));
        // We can pick an hour
        onView(withId(R.id.hoursPicker)).perform(setNumber(1));
        // Check to see if minutes picker exists
        onView(withId(R.id.minutesPicker)).perform(setNumber(30));
        // Check to see if button exists
        onView(withId(R.id.timerStart)).check(matches(isDisplayed()));
    }

    /**
     * Checks to see if the user can create a timer without selecting a profile
     * assuming the user set a valid time
     * @STATUS: passed
     */
    @Test
    public void userMustSelectProfile() {
        // Open the main page. NOTE: you must manually accept the permissions (for now)
        mMainActivityRule.launchActivity(null);

        // Dismiss the Accessibility Window
        allowPermissionsIfNeeded();

        // Navigate to the TimerFragment
        mMainActivityRule.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TimerFragment timerFragment = startTimerFragment();
            }
        });

        // Set valid hour
        onView(withId(R.id.hoursPicker)).perform(setNumber(1));
        // set valid minute
        onView(withId(R.id.minutesPicker)).perform(setNumber(10));
        // Try to start timer
        onView(withId(R.id.timerStart)).perform(click());
        // check for dialogue
        String dialogueTitle = "Invalid Timer";
        onView(withText(dialogueTitle))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));

    }

    /**
     * Checks to see if the user can submit with time greater than 10 hours
     * Assumes user checked a profile
     * @STATUS: passes
     */
    @Test
    public void userMustEnterTimeLess10Hr() {
        // Open the main page. NOTE: you must manually accept the permissions (for now)
        mMainActivityRule.launchActivity(null);

        // Dismiss the Accessibility Window
        allowPermissionsIfNeeded();

        // Navigate to the TimerFragment
        mMainActivityRule.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TimerFragment timerFragment = startTimerFragment();
            }
        });

        // We select a profile
        onData(anything()).inAdapterView(withId(R.id.profileListView))
                .atPosition(0).onChildView(withId(R.id.cbProfileName)).perform(click());

        // Enter: time where hours > 10 and minutes != 0
        onView(withId(R.id.hoursPicker)).perform(setNumber(10));
        onView(withId(R.id.minutesPicker)).perform(setNumber(10));
        // Try to start timer
        onView(withId(R.id.timerStart)).perform(click());
        // check for dialogue
        String dialogueTitle = "Invalid Timer";
        onView(withText(dialogueTitle))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));
        // Close the dialogue
        onView(withText("OK"))
                .inRoot(isDialog())
                .check(matches(isDisplayed()))
                .perform(click());

    }

    /**
     * Checks to see if the user can submit with time less than 10 minutes
     * Assumes user checked a profile
     * @STATUS:
     */
    @Test
    public void userMustEnterTimeGreater10Min() {
        // Open the main page. NOTE: you must manually accept the permissions (for now)
        mMainActivityRule.launchActivity(null);

        // Dismiss Accessibility Window
        allowPermissionsIfNeeded();

        // Navigate to the TimerFragment
        mMainActivityRule.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TimerFragment timerFragment = startTimerFragment();
            }
        });

        // We select a profile
        onData(anything()).inAdapterView(withId(R.id.profileListView))
                .atPosition(0).onChildView(withId(R.id.cbProfileName)).perform(click());


        // Enter: time where hours = 0 and minutes < 10
        onView(withId(R.id.hoursPicker)).perform(setNumber(0));
        onView(withId(R.id.minutesPicker)).perform(setNumber(9));
        // Try to start timer
        onView(withId(R.id.timerStart)).perform(click());

        // check for dialogue
        String dialogueTitle = "Invalid Timer";
        onView(withText(dialogueTitle))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));
        // Close the dialogue
        onView(withText("OK"))
                .inRoot(isDialog())
                .check(matches(isDisplayed()))
                .perform(click());

    }

    private TimerFragment startTimerFragment() {
        // Start on the timer fragment
        MainActivity mainActivity = (MainActivity) mMainActivityRule.getActivity();
        FragmentTransaction transaction = mainActivity.getSupportFragmentManager().beginTransaction();
        TimerFragment timerFragment = new TimerFragment();
        transaction.replace(R.id.content_frame, timerFragment);
        transaction.commitAllowingStateLoss();
        return timerFragment;
    }

    private ViewAction setNumber(final int num) {
        return new ViewAction() {
            @Override
            public void perform(UiController uiController, View view) {
                NumberPicker np = (NumberPicker) view;
                np.setValue(num);

            }

            @Override
            public String getDescription() {
                return "Set the passed number into the NumberPicker";
            }

            @Override
            public Matcher<View> getConstraints() {
                return ViewMatchers.isAssignableFrom(NumberPicker.class);
            }
        };
    }


}
