package com.team9.focus.models.mappers;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.team9.focus.models.objects.Schedule;
import com.team9.focus.models.objects.Timeslot;

/**
 * Created by jtsui on 9/30/17.
 */

@Table(name="schedules_timeslots")
public class ScheduleTimeslot extends Model {

    @Column(name="schedule")
    public Schedule schedule;

    @Column(name="timeslot")
    public Timeslot timeslot;


    public ScheduleTimeslot() {
        super();
    }

    public ScheduleTimeslot(Schedule schedule, Timeslot timeslot) {
        super();
        this.schedule = schedule;
        this.timeslot = timeslot;
    }
}
