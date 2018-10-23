package com.team9.focus.fragments;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.NumberPicker;

import com.team9.focus.BackgroundServices.ScheduleListener;
import com.team9.focus.R;
import com.team9.focus.adapters.ProfileAdapter;
import com.team9.focus.exceptions.AlreadyExistsException;
import com.team9.focus.models.DBHelper;
import com.team9.focus.models.objects.Badge;
import com.team9.focus.models.objects.Profile;
import com.team9.focus.models.objects.Schedule;
import com.team9.focus.models.objects.Timeslot;
import com.team9.focus.utilities.BadgeComparator;
import com.team9.focus.utilities.ProfileUseComparator;
import com.team9.focus.utilities.Utility;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/*
 * @TODO:
 * 1. Change the calculation of the durations to correct # of seconds (keep as is for testing purposes)
 */
public class TimerFragment extends Fragment {

    private NumberPicker mHoursPicker;
    private NumberPicker mMinutesPicker;
    private Button mSetTimerButton;
    private ListView mProfileList;
    private ArrayList<Profile> mProfiles;
    private ArrayList<Profile> mSelectedProfiles;

    public TimerFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_timer, container, false);

        mHoursPicker = view.findViewById(R.id.hoursPicker);
        mMinutesPicker = view.findViewById(R.id.minutesPicker);
        mSetTimerButton = view.findViewById(R.id.timerStart);
        mProfileList = view.findViewById(R.id.profileListView);

        mHoursPicker.setMinValue(0);
        mHoursPicker.setMaxValue(10);

        mMinutesPicker.setMinValue(0);
        mMinutesPicker.setMaxValue(59);
        mMinutesPicker.setValue(10);

        mProfiles = new ArrayList<>(DBHelper.getAllProfiles());

        // Sort the profiles
        for(Profile p : mProfiles){
            DBHelper.setNumProfileUses(p);
        }
        Collections.sort(mProfiles, new ProfileUseComparator());

        final ProfileAdapter adapter = new ProfileAdapter(getActivity(), mProfiles);
        mProfileList.setAdapter(adapter);

        mSetTimerButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
                {
                    mSelectedProfiles = adapter.getSelectedProfiles();
                    startTimer();
                }
        });

        return view;
    }

   private void startTimer() {
       long hours = mHoursPicker.getValue();
       long minutes = mMinutesPicker.getValue();

       if (hours >= 10 && minutes != 0) {
           Utility.createErrorDialog(getContext(),"Invalid Timer", "Timer cannot be longer than 10 hours!");
           return;
       } else if (hours == 0 && minutes < 10) {
           Utility.createErrorDialog(getContext(),"Invalid Timer", "Timer cannot be less than 10 minutes!");
           return;
       } else if (mSelectedProfiles.size() == 0) {
           Utility.createErrorDialog(getContext(),"Invalid Timer", "Must select at least one profile!");
           return;
       }

       // public static Timeslot createTimeslot(String daysOfWeek, double startTime, double endTime)
       Calendar calendar = Calendar.getInstance();
       int day = calendar.get(Calendar.DAY_OF_WEEK);
       Date date = calendar.getTime();
       int currHour = calendar.get(Calendar.HOUR_OF_DAY);
       int currMinute = calendar.get(Calendar.MINUTE);
       int endHour = currHour + (int) hours;
       int endMinute = currMinute + (int) minutes;
     
       //if timer goes to the next hour
       if (endMinute >= 60) {
            endMinute -= 60;
       }

        Timeslot t = DBHelper.createTimeslot(Integer.toString(day), currHour, currMinute, endHour, endMinute, false);
        ArrayList<Timeslot> slots = new ArrayList<Timeslot>();
        slots.add(t);

        Schedule s = DBHelper.getScheduleByName("Default");

        com.team9.focus.models.objects.Calendar c = DBHelper.getCalendarByName("Default");
        ArrayList<com.team9.focus.models.objects.Calendar> calendars = new ArrayList<com.team9.focus.models.objects.Calendar>();
        calendars.add(c);

       ArrayList<Schedule> defaultScheduleList = new ArrayList<>();
       defaultScheduleList.add(s);
       ArrayList<Schedule> timer = DBHelper.getSchedulesWithOnTimeslots(defaultScheduleList, false);
       if(timer.size() > 0) {
           Utility.createErrorDialog(getContext(),"Timer Error", "Cannot have two simultaneous timers. Please override the other timer first");
           return;
       }


       try {
           DBHelper.updateSchedule(s, "Default", slots, mSelectedProfiles, calendars);
       } catch (AlreadyExistsException e) {
           e.printStackTrace();
       }
       DBHelper.turnOnTimeslot(t);
        System.out.println("making a timer starting at " + currHour + ":" + currMinute + " and ending at " + endHour + ":" + endMinute);
        createIntent(t.getId());
        checkBadgeStatus(hours, minutes);


   }

   private void createIntent(long id) {
       AlarmManager manager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
       long hours = mHoursPicker.getValue();
       long minutes = mMinutesPicker.getValue();

       long interval = (hours*3600*1000)+(minutes*60*1000);
       Intent i = new Intent(getActivity(), ScheduleListener.class);
       i.putExtra("type", "end");
       i.putExtra("timeslotID", id);
       PendingIntent pi = PendingIntent.getBroadcast(getActivity(), 0, i, 0);

       // Make toast to show user time
       StringBuilder sb = new StringBuilder();
       long minsInterval = interval / (1000*60);
       sb.append("Timer set for " + minsInterval + " minutes");

       Utility.sendNotification(getContext(), sb, "Timer On");
       //Toast.makeText(getActivity(), sb.toString(), Toast.LENGTH_LONG).show();

       // Fire an alarm at the current time + interval that goes into onReceive()
       manager.set(AlarmManager.RTC, System.currentTimeMillis()+ interval, pi);
   }

   public void checkBadgeStatus(long hours, long minutes) {
       double interval = hours + minutes/60;

       List<Badge> badges = DBHelper.getBadgesByCategory("timer");
       Collections.sort(badges, new BadgeComparator());
       for(Badge badge : badges) {
           if(!badge.isEarned) {
               DBHelper.updateBadge(badge, interval, getContext());
           }
       }

   }


}
