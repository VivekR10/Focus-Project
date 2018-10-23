package com.team9.focus.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.team9.focus.R;
import com.team9.focus.activities.EditProfileActivity;
import com.team9.focus.models.DBHelper;
import com.team9.focus.models.objects.App;
import com.team9.focus.models.objects.Profile;
import com.team9.focus.models.objects.Schedule;
import com.team9.focus.utilities.Utility;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by jtsui on 10/13/17.
 */

public class ExpandableProfileAdapter extends BaseExpandableListAdapter {

    private Activity mContext;
    private ArrayList<Profile> mProfiles;
    private Map<Profile, List<App>> mProfileToAppsMap;

    public ExpandableProfileAdapter(Activity context, ArrayList<Profile> profiles,
                                    Map<Profile, List<App>> map) {
        this.mContext = context;
        this.mProfiles = profiles;
        this.mProfileToAppsMap = map;
    }

    @Override
    public int getGroupCount() {
        return mProfiles.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mProfileToAppsMap.get(mProfiles.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mProfiles.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mProfileToAppsMap.get(mProfiles.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        Profile profile = (Profile) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.profile_parent_item, null);
        }

        TextView textView = convertView.findViewById(R.id.tvProfileNameParent);
        textView.setTypeface(null, Typeface.BOLD);
        textView.setText(profile.profileName);

        Button btn = convertView.findViewById(R.id.btnEditProfile);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Profile profile = (Profile) getGroup(groupPosition);

                // Determine if it is part of a schedule with a Timeslot that is on
                ArrayList<Schedule> allSchedules = new ArrayList<>(DBHelper.getSchedulesByProfile(profile));
                ArrayList<Schedule> onSchedules = DBHelper.getSchedulesWithOnTimeslots(allSchedules, false);
                if(onSchedules.size()!=0){
                    Utility.createErrorDialog(mContext, "Cannot Edit", "Override the schedule before editing this profile.");
                    return;
                }
                Intent intent = new Intent(mContext, EditProfileActivity.class);
                intent.putExtra("profileName", profile.profileName);
                mContext.startActivity(intent);
            }
        });

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView,
                             ViewGroup parent) {
        App app = (App) getChild(groupPosition, childPosition);
        LayoutInflater inflater = mContext.getLayoutInflater();
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.profile_child_item, null);
        }
        TextView textView = convertView.findViewById(R.id.tvAppNameChild);
        textView.setTypeface(null, Typeface.BOLD);
        textView.setText(app.appName);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
