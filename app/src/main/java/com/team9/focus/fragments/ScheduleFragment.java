package com.team9.focus.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.team9.focus.R;
import com.team9.focus.activities.CreateScheduleActivity;
import com.team9.focus.activities.EditScheduleActivity;
import com.team9.focus.adapters.ScheduleAdapter;
import com.team9.focus.models.DBHelper;
import com.team9.focus.models.objects.Schedule;

import java.util.ArrayList;


public class ScheduleFragment extends Fragment {

    private FloatingActionButton mCreateScheduleButton;
    private Button mEditScheduleButton;
    private ListView mScheduleListView;
    private ArrayList<Schedule> mSchedules;
    private ScheduleAdapter mScheduleAdapter;

    public ScheduleFragment() {

    }

    @Override
    public void onResume() {
        super.onResume();
        mSchedules.clear();
        mSchedules.addAll(DBHelper.getAllSchedules());
        mScheduleAdapter.refresh(mSchedules);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);

        mCreateScheduleButton = view.findViewById(R.id.floatingActionButton);
        mCreateScheduleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CreateScheduleActivity.class);
                startActivity(intent);
            }
        });

        // Get the GUI elements
        mScheduleListView = view.findViewById(R.id.scheduleListView);

        //Set up Schedule adapter
        mSchedules = new ArrayList<>(DBHelper.getAllSchedules());
        mScheduleAdapter = new ScheduleAdapter(getActivity(), mSchedules);
        mScheduleListView.setAdapter(mScheduleAdapter);

        return view;
    }


}
