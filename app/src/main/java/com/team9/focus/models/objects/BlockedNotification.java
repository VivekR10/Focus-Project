package com.team9.focus.models.objects;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by jtsui on 10/2/17.
 */

@Table(name = "blocked_notifications")
public class BlockedNotification extends Model {

    @Column(name="package_name")
    public String packageName;

    @Column(name="count")
    public int count;

    public BlockedNotification() {
        super();
        this.count = 1;
    }
}
