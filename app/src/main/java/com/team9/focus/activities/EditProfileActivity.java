package com.team9.focus.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.team9.focus.R;
import com.team9.focus.exceptions.AlreadyExistsException;
import com.team9.focus.models.DBHelper;
import com.team9.focus.models.objects.App;
import com.team9.focus.models.objects.Profile;
import com.team9.focus.models.objects.Schedule;
import com.team9.focus.utilities.Utility;

import java.util.ArrayList;
import java.util.List;


public class EditProfileActivity extends AppCompatActivity {

    private ListView mAppList;

    private ArrayList<String> mAddedAppNames = new ArrayList<>();
    private List<App> mAllApps = new ArrayList<>();
    private List<String> mAllAppNames = new ArrayList<>();
    private Profile mProfile;

    private ArrayAdapter<String> mAdapter;

    private EditText etProfileName;
    private Button btnSaveProfile, btnDeleteProfile, btnAddApp;

    private AlertDialog.Builder mBuilder;
    private AlertDialog mDialog;

    private Context mContext;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        mContext = this;

        // Retrieve the profile from the profile name passed into the intent
        Intent intent = getIntent();
        String profileName = intent.getStringExtra("profileName");

        mProfile = DBHelper.getProfileByName(profileName);

        etProfileName = findViewById(R.id.etCurrentProfileName);
        etProfileName.setText(profileName);

        btnAddApp = findViewById(R.id.btnAddApp);
        btnAddApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.show();
            }
        });

        btnSaveProfile = findViewById(R.id.btnSaveProfile);
        btnSaveProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<App> newApps = new ArrayList<App>();
                for (String appName : mAddedAppNames) {
                    newApps.add(DBHelper.getAppByName(appName));
                }
                try {
                    DBHelper.updateProfile(mProfile, etProfileName.getText().toString(), newApps);
                    // Get all schedules the profile is in
                    ArrayList<Schedule> allSchedules = new ArrayList<>(DBHelper.getSchedulesByProfile(mProfile));
                    dialogAndDismiss(allSchedules);
                } catch (AlreadyExistsException e) {
                    System.out.println(e.getMessage());
                }
            }
        });

        btnDeleteProfile = findViewById(R.id.btnDeleteProfile);
        btnDeleteProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<Schedule> allSchedules = new ArrayList<>(DBHelper.getSchedulesByProfile(mProfile));

                // Can't delete the profile if it is the only one in a schedule
                if(checkDeleteProfile(allSchedules)){
                    DBHelper.deleteProfile(mProfile);
                    dialogAndDismiss(allSchedules);
                }
            }
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Keep the soft keyboard from shifting everything else
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        // Get the appList from the Layout
        mAppList = findViewById(R.id.appList);

        // List of apps that will be dynamically filled or taken from the database
        List<App> apps = DBHelper.getAppsByProfile(mProfile);
        for (App app : apps) {
            mAddedAppNames.add(app.appName);
        }

        mAllApps = DBHelper.getAllApps();
        mAllAppNames = new ArrayList<>();

        // itemsChecked is a boolean array that is passed into the DialogBuilder
        // to specify which apps are pre-checked
        final boolean[] itemsChecked = new boolean[mAllApps.size()];
        for (int i = 0; i < mAllApps.size(); ++i) {
            if (mAddedAppNames.contains(mAllApps.get(i).appName)) {
                itemsChecked[i] = true;
            }
            mAllAppNames.add(mAllApps.get(i).appName);
        }

        // Charsequence is passed into the DialogBuilder to create the titles
        final CharSequence[] items = mAllAppNames.toArray(new CharSequence[mAllAppNames.size()]);

        mBuilder = new AlertDialog.Builder(this);
        mBuilder.setTitle("Add apps to profile");

        mBuilder.setMultiChoiceItems(items, itemsChecked, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int selectedItemId,
                                boolean isSelected) {
                if (isSelected) {
                    mAddedAppNames.add(mAllAppNames.get(selectedItemId));
                } else {
                    mAddedAppNames.remove(mAllAppNames.get(selectedItemId));
                }
            }
        }).setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mAdapter.notifyDataSetChanged();
            }
        });

        mDialog = mBuilder.create();

        mAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, mAddedAppNames);
        mAppList.setAdapter(mAdapter);

    }

    /**
     *
     * @return the schedules that have been modified in the edit/delete
     */
    private String schedulesModified(ArrayList<Schedule> allSchedules){
        StringBuilder sb = new StringBuilder();
        int size = allSchedules.size();
        if(size!=0){
            sb.append("The following schedule(s) will be modified: \n \n");
            for(int j=0; j<size; j++){
                sb.append(allSchedules.get(j).scheduleName);
                if(j < size-1){
                    sb.append(", ");
                }
            }
        } else {
            sb.append("No schedules have been modified.");
        }

        return sb.toString();
    }

    /**
     * Displays the schedules changed and dismisses the activity
     */
    private void dialogAndDismiss(ArrayList<Schedule> allSchedules){
        String schedulesModified = schedulesModified(allSchedules);

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(mContext);
        builder.setTitle("Schedules Modified")
                .setMessage(schedulesModified)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                })
                .show();
    }

    private boolean checkDeleteProfile(ArrayList<Schedule> allSchedules){
        boolean canDelete = true;

        ArrayList<Schedule> scheduleWithOneProfile = new ArrayList<>();

        // For each schedule, check if it only has 1 profile
        for(Schedule s :allSchedules){
            ArrayList<Profile> profiles = new ArrayList<>(DBHelper.getProfilesBySchedule(s));
            if(profiles.size()==1){
                scheduleWithOneProfile.add(s);
                canDelete = false;
            }
        }

        if(!canDelete){
            StringBuilder sb = new StringBuilder();
            sb.append("This is the only profile in the following schedule(s): \n\n");
            int size = scheduleWithOneProfile.size();
            for(int j=0; j<size; j++){
                sb.append(allSchedules.get(j).scheduleName);
                if(j < size-1){
                    sb.append(", ");
                }
            }
            sb.append("\n\n");
            sb.append("You must edit these schedules before deleting this profile.");
            Utility.createErrorDialog(mContext, "Cannot Delete Profile", sb.toString());
        }

        return canDelete;
    }

}
