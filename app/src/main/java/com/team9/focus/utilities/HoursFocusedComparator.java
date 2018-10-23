package com.team9.focus.utilities;

import com.team9.focus.models.objects.Schedule;

import java.util.Comparator;

/**
 * Created by maya on 11/13/17.
 */

public class HoursFocusedComparator implements Comparator<Schedule> {

    @Override
    public int compare(Schedule o1, Schedule o2) {
        return (int)(o2.hoursFocused - o1.hoursFocused);
    }
}
