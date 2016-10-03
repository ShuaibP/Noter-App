package com.uct.noter.noter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shuaib on 2016-08-14.
 * Class for Theme which is a type of annotation. Theme behaves differently because it spans a time
 * period
 */
public class Theme extends Annotation {

    private List<TimeStamp> timeStamps;

    public Theme(){}

    public Theme(String description) {
        super(description);
        timeStamps = new ArrayList<>();
    }

    public Theme(String description, long startTime, long endTime) {
        super(description, startTime);
        timeStamps = new ArrayList<>();
    }

    /**
     * Obtains list of time stamps
     * @return list timestamps
     */
    public List<TimeStamp> getTimeStamps() {
        return timeStamps;
    }

    /**
     * Sets list of time stamps
     * @param timeStamps
     */
    public void setTimeStamps(List<TimeStamp> timeStamps) {
        this.timeStamps = timeStamps;
    }

    /**
     * Add a timeStamp to theme
     * @param timeStamp
     */
    public void addTimeStamp(TimeStamp timeStamp){
        timeStamps.add(timeStamp);
    }

    /**
     * To string method
     * @param time
     * @return String details
     */
    public String toString(TimeStamp time) {
        String[] timeComponents = null;
        for (TimeStamp timeStamp:  timeStamps) {
            if (time == timeStamp) {
                timeComponents = timeStamp.getAbsoluteTime().split(" ");
                break;
            }
        }
        return "@" + timeComponents[3] + ": " + this.getDescription();
    }
}
