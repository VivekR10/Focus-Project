package com.team9.focus.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.team9.focus.R;
import com.team9.focus.models.DBHelper;
import com.team9.focus.models.objects.Calendar;
import com.team9.focus.models.objects.Schedule;
import com.team9.focus.utilities.Utility;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by sophi on 10/15/2017.
 */

public class ExpandableCalendarAdapter extends BaseExpandableListAdapter {
    private boolean mToggleSwitches[];
    private Button mDeleteButtons[];
    private Activity mContext;
    private ArrayList<Calendar> mCalendars;
    private Map<Calendar, List<Schedule>> mCalendartoSchedulesMap;

    public ExpandableCalendarAdapter(Activity context, ArrayList<Calendar> calendars,
                                     Map<Calendar, List<Schedule>> map) {
        this.mContext = context;
        this.mToggleSwitches = new boolean[calendars.size()];
        this.mCalendars = calendars;
        this.mCalendartoSchedulesMap = map;
        this.mDeleteButtons = new Button[calendars.size()];

        for (int i=0; i<mCalendars.size(); i++) {
            mToggleSwitches[i] = mCalendars.get(i).isActive;
        }
    }

    @Override
    public int getGroupCount() {
        return mCalendars.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mCalendartoSchedulesMap.get(mCalendars.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mCalendars.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mCalendartoSchedulesMap.get(mCalendars.get(groupPosition)).get(childPosition);
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

    public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final Calendar calendar = (Calendar) getGroup(groupPosition);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.calendar_parent_item, null);        }

        // Set the name of the Schedule in the list
        TextView tvCalendarName = convertView.findViewById(R.id.tvCalendarName);
        tvCalendarName.setText(calendar.calendarName);

        final Switch sw =  convertView.findViewById(R.id.swToggle);
        sw.setChecked(mToggleSwitches[groupPosition]);
        sw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mToggleSwitches[groupPosition] = sw.isChecked();
                DBHelper.toggleCalendarActiveStatus(mCalendars.get(groupPosition), mToggleSwitches[groupPosition]);
                if (!sw.isChecked()) {
                    //deactivate all calendar's schedules, unless it's in another active calendar
                    for (Schedule s : DBHelper.getSchedulesByCalendar(mCalendars.get(groupPosition))) {
                        boolean inOnSchedule = false;
                        for (Calendar c : DBHelper.getCalendarsBySchedule(s)) {
                            if (c.isActive) {
                                inOnSchedule = true;
                                break;
                            }
                        }
                        if (!inOnSchedule) {
                            Utility.deactivateSchedule(s, mContext);
                            System.out.println("deactivating "+s.scheduleName);
                        }
                    }
                } else {
                    for (Schedule s : DBHelper.getSchedulesByCalendar(mCalendars.get(groupPosition))) {
                        if (!s.isActive) {
                            Utility.activateSchedule(s.scheduleName, mContext);
                        }
                    }
                }
            }
        });

        final Button delete = convertView.findViewById(R.id.deleteButton);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCalendars.get(groupPosition).calendarName.equals("Default")) {
                    Utility.createErrorDialog(mContext, "Cannot Delete", "You cannot delete the default calendar.");
                }
                else if (mToggleSwitches[groupPosition]) {
                    Utility.createErrorDialog(mContext, "Cannot Delete","Please deactivate this calendar before deleting.");
                } else {
                    StringBuilder sb = new StringBuilder();
                    DBHelper.deleteCalendar(mCalendars.get(groupPosition));
                    sb.append(mCalendars.get(groupPosition).calendarName + " was deleted");
                    Toast.makeText(mContext, sb.toString(), Toast.LENGTH_LONG).show();
                    mCalendartoSchedulesMap.remove(mCalendars.get(groupPosition));
                    mCalendars.remove(groupPosition);
                    refresh(mCalendars, mCalendartoSchedulesMap);
                }
            }
        });
        // Return the completed view to render on screen
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView,
                             ViewGroup parent) {
        Schedule schedule = (Schedule) getChild(groupPosition, childPosition);
        LayoutInflater inflater = mContext.getLayoutInflater();
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.calendar_child_item, null);
        }
        TextView textView = convertView.findViewById(R.id.tvScheduleNameChild);
        textView.setText(schedule.scheduleName);
        System.out.println("the child schedule is "+schedule.scheduleName);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public void refresh(ArrayList<Calendar> calendars, Map<Calendar, List<Schedule>> map) {
        this.mCalendars = calendars;
        this.mCalendartoSchedulesMap = map;
        this.mToggleSwitches = new boolean[mCalendars.size()];
        this.mDeleteButtons = new Button[calendars.size()];
        for (int i=0; i<mCalendars.size(); i++) {
            mToggleSwitches[i] = mCalendars.get(i).isActive;
        }
        notifyDataSetChanged();
    }
}
