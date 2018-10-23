package com.team9.focus.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.team9.focus.R;
import com.team9.focus.activities.CreateProfileActivity;
import com.team9.focus.adapters.ExpandableProfileAdapter;
import com.team9.focus.models.DBHelper;
import com.team9.focus.models.objects.App;
import com.team9.focus.models.objects.Profile;
import com.team9.focus.utilities.ProfileUseComparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jtsui on 9/29/17.
 */

public class ProfileFragment extends Fragment {

    private FloatingActionButton mCreateProfileButton;
    private Button mEditProfileButton;
    private ExpandableListView mListView;
    private ExpandableProfileAdapter mAdapter;

    // ArrayList holding all of the profiles
    private ArrayList<Profile> mProfiles;
    // HashMap mapping each profile to its apps. Used for the ExpandableProfileAdapter
    private Map<Profile, List<App>> mProfileToAppsMap;

    public ProfileFragment() {

    }


    @Override
    public void onResume() {
        super.onResume();
        // Refresh the profiles
        mProfiles.clear();
        mProfiles.addAll(DBHelper.getAllProfiles());
        Collections.sort(mProfiles, new ProfileUseComparator());
        mProfileToAppsMap.clear();
        for (Profile p : mProfiles) {
            mProfileToAppsMap.put(p, DBHelper.getAppsByProfile(p));
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Populate the arraylist
        mProfiles = new ArrayList<>(DBHelper.getAllProfiles());

        // Sort the profiles
        for(Profile p : mProfiles){
            DBHelper.setNumProfileUses(p);
        }
        Collections.sort(mProfiles, new ProfileUseComparator());

        // Populate the hashmap and set number
        mProfileToAppsMap = new HashMap<>();
        for (Profile p : mProfiles) {
            mProfileToAppsMap.put(p, DBHelper.getAppsByProfile(p));
        }

        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_profile, container, false);
        mCreateProfileButton = view.findViewById(R.id.addProfileButton);
        mCreateProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CreateProfileActivity.class);
                startActivity(intent);
            }
        });


        // Populate the listview with the custom expandable adaper
        mListView = view.findViewById(R.id.lvProfiles);
        mAdapter = new ExpandableProfileAdapter(getActivity(), mProfiles, mProfileToAppsMap);
        mListView.setAdapter(mAdapter);
        mListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition,
                                        int childPosition, long id) {
                final App app = (App) mAdapter.getChild(groupPosition, childPosition);
                Toast.makeText(getActivity(), app.appName, Toast.LENGTH_LONG)
                        .show();

                return true;
            }
        });



        return view;
    }
}
