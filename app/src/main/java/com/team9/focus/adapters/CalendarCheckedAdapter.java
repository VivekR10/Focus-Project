package com.team9.focus.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.team9.focus.R;
import com.team9.focus.models.objects.Calendar;

import java.util.ArrayList;

import static com.activeandroid.Cache.getContext;

/**
 * Created by Nandhini on 10/15/17.
 */

public class CalendarCheckedAdapter extends ArrayAdapter<Calendar>{
    private boolean mCheckBoxes[];
    private ArrayList<Calendar> mCalendars, mSelectedCalendars;

    public CalendarCheckedAdapter(Context context, ArrayList<Calendar> calendars) {
        super(context, 0, calendars);

        // Check if calendars is null before creating checkboxes
        if(calendars != null){
            mCheckBoxes = new boolean[calendars.size()];
        } else {
            mCheckBoxes = new boolean[0];
        }

        mCalendars = calendars;
    }

    // Second constructor for when calendars are already checked
    public CalendarCheckedAdapter(Context context, ArrayList<Calendar> calendars,
                                  ArrayList<Calendar> selectedCalendars) {
        super(context, 0, calendars);

        // Check if calendars is null before creating checkboxes
        if(calendars != null){
            mCheckBoxes = new boolean[calendars.size()];
        } else {
            mCheckBoxes = new boolean[0];
        }

        mCalendars = calendars;
        mSelectedCalendars = selectedCalendars;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Calendar calendar = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_view_item_checked_calendar, parent, false);
        }

        // Set the text to the name of the calendar
        TextView tvCalendarName = convertView.findViewById(R.id.tvCalendarName);
        tvCalendarName.setText(calendar.calendarName);

        // Check the calendars that are already a part of it
        if(mSelectedCalendars != null){
            if(mSelectedCalendars.contains(calendar)){
                mCheckBoxes[position] = true;
            }
        }

        final CheckBox cb =  convertView.findViewById(R.id.cbCalendarName);
        cb.setChecked(mCheckBoxes[position]);
        cb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCheckBoxes[position] = cb.isChecked();
            }
        });

        // Return the completed view to render on screen
        return convertView;
    }

    /**
     *
     * @return the list of calendars that were selected by checkboxes
     */
    public ArrayList<Calendar> getSelectedCalendars() {
        ArrayList<Calendar> selectedCalendars = new ArrayList<>();
        for (int i = 0; i < mCheckBoxes.length; ++i) {
            if (mCheckBoxes[i]) {
                selectedCalendars.add(mCalendars.get(i));
            }
        }
        return selectedCalendars;
    }
}
