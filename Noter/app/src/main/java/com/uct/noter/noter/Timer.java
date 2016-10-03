package com.uct.noter.noter;

import android.os.Handler;
import android.os.SystemClock;

/**
 * Created by Shuaib on 2016-08-07.
 * Class which encapsulates the timer logic, uses a separate thread to update timer and UI.
 */
public class Timer {

    private long timeInMilliseconds;
    private long timeSwapBuff;
    private long updatedTime;
    private long startTime;
    public final static String DEFAULT_TIME_DISPLAYED = "00:00:00";
    private Handler timeHandler = new Handler();

    public Timer() {
        this.timeInMilliseconds = 0L;
        this.timeSwapBuff = 0L;
        this.updatedTime = 0L;
        this.startTime = 0L;
    }

    /**
     * Starts timer when recording starts
     */
    public void start(){
        this.startTime = SystemClock.uptimeMillis();
        timeHandler.postDelayed(updateTimerThread,0);
    }

    /**
     * Stops timer when recording stops
     */
    public void stop() {
        timeSwapBuff += timeInMilliseconds;
        timeHandler.removeCallbacks(updateTimerThread);
        reset();
    }

    /**
     * Reset timer after recording has stopped
     */
    private void reset() {
        this.timeInMilliseconds = 0L;
        this.timeSwapBuff = 0L;
        this.updatedTime = 0L;
        this.startTime = 0L;
    }

    /**
     * Obtain time elapsed
     * @return time in milliseconds
     */
    public long getTimeInMilliseconds() {
        return timeInMilliseconds;
    }

    /**
     * Updates timer in Recorder Activity with time elapsed
     */
    private Runnable updateTimerThread = new Runnable() {
        @Override
        public void run() {
            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
            updatedTime = timeSwapBuff + timeInMilliseconds;

            timeHandler.postDelayed(this,0);

            RecorderActivity.setTimerDisplay(TimerUtility.milliSecondsToTimer(updatedTime));
        }
    };
}
