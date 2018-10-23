package com.team9.focus.activities;

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

public class WeekViewActivity extends AppCompatActivity {
    private ListView mSundaySchedules, mMondaySchedules, mTuesdaySchedules, mWednesdaySchedules,
    mThursdaySchedules, mFridaySchedules, mSaturdaySchedules;

    private ArrayList<ScheduleListAdapter> mAdapters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_week_view);

        mSundaySchedules = findViewById(R.id.lvSunday);
        mMondaySchedules = findViewById(R.id.lvMonday);
        mTuesdaySchedules = findViewById(R.id.lvTuesday);
        mWednesdaySchedules = findViewById(R.id.lvWednesday);
        mThursdaySchedules = findViewById(R.id.lvThursday);
        mFridaySchedules = findViewById(R.id.lvFriday);
        mSaturdaySchedules = findViewById(R.id.lvSaturday);


        mAdapters = new ArrayList<ScheduleListAdapter>(7);
        for (int i=0; i<7; i++) {
            //update the adapter with the appropriate day
            ArrayList<Schedule> daySchedules = new ArrayList<Schedule>();
            int day = i+1;
            for (Schedule s : DBHelper.getAllSchedules()) {
                boolean hasDay = false;
                List<Timeslot> timeslots = DBHelper.getTimeslotsBySchedule(s);
                for (Timeslot t : timeslots) {
                    String[] days = t.dayOfWeek.split(",");
                    for (String d : days) {
                        //if the timeslot has a day that matches current day
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
            ScheduleListAdapter adapter = new ScheduleListAdapter(this, daySchedules);
            adapter.refresh(daySchedules, day);
            mAdapters.add(adapter);
        }

        mSundaySchedules.setAdapter(mAdapters.get(0));
        mMondaySchedules.setAdapter(mAdapters.get(1));
        mTuesdaySchedules.setAdapter(mAdapters.get(2));
        mWednesdaySchedules.setAdapter(mAdapters.get(3));
        mThursdaySchedules.setAdapter(mAdapters.get(4));
        mFridaySchedules.setAdapter(mAdapters.get(5));
        mSaturdaySchedules.setAdapter(mAdapters.get(6));
    }
}
