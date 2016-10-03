package com.uct.noter.noter;

/**
 * Created by Shuaib on 2016-09-14.
 * TimeStamp class is used in theme, so that themes can be reused at different times
 */
public class TimeStamp {

    private long startTime,endTime;
    private String absoluteTime;

    public TimeStamp(long startTime, String absoluteTime) {
        this.startTime = startTime;
        this.absoluteTime = absoluteTime;
        this.endTime = -1;
    }

    public TimeStamp() {}

    /**
     * Obtain the start time of a tiem stamp
     * @return long start time
     */
    public long getStartTime() {
        return startTime;
    }

    /**
     * Set the start time
     * @param startTime
     */
    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    /**
     * Obtains the end time
     * @return long end time
     */
    public long getEndTime() {
        return endTime;
    }

    /**
     * Obtains the end time
     * @param endTime
     */
    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    /**
     * Returns the time
     * @return String absolute time
     */
    public String getAbsoluteTime() {
        return absoluteTime;
    }

    /**
     * Sets the time
     * @param absoluteTime
     */
    public void setAbsoluteTime(String absoluteTime) {
        this.absoluteTime = absoluteTime;
    }
}
