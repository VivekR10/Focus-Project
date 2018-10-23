package com.team9.focus.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
import com.team9.focus.models.objects.Profile;
import com.team9.focus.models.objects.Timeslot;
import com.team9.focus.utilities.ProfileUseComparator;
import com.team9.focus.utilities.Utility;

import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CreateScheduleActivity extends AppCompatActivity {
    EditText mScheduleName;
    ListView mTimeslotListView;
    Button mCreateTimeslotButton;
    ListView mProfileListView;
    ListView mCalendarListView;
    Button mSaveButton;

    SharedPreferences mPrefs;
    ArrayList<Timeslot> mTimeslotArray;
    GsonBuilder builder = new GsonBuilder();
    Gson gson;
    TimeslotDeleteAdapter mTimeslotAdapter;

    // All profiles previously created
    private ArrayList<Profile> mProfiles;
    // Profiles that the user selects to add to the schedule
    private ArrayList<Profile> mSelectedProfiles;

    // All calendars previously created
    private ArrayList<com.team9.focus.models.objects.Calendar> mCalendars;
    // Calendars that the user wants the schedule to be added to.
    private ArrayList<com.team9.focus.models.objects.Calendar> mSelectedCalendars;

    public CreateScheduleActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        builder.excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC);
        gson = builder.create();

        mPrefs = getSharedPreferences("timeslots",Context.MODE_PRIVATE);

        setContentView(R.layout.activity_create_schedule);

        // Get all elements from teh GUI
        mScheduleName = (EditText) findViewById(R.id.scheduleName);
        mTimeslotListView = (ListView) findViewById(R.id.timeslotListView);
        mCreateTimeslotButton = (Button) findViewById(R.id.createTimeslotButton);
        mProfileListView = (ListView) findViewById(R.id.profileListView);
        mCalendarListView = (ListView) findViewById(R.id.calendarListView);
        mSaveButton = (Button) findViewById(R.id.saveButton);

        // Retrieve the list of profiles and put them in mProfiles
        mProfiles = new ArrayList<>(DBHelper.getAllProfiles());

        // Sort the profiles
        for(Profile p : mProfiles){
            DBHelper.setNumProfileUses(p);
        }
        Collections.sort(mProfiles, new ProfileUseComparator());

        //Set up Profile adapter
        final ProfileAdapter profileAdapter = new ProfileAdapter(this, mProfiles);
        mProfileListView.setAdapter(profileAdapter);

        // Get all timeslots that were already created
        mTimeslotArray = new ArrayList<Timeslot>();
        if(mPrefs.contains("timeslots")) {
            String json = mPrefs.getString("timeslots","");
            Type type = new TypeToken<ArrayList<Timeslot>>(){}.getType();
            mTimeslotArray = gson.fromJson(json,type);
        }

        // Set up Timeslot Adapter
        mTimeslotAdapter = new TimeslotDeleteAdapter(this, mTimeslotArray, null);
        if(mTimeslotArray!=null) {
            mTimeslotListView.setAdapter(mTimeslotAdapter);
        }

        // Get all calendars that were made
        mCalendars = new ArrayList<>(DBHelper.getAllCalendars());

        // Set up the calendar adapter
        final CalendarCheckedAdapter calendarAdapter = new CalendarCheckedAdapter(this, mCalendars);
        mCalendarListView.setAdapter(calendarAdapter);

        //add listeners to buttons
        mCreateTimeslotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createTimeslot();
            }
        });
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSelectedProfiles = profileAdapter.getSelectedProfiles();
                mSelectedCalendars = calendarAdapter.getSelectedCalendars();
                if(trySaveSchedule()){
                    // Go back to the schedule fragment
                    //TODO: actually clear the timeslots and name
                    onBackPressed();
                }
            }
        });

    }


    @Override
    protected void onPause() {

        super.onPause();

        // Clear the current array in case anything was deleted
        mTimeslotArray.clear();

        // Add each item from the timeslotAdapter to timeslotArray
        for(int i=0; i<mTimeslotAdapter.getCount(); i++){
            mTimeslotArray.add(mTimeslotAdapter.getItem(i));
        }

        // Save the timeslots to the SharedPreferences
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
        if(mPrefs.contains("name"))
        {
            String name = mPrefs.getString("name","");
            mScheduleName.setText(name);
        }

        // Add the newly added timeslot
        if(getIntent().hasExtra("timeslot")) {
            Timeslot t = getIntent().getExtras().getParcelable("timeslot");
            if(t != null) {
                mTimeslotArray.add(t);
                mTimeslotAdapter.add(t);
                mTimeslotAdapter.notifyDataSetChanged();
                getIntent().removeExtra("timeslot");
            }
        }
    }

    @Override
    public void onBackPressed()
    {
        clearTimeslots();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

    }

    public void clearTimeslots()
    {
        SharedPreferences.Editor ed = mPrefs.edit();
        ed.remove("timeslots");
        ed.commit();

        mTimeslotArray.clear();
        mTimeslotAdapter.clear();

    }

    private void createTimeslot() {
        Intent intent = new Intent(this, CreateTimeslotActivity.class);
        intent.putExtra("sender", "create");
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

        // Get the schedule name
        String scheduleName = mScheduleName.getText().toString();

        Log.d("CSA_Save_Name", scheduleName);
        Log.d("CSA_Save_Timeslot_Size", String.valueOf(mTimeslotArray.size()));


        // Save each timeslot and create it's intents
        if(DBHelper.getScheduleByName(scheduleName)!=null)
        {

        }
        else
        {
            for (Timeslot t : mTimeslotArray) {
                t.save();
                Log.d("CSA_Timeslot_Save_ID", String.valueOf(t.getId()));
                List<BlockIntent> createdIntents = createIntents(t);
                Log.d("CSA_Intent_Size", String.valueOf(createdIntents.size()));
                for (BlockIntent bi : createdIntents) {
                    bi.save();
                }
            }
        }

        // Save the schedule
        try {
            if (DBHelper.saveSchedule(scheduleName, mTimeslotArray, mSelectedProfiles, mSelectedCalendars)) {
                Log.d("CSA_SaveSched_Timeslot", String.valueOf(mTimeslotArray.size()));
                System.out.println("Successfully saved!");
                clearTimeslots();
                Utility.activateSchedule(scheduleName, this);
            } else {
                Utility.createErrorDialog(this, "Invalid Name", "You must enter a unique name for your schedule");
                System.out.println("Unsuccessful save");
                return false;
            }
        } catch (AlreadyExistsException e) {
            System.out.println(e.getMessage());
            return false;
        }

        return true;
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

