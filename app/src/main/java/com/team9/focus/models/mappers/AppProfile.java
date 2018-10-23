package com.team9.focus.models.mappers;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.team9.focus.models.objects.App;
import com.team9.focus.models.objects.Profile;

/**
 * Created by jtsui on 10/1/17.
 */

@Table(name="apps_profiles")
public class AppProfile extends Model {

    @Column(name="app")
    App app;

    @Column(name="profile")
    Profile profile;

    public AppProfile() {
        super();
    }

    public AppProfile(App app, Profile profile) {
        super();
        this.app = app;
        this.profile = profile;
    }
}
