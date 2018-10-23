package com.team9.focus.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.team9.focus.R;
import com.team9.focus.models.objects.Badge;

import java.util.ArrayList;

/**
 * Created by jtsui on 10/12/17.
 */

public class BadgeAdapter extends ArrayAdapter<Badge> {

    private ArrayList<Badge> mBadges;

    public BadgeAdapter(Context context, ArrayList<Badge> badges) {
        super(context, 0, badges);
        mBadges = badges;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Badge badge = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_view_item_badge, parent, false);
        }

        ImageView badgeImage = convertView.findViewById(R.id.badgeImage);
        TextView tvDateEarned = convertView.findViewById(R.id.tvDateEarned);
        TextView tvBadgeName = convertView.findViewById(R.id.tvBadgeName);
        TextView tvGoal = convertView.findViewById(R.id.tvGoal);
        if(badge.isEarned)
        {
            badgeImage.setImageURI(Uri.parse(badge.badgeIcon));
            tvDateEarned.setText(badge.dateEarned);
            tvBadgeName.setText(badge.badgeName);
            tvGoal.setText(Double.toString(badge.dataGoal));

        }
        else
        {
            badgeImage.setImageURI(Uri.parse("android.resource://com.team9.focus/drawable/question_mark"));
            tvDateEarned.setText("???");
            tvBadgeName.setText("???");
            tvGoal.setText("???");
        }


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
