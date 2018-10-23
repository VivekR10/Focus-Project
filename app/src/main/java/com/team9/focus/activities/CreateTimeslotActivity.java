package com.team9.focus.activities;

import android.app.AlarmManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.team9.focus.R;
import com.team9.focus.models.objects.Timeslot;
import com.team9.focus.utilities.Utility;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.ArrayList;
import java.util.Calendar;

public class CreateTimeslotActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {

    private Button mScheduleButton;
    private Button mSelectDayButton;
    private Button mStartTimeButton;
    private Button mEndTimeButton;

    private TextView mStartTimeTv;
    private TextView mEndTimeTv;
    private TextView mDaysOfWeekTv;

    private TimePickerDialog mStartTimePicker;
    private TimePickerDialog mEndTimePicker;
    private int mStartHour = -1;
    private int mStartMinute = -1;
    private int mEndHour = -1;
    private int mEndMinute = -1;

    private AlertDialog mDialog;
    private final ArrayList<Integer> mCheckedIndices = new ArrayList<>();

    String previousActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_timeslot);

        if(getIntent().hasExtra("sender")) {
            previousActivity = getIntent().getStringExtra("sender");
        }
        else {
            Log.d("CTA", "Error");
        }

        final CharSequence[] items = { "Sunday", "Monday",
                "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};

        mDialog = new AlertDialog.Builder(this)
                .setTitle("Select Days of Week")
                .setMultiChoiceItems(items, null, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int index, boolean isChecked) {
                        if (isChecked) {
                            mCheckedIndices.add(index);
                        } else if (mCheckedIndices.contains(index)) {
                            mCheckedIndices.remove(Integer.valueOf(index));
                        }
                    }
                }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        StringBuilder sb = new StringBuilder();

                        for (int j = 0; j < mCheckedIndices.size(); ++j) {
                            sb.append(items[mCheckedIndices.get(j)]);
                            if (j != mCheckedIndices.size()) {
                                sb.append(", ");
                            }
                        }
                        mDaysOfWeekTv.setText(sb.toString());
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).create();



        mSelectDayButton = findViewById(R.id.btnSelectDays);
        mSelectDayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.show();
            }
        });

        mScheduleButton = findViewById(R.id.scheduleButton);
        //add listener to button
        mScheduleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createTimeslot();
            }
        });

        Calendar now = Calendar.getInstance();
        mStartTimePicker = TimePickerDialog.newInstance(
                CreateTimeslotActivity.this,
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                false
        );
        mStartTimePicker.setVersion(TimePickerDialog.Version.VERSION_2);
        mStartTimePicker.setTitle("Start Time");

        mEndTimePicker = TimePickerDialog.newInstance(
                CreateTimeslotActivity.this,
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                false
        );
        mEndTimePicker.setVersion(TimePickerDialog.Version.VERSION_2);
        mEndTimePicker.setTitle("End Time");

        mStartTimeButton = findViewById(R.id.btnStartTime);
        mStartTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mStartTimePicker.show(getFragmentManager(), "start time");
            }
        });

        mEndTimeButton = findViewById(R.id.btnEndTime);
        mEndTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mEndTimePicker.show(getFragmentManager(), "end time");
            }
        });

        mStartTimeTv = findViewById(R.id.tvStartTime);
        mEndTimeTv = findViewById(R.id.tvEndTime);
        mDaysOfWeekTv = findViewById(R.id.tvSelectedDays);
    }


    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
        System.out.println(view.getTitle());
        System.out.println(hourOfDay);
        System.out.println(minute);
        if (view.getTitle().equals("Start Time")) {
            mStartHour = hourOfDay;
            mStartMinute = minute;
            String timeConvention = mStartHour >= 12 ? "PM" : "AM";

            mStartTimeTv.setText(String.format("%d:%d%s", mStartHour%12, mStartMinute, timeConvention));
        } else {
            mEndHour = hourOfDay;
            mEndMinute = minute;
            String timeConvention = mStartHour >= 12 ? "PM" : "AM";
            mEndTimeTv.setText(String.format("%d:%d%s", mEndHour%12, mEndMinute, timeConvention));
        }
    }


    private void createTimeslot() {

        AlarmManager manager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);

        //Validate
        if(((mStartHour == mEndHour) && mStartMinute >= mEndMinute) || (mStartHour > mEndHour)) {
            Utility.createErrorDialog(this, "Invalid Input", "Please fix the start and end time");
            return;
        }

        StringBuilder daysOfWeek = new StringBuilder();
        if (mCheckedIndices.isEmpty()) {
            Utility.createErrorDialog(this, "Invalid Input", "Please select a day of the week");
            return;
        }
        for (int i = 0; i < mCheckedIndices.size(); ++i) {
            daysOfWeek.append(mCheckedIndices.get(i)+1);
            daysOfWeek.append(",");

        }
        Timeslot t = new Timeslot(daysOfWeek.toString(), mStartHour, mStartMinute, mEndHour, mEndMinute);

        //1 = Sunday, 7 = Saturday
        Toast.makeText(this, "Timeslot created!", Toast.LENGTH_SHORT).show();
        if(previousActivity.equals("create")) {
            Intent backToScheduleIntent;

            backToScheduleIntent= new Intent(this, CreateScheduleActivity.class);
            backToScheduleIntent.putExtra("timeslot", t);
            startActivity(backToScheduleIntent);
        }
        if(previousActivity.equals("edit")) {
            Intent backToScheduleIntent;
            String scheduleName = getIntent().getExtras().getString("scheduleName");
            backToScheduleIntent= new Intent(this, EditScheduleActivity.class);
            backToScheduleIntent.putExtra("timeslot", t);
            backToScheduleIntent.putExtra("scheduleName", scheduleName);
            startActivity(backToScheduleIntent);
        }
    }


}
