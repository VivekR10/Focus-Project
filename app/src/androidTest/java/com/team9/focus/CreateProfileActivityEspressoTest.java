package com.team9.focus;

import android.os.Build;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.team9.focus.activities.CreateProfileActivity;
import com.team9.focus.activities.MainActivity;
import com.team9.focus.fragments.ProfileFragment;
import com.team9.focus.models.DBHelper;
import com.team9.focus.models.objects.App;
import com.team9.focus.models.objects.Profile;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
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
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.isDialog;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anything;

/**
 * Created by Vivek on 10/25/2017.
 */
@RunWith(AndroidJUnit4.class)
public class CreateProfileActivityEspressoTest {

    @Rule
    public ActivityTestRule<CreateProfileActivity> mCreateProfileActivityRule =
            new ActivityTestRule<CreateProfileActivity>(CreateProfileActivity.class, true, false);

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
     * Checks for the profile name and list of apps.
     * Checks to see if the user can edit the list of names
     * and can select an app.
     * @STATUS: passes
     */
    @Test
    public void createProfileActivityHasAllElements() {
        //start our activity
        mCreateProfileActivityRule.launchActivity(null);
        String profileName = "New Profile Name";
        //Checks if Profile Name Text View exists
        onView(withId(R.id.etProfileName)).check(matches(isDisplayed()));
        //Tries to type in a new schedule name
        onView(withId(R.id.etProfileName))
                .perform(replaceText(profileName), closeSoftKeyboard());

        // Check that the text was changed.
        onView(withId(R.id.etProfileName)).check(matches(withText(profileName)));

        //Check if the list of apps exists
        onData(anything()).inAdapterView(withId(R.id.appListView)).atPosition(0).check(matches(isDisplayed()));

        //check to see if we can select the first app
        onData(anything()).inAdapterView(withId(R.id.appListView)).atPosition(0).onChildView(withId(R.id.cbAppName)).perform(click());

    }

    /**
     * Checks whether the user can submit a profile with a blank name
     * @STATUS: passed
     */
    @Test
    public void userMustEnterName() {
        mCreateProfileActivityRule.launchActivity(null);
        //clear name
        onView(withId(R.id.etProfileName)).perform(clearText());
        //check the first app
        onData(anything()).inAdapterView(withId(R.id.appListView)).atPosition(0).onChildView(withId(R.id.cbAppName)).perform(click());

        //Click "create profile" button
        onView(withId(R.id.createButton)).perform(click());
        String dialogueTitle = "Invalid Profile";
        onView(withText(dialogueTitle))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));
    }

    /**
     * Checks whether the user can submit a profile with a non-unique name
     * @STATUS: passed
     */
    @Test
    public void userMustEnterUniqueName() {
        mMainActivityRule.launchActivity(null);
        allowPermissionsIfNeeded();
        mCreateProfileActivityRule.launchActivity(null);

        // Get a name from the DB
        List<Profile> allProfiles = DBHelper.getAllProfiles();
        String existingProfileName = allProfiles.get(0).profileName;
        onView(withId(R.id.etProfileName)).perform(replaceText(existingProfileName), closeSoftKeyboard());

        //check the first app
        onData(anything())
                .inAdapterView(withId(R.id.appListView))
                .atPosition(0).onChildView(withId(R.id.cbAppName))
                .perform(click());

        // Click "create profile" button
        onView(withId(R.id.createButton)).perform(click());

        // Check that the error message is present and correct
        String dialogueTitle = "Invalid Profile";
        onView(withText(dialogueTitle))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));
    }

    /**
     * Checks whether a user can submit a profile without checking apps
     * @STATUS: passed
     */
    @Test
    public void userMustCheckApps() {
        mCreateProfileActivityRule.launchActivity(null);
        //enter name
        String profileName = "ProfileName";
        onView(withId(R.id.etProfileName)).perform(replaceText(profileName), closeSoftKeyboard());

        //Click "create profile" button
        onView(withId(R.id.createButton)).perform(click());
        String dialogueTitle = "Invalid Profile";
        onView(withText(dialogueTitle))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));
    }

    /**
     * Checks whether a user can submit a profile with a unique name and at least one app
     * @STATUS: passed
     */
    @Test
    public void userCanCreateProfile() {

        mCreateProfileActivityRule.launchActivity(null);
        //enter name
        String profileName = "UniqueProfileName";
        onView(withId(R.id.etProfileName)).perform(replaceText(profileName), closeSoftKeyboard());

        //check the first app
        onData(anything()).inAdapterView(withId(R.id.appListView)).atPosition(0).onChildView(withId(R.id.cbAppName)).perform(click());


        //Click "create profile" button
        onView(withId(R.id.createButton)).perform(click());

    }

    /**
     * Checks to see if the user has "Focus" as an option when selecting apps.
     * @STATUS:
     */
    @Test
    public void userDoesNotSeeFocus() {
        mCreateProfileActivityRule.launchActivity(null);
        //Create matcher to see if Focus is there
        onView(withId(R.id.appListView)).check(matches(new TypeSafeMatcher<View>() {
            @Override
            public boolean matchesSafely(View view) {
                ListView listView = (ListView) view;
                int count = listView.getAdapter().getCount();
                for(int i = 0; i < count; i++) {
                    App app = (App) listView.getAdapter().getItem(i);
                    if(app.appName.equals("Focus")) return false;
                }

                return true;
            }

            @Override
            public void describeTo(Description description) {

            }
        }));

    }
    /**
     * Checks if the user can start on the Profile Fragment, add a new profile successfully,
     * and is returned to the Profile Fragment. Checks if upon return their profile is listed.
     * @STATUS: passed
     * @TODO: works with more than 1 app? check on real phone
     */
    @Test
    public void userCreatesProfileAndSavesIt() {
        //Open the main page. NOTE: you must manually accept the permissions (for now)
        mMainActivityRule.launchActivity(null);

        // Handle the Accessibility Page
        allowPermissionsIfNeeded();

        //Navigate to the ProfileFragment
        mMainActivityRule.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ProfileFragment profileFragment = startProfileFragment();
            }
        });

        //Check to see if we started the profile fragment
        onView(withId(R.id.tvHeader)).check(matches(withText("Current Profiles")));
        //Add new profile
        onView(withId(R.id.addProfileButton)).perform(click());
        //Give the profile a name
        String profileName = "UserCanOpenProfileFragmentName";
        onView(withId(R.id.etProfileName)).perform(replaceText(profileName), closeSoftKeyboard());

        //CHECKING APPS
        //set how many apps to check
        final int numAppsChecked = 1;
        //check the first numAppsChecked apps
        for(int i = 0; i < numAppsChecked; i++) {
            //Check the first app
            onData(anything()).inAdapterView(withId(R.id.appListView))
                    .atPosition(i).onChildView(withId(R.id.cbAppName)).perform(click());
        }

        //Get the app names of the apps you checked
        final String[] appName = new String[numAppsChecked];
        onView(withId(R.id.appListView)).check(matches(new TypeSafeMatcher<View>() {
            @Override
            public boolean matchesSafely(View view) {
                ListView listView = (ListView) view;

                for(int i = 0; i < numAppsChecked; i++) {
                    App app = (App) listView.getAdapter().getItem(i);
                    appName[i] = app.appName;
                }

                return true;
            }

            @Override
            public void describeTo(Description description) {

            }
        }));


        //Click "create profile" button
        onView(withId(R.id.createButton)).perform(click());
        //Check to see if we went back to the profile fragment
        onView(withId(R.id.tvHeader)).check(matches(withText("Current Profiles")));
        //Check to see if the list contains our new profile

        //Get number of items in the profile list view
        final int[] numberOfProfileAdapterItems = new int[1];
        onView(withId(R.id.lvProfiles)).check(matches(new TypeSafeMatcher<View>() {
            @Override
            public boolean matchesSafely(View view) {
                ListView listView = (ListView) view;

                numberOfProfileAdapterItems[0] = listView.getAdapter().getCount();

                return true;
            }

            @Override
            public void describeTo(Description description) {

            }
        }));
        //check if the profile we just made is in the adapter view
        onData(anything()).inAdapterView(withId(R.id.lvProfiles))
                .atPosition(numberOfProfileAdapterItems[0]-1).onChildView(withId(R.id.tvProfileNameParent))
                .check(matches(withText(profileName)));

        //Open the profile again
        onData(anything()).inAdapterView(withId(R.id.lvProfiles))
                .atPosition(numberOfProfileAdapterItems[0]-1).onChildView(withId(R.id.btnEditProfile))
                .perform(click());

        //Check for all the apps checked
        for(int i = 0; i < numAppsChecked; i++) {
            onData(anything()).inAdapterView(withId(R.id.appList))
                    .atPosition(0)
                    .check(matches(withText(appName[i])));
        }

    }


    private ProfileFragment startProfileFragment() {
        //Start on the profile fragment
        MainActivity mainActivity = (MainActivity) mMainActivityRule.getActivity();
        FragmentTransaction transaction = mainActivity.getSupportFragmentManager().beginTransaction();
        ProfileFragment profileFragment = new ProfileFragment();
        transaction.replace(R.id.content_frame, profileFragment);
        transaction.commitAllowingStateLoss();
        return profileFragment;
    }


}
