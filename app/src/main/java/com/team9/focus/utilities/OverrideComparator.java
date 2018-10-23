package com.team9.focus.utilities;

import com.team9.focus.models.objects.App;

import java.util.Comparator;

/**
 * Created by maya on 11/13/17.
 */

public class OverrideComparator implements Comparator<App> {
    @Override
    public int compare(App o1, App o2) {
       return o2.overrides - o1.overrides;
    }
}
