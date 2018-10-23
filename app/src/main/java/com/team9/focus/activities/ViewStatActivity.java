package com.team9.focus.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.team9.focus.R;
import com.team9.focus.adapters.AppStatAdapter;
import com.team9.focus.models.DBHelper;
import com.team9.focus.models.objects.App;

import java.util.ArrayList;

public class ViewStatActivity extends AppCompatActivity {
    private Button mVisualizeStatsButton;
    private Context mContext;
    private ListView mListView;
    // All apps
    private ArrayList<App> mApps;

    // Apps that the user selects to add to the profile
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_stat);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Retrieve the list of apps and put them in mApps
        mApps = new ArrayList<>(DBHelper.getAllApps());

        final AppStatAdapter adapter = new AppStatAdapter(this, mApps);

        mListView = findViewById(R.id.appListView);

        mListView.setAdapter(adapter);

        mContext = this;

        mVisualizeStatsButton = (Button) findViewById(R.id.visualizeStatsButton);

        mVisualizeStatsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, VisualizeStatsActivity.class);
                startActivity(intent);
            }
        });
    }


}



//
