package com.team9.focus.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.team9.focus.R;
import com.team9.focus.models.DBHelper;
import com.team9.focus.models.objects.App;
import com.team9.focus.models.objects.Schedule;
import com.team9.focus.models.objects.Timeslot;
import com.team9.focus.utilities.Utility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by jtsui on 10/16/17.
 */


public class ExpandableScheduleAdapter extends BaseExpandableListAdapter {
    private Activity mContext;
    private ArrayList<Schedule> mSchedules;
    private Map<Schedule, List<App>> mScheduleToAppsMap;
    private Map<Schedule, List<Timeslot>> mScheduleToTimeslotMap;
    private boolean mIsOverriden[];

    private TextView tvEndTime;

    public ExpandableScheduleAdapter(Activity context, ArrayList<Schedule> schedules,
                                     Map<Schedule, List<App>> appMap,
                                     Map<Schedule, List<Timeslot>> timeslotMap) {
        this.mContext = context;
        this.mSchedules = schedules;
        this.mScheduleToAppsMap = appMap;
        this.mScheduleToTimeslotMap = timeslotMap;
        this.mIsOverriden = new boolean[mSchedules.size()];
        for (int i = 0; i < mSchedules.size(); ++i) {
            Log.d("ESA schedule", mSchedules.get(i).scheduleName);
            mIsOverriden[i] = true;
            List<Timeslot> timeslots = DBHelper.getTimeslotsBySchedule(mSchedules.get(i));
            for (Timeslot timeslot : timeslots) {
                if (!timeslot.isOverridden) {
                    Log.d("ESA is not overridden", timeslot.formatAsString());
                    mIsOverriden[i] = false;
                } else {
                    Log.d("ESA is overridden", timeslot.formatAsString());
                }
            }
//            for (Timeslot timeslot : mScheduleToTimeslotMap.get(mSchedules.get(i))) {
//                if (!timeslot.isOverridden) {
//                    Log.d("ESA is not overridden", timeslot.formatAsString());
//                    mIsOverriden[i] = false;
//                } else {
//                    Log.d("ESA is overridden", timeslot.formatAsString());
//                }
//            }
        }
    }

    @Override
    public int getGroupCount() {
        return mSchedules.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mScheduleToAppsMap.get(mSchedules.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mSchedules.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mScheduleToAppsMap.get(mSchedules.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        final Schedule schedule = (Schedule) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.schedule_parent_item, null);
        }

        TextView tvScheduleName = convertView.findViewById(R.id.tvScheduleNameParent);
        tvScheduleName.setTypeface(null, Typeface.BOLD);
        if(schedule.scheduleName.equals("Default")) {
            tvScheduleName.setText("Timer");
        } else {
            tvScheduleName.setText(schedule.scheduleName);
        }


        // Find latest timeslot
        List<Timeslot> timeslots = mScheduleToTimeslotMap.get(schedule);
        List<Timeslot> onTimeslots = new ArrayList<>();

        for (Timeslot timeslot : timeslots) {
            if (timeslot.isOn && !timeslot.isOverridden) {
                onTimeslots.add(timeslot);
            }
        }

        Timeslot[] timeslotArray = onTimeslots.toArray(new Timeslot[onTimeslots.size()]);
        Arrays.sort(timeslotArray);

        final List<Timeslot> timeslotsToOverride = new ArrayList<>();

        if (!onTimeslots.isEmpty()) {
            // Convert back to arraylist?
            onTimeslots = Arrays.asList(timeslotArray);
            int hour = onTimeslots.get(0).endHour;
            int minute = onTimeslots.get(0).endMinute;
            timeslotsToOverride.add(onTimeslots.get(0));
            for (int i = 1; i < onTimeslots.size(); ++i) {
                Timeslot t = onTimeslots.get(i);
                if (t.startHour < hour || (t.startHour == hour && t.startMinute < minute)) {
                    hour = t.endHour;
                    minute = t.endMinute;
                    timeslotsToOverride.add(t);
                }
            }

            StringBuilder sb = new StringBuilder();
            sb.append("Blocked until: ");
            sb.append(hour % 12+ ":");
            if(minute < 10){
                sb.append("0");
            }
            sb.append(minute);

            // AM or PM
            if(hour < 12){
                sb.append("AM");
            } else {
                sb.append("PM");
            }

            tvEndTime = convertView.findViewById(R.id.tvEndTime);
            tvEndTime.setText(sb.toString());
        } else {
            tvEndTime = convertView.findViewById(R.id.tvEndTime);
            tvEndTime.setText("All timeslots are overriden.");
        }


        // IF ALL TIMESLOTS ARE OFF, TURN OFF SCHEDULE

        final Button btn = convertView.findViewById(R.id.btnOverrideSchedule);
        if (mIsOverriden[groupPosition]) {
            Log.d("ESA", "isOverridden");
            btn.setText("Overridden");
            btn.setClickable(false);
            tvEndTime.setText("");
        } else {
            btn.setText("Override!");
            btn.setClickable(true);
        }
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mIsOverriden[groupPosition] = true;
                btn.setText("Overridden");
                btn.setClickable(false);
                tvEndTime.setText("");
                for (Timeslot timeslot : timeslotsToOverride) {
                    // Override the timeslot and show notifications
                    if(schedule.scheduleName.equals("Default")) {
                        DBHelper.deleteScheduleAssociations(schedule);
                    } else {
                        Log.d("ESA_mIsOverriden", Boolean.toString(mIsOverriden[groupPosition]));
                        DBHelper.toggleTimeslotOverride(timeslot, mIsOverriden[groupPosition]);
                    }
                    Utility.showBlockedNotifications(mContext.getApplicationContext());
                }
            }
        });

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView,
                             ViewGroup parent) {
        App app = (App) getChild(groupPosition, childPosition);
        LayoutInflater inflater = mContext.getLayoutInflater();
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.schedule_child_item, null);
        }
        TextView textView = convertView.findViewById(R.id.schedule_tvAppNameChild);
        textView.setTypeface(null, Typeface.BOLD);
        textView.setText(app.appName);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
