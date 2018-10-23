package com.team9.focus;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.intent.Intents;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;

import android.support.test.uiautomator.UiScrollable;

import android.support.test.uiautomator.UiSelector;
import android.support.test.uiautomator.Until;

import com.team9.focus.activities.CreateCalendarActivity;
import com.team9.focus.activities.MainActivity;
import com.team9.focus.fragments.CalendarFragment;
import com.team9.focus.models.DBHelper;
import com.team9.focus.models.objects.Calendar;

import org.junit.Before;
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




import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Created by vivek on 10/28/17.
 */

@RunWith(AndroidJUnit4.class)
public class NotificationTest {

    private static final String BASIC_SAMPLE_PACKAGE
            = "com.example.android.testing.uiautomator.BasicSample";
    private static final int LAUNCH_TIMEOUT = 5000;
    private static final String STRING_TO_BE_TYPED = "UiAutomator";
    private UiDevice mDevice;


    @Test
    public void checkMessengerNotificationIsPresent() throws Exception {

        Context appContext = InstrumentationRegistry.getTargetContext();
        assertEquals("com.team9.focus", appContext.getPackageName());

        //Starts MainActivity
        appContext.startActivity(new Intent(appContext, MainActivity.class));

        //Initializes Device (use a physical phone if possible)
        UiDevice mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());

        //Opens notification drawer
        mDevice.openNotification();

        //Finds the notification drawer UI object
        UiSelector panelOpenFinder = new UiSelector().packageName("com.android.systemui").className("android.widget.FrameLayout").resourceId(
                "com.android.systemui:id/notification_panel");
        UiObject panelUiObject = mDevice.findObject(panelOpenFinder);

        //Checks to see if the notification drawer is open
        assertTrue(panelUiObject.exists());

        //Finds the Facebook notification on the screen
        UiSelector notificationFinder = new UiSelector().packageName("com.facebook.orca");
        UiObject notificationUiObject = mDevice.findObject(notificationFinder);

        //Checks to see if the notification exists
        assertTrue(notificationUiObject.exists());

    }

    @Test
    public void checkMessengerNotificationIsNotPresent() throws Exception {

        Context appContext = InstrumentationRegistry.getTargetContext();
        assertEquals("com.team9.focus", appContext.getPackageName());

        //Starts MainActivity
        appContext.startActivity(new Intent(appContext, MainActivity.class));

        //Initializes Device (use a physical phone if possible)
        UiDevice mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());

        //Opens notification drawer
        mDevice.openNotification();

        //Finds the notification drawer UI object
        UiSelector panelOpenFinder = new UiSelector().packageName("com.android.systemui").className("android.widget.FrameLayout").resourceId(
                "com.android.systemui:id/notification_panel");
        UiObject panelUiObject = mDevice.findObject(panelOpenFinder);

        //Checks to see if the notification drawer is open
        assertTrue(panelUiObject.exists());

        //Finds the Facebook notification on the screen
        UiSelector notificationFinder = new UiSelector().packageName("com.facebook.orca");
        UiObject notificationUiObject = mDevice.findObject(notificationFinder);

        //Checks to see if the notification object does not exist
        assertFalse(notificationUiObject.exists());

    }


    @Test
    public void checkAppLaunch() throws Exception {

        Context appContext = InstrumentationRegistry.getTargetContext();
        assertEquals("com.team9.focus", appContext.getPackageName());

        //Starts MainActivity
        appContext.startActivity(new Intent(appContext, MainActivity.class));

        Thread.sleep(10000);
        appContext.startActivity(new Intent(appContext, MainActivity.class));

        //Initializes Device (use a physical phone if possible)
        UiDevice mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());


        UiObject drawerButton = mDevice.findObject(new UiSelector().className("android.widget.ImageButton").packageName("com.team9.focus"));
        drawerButton.click();

        UiObject profileButton = mDevice.findObject(new UiSelector().className("android.widget.RelativeLayout").packageName("com.team9.focus").index(1));
        profileButton.click();

        UiObject newProfileButton = mDevice.findObject(new UiSelector().className("android.widget.ImageButton").packageName("com.team9.focus").resourceId("com.team9.focus:id/addProfileButton"));
        newProfileButton.click();

        mDevice.pressBack();



        //UiObject listViewObject = mDevice.findObject(new UiSelector().scrollable(true).className("android.widget.ListView").packageName("com.team9.focus").resourceId("com.team9.focus:id/appListView"));

        UiScrollable listView = new UiScrollable(new UiSelector().scrollable(true).className("android.widget.ListView"));
        UiObject blockedAppLabel = listView.getChildByText(new UiSelector().className("android.widget.TextView").packageName("com.team9.focus").resourceId("com.team9.focus:id/tvAppName"), "Joule", true);

        assertTrue(blockedAppLabel.exists());

        UiObject checkBox = blockedAppLabel.getFromParent(new UiSelector().resourceId("com.team9.focus:id/cbAppName"));
        checkBox.click();

        UiObject createProfileButton = mDevice.findObject(new UiSelector().className("android.widget.Button").packageName("com.team9.focus").resourceId("com.team9.focus:id/createButton"));
        createProfileButton.click();

        drawerButton.click();

        UiObject timerButton = mDevice.findObject(new UiSelector().className("android.widget.RelativeLayout").packageName("com.team9.focus").index(3));
        timerButton.click();

        UiScrollable timerListView = new UiScrollable(new UiSelector().scrollable(false).className("android.widget.ListView"));
       // UiObject profileLabel = listView.getChildByText(new UiSelector().className("android.widget.TextView").packageName("com.team9.focus").resourceId("com.team9.focus:id/tvProfileName"), "Name", false);

        UiObject profileLabel = mDevice.findObject(new UiSelector().className("android.widget.TextView").packageName("com.team9.focus").resourceId("com.team9.focus:id/tvProfileName").textMatches("Name"));
        assertTrue(profileLabel.exists());

        UiObject timerCheckBox = profileLabel.getFromParent(new UiSelector().resourceId("com.team9.focus:id/cbProfileName"));
        timerCheckBox.click();

        UiObject timerSaveButton = mDevice.findObject(new UiSelector().resourceId("com.team9.focus:id/timerStart"));
        timerSaveButton.click();

        mDevice.pressHome();

        UiObject jouleLauncher = mDevice.findObject(new UiSelector().className("android.widget.TextView").textMatches("Joule"));
        jouleLauncher.click();

        //Finds the notification drawer UI object
       /* UiSelector panelOpenFinder = new UiSelector().packageName("com.android.systemui").className("android.widget.FrameLayout").resourceId(
                "com.android.systemui:id/notification_panel");
        UiObject panelUiObject = mDevice.findObject(panelOpenFinder);

        //Checks to see if the notification drawer is open
        assertTrue(panelUiObject.exists());

        //Finds the Facebook notification on the screen
        UiSelector notificationFinder = new UiSelector().packageName("com.facebook.orca");
        UiObject notificationUiObject = mDevice.findObject(notificationFinder);

        //Checks to see if the notification object does not exist
        assert(notificationUiObject.exists());*/

    }

}