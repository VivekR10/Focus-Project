package com.team9.focus.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.team9.focus.R;
import com.team9.focus.adapters.AppAdapter;
import com.team9.focus.exceptions.AlreadyExistsException;
import com.team9.focus.models.DBHelper;
import com.team9.focus.models.objects.App;
import com.team9.focus.models.objects.Badge;
import com.team9.focus.utilities.BadgeComparator;
import com.team9.focus.utilities.Utility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CreateProfileActivity extends AppCompatActivity {
    private Button mCreateButton, mAddAppButton;
    private ListView mListView;
    private EditText mEditText;

    // All apps
    private ArrayList<App> mApps;
    // Apps that the user selects to add to the profile
    private ArrayList<App> mSelectedApps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Retrieve the list of apps and put them in mApps
        mApps = new ArrayList<>(DBHelper.getAllApps());

        final AppAdapter adapter = new AppAdapter(this, mApps);
        mListView = findViewById(R.id.appListView);
        mListView.setAdapter(adapter);

        mEditText = findViewById(R.id.etProfileName);

        mCreateButton = findViewById(R.id.createButton);
        mCreateButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                mSelectedApps = adapter.getSelectedApps();
                if (trySaveProfile(mEditText.getText().toString(), mSelectedApps)) {
                    finish();
                }
            }
        });
    }

    private boolean trySaveProfile(String name, ArrayList<App> apps) {
        try {
            if (DBHelper.saveProfile(name, apps)) {
                updateBadge(apps.size());
                System.out.println("Successfully saved!");
                return true;
            } else {


                Utility.createErrorDialog(this, "Invalid Profile", "Please enter in a unique name and select at least 1 app");


                System.out.println("Unsuccessful save");
                return false;
            }
        } catch (AlreadyExistsException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }


    private void showErrorDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Invalid Profile")
                .setMessage("Please enter in a unique name and select at least 1 app")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Nothing happens
                    }
                })
                .show();
    }

    private void updateBadge(int appsSize) {
        List<Badge> badges = DBHelper.getBadgesByCategory("profile");
        Collections.sort(badges, new BadgeComparator());
        if(appsSize >= 20) {
            // unlock the first 6 badges
            for(int i = 0; i < 6; i++) {
                DBHelper.updateBadge(badges.get(i), appsSize, this);
                Utility.sendBadgeEmail(this, badges.get(i));
            }
        }
        else if(appsSize >= 20) {
            // unlock the first 5 badges
            for(int i = 0; i < 5; i++) {
                DBHelper.updateBadge(badges.get(i), appsSize, this);
                Utility.sendBadgeEmail(this, badges.get(i));
            }
        }
        else if(appsSize >= 15) {
            // unlock the first 4 badges
            for(int i = 0; i < 4; i++) {
                DBHelper.updateBadge(badges.get(i), appsSize, this);
                Utility.sendBadgeEmail(this, badges.get(i));
            }
        } else if(appsSize >= 10) {
            // unlock the first 3 badges
            for(int i = 0; i < 3; i++) {
                DBHelper.updateBadge(badges.get(i), appsSize, this);
                Utility.sendBadgeEmail(this, badges.get(i));
            }
        } else if(appsSize >= 5) {
            //unlock the first 2 badges
            for(int i = 0; i < 2; i++) {
                DBHelper.updateBadge(badges.get(i), appsSize, this);
                Utility.sendBadgeEmail(this, badges.get(i));
            }
        } else if(appsSize >= 1) {
            //unlock first badge
            DBHelper.updateBadge(badges.get(0), appsSize, this);
            Utility.sendBadgeEmail(this, badges.get(0));
        }
    }

//    void addItems() {
//        String input = mName.getText().toString();
//        addedItems.add(input);
//        adapter.notifyDataSetChanged();
//    } */

}
