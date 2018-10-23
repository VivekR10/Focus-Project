package com.team9.focus.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.team9.focus.R;
import com.team9.focus.models.DBHelper;
import com.team9.focus.models.objects.Schedule;
import com.team9.focus.models.objects.Timeslot;
import com.team9.focus.utilities.Utility;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sophi on 10/13/2017.
 */

public class ScheduleCheckedAdapter extends ArrayAdapter<Schedule> {
    private boolean mCheckBoxes[];
    private ArrayList<Schedule> mSchedules;

    public ScheduleCheckedAdapter(Context context, ArrayList<Schedule> schedules) {
        super(context, 0, schedules);
        mCheckBoxes = new boolean[schedules.size()];
        mSchedules = schedules;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Schedule schedule = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_view_item_checked_schedule, parent, false);
        }

        TextView tvScheduleName = convertView.findViewById(R.id.tvScheduleName);
        tvScheduleName.setText(schedule.scheduleName);

        // Get the Timeslots associated to the Schedule
        List<Timeslot> associatedTimeslots = DBHelper.getTimeslotsBySchedule(schedule);
        String associatedTimeslotString = Utility.timeslotsToString(associatedTimeslots);

        // Set the timeslot string in the list
        TextView tvTimeslot = convertView.findViewById(R.id.tvTimeslot);
        tvTimeslot.setText(associatedTimeslotString);

        final CheckBox cb =  convertView.findViewById(R.id.cbScheduleName);
        cb.setChecked(mCheckBoxes[position]);
        cb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCheckBoxes[position] = cb.isChecked();
            }
        });
        // Return the completed view to render on screen
        return convertView;
    }

    public ArrayList<Schedule> getSelectedSchedules() {
        ArrayList<Schedule> selectedSchedules = new ArrayList<>();
        for (int i = 0; i < mCheckBoxes.length; ++i) {
            if (mCheckBoxes[i]) {
                selectedSchedules.add(mSchedules.get(i));
            }
        }
        return selectedSchedules;
    }

}
