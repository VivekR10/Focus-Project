package com.team9.focus.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.team9.focus.R;
import com.team9.focus.adapters.ScheduleAdapter;
import com.team9.focus.adapters.ScheduleCheckedAdapter;
import com.team9.focus.exceptions.AlreadyExistsException;
import com.team9.focus.models.DBHelper;
import com.team9.focus.models.objects.Schedule;
import com.team9.focus.utilities.Utility;

import java.util.ArrayList;

public class CreateCalendarActivity extends AppCompatActivity {

    private Button mSaveCalendar;
    private ListView mScheduleList;
    private ArrayList<Schedule> mSchedules;
    private ArrayList<Schedule> mSelectedSchedules;
    private EditText mEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_calendar);

        mSaveCalendar = findViewById(R.id.saveButton);
        mSchedules = new ArrayList<>(DBHelper.getAllSchedules());
        mScheduleList = findViewById(R.id.scheduleListView);
        mEditText = findViewById(R.id.etCalendarName);

        final ScheduleCheckedAdapter adapter = new ScheduleCheckedAdapter(this, mSchedules);
        mScheduleList.setAdapter(adapter);

        mSaveCalendar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                mSelectedSchedules = adapter.getSelectedSchedules();
                if (trySaveCalendar(mEditText.getText().toString(), mSelectedSchedules)) {
                    finish();
                }
            }

        });


    }

    private boolean trySaveCalendar(String name, ArrayList<Schedule> schedules) {
        if (DBHelper.saveCalendar(name, schedules)) {
            return true;
        } else if (name == null || name.isEmpty()) {
            Utility.createErrorDialog(this, "No Name", "You must name your calendar.");
        } else {
            Utility.createErrorDialog(this, "Name Exists", "A calendar with this name already exists.");
        }
        return false;
    }
}
