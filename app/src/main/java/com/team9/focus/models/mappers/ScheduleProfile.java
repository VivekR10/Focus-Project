package com.team9.focus.models.mappers;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.team9.focus.models.objects.Profile;
import com.team9.focus.models.objects.Schedule;

/**
 * Created by jtsui on 10/1/17.
 */


@Table(name="schedules_profiles")
public class ScheduleProfile extends Model {
    @Column(name="schedule")
    Schedule schedule;

    @Column(name="profile", onUpdate = Column.ForeignKeyAction.CASCADE, onDelete = Column.ForeignKeyAction.CASCADE)
    Profile profile;

    public ScheduleProfile() {
        super();
    }

    public ScheduleProfile(Schedule schedule, Profile profile) {
        super();
        this.schedule = schedule;
        this.profile = profile;
    }
}
