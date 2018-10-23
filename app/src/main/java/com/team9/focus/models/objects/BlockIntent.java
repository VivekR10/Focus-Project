package com.team9.focus.models.objects;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by jtsui on 10/14/17.
 */

@Table(name = "block_intents")
public class BlockIntent extends Model {

    @Column(name = "timeslot")
    public Timeslot timeslot;

    @Column(name = "request_code")
    public int requestCode;

    @Column(name = "type")
    public String type;

    @Column(name = "day_of_week")
    public int dayOfWeek;



    public BlockIntent() {
        super();
    }

    public BlockIntent(Timeslot timeslot, int requestCode, String type, int dayOfWeek) {
        super();
        this.timeslot = timeslot;
        this.requestCode = requestCode;
        this.type = type;
        this.dayOfWeek = dayOfWeek;
    }
}
