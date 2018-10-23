package com.team9.focus.models.objects;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by sophi on 10/15/2017.
 */

@Table(name = "calendars")
public class Calendar extends Model {
    @Column(name="calendar_name", unique = true)
    public String calendarName;

    @Column(name="is_active")
    public boolean isActive;

    public Calendar() {
        super();
        this.isActive = true;
    }

    public Calendar(String name) {
        super();
        this.calendarName = name;
        this.isActive = true;
    }
}
