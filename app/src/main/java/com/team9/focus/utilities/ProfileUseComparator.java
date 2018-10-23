package com.team9.focus.utilities;

import com.team9.focus.models.objects.Profile;

import java.util.Comparator;

/**
 * Created by Nandhini on 11/10/17.
 */

public class ProfileUseComparator implements Comparator<Profile> {

    @Override
    public int compare(Profile o1, Profile o2) {
        return o2.numUses - o1.numUses;
    }
}
