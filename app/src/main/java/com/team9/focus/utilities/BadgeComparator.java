package com.team9.focus.utilities;

import com.team9.focus.models.objects.Badge;

import java.util.Comparator;

/**
 * Created by maya on 11/15/17.
 */

public class BadgeComparator implements Comparator<Badge>{

        @Override
        public int compare(Badge o1, Badge o2) {
            return (int) (o1.level - o2.level);
        }

}
