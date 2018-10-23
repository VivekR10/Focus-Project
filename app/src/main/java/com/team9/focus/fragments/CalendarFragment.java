package com.team9.focus.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TabHost;

import com.team9.focus.R;
import com.team9.focus.activities.CreateCalendarActivity;
import com.team9.focus.activities.DayViewActivity;
import com.team9.focus.activities.WeekViewActivity;
import com.team9.focus.adapters.ExpandableCalendarAdapter;
import com.team9.focus.models.DBHelper;
import com.team9.focus.models.objects.Calendar;
import com.team9.focus.models.objects.Schedule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CalendarFragment extends Fragment {
    private FloatingActionButton mCreateCalendarButton;
    private ExpandableListView mCalendarListView;
    private ArrayList<Calendar> mCalendars;
    private ExpandableCalendarAdapter mAdapter;
    private Map<Calendar, List<Schedule>> mCalendarToSchedulesMap;
    private TabHost mTabHost;
    private Button mDayViewButton, mWeekViewButton;


    public CalendarFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh the profiles
        mCalendars.clear();
        mCalendars.addAll(DBHelper.getAllCalendars());
        mCalendarToSchedulesMap.clear();
        for (Calendar c : mCalendars) {
            mCalendarToSchedulesMap.put(c, DBHelper.getSchedulesByCalendar(c));
        }
        System.out.println("RESUMING");
        mAdapter.refresh(mCalendars, mCalendarToSchedulesMap);

        //mAdapter.notifyDataSetChanged();
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);
        mCalendarListView = view.findViewById(R.id.lvCalendars);
        mDayViewButton = view.findViewById(R.id.dayViewButton);
        mWeekViewButton = view.findViewById(R.id.weekViewButton);

        mCreateCalendarButton = (FloatingActionButton) view.findViewById(R.id.floatingActionButton);

        mCalendars = new ArrayList<>(DBHelper.getAllCalendars());

        mCalendarToSchedulesMap = new HashMap<>();
        for (Calendar c : mCalendars) {
            mCalendarToSchedulesMap.put(c, DBHelper.getSchedulesByCalendar(c));
        }
        mAdapter = new ExpandableCalendarAdapter(getActivity(), mCalendars, mCalendarToSchedulesMap);

        mCalendarListView.setAdapter(mAdapter);


        mCreateCalendarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CreateCalendarActivity.class);
                startActivity(intent);
            }
        });

        mDayViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), DayViewActivity.class);
                startActivity(intent);
            }
        });

        mWeekViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), WeekViewActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }


}
