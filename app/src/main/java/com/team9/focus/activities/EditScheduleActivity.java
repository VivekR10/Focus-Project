package com.team9.focus.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.team9.focus.R;
import com.team9.focus.adapters.CalendarCheckedAdapter;
import com.team9.focus.adapters.ProfileAdapter;
import com.team9.focus.adapters.TimeslotDeleteAdapter;
import com.team9.focus.exceptions.AlreadyExistsException;
import com.team9.focus.models.DBHelper;
import com.team9.focus.models.objects.BlockIntent;
import com.team9.focus.models.objects.Calendar;
import com.team9.focus.models.objects.Profile;
import com.team9.focus.models.objects.Schedule;
import com.team9.focus.models.objects.Timeslot;
import com.team9.focus.utilities.ProfileUseComparator;
import com.team9.focus.utilities.Utility;

import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EditScheduleActivity extends AppCompatActivity {

    private EditText mScheduleName;
    SharedPreferences mPrefs;
    private ListView mTimeslotListView, mProfileListView, mCalendarListView;
    private Button mCreateTimeslotButton, mSaveButton, mDeleteButton;
    TimeslotDeleteAdapter mTimeslotAdapter;
    Schedule schedule;


    ArrayList<Timeslot> mTimeslotArray;
    GsonBuilder builder = new GsonBuilder();
    Gson gson;

    // All profiles previously created
    private ArrayList<Profile> mProfiles;
    // Profiles that the user selects to add to the schedule
    private ArrayList<Profile> mSelectedProfiles;

    // All calendars previously created
    private ArrayList<com.team9.focus.models.objects.Calendar> mCalendars;
    // Calendars that the user wants the schedule to be added to.
    private ArrayList<com.team9.focus.models.objects.Calendar> mSelectedCalendars;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        builder.excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC);

        gson = builder.create();

        mPrefs = getSharedPreferences("timeslots", Context.MODE_PRIVATE);

        setContentView(R.layout.activity_edit_schedule);

        // Initialize all GUI components
        mScheduleName = (EditText) findViewById(R.id.scheduleName);
        mTimeslotListView = (ListView) findViewById(R.id.timeslotListView);
        mCreateTimeslotButton = (Button) findViewById(R.id.createTimeslotButton);
        mProfileListView = (ListView) findViewById(R.id.profileListView);
        mCalendarListView = (ListView) findViewById(R.id.calendarListView);
        mSaveButton = (Button) findViewById(R.id.saveButton);
        mDeleteButton = (Button) findViewById(R.id.deleteButton);

        // Get the schedule that is being edited
        Intent intent = getIntent();
        String scheduleName = intent.getExtras().getString("scheduleName");
        schedule = DBHelper.getScheduleByName(scheduleName);

        // Set the name of the schedule in the text field
        mScheduleName.setText(scheduleName);

        // Get all profiles and Profiles associated to the Schedule
        mProfiles = new ArrayList<>(DBHelper.getAllProfiles());
        mSelectedProfiles = new ArrayList<Profile>();
        if(DBHelper.getProfilesBySchedule(schedule) != null) {
            mSelectedProfiles = (ArrayList) DBHelper.getProfilesBySchedule(schedule);
        }

        // Sort the profiles
        for(Profile p : mProfiles){
            DBHelper.setNumProfileUses(p);
        }
        Collections.sort(mProfiles, new ProfileUseComparator());

        // Set up Profile adapter
        final ProfileAdapter profileAdapter = new ProfileAdapter(this,  mProfiles, mSelectedProfiles);
        mProfileListView.setAdapter(profileAdapter);

        // Set up Timeslot Adapter
        mTimeslotArray = new ArrayList<Timeslot>();
        if (DBHelper.getTimeslotsBySchedule(schedule) != null) {
            mTimeslotArray = (ArrayList) DBHelper.getTimeslotsBySchedule(schedule);
        }

        mTimeslotAdapter = new TimeslotDeleteAdapter(this, mTimeslotArray, schedule);
        mTimeslotListView.setAdapter(mTimeslotAdapter);


        // Get all Calendars and Calendars associated to the Schedule
        mSelectedCalendars = new ArrayList<Calendar>();
        if(DBHelper.getCalendarsBySchedule(schedule) != null) {
            mSelectedCalendars = (ArrayList) DBHelper.getCalendarsBySchedule(schedule);
        }
        mCalendars = new ArrayList<>(DBHelper.getAllCalendars());

        // Set up Calendar Adapter
        final CalendarCheckedAdapter calendarAdapter = new CalendarCheckedAdapter(this, mCalendars,
                mSelectedCalendars);
        mCalendarListView.setAdapter(calendarAdapter);

        // Add Timeslot Button
        mCreateTimeslotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createTimeslot();
            }
        });

        // Save the schedule
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSelectedProfiles = profileAdapter.getSelectedProfiles();
                mSelectedCalendars = calendarAdapter.getSelectedCalendars();
                if(trySaveSchedule()){
                    // Go back to the Home Activity
                    onBackPressed();
                }
            }
        });

        // Delete the schedule
        mDeleteButton.setOnClickListener(new View.OnClickListener(){
           @Override
            public void onClick(View view){
               deleteSchedule();

               // Go back to the Home Activity
               onBackPressed();
           }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();

        SharedPreferences.Editor ed = mPrefs.edit();
        String json = gson.toJson(mTimeslotArray);
        ed.putString("name", mScheduleName.getText().toString());
        ed.putString("timeslots", json);
        ed.commit();
    }

    @Override
    public void onResume() {
        super.onResume();
        mTimeslotArray = new ArrayList<Timeslot>();

        // Get the timeslots that were previously made
        if(mPrefs.contains("timeslots")) {
            String json = mPrefs.getString("timeslots","");
            Type type = new TypeToken<ArrayList<Timeslot>>(){}.getType();
            mTimeslotArray = gson.fromJson(json,type);
        }

        // Get the name that was previously made
        /*
        if(mPrefs.contains("name"))
        {
            String name = mPrefs.getString("name","");
            mScheduleName.setText(name);
            Log.d("ESA_Load_Name", name);
        }*/

        // Add the newly created timeslot
        if(getIntent().hasExtra("timeslot")) {
            Timeslot t = getIntent().getExtras().getParcelable("timeslot");
            if(t != null) {
                // Add the new timeslot to the Array
                mTimeslotArray.add(t);
                mTimeslotAdapter.addAll(mTimeslotArray);

                mTimeslotAdapter.notifyDataSetChanged();
                getIntent().removeExtra("timeslot");
            }
        }
    }

    @Override
    public void onBackPressed()
    {
        clearTimeslots();

        // Clear the stored name
        SharedPreferences.Editor ed = mPrefs.edit();
        ed.remove("name");
        ed.commit();

        // Go back to the main activity
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

    }

    public void clearTimeslots()
    {
        SharedPreferences.Editor ed = mPrefs.edit();
        ed.remove("timeslots");
        ed.commit();
        mTimeslotArray.clear();

    }

    private void createTimeslot() {
        Intent intent = new Intent(this, CreateTimeslotActivity.class);
        intent.putExtra("sender", "edit");
        intent.putExtra("scheduleName", schedule.scheduleName);
        startActivity(intent);
    }

    private boolean trySaveSchedule() {
        // Check that at least 1 profile is selected
        if(mSelectedProfiles == null || mSelectedProfiles.size()<1){
            Utility.createErrorDialog(this, "No Profiles", "You must select a Profile");
            return false;
        }

        // Check that at least 1 Calendar is selected
        if(mSelectedCalendars == null || mSelectedCalendars.size()<1){
            Utility.createErrorDialog(this, "No Calendars", "You must select a Calendar");
            return false;
        }

        String scheduleName = mScheduleName.getText().toString();
        Utility.deactivateSchedule(schedule, this);

        // Delete all associations to this schedule
        DBHelper.deleteScheduleAssociations(schedule);

        // Add each new timeslot to the timeslot Array
        mTimeslotArray.clear();
        for(int i=0; i<mTimeslotAdapter.getCount(); i++){
            mTimeslotArray.add(mTimeslotAdapter.getItem(i));
        }

        // Create a new array of timeslots
        ArrayList<Timeslot> newTimeslots = new ArrayList<>();

        // Create each new timeslot
        for(Timeslot t : mTimeslotArray) {
            newTimeslots.add(DBHelper.createTimeslot(t.dayOfWeek, t.startHour, t.startMinute,
                    t.endHour, t.endMinute, false));
        }

        // Create the intents for each timeslot
        for(Timeslot t : newTimeslots){
            List<BlockIntent> createdIntents = createIntents(t);
            for(BlockIntent bi : createdIntents) {
                bi.save();
            }
        }


        try {
            if (DBHelper.updateSchedule(schedule, mScheduleName.getText().toString(),
                    newTimeslots, mSelectedProfiles, mSelectedCalendars)) {
                Utility.activateSchedule(mScheduleName.getText().toString(), this);
                clearTimeslots();
            } else {
                System.out.println("Unsuccessful save");
                return false;
            }
        } catch (AlreadyExistsException e) {
            System.out.println(e.getMessage());
            return false;
        }

        return true;
    }

    private void deleteSchedule(){
        // Deactivate the Schedule first
        Utility.deactivateSchedule(schedule, this);

        // Delete it using DBHelper function
        DBHelper.deleteSchedule(schedule);
    }

    private List<BlockIntent> createIntents(Timeslot t) {
        String daysOfWeek = t.dayOfWeek;
        String[] days = daysOfWeek.split(",");
        List<BlockIntent> blockIntents = new ArrayList<>();
        for(String d : days) {
            int requestCodeStart = Utility.getUniqueRequestCode();
            int dayOfWeek = Integer.parseInt(d);
            BlockIntent blockIntentStart = new BlockIntent(t, requestCodeStart, "start", dayOfWeek);
            int requestCodeEnd = Utility.getUniqueRequestCode();
            BlockIntent blockIntentEnd = new BlockIntent(t, requestCodeEnd, "end", dayOfWeek);
            blockIntents.add(blockIntentStart);
            blockIntents.add(blockIntentEnd);
        }

        return blockIntents;

    }
}
