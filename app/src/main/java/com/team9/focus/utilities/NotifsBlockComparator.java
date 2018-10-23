package com.team9.focus.utilities;

import com.team9.focus.models.objects.App;

import java.util.Comparator;

/**
 * Created by maya on 11/13/17.
 */

public class NotifsBlockComparator implements Comparator<App> {
    @Override
    public int compare(App o1, App o2) {
        return o2.blockCount - o1.blockCount;
    }
}
