package com.team9.focus.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Switch;
import android.widget.TextView;

import com.team9.focus.R;
import com.team9.focus.activities.EditScheduleActivity;
import com.team9.focus.models.DBHelper;
import com.team9.focus.models.objects.Schedule;
import com.team9.focus.models.objects.Timeslot;
import com.team9.focus.utilities.Utility;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nandhini on 10/13/17.
 */

public class ScheduleAdapter extends ArrayAdapter<Schedule> {
    private boolean mToggleSwitches[];
    private ArrayList<Schedule> mSchedules;

    // Constructor
    public ScheduleAdapter(Context context, ArrayList<Schedule> schedules) {
        super(context, 0, schedules);
        mSchedules = schedules;
        initializeToggleSwitches();
    }

    public void refresh(ArrayList<Schedule> schedules) {
        this.mSchedules = schedules;
        initializeToggleSwitches();
        notifyDataSetChanged();
    }

    public void initializeToggleSwitches() {
        mToggleSwitches = new boolean[mSchedules.size()];
        for (int i = 0; i < mSchedules.size(); ++i) {
            if (mSchedules.get(i).isActive) {
                mToggleSwitches[i] = true;
            } else {
                mToggleSwitches[i] = false;
            }
        }
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final Schedule schedule = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_view_item_schedule, parent, false);
        }

        // Set the name of the Schedule in the list
        TextView tvScheduleName = convertView.findViewById(R.id.tvScheduleName);
        tvScheduleName.setText(schedule.scheduleName);

        // Set the onclick for the item
        tvScheduleName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("SA_clickToEdit",schedule.scheduleName);
                // Determine if it is part of a schedule with a Timeslot that is on
                ArrayList<Schedule> scheduleList = new ArrayList<>();
                scheduleList.add(schedule);
                ArrayList<Schedule> resultList = DBHelper.getSchedulesWithOnTimeslots(scheduleList, false);
                if(resultList.size() > 0) {
                    Utility.createErrorDialog(getContext(), "Cannot Edit", "Override the schedule before editing this schedule.");
                    return;
                }
                Intent intent = new Intent(getContext(), EditScheduleActivity.class);
                intent.putExtra("scheduleName",schedule.scheduleName);
                getContext().startActivity(intent);
            }
        });

        // Get the Timeslots associated to the Schedule
        List<Timeslot> associatedTimeslots = DBHelper.getTimeslotsBySchedule(schedule);
        Log.d("SA_timeslotSize",Integer.toString(associatedTimeslots.size()));
        String associatedTimeslotString = Utility.timeslotsToString(associatedTimeslots);

        // Set the timeslot string in the list
        TextView tvTimeslot = convertView.findViewById(R.id.tvTimeslot);
        tvTimeslot.setText(associatedTimeslotString);

        final Switch sw =  convertView.findViewById(R.id.swActivate);
        sw.setChecked(mToggleSwitches[position]);
        sw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mToggleSwitches[position] = sw.isChecked();
                if(!sw.isChecked()) {
                    Utility.deactivateSchedule(schedule, getContext());
                    Utility.showBlockedNotifications(getContext());
                } else {
                    Utility.activateSchedule(schedule.scheduleName, getContext());
                }

            }
        });
        // Return the completed view to render on screen
        return convertView;
    }
    /*
    public ArrayList<Schedule> getSelectedSchedules() {
        ArrayList<Schedule> selectedSchedules = new ArrayList<>();
        for (int i = 0; i < mToggleSwitches.length; ++i) {
            if (mToggleSwitches[i]) {
                selectedProfiles.add(mProfiles.get(i));
            }
        }
        return selectedProfiles;
    }*/
}
