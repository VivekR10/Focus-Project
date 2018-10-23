package com.team9.focus.models.objects;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by Vivek on 11/8/2017.
 */

@Table (name = "badges")
public class Badge extends Model {

        public Badge()
        {
            super();
        }

        public Badge(boolean isEarned, String dateEarned, String badgeIcon, String category, String badgeName, int dataGoal)
        {
            super();
            this.isEarned = isEarned;
            this.dateEarned = dateEarned;
            this.badgeIcon = badgeIcon;
            this.category = category;
            this.badgeName = badgeName;
            this.dataEarned = 0;
            this.dataGoal = dataGoal;
        }

        @Column(name= "is_earned")
        public boolean isEarned;

        @Column(name = "date_earned")
        public String dateEarned;

        @Column(name = "badge_icon")
        public  String badgeIcon;

        @Column(name = "badge_name", unique = true)
        public String badgeName;

        @Column(name = "category")
        public String category;

        @Column(name = "level")
        public int level;

        @Column(name="data_earned")
        public double dataEarned;

        @Column(name="data_goal")
        public double dataGoal;


}

