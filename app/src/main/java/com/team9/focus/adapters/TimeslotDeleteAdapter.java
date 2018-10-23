package com.team9.focus.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.team9.focus.R;
import com.team9.focus.models.DBHelper;
import com.team9.focus.models.objects.Schedule;
import com.team9.focus.models.objects.Timeslot;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nandhini on 10/23/17.
 */

public class TimeslotDeleteAdapter extends ArrayAdapter<Timeslot> {
    private ArrayList<Timeslot> mTimeslots;
    private Context mContext;
    private Schedule mSchedule;

    public TimeslotDeleteAdapter(Context context, ArrayList<Timeslot> timeslots, Schedule schedule) {
        super(context, 0, timeslots);
        mTimeslots = timeslots;
        mContext = context;
        mSchedule = schedule;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        // Get the data item for this position
        final Timeslot timeslot = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_view_item_timeslot, parent, false);
        }

        TextView tvTimeslot = convertView.findViewById(R.id.tvTimeslot);
        tvTimeslot.setText(timeslot.formatAsString());

        // Set the onclick for the item
        tvTimeslot.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(mContext, android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(mContext);
                }
                StringBuilder sb = new StringBuilder();
                sb.append("Delete timeslot: " + "\n" +
                        timeslot.formatAsString() + "?" + "\n" + "\n");
                sb.append("This change will be saved automatically.");

                builder.setTitle("Delete Timeslot")
                        .setMessage(sb)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // remove the timeslot from the adapter
                                remove(timeslot);

                                // if the timeslot is in the database, remove it
                                if(mSchedule!=null) {
                                    List<Timeslot> timeslots = DBHelper.getTimeslotsBySchedule(mSchedule);
                                    if (timeslots.contains(timeslot)) {
                                        DBHelper.deleteTimeslot(timeslot);
                                    }
                                }
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
                return true;
            }
        });

        // Return the completed view to render on screen
        return convertView;
    }

}

