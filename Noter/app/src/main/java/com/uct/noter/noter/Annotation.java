package com.uct.noter.noter;

/**
 * Created by Shuaib and Khadeejah on 2016-08-08.
 * Base class used for simple annotations
 */
public class Annotation implements Comparable<Annotation> {

    private String description, time;
    private long startTime;

    public Annotation(String description, long startTime, String time) {
        this.description = description;
        this.startTime = startTime;
        this.time = time;
    }

    public Annotation(String description) {
        this.description = description;
        this.startTime = -1;
    }

    public Annotation(String description, long startTime) {
        this.description = description;
        this.startTime = startTime;
    }

    public Annotation() {
    }

    /**
     * Obtains annotation description
     * @return description
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Obtains annotation time
     * @return startTime
     */
    public long getStartTime() {
        return this.startTime;
    }

    /**
     * Store annotation description
     * @param description specified description to store
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Store start time
     * @param time specified time
     */
    public void setStartTime(long time) {
        this.startTime = time;
    }

    /**
     * Compares annotation times, used during sorting
     * @param annotation annotation to compare to
     * @return -1 if less than, 1 if more than, 0 if equal
     */
    @Override
    public int compareTo(Annotation annotation) {
        if (this.getStartTime() < annotation.getStartTime())
            return -1;
        else if (this.getStartTime() > annotation.getStartTime())
            return 1;
        return 0;
    }

    /**
     * Obtain absolute time
     * @return time
     */
    public String getTime() {
        return time;
    }

    /**
     * Set absolute time
     * @param time specified time
     */
    public void setTime(String time) {
        this.time = time;
    }

    public String toString(){

        String[] timeComponents = this.time.split(" ");
        return "@" + timeComponents[3] + ": " + this.description;
    }
}
