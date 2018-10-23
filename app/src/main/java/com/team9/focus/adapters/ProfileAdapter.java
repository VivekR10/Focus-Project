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
import com.team9.focus.models.objects.Profile;

import java.util.ArrayList;

import static com.team9.focus.R.id.tvAppName;

/**
 * Created by maya on 10/12/17.
 */

public class ProfileAdapter extends ArrayAdapter<Profile> {
    private boolean mCheckBoxes[];
    private ArrayList<Profile> mProfiles, mSelectedProfiles;
    private CheckBox mCB;

    public ProfileAdapter(Context context, ArrayList<Profile> profiles) {
        super(context, 0, profiles);
        mCheckBoxes = new boolean[profiles.size()];
        mProfiles = profiles;
    }

    // Constructor for view when profiles are already checked
    public ProfileAdapter(Context context, ArrayList<Profile> profiles, ArrayList<Profile> selectedProfiles){
        super(context, 0, profiles);
        mCheckBoxes = new boolean[profiles.size()];
        mProfiles = profiles;
        mSelectedProfiles = selectedProfiles;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Profile profile = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_view_item_profile, parent, false);
        }

        // Get the ProfileName text views from the view
        TextView tvProfileName = convertView.findViewById(R.id.tvProfileName);
        tvProfileName.setText(profile.profileName);

        // Get the checkboxes from the view
        mCB =  convertView.findViewById(R.id.cbProfileName);

        // Set the profiles that are already checked
        if(mSelectedProfiles != null){
            if(mSelectedProfiles.contains(profile)){
                mCheckBoxes[position] = true;
            }
        }

        // Set all profiles which are not checked
        mCB.setChecked(mCheckBoxes[position]);
        mCB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCheckBoxes[position] = !mCheckBoxes[position];
            }
        });

        // Return the completed view to render on screen
        return convertView;
    }

    public ArrayList<Profile> getSelectedProfiles() {
        ArrayList<Profile> selectedProfiles = new ArrayList<>();
        for (int i = 0; i < mCheckBoxes.length; ++i) {
            if (mCheckBoxes[i]) {
                selectedProfiles.add(mProfiles.get(i));
            }
        }
        return selectedProfiles;
    }
}
