package com.team9.focus.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.team9.focus.R;
import com.team9.focus.models.objects.App;

import java.util.ArrayList;

/**
 * Created by jtsui on 10/12/17.
 */

public class AppStatAdapter extends ArrayAdapter<App> {
    private boolean mCheckBoxes[];
    private ArrayList<App> mApps;

    public AppStatAdapter(Context context, ArrayList<App> apps) {
        super(context, 0, apps);
        mApps = apps;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        App app = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_view_item_app_stat, parent, false);
        }

        TextView tvAppName = convertView.findViewById(R.id.tvAppName);
        tvAppName.setText(app.appName);

        TextView tvBlockCount = convertView.findViewById(R.id.tvBlockCount);
        tvBlockCount.setText(Integer.toString(app.blockCount));

        TextView tvLaunchCount = convertView.findViewById(R.id.tvLaunchCount);
        tvLaunchCount.setText(Integer.toString(app.launchBlockCount));

        TextView tvMinutesCount = convertView.findViewById(R.id.tvMinutesCount);
        tvMinutesCount.setText(Integer.toString(app.minutesBlocked));

        //TODO: Only display apps that have a non-zero block count
        /*if(app.blockCount!=0) {
            TextView tvAppName = convertView.findViewById(R.id.tvAppName);
            tvAppName.setText(app.appName);

            TextView tvBlockCount = convertView.findViewById(R.id.tvBlockCount);
            tvBlockCount.setText(Integer.toString(app.blockCount));
        }
        else
        {
            TextView tvAppName = convertView.findViewById(R.id.tvAppName);
            tvAppName.setText("");
            tvAppName.setVisibility(View.INVISIBLE);
            TextView tvBlockCount = convertView.findViewById(R.id.tvBlockCount);
            tvBlockCount.setText("");
            tvBlockCount.setHeight(View.INVISIBLE);
        }*/
        // Return the completed view to render on screen
        return convertView;
    }

}
