package com.uct.noter.noter;

/**
 * Created by Shuaib on 2016-09-06.
 * This class contains a method to convert and format time from milliseconds to hours, minutes and
 * seconds.
 */
public class TimerUtility {

    /**
     * Method used represent milliseconds as a string in units of hours, minutes and seconds
     * @param milliseconds
     * @return String timer
     */
    public static String milliSecondsToTimer(long milliseconds){
        String finalTimerString = "";
        String secondsString = "";

        int hours = (int)( milliseconds / (1000*60*60));
        int minutes = (int)(milliseconds % (1000*60*60)) / (1000*60);
        int seconds = (int) ((milliseconds % (1000*60*60)) % (1000*60) / 1000);

        if(hours > 0){
            finalTimerString = hours + ":";
        }
        if(seconds < 10){
            secondsString = "0" + seconds;
        }
        else{
            secondsString = "" + seconds;}

        finalTimerString = finalTimerString + minutes + ":" + secondsString;

        return finalTimerString;
    }
}
