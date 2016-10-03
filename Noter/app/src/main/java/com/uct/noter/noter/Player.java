package com.uct.noter.noter;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.widget.SeekBar;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Shuaib & Khadeejah on 2016-08-07.
 * Class that encapsulates logic to play recordings.
 */
public class Player {

    private MediaPlayer mediaPlayer;
    private SeekBar seekBar;
    private Handler audioProgressHandler;
    private final PlayActivity context;
    private int recentAnnotationIndex;

    Player(String filePath, Context context, SeekBar seekBar) {
        this.mediaPlayer = new MediaPlayer();
        this.audioProgressHandler = new Handler();
        this.seekBar = seekBar;

        // add listener for seekbar dragging
        this.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int milliseconds, boolean draggedByUser) {
                if(draggedByUser) {
                    mediaPlayer.seekTo(milliseconds);
                    seekBar.setProgress(milliseconds);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        this.context = (PlayActivity) context;
        this.recentAnnotationIndex = -1;
        try {
            mediaPlayer.setDataSource(filePath);
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    /**
     * Plays audio and sets listener to restart track on completion
     */
    public void play (){

        audioProgressHandler.post(audioProgressUpdater);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                context.onFinishPlaying();
                audioProgressHandler.removeCallbacks(audioProgressUpdater);
                mediaPlayer.seekTo(0);
            }
        });
        try{
            mediaPlayer.start();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Pause audio track
     */
    public void pause(){
        mediaPlayer.pause();
    }


    public void stopPlay(){
        try{
            if (mediaPlayer != null){
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Replays previous 5 seconds of audio track
     */
    public void replay() {
        if (mediaPlayer.getCurrentPosition() > 5000)
            mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() - 5000);
        else
            mediaPlayer.seekTo(0);
    }

    /**
     * Skips to next 5 seconds in audio track
     */
    public void forward() {
        if (mediaPlayer.getCurrentPosition() < mediaPlayer.getDuration())
            mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() + 5000);
        else
            mediaPlayer.seekTo(mediaPlayer.getDuration());
    }

    /**
     * Skips to next question or note
     * @param annotations list of questions and notes
     * @return whether there was an annotation to skip to
     */
    public boolean seekToNext(ArrayList<Annotation> annotations) {

        if (this.recentAnnotationIndex < annotations.size()-1) {
            this.mediaPlayer.seekTo((int) annotations.get(this.recentAnnotationIndex+1).getStartTime());
            this.recentAnnotationIndex++;
            return true;
        }
        return false;
    }

    /**
     * Skips to previous question or note
     * @param annotations list of questions and notes
     * @return whether there was an annotation to skip to
     */
    public boolean seekToPrevious(ArrayList<Annotation> annotations) {
        long currentPosition = this.mediaPlayer.getCurrentPosition();
        if (this.recentAnnotationIndex > 0) {
            this.mediaPlayer.seekTo((int) annotations.get(this.recentAnnotationIndex-1).getStartTime());
            this.recentAnnotationIndex--;
            return true;
        }
        else if (this.recentAnnotationIndex == 0 && currentPosition > annotations.get(this.recentAnnotationIndex).getStartTime()) {
            this.mediaPlayer.seekTo((int) annotations.get(this.recentAnnotationIndex).getStartTime());
            return true;
        }
        return false;
    }

    /**
     * Gets length of audio track
     * @return track length in milliseconds
     */
    public long getAudioLength() {
        return this.mediaPlayer.getDuration();
    }

    /**
     * Gets current position in audio track
     * @return position in milliseconds
     */
    public long getCurrentProgress() {
        return this.mediaPlayer.getCurrentPosition();
    }

    /**
     * Updates most recent annotation when user uses seekbar instead of skip buttons
     * @param index value to update recent annotation to
     */
    public void setRecentAnnotationIndex(int index) {
        this.recentAnnotationIndex = index;
    }

    /**
     * Updates seekbar when not dragged by user
     */
    Runnable audioProgressUpdater = new Runnable() {
        @Override
        public void run() {
            seekBar.setProgress(mediaPlayer.getCurrentPosition());
            audioProgressHandler.post(audioProgressUpdater);
        }
    };
}
