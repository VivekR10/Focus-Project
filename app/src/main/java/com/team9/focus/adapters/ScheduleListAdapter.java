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
import com.team9.focus.models.DBHelper;
import com.team9.focus.models.objects.Schedule;
import com.team9.focus.models.objects.Timeslot;
import com.team9.focus.utilities.Utility;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sophi on 10/16/2017.
 */

public class ScheduleListAdapter extends ArrayAdapter<Schedule> {

    private ArrayList<Schedule> mSchedules;
    private int day;

    // Constructor
    public ScheduleListAdapter(Context context, ArrayList<Schedule> schedules) {
        super(context, 0, schedules);
        mSchedules = schedules;
        day = 0;
    }

    public void refresh(ArrayList<Schedule> schedules, int day) {
        this.mSchedules = schedules;
        this.day = day;
        notifyDataSetChanged();
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final Schedule schedule = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_view_item_schedule_list, parent, false);
        }

        // Set the name of the Schedule in the list
        TextView tvScheduleName = convertView.findViewById(R.id.tvScheduleName);
        tvScheduleName.setText(schedule.scheduleName);

        // Get the Timeslots associated to the Schedule
        List<Timeslot> associatedTimeslots = DBHelper.getTimeslotsBySchedule(schedule);
        List<Timeslot> dayTimeslots = new ArrayList<Timeslot>();

        //only display timeslots matching the day of the week view
        if (day != 0) {
            for (Timeslot t : associatedTimeslots) {
                String[] days = t.dayOfWeek.split(",");
                for (String d : days) {
                    //if the timeslot has a day that matches current tab's day
                    if (Integer.parseInt(d) == day) {
                        dayTimeslots.add(t);
                    }
                }

            }
        } else {
            dayTimeslots = DBHelper.getTimeslotsBySchedule(schedule);
        }

        String associatedTimeslotString = Utility.timeslotsToString(dayTimeslots);

        // Set the timeslot string in the list
        TextView tvTimeslot = convertView.findViewById(R.id.tvTimeslot);
        tvTimeslot.setText(associatedTimeslotString);

        // Return the completed view to render on screen
        return convertView;
    }
}
