package com.team9.focus.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ListView;

import com.team9.focus.R;
import com.team9.focus.adapters.AppStatAdapter;
import com.team9.focus.adapters.BadgeAdapter;
import com.team9.focus.models.DBHelper;
import com.team9.focus.models.objects.App;
import com.team9.focus.models.objects.Badge;

import java.util.ArrayList;

public class ViewBadgeActivity extends AppCompatActivity {

    private ListView mListView;
    // All apps
    private ArrayList<Badge> mBadges;

    // Apps that the user selects to add to the profile
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_badge);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Retrieve the list of apps and put them in mApps
        mBadges = new ArrayList<>(DBHelper.getAllBadges());

        final BadgeAdapter adapter = new BadgeAdapter(this, mBadges);

        mListView = findViewById(R.id.badgeListView);

        mListView.setAdapter(adapter);
    }

}
