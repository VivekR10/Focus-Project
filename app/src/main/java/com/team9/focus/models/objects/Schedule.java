package com.team9.focus.models.objects;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by jtsui on 9/30/17.
 */

@Table(name="schedules")
public class Schedule extends Model {
    @Column(name="schedule_name", unique = true)
    public String scheduleName;

    @Column(name="is_active")
    public boolean isActive;

    @Column(name="hours_focused")
    public double hoursFocused;

    public Schedule() {
        super();
        this.isActive = true;
        this.hoursFocused = 0;
    }

    public Schedule(String scheduleName) {
        super();
        this.scheduleName = scheduleName;
        this.isActive = true;
        this.hoursFocused = 0;
    }

    public void addHoursFocused(int additionalMinutes){
        double tempMin = this.hoursFocused*60;
        tempMin+=additionalMinutes;
        this.hoursFocused = tempMin/60;
    }
}
