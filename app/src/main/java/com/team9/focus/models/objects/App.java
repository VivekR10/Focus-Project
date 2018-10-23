package com.team9.focus.models.objects;

import android.util.Log;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by jtsui on 9/30/17.
 */

@Table(name="apps")
public class App extends Model {

    public App()
    {
      super();
    }

    public App(String appName, String packageName)
    {
        super();
        this.appName = appName;
        this.packageName = packageName;
        this.blockCount = 0;
    }

    @Column(name = "app_name")
    public String appName;

    @Column(name = "package_name")
    public String packageName;

    @Column(name="block_count")
    public int blockCount;

    @Column(name="launch_block_count")
    public int launchBlockCount;

    @Column(name="minutes_blocked")
    public int minutesBlocked;

    @Column(name="overrides")
    public int overrides;


    public void setBlockCount(int count)
    {
        this.blockCount = count;
    }
    public int addBlockCount(int count)
    {
        this.blockCount += count;
        return blockCount;
    }

    public int incrementLaunchBlockCount()
    {
        this.launchBlockCount += 1;
        return launchBlockCount;
    }

    public int incrementOverrides() {
        this.overrides += 1;
        return this.overrides;
    }

    public int addBlockAppMinutes(int minutes)
    {
        this.minutesBlocked += minutes;
        Log.d("APP_OBJ", String.valueOf(minutesBlocked));
        return minutesBlocked;
    }
}
