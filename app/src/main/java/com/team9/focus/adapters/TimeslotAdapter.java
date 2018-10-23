package com.team9.focus.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.team9.focus.R;
import com.team9.focus.models.objects.Profile;
import com.team9.focus.models.objects.Timeslot;

import java.sql.Time;
import java.util.ArrayList;

/**
 * Created by maya on 10/12/17.
 */

public class TimeslotAdapter extends ArrayAdapter<Timeslot> {

    private ArrayList<Timeslot> mTimeslots;

    public TimeslotAdapter(Context context, ArrayList<Timeslot> timeslots) {
        super(context, 0, timeslots);
        mTimeslots = timeslots;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Timeslot timeslot = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_view_item_timeslot, parent, false);
        }

        TextView tvTimeslotTime = convertView.findViewById(R.id.tvTimeslot);
        tvTimeslotTime.setText(timeslot.formatAsString());

        // Return the completed view to render on screen
        return convertView;
    }
}
