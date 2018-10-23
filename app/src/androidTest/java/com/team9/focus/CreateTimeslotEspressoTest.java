package com.team9.focus;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.contrib.PickerActions;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.widget.Checkable;

import com.team9.focus.activities.CreateScheduleActivity;
import com.team9.focus.activities.CreateTimeslotActivity;
import com.team9.focus.activities.EditScheduleActivity;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.RootMatchers.isDialog;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.isA;

/**
 * Created by maya on 10/24/17.
 */

@RunWith(AndroidJUnit4.class)
public class CreateTimeslotEspressoTest {
    @Rule
    public ActivityTestRule<CreateTimeslotActivity> mActivityRule =
            new ActivityTestRule<CreateTimeslotActivity>(CreateTimeslotActivity.class, true, false);

    @Rule
    public IntentsTestRule<CreateTimeslotActivity> mIntentsRule =
            new IntentsTestRule<CreateTimeslotActivity>(CreateTimeslotActivity.class, true, false);
    /**
     * Checks what happens when the user does not specify a day but the time is valid
     * Checks for the existence of a dialogue.
     * @STATUS: passes
     */
    @Test
    public void userMustEnterDay() {
        mActivityRule.launchActivity(null);
        //make sure each button is unchecked
        onView(withId(R.id.sundayButton)).perform(scrollTo(), setChecked(false));
        onView(withId(R.id.mondayButton)).perform(scrollTo(), setChecked(false));
        onView(withId(R.id.tuesdayButton)).perform(scrollTo(), setChecked(false));
        onView(withId(R.id.wednesdayButton)).perform(scrollTo(), setChecked(false));
        onView(withId(R.id.thursdayButton)).perform(scrollTo(), setChecked(false));
        onView(withId(R.id.fridayButton)).perform(scrollTo(), setChecked(false));
        onView(withId(R.id.saturdayButton)).perform(scrollTo(), setChecked(false));

        //make sure time is valid
        onView(withId(R.id.startTimePicker)).perform(scrollTo(), PickerActions.setTime(7, 30));
        onView(withId(R.id.endTimePicker)).perform(scrollTo(), PickerActions.setTime(7, 40));
        //Click "schedule" button
        onView(withId(R.id.scheduleButton)).perform(scrollTo(), click());
        String dialogueTitle = "Invalid Input";
        String dialogueMessage = "Please fix the start and end time";
        onView(withText(dialogueTitle))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));
    }

    /**
     * Checks what happens if the user enters a day but an invalid time.
     * Checks for the dialogue.
     * @STATUS: passed 
     */
    @Test
    public void userMustEnterValidTime() {
        mActivityRule.launchActivity(null);
        //make sure at least one box is checked
        onView(withId(R.id.sundayButton)).perform(scrollTo(), setChecked(true));
        //invalid time
        onView(withId(R.id.startTimePicker)).perform(scrollTo(), PickerActions.setTime(7, 30));
        onView(withId(R.id.endTimePicker)).perform(scrollTo(), PickerActions.setTime(7, 20));

        //Click "schedule" button
        onView(withId(R.id.scheduleButton)).perform(scrollTo(), click());
        String dialogueTitle = "Invalid Input";
        String dialogueMessage = "Please fix the start and end time";
        onView(withText(dialogueTitle))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));

    }

    /**
     * Checks if, given valid input, the user is navigated to the create page
     * Checks for the presence of the "scheduleName" editText
     * @STATUS: passed
     */
    @Test
    public void userIsNavigatedToCreateSchedule() {
        Context targetContext = InstrumentationRegistry.getInstrumentation()
                .getTargetContext();
        Intent intent = new Intent(targetContext, CreateTimeslotActivity.class);
        intent.putExtra("sender", "create");
        mIntentsRule.launchActivity(intent);
        //make sure at least one box is checked
        onView(withId(R.id.sundayButton)).perform(scrollTo(), setChecked(true));
        //valid time
        onView(withId(R.id.startTimePicker)).perform(scrollTo(), PickerActions.setTime(7, 30));
        onView(withId(R.id.endTimePicker)).perform(scrollTo(), PickerActions.setTime(7, 40));

        //Click "schedule" button
        onView(withId(R.id.scheduleButton)).perform(scrollTo(), click());

        intended(hasComponent(new ComponentName(targetContext, CreateScheduleActivity.class)));


    }

    /**
     * Checks if, given valid input, the user is navigated to the edit page
     * Checks for the presence of the "scheduleName" editText
     * @STATUS: passed
     */
    @Test
    public void userIsNavigatedToEditSchedule() {
        Context targetContext = InstrumentationRegistry.getInstrumentation()
                .getTargetContext();
        Intent intent = new Intent(targetContext, CreateTimeslotActivity.class);
        intent.putExtra("sender", "edit");
        mIntentsRule.launchActivity(intent);
        //make sure at least one box is checked
        onView(withId(R.id.sundayButton)).perform(scrollTo(), setChecked(true));
        //valid time
        onView(withId(R.id.startTimePicker)).perform(scrollTo(), PickerActions.setTime(7, 30));
        onView(withId(R.id.endTimePicker)).perform(scrollTo(), PickerActions.setTime(7, 40));

        //Click "schedule" button
        onView(withId(R.id.scheduleButton)).perform(scrollTo(), click());

        intended(hasComponent(new ComponentName(targetContext, EditScheduleActivity.class)));


    }

    public static ViewAction setChecked(final boolean checked) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return new Matcher<View>() {
                    @Override
                    public boolean matches(Object item) {
                        return isA(Checkable.class).matches(item);
                    }

                    @Override
                    public void describeMismatch(Object item, Description mismatchDescription) {}

                    @Override
                    public void _dont_implement_Matcher___instead_extend_BaseMatcher_() {}

                    @Override
                    public void describeTo(Description description) {}
                };
            }

            @Override
            public String getDescription() {
                return null;
            }

            @Override
            public void perform(UiController uiController, View view) {
                Checkable checkableView = (Checkable) view;
                checkableView.setChecked(checked);
            }
        };
    }
}


