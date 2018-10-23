package com.team9.focus.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.team9.focus.R;
import com.team9.focus.models.objects.App;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jtsui on 10/12/17.
 */

public class AppAdapter extends ArrayAdapter<App> {
    private boolean mCheckBoxes[];
    private ArrayList<App> mApps;

    public AppAdapter(Context context, ArrayList<App> apps) {
        super(context, 0, apps);
        mCheckBoxes = new boolean[apps.size()];
        mApps = apps;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        App app = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_view_item_app, parent, false);
        }

        TextView tvAppName = convertView.findViewById(R.id.tvAppName);
        tvAppName.setText(app.appName);

        final CheckBox cb =  convertView.findViewById(R.id.cbAppName);
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

    public ArrayList<App> getSelectedApps() {
        ArrayList<App> selectedApps = new ArrayList<>();
        for (int i = 0; i < mCheckBoxes.length; ++i) {
            if (mCheckBoxes[i]) {
                selectedApps.add(mApps.get(i));
            }
        }
        return selectedApps;
    }
}
