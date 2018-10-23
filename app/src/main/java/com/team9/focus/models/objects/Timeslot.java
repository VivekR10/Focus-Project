package com.team9.focus.models.objects;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.team9.focus.exceptions.InvalidDateException;
import com.team9.focus.utilities.Utility;

/**
 * Created by jtsui on 9/30/17.
 */


@Table(name="timeslots")
public class Timeslot extends Model implements Parcelable, Comparable<Timeslot> {

    public Timeslot() {
        super();
        this.isOverridden = false;
        this.isOn = false;
    }

    public Timeslot(String dayOfWeek, int startHour, int startMinute, int endHour, int endMinute) {
        super();
        this.dayOfWeek = dayOfWeek;
        this.startHour = startHour;
        this.startMinute = startMinute;
        this.endHour = endHour;
        this.endMinute = endMinute;
        this.isOverridden = false;
        this.isOn = false;
    }

    @Column(name="day_of_week")
    public String dayOfWeek;

    @Column(name="start_hour")
    public int startHour;

    @Column(name="start_minute")
    public int startMinute;

    @Column(name="end_hour")
    public int endHour;

    @Column(name="end_minute")
    public int endMinute;

    @Column(name="is_overridden")
    public boolean isOverridden;

    @Column(name="is_on")
    public boolean isOn;

    public String formatAsString() {
        StringBuilder sb = new StringBuilder();

        // Get the days of the week
        String[] days = dayOfWeek.split(",");
        for(String day : days) {
            try {
                sb.append(Utility.getDayOfWeekFromInt(Integer.parseInt(day)));
            } catch (InvalidDateException e) {
                System.out.println("InvalidDateException: " + e.getMessage());
            }
        }
        sb.append(" ");

        // Append the Start Time
        sb.append(startHour%12 + ":");
        if(startMinute<10){
            sb.append("0");
        }
        sb.append(startMinute);

        // AM or PM
        if(startHour<12){
            sb.append("AM");
        } else {
            sb.append("PM");
        }

        sb.append(" - ");

        // End time
        sb.append(endHour%12+ ":");
        if(endMinute<10){
            sb.append("0");
        }
        sb.append(endMinute);

        // AM or PM
        if(endHour<12){
            sb.append("AM");
        } else {
            sb.append("PM");
        }


        return sb.toString();
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator<Timeslot>() {
        public Timeslot createFromParcel(Parcel in) {
            return new Timeslot(in);
        }

        public Timeslot[] newArray(int size) {
            return new Timeslot[size];
        }
    };

    public Timeslot(Parcel in) {
        this.dayOfWeek = in.readString();
        this.startHour = in.readInt();
        this.startMinute = in.readInt();
        this.endHour = in.readInt();
        this.endMinute = in.readInt();
        if(in.readInt() == 1) {
            this.isOverridden = true;
        } else {
            this.isOverridden = false;
        }
        if(in.readInt() == 1) {
            this.isOn = true;
        } else {
            this.isOn = false;
        }
    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel pc, int flags) {
        pc.writeString(dayOfWeek);
        pc.writeInt(startHour);
        pc.writeInt(startMinute);
        pc.writeInt(endHour);
        pc.writeInt(endMinute);
        if (isOverridden) {
            pc.writeInt(1);
        }
        else {
            pc.writeInt(0);
        }
        if (isOn) {
            pc.writeInt(1);
        }
        else {
            pc.writeInt(0);
        }

    }

    @Override
    public String toString() {
        return formatAsString();
    }

    @Override
    public int compareTo(@NonNull Timeslot timeslot) {
        double thisScore = this.endHour * 60 + this.endMinute;
        double otherScore = timeslot.endHour * 60 + timeslot.endMinute;
        if (thisScore < otherScore) {
            return -1;
        } else if (otherScore > thisScore) {
            return 1;
        } else {
            return 0;
        }
    }
}
