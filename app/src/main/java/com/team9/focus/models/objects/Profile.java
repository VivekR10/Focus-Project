package com.team9.focus.models.objects;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.team9.focus.models.DBHelper;

import java.util.Comparator;

/**
 * Created by jtsui on 9/30/17.
 */

@Table(name="profiles")
public class Profile extends Model {
    @Column(name="profile_name", unique = true)
    public String profileName;

    public int numUses;

}
