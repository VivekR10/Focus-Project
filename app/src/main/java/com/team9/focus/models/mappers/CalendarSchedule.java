package com.team9.focus.models.mappers;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.team9.focus.models.objects.Calendar;
import com.team9.focus.models.objects.Schedule;

/**
 * Created by sophi on 10/15/2017.
 */

@Table(name="calendars_schedules")
public class CalendarSchedule extends Model {
    @Column(name="calendar")
    Calendar calendar;

    @Column(name="schedule")
    Schedule schedule;

    public CalendarSchedule() {super();}

    public CalendarSchedule(Calendar calendar, Schedule schedule) {
        super();
        this.calendar = calendar;
        this.schedule = schedule;
    }
}
