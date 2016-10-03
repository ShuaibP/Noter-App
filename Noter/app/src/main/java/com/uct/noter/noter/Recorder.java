package com.uct.noter.noter;

import android.media.MediaRecorder;
import android.os.Environment;

import java.io.File;
import java.io.IOException;

/**
 * Created by Shuaib on 2016-08-07.
 * Class that encapsulates all the voice recording logic.
 */
public class Recorder {
    private static Recorder recorder = null;
    private static MediaRecorder mediaRecorder;

    /**
     * Singleton Pattern. To return single instance of Recorder
     * @return
     */
    public static Recorder getRecorder() {
        if (recorder == null)
            recorder = new Recorder();
        recorder.mediaRecorderInit();
        return recorder;
    }

    /**
     * Set the name of the audio file
     * @param recordingTitle
     */
    public void setOutputFile(String recordingTitle) {
        String path  = FileReader.path;
        if (!dirExists(path))
        {
            File directory = new File(path);
            directory.mkdirs();
        }

        mediaRecorder.setOutputFile(FileReader.path+recordingTitle+".mpeg");
    }

    /**
     * Start recording
     */
    public void start(){
        mediaRecorderInit();
        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
        } catch (IllegalArgumentException e){
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * Stop recording
     */
    public void stop(){
        try {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
        } catch (IllegalStateException e){
            e.printStackTrace();
        } catch (RuntimeException e){
            e.printStackTrace();
        }
    }

    /**
     * Check if folder exists
     * @param path
     * @return
     */
    public boolean dirExists(String path)
    {
        File dir = new File(path);
        if(dir.exists() && dir.isDirectory())
            return true;
        return false;
    }

    /**
     * Obtains the amplitude of the audio at any given point
     * @return int amplitude
     */
    public int getAmplitude() {
        return mediaRecorder.getMaxAmplitude();
    }

    /**
     * Initializes recording and sets the format and codec
     */

    private void mediaRecorderInit() {
        if (mediaRecorder == null) {
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        }
    }
}
