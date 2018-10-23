package com.team9.focus.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.team9.focus.R;
import com.team9.focus.adapters.ExpandableScheduleAdapter;
import com.team9.focus.models.DBHelper;
import com.team9.focus.models.objects.App;
import com.team9.focus.models.objects.Profile;
import com.team9.focus.models.objects.Schedule;
import com.team9.focus.models.objects.Timeslot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class HomeFragment extends Fragment {

    private ExpandableListView mListView;
    //private ArrayList<Timeslot> mTimeslots;
    private ArrayList<Schedule> mSchedules, mOnSchedules;
    private Map<Schedule, List<App>> mSchedulesToAppMap;
    private Map<Schedule, List<Timeslot>> mSchedulesToTimeslotMap;
    private ExpandableScheduleAdapter mAdapter;


    public HomeFragment() {

    }

    @Override
    public void onResume() {
        super.onResume();
        mSchedules.clear();
        mSchedules.addAll(DBHelper.getAllSchedules());
        mSchedulesToAppMap.clear();
        fillHashMaps();
        mAdapter.notifyDataSetChanged();
    }

    private void fillHashMaps() {

        String day = Integer.toString(java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_WEEK));
        for (Schedule schedule : mSchedules) {
            List<Profile> profiles = DBHelper.getProfilesBySchedule(schedule);
            // Use a hashset because some profiles might block the same apps
            Set<App> apps = new HashSet<>();
            for (Profile profile : profiles) {
                apps.addAll(DBHelper.getAppsByProfile(profile));
            }
            mSchedulesToAppMap.put(schedule, new ArrayList<App>(apps));
            List<Timeslot> allTimeslots = DBHelper.getTimeslotsBySchedule(schedule);
            List<Timeslot> filteredTimeslots = new ArrayList<>();
            for (Timeslot timeslot : allTimeslots) {
                if (timeslot.dayOfWeek.contains(day)) {
                    filteredTimeslots.add(timeslot);
                }
            }
            mSchedulesToTimeslotMap.put(schedule, filteredTimeslots);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mSchedules = new ArrayList<>(DBHelper.getAllSchedules());
        // Get all schedules whose Timeslots are currently on
        mOnSchedules = new ArrayList<>(DBHelper.getSchedulesWithOnTimeslots(mSchedules, true));
        mSchedulesToAppMap = new HashMap<>();
        mSchedulesToTimeslotMap = new HashMap<>();
        fillHashMaps();

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        mListView = view.findViewById(R.id.lvExpandableSchedule);
        mAdapter = new ExpandableScheduleAdapter(getActivity(), mOnSchedules,
                mSchedulesToAppMap, mSchedulesToTimeslotMap);
        mListView.setAdapter(mAdapter);
        mListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition,
                                        int childPosition, long id) {
                final App app = (App) mAdapter.getChild(groupPosition, childPosition);
                Toast.makeText(getActivity(), app.appName, Toast.LENGTH_LONG)
                        .show();

                return true;
            }
        });


        //mTimeslots = new ArrayList<>(DBHelper.getAllOnTimeslots());
        //mAdapter = new TimeslotToggleAdapter(getActivity(), mTimeslots);
        //mListView.setAdapter(mAdapter);

        return view;
    }
}
