package com.team9.focus.activities;

import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.team9.focus.R;
import com.team9.focus.adapters.ScheduleListAdapter;
import com.team9.focus.models.DBHelper;
import com.team9.focus.models.objects.Schedule;
import com.team9.focus.models.objects.Timeslot;

import java.util.ArrayList;
import java.util.List;

public class DayViewActivity extends AppCompatActivity {
    private ListView mScheduleListView;
    private ArrayList<Schedule> mSchedules;
    private TabLayout mTabLayout;
    private ScheduleListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day_view);
        mSchedules = new ArrayList<Schedule>();

        for (Schedule s : DBHelper.getAllSchedules()) {
            boolean hasDay = false;
            List<Timeslot> timeslots = DBHelper.getTimeslotsBySchedule(s);
            for (Timeslot t : timeslots) {
                String[] days = t.dayOfWeek.split(",");
                for (String d : days) {
                    //if the timeslot has a day that matches current tab's day
                    if (Integer.parseInt(d) == 1) {
                        hasDay = true;
                        break;
                    }
                }

            }
            if (hasDay) {
                mSchedules.add(s);
            }
        }

        mScheduleListView = findViewById(R.id.lvSchedules);
        mTabLayout = findViewById(R.id.tabLayout);

        mAdapter = new ScheduleListAdapter(this, mSchedules);
        mAdapter.refresh(mSchedules, 1);

        mScheduleListView.setAdapter(mAdapter);

        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                ArrayList<Schedule> daySchedules = new ArrayList<Schedule>();
                int day = mTabLayout.getSelectedTabPosition() + 1;
                for (Schedule s : DBHelper.getAllSchedules()) {
                    boolean hasDay = false;
                    List<Timeslot> timeslots = DBHelper.getTimeslotsBySchedule(s);
                    for (Timeslot t : timeslots) {
                        String[] days = t.dayOfWeek.split(",");
                        for (String d : days) {
                            //if the timeslot has a day that matches current tab's day
                            if (Integer.parseInt(d) == day) {
                                hasDay = true;
                                break;
                            }
                        }

                    }
                    if (hasDay && s.isActive) {
                        daySchedules.add(s);
                    }
                }
                mSchedules.clear();
                mSchedules.addAll(daySchedules);
                mAdapter.refresh(mSchedules, day);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }
}
