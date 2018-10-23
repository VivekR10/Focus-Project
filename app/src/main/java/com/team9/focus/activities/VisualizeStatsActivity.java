package com.team9.focus.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.team9.focus.R;
import com.team9.focus.models.DBHelper;
import com.team9.focus.models.objects.App;
import com.team9.focus.models.objects.Profile;
import com.team9.focus.models.objects.Schedule;

import java.util.ArrayList;

public class VisualizeStatsActivity extends AppCompatActivity {

    // All apps
    private ArrayList<App> mApps;
  
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualize_stats);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Retrieve the list of apps and put them in mApps
        mApps = new ArrayList<>(DBHelper.getAllApps());
        int maxVariable = 3;
        createHoursFocusedGraph(maxVariable);
        createOverridesGraph(maxVariable);
        createMaxProfileUsesGraph(maxVariable);
        createMaxLaunchesGraph(maxVariable);
        createMaxNotifsBlockedGraph(maxVariable);
        createMaxMinutesBlockedGraph(maxVariable);
    }

    //@TODO: error thrown if size < 2. fix this.
    public void createHoursFocusedGraph(int maxNumSchedules) {
        // Data for total hours focused graph
        final ArrayList<Schedule> hoursFocusedSchedules = new ArrayList<>(DBHelper.getTopHoursFocused(maxNumSchedules));
        if(hoursFocusedSchedules.size() < 2) {
            return;
        }
        // Create data point array and labels array
        DataPoint[] dp = new DataPoint[hoursFocusedSchedules.size()];
        String[] scheduleNames = new String[hoursFocusedSchedules.size()];
        for(int i=0; i<hoursFocusedSchedules.size(); i++){
            dp[i] = new DataPoint(i, hoursFocusedSchedules.get(i).hoursFocused);
            scheduleNames[i] = hoursFocusedSchedules.get(i).scheduleName;
            Log.d("VSA_scheduleName", hoursFocusedSchedules.get(i).scheduleName);
            Log.d("VSA_hoursFocused", Double.toString(hoursFocusedSchedules.get(i).hoursFocused));
        }
        // Create BarGraphSeries
        BarGraphSeries<DataPoint> series = new BarGraphSeries<>(dp);
        series.setSpacing(10);
        // Set colors
        final int[] colors = new int[3];
        colors[0] = Color.rgb(80, 169, 195);
        colors[1] = Color.rgb(119, 203, 209);
        colors[2] = Color.rgb(172, 244, 249);

        series.setValueDependentColor(new ValueDependentColor<DataPoint>() {
            @Override
            public int get(DataPoint data) {
                return colors[(int) data.getX()];
            }
        });
        // Create BarGraph
        final GraphView hoursFocusedGraph = (GraphView) findViewById(R.id.hoursFocusedGraph);
        hoursFocusedGraph.addSeries(series);
        hoursFocusedGraph.getViewport().setScalable(true);
        hoursFocusedGraph.getViewport().setXAxisBoundsManual(true);
        hoursFocusedGraph.getViewport().setXAxisBoundsStatus(Viewport.AxisBoundsStatus.AUTO_ADJUSTED);
        hoursFocusedGraph.getViewport().setMinY(0);
        hoursFocusedGraph.getViewport().setYAxisBoundsManual(true);
        hoursFocusedGraph.setTitle("Top Hours Focused");

        //Create Labels
        StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(hoursFocusedGraph);
        staticLabelsFormatter.setHorizontalLabels(scheduleNames);
        hoursFocusedGraph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);
    }

    public void createOverridesGraph(int maxNumApps) {
        final ArrayList<App> appOverrides = new ArrayList<>(DBHelper.getTopOverrides(maxNumApps));

        // Create data point array and labels array
        DataPoint[] dp = new DataPoint[appOverrides.size()];
        String[] appNames = new String[appOverrides.size()];
        for(int i=0; i<appOverrides.size(); i++){
            dp[i] = new DataPoint(i, appOverrides.get(i).overrides);
            appNames[i] = appOverrides.get(i).appName;
            Log.d("VSA_appName", appNames[i]);
            Log.d("VSA_numOverrides", Double.toString(appOverrides.get(i).overrides));
        }

        // Create BarGraphSeries
        BarGraphSeries<DataPoint> series = new BarGraphSeries<>(dp);
        series.setSpacing(10);

        // Set colors
        final int[] colors = new int[3];
        colors[0] = Color.rgb(36, 26, 127);
        colors[1] = Color.rgb(80, 67, 198);
        colors[2] = Color.rgb(128, 114, 249);
        series.setValueDependentColor(new ValueDependentColor<DataPoint>() {
            @Override
            public int get(DataPoint data) {
                return colors[(int) data.getX()];
            }
        });
        // Create BarGraph
        final GraphView overridesGraph = (GraphView) findViewById(R.id.overridesGraph);
        overridesGraph.addSeries(series);
        overridesGraph.getViewport().setScalable(true);
        overridesGraph.getViewport().setXAxisBoundsManual(true);
        overridesGraph.getViewport().setXAxisBoundsStatus(Viewport.AxisBoundsStatus.AUTO_ADJUSTED);
        overridesGraph.getViewport().setMinY(0);
        overridesGraph.getViewport().setYAxisBoundsManual(true);
        series.setDrawValuesOnTop(true);
        overridesGraph.setTitle("Top Overrides Used");

        //Create Labels
        StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(overridesGraph);
        staticLabelsFormatter.setHorizontalLabels(appNames);
        overridesGraph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);
    }

    public void createMaxProfileUsesGraph(int maxNumProfiles) {
        final ArrayList<Profile> maxProfileUses = new ArrayList<>(DBHelper.getMaxProfileUses(maxNumProfiles));

        // Create data point array and labels array
        DataPoint[] dp = new DataPoint[maxProfileUses.size()];
        String[] profileNames = new String[maxProfileUses.size()];
        for(int i=0; i<maxProfileUses.size(); i++){
            dp[i] = new DataPoint(i, maxProfileUses.get(i).numUses);
            profileNames[i] = maxProfileUses.get(i).profileName;
            Log.d("VSA_profileName", profileNames[i]);
            Log.d("VSA_numProfileUses", Double.toString(maxProfileUses.get(i).numUses));
        }

        // Create BarGraphSeries
        BarGraphSeries<DataPoint> series = new BarGraphSeries<>(dp);
        series.setSpacing(10);
        // Set colors
        final int[] colors = new int[3];
        colors[0] = Color.rgb(160, 195, 255);
        colors[1] = Color.rgb(104, 159, 255);
        colors[2] = Color.rgb(0, 93, 255);
        series.setValueDependentColor(new ValueDependentColor<DataPoint>() {
            @Override
            public int get(DataPoint data) {
                return colors[(int) data.getX()];
            }
        });

        // Create BarGraph
        final GraphView profileUseGraph = (GraphView) findViewById(R.id.maxProfileUsesGraph);
        profileUseGraph.addSeries(series);
        profileUseGraph.getViewport().setScalable(true);
        profileUseGraph.getViewport().setXAxisBoundsManual(true);
        profileUseGraph.getViewport().setXAxisBoundsStatus(Viewport.AxisBoundsStatus.AUTO_ADJUSTED);
        profileUseGraph.getViewport().setMinY(0);
        profileUseGraph.getViewport().setYAxisBoundsManual(true);
        profileUseGraph.setTitle("Top Profile Uses");

        //Create Labels
        StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(profileUseGraph);
        staticLabelsFormatter.setHorizontalLabels(profileNames);
        profileUseGraph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);
    }

    public void createMaxLaunchesGraph(int maxNumApps) {
        final ArrayList<App> maxLaunches = new ArrayList<>(DBHelper.getTopLaunchBlocks(maxNumApps));

        // Create data point array and labels array
        DataPoint[] dp = new DataPoint[maxLaunches.size()];
        String[] appNames = new String[maxLaunches.size()];
        for(int i=0; i<maxLaunches.size(); i++){
            dp[i] = new DataPoint(i, maxLaunches.get(i).launchBlockCount);
            appNames[i] = maxLaunches.get(i).appName;
            Log.d("VSA_appName", appNames[i]);
            Log.d("VSA_numLaunchBlocks", Double.toString(maxLaunches.get(i).launchBlockCount));
        }

        // Create BarGraphSeries
        BarGraphSeries<DataPoint> series = new BarGraphSeries<>(dp);
        series.setSpacing(10);

        // Set colors
        final int[] colors = new int[3];
        colors[0] = Color.rgb(255, 198, 197);
        colors[1] = Color.rgb(255, 156, 86);
        colors[2] = Color.rgb(237, 114, 26);
        series.setValueDependentColor(new ValueDependentColor<DataPoint>() {
            @Override
            public int get(DataPoint data) {
                return colors[(int) data.getX()];
            }
        });

        // Create BarGraph
        final GraphView maxLaunchesGraph = (GraphView) findViewById(R.id.maxLaunchBlocksGraph);
        maxLaunchesGraph.addSeries(series);
        maxLaunchesGraph.getViewport().setScalable(true);
        maxLaunchesGraph.getViewport().setXAxisBoundsManual(true);
        maxLaunchesGraph.getViewport().setXAxisBoundsStatus(Viewport.AxisBoundsStatus.AUTO_ADJUSTED);
        maxLaunchesGraph.getViewport().setMinY(0);
        maxLaunchesGraph.getViewport().setYAxisBoundsManual(true);
        maxLaunchesGraph.setTitle("Top Launches Blocked");

        //Create Labels
        StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(maxLaunchesGraph);
        staticLabelsFormatter.setHorizontalLabels(appNames);
        maxLaunchesGraph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);

    }

    public void createMaxNotifsBlockedGraph(int maxNumApps) {
        final ArrayList<App> maxNotifs = new ArrayList<>(DBHelper.getTopNotifsBlocks(maxNumApps));

        // Create data point array and labels array
        DataPoint[] dp = new DataPoint[maxNotifs.size()];
        String[] appNames = new String[maxNotifs.size()];
        for(int i=0; i<maxNotifs.size(); i++){
            dp[i] = new DataPoint(i, maxNotifs.get(i).blockCount);
            appNames[i] = maxNotifs.get(i).appName;
            Log.d("VSA_appName", appNames[i]);
            Log.d("VSA_numNotifsBlocked", Double.toString(maxNotifs.get(i).blockCount));
        }

        // Create BarGraphSeries
        BarGraphSeries<DataPoint> series = new BarGraphSeries<>(dp);
        series.setSpacing(10);

        // Set colors
        final int[] colors = new int[3];
        colors[0] = Color.rgb(201, 255, 196);
        colors[1] = Color.rgb(171, 239, 165);
        colors[2] = Color.rgb(119, 188, 113);
        series.setValueDependentColor(new ValueDependentColor<DataPoint>() {
            @Override
            public int get(DataPoint data) {
                return colors[(int) data.getX()];
            }
        });
        // Create BarGraph
        final GraphView maxNotifsGraph = (GraphView) findViewById(R.id.maxNotifsBlockedGraph);
        maxNotifsGraph.addSeries(series);
        maxNotifsGraph.getViewport().setScalable(true);
        maxNotifsGraph.getViewport().setXAxisBoundsManual(true);
        maxNotifsGraph.getViewport().setXAxisBoundsStatus(Viewport.AxisBoundsStatus.AUTO_ADJUSTED);
        maxNotifsGraph.getViewport().setMinY(0);
        maxNotifsGraph.getViewport().setYAxisBoundsManual(true);

        maxNotifsGraph.setTitle("Top Notifs Blocked");

        //Create Labels
        StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(maxNotifsGraph);
        staticLabelsFormatter.setHorizontalLabels(appNames);
        maxNotifsGraph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);

    }

    public void createMaxMinutesBlockedGraph(int maxNumApps) {
        final ArrayList<App> maxMinutes = new ArrayList<>(DBHelper.getTopMinutesBlocks(maxNumApps));

        // Create data point array and labels array
        DataPoint[] dp = new DataPoint[maxMinutes.size()];
        String[] appNames = new String[maxMinutes.size()];
        for(int i=0; i<maxMinutes.size(); i++){
            dp[i] = new DataPoint(i, maxMinutes.get(i).minutesBlocked);
            appNames[i] = maxMinutes.get(i).appName;
            Log.d("VSA_appName", appNames[i]);
            Log.d("VSA_numMinBlocked", Double.toString(maxMinutes.get(i).minutesBlocked));
        }

        // Create BarGraphSeries
        BarGraphSeries<DataPoint> series = new BarGraphSeries<>(dp);
        series.setSpacing(10);

        // Set colors
        final int[] colors = new int[3];
        colors[0] = Color.rgb(255, 216, 248);
        colors[1] = Color.rgb(252, 179, 229);
        colors[2] = Color.rgb(232, 125, 198);
        series.setValueDependentColor(new ValueDependentColor<DataPoint>() {
            @Override
            public int get(DataPoint data) {
                return colors[(int) data.getX()];
            }
        });

        // Create BarGraph
        final GraphView maxMinsGraph = (GraphView) findViewById(R.id.maxMinutesBlockedGraph);
        maxMinsGraph.addSeries(series);
        maxMinsGraph.getViewport().setScalable(true);
        maxMinsGraph.getViewport().setXAxisBoundsManual(true);
        maxMinsGraph.getViewport().setXAxisBoundsStatus(Viewport.AxisBoundsStatus.AUTO_ADJUSTED);
        maxMinsGraph.getViewport().setMinY(0);
        maxMinsGraph.getViewport().setYAxisBoundsManual(true);
        maxMinsGraph.setTitle("Top Mins Blocked");

        //Create Labels
        StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(maxMinsGraph);
        staticLabelsFormatter.setHorizontalLabels(appNames);
        maxMinsGraph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);

    }

}
