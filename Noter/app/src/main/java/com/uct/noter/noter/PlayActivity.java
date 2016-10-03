package com.uct.noter.noter;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shuaib & Khadeejah on 2016-08-07.
 * Simple activity which allows the user to play and stop recordings
 */

public class PlayActivity extends AppCompatActivity  {

    ImageButton play, seekPrevious, seekNext, replay, forward;
    SeekBar seekbar;
    Player player;
    TextView timeElapsed, timeTotal, title, themeTextView, question, note, interviewDetails, interviewDate;
    Intent intent;
    RecordingRepo recordingRepo;
    boolean isPlaying;
    AudioVisualizerView visualizerView;
    Handler timerHandler, annotationHandler, visualizerViewHandler;
    ArrayList<Runnable> annotationUpdaters;
    ArrayList<Annotation> annotations;
    ArrayList<Annotation> questionsAndNotes;
    List<Float> amplitudes;
    List<Integer> visualAnnotations, visualThemes;
    String[] recordingDetails;
    boolean stop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        stop = false;

        visualizerViewHandler = new Handler();
        visualizerView = ((AudioVisualizerView) findViewById(R.id.play_visualizer_view));

        play = (ImageButton) findViewById(R.id.play_button);
        seekPrevious = (ImageButton) findViewById(R.id.seek_previous_button);
        seekNext = (ImageButton) findViewById(R.id.seek_next_button);
        replay = (ImageButton)  findViewById(R.id.replay_button);
        forward = (ImageButton) findViewById(R.id.forward_button);

        seekbar = (SeekBar) findViewById(R.id.seek_bar);
        timeElapsed = (TextView) findViewById(R.id.timer_text_view_elapsed);
        timeTotal = (TextView) findViewById(R.id.timer_text_view_total);


        timerHandler = new Handler();
        annotationHandler = new Handler();
        annotationUpdaters = new ArrayList<Runnable>();

        intent = getIntent();
        recordingRepo = RecordingRepo.getInstance();
        annotations = new ArrayList<Annotation>();

        title = (TextView) findViewById(R.id.title_play_text_view);
        interviewDetails = (TextView) findViewById(R.id.interview_details_play_text_view);
        interviewDate = (TextView) findViewById(R.id.interview_date_play_text_view);
        themeTextView = (TextView) findViewById(R.id.theme_play_text_view);
        question = (TextView) findViewById(R.id.question_play_text_view);
        note = (TextView) findViewById(R.id.note_play_text_view);


        int index = intent.getExtras().getInt("recordingIndex");
        recordingDetails = recordingRepo.getRecordingDetails(index);
        annotations = recordingRepo.getAllAnnotations(index);
        amplitudes = recordingRepo.getAmplitudes(index);
        visualAnnotations = recordingRepo.getVisualAnnotations(index);
        visualThemes = recordingRepo.getVisualThemes(index);

        questionsAndNotes = new ArrayList<Annotation>();
        for (Annotation annotation: annotations) {
            if (!(annotation instanceof Theme))
                questionsAndNotes.add(annotation);
        }

        player = new Player(FileReader.path + recordingDetails[0] + ".mpeg", this, seekbar);


        timerHandler.post(mUpdateTimeTask);
        enableSeekButtons(false);

        seekbar.setMax((int) player.getAudioLength());
        timeTotal.setText(TimerUtility.milliSecondsToTimer(player.getAudioLength()));
        title.setText(recordingDetails[0]);
        interviewDetails.setText(recordingDetails[1]);
        interviewDate.setText(recordingDetails[2]);

        playerAnnotationsInit();

        // set click listener for playing and pausing playback
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isPlaying==false) {
                    player.play();
                    isPlaying = true;
                    play.setImageResource(R.mipmap.ic_pause_white_48dp);
                    enableSeekButtons(true);
                }
                else {
                   pause();
                }
            }
        });

        // set listener for skipping to previous annotation
        seekPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                player.seekToPrevious(questionsAndNotes);

            }
        });

        // set listener for skipping to next annotation
        seekNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                player.seekToNext(questionsAndNotes);
            }
        });

        // set listener for replaying previous 5 seconds
        replay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                player.replay();
            }
        });

        // set listener for forwarding next 5 seconds
        forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                player.forward();
            }
        });
    }

    protected void onStop(){
        super.onStop();
        if (isPlaying)
            pause();
    }

    private void pause(){
        player.pause();
        isPlaying = false;
        play.setImageResource(R.mipmap.ic_play_arrow_white_48dp);
        enableSeekButtons(false);
    }


    /**
     * Loads waveform and sets it to seekbar
     * @param hasFocus
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus) {
            visualizerViewHandler.post(updateVisualizer);
            visualizerView.initPlay(amplitudes, visualAnnotations, visualThemes);
            Drawable progress = new BitmapDrawable(getResources(), visualizerView.getPlayWaveform());
            seekbar.setProgressDrawable(progress);

        }
    }

    /**
     * Updates waveform as position in audio track changes
     */
    Runnable updateVisualizer = new Runnable() {
        @Override
        public void run() {
            Drawable progress = new BitmapDrawable(getResources(), visualizerView.updateProgress(player.getCurrentProgress(), player.getAudioLength()));
            seekbar.setProgressDrawable(progress);
            visualizerViewHandler.post(this);
        }
    };


    /**
     * Creates thread to display question at the time it was annotated while recording
     * @param annotation question to be displayed
     * @return aRunnable
     */
    private Runnable createQuestionRunnable(final Question annotation){
        Runnable aRunnable = new Runnable(){
            public void run(){
                if (player.getCurrentProgress()/1000 >= annotation.getStartTime()/1000
                        && annotation.getStartTime()/1000  <= player.getCurrentProgress()/1000) {
                    question.setText("Question " + annotation.toString());
                    player.setRecentAnnotationIndex(questionsAndNotes.indexOf(annotation));
                }
                annotationHandler.post(this);
            }

        };
        return aRunnable;
    }

    /**
     * Creates thread to display note at the time it was annotated while recording
     * @param annotation note to be displayed
     * @return aRunnable
     */
    private Runnable createNoteRunnable(final Annotation annotation){
        Runnable aRunnable = new Runnable(){
            public void run(){
                if (player.getCurrentProgress()/1000 >= annotation.getStartTime()/1000) {
                    note.setText("Note " + annotation.toString());
                    player.setRecentAnnotationIndex(questionsAndNotes.indexOf(annotation));
                }
                annotationHandler.post(this);
            }
        };
        return aRunnable;
    }

    /**
     * Creates thread that displays a theme at the time it was activated, for the duration that it
     * was activated
     * @param timeStamp contains start and end time of theme
     * @param theme theme to be displayed
     * @return aRunnable
     */
    private Runnable createThemeRunnable(final TimeStamp timeStamp, final Theme theme){
        Runnable aRunnable = new Runnable(){
            public void run() {
                if (player.getCurrentProgress()/1000 >= timeStamp.getStartTime()/1000
                        && player.getCurrentProgress()/1000 < timeStamp.getEndTime()/1000)
                    themeTextView.setText("Theme " + theme.toString(timeStamp));
                else if (player.getCurrentProgress()/1000 == timeStamp.getEndTime()/1000
                        || player.getCurrentProgress() == (timeStamp.getStartTime()-1)/1000) {
                    themeTextView.setText("");
                }
                annotationHandler.post(this);
            }
        };
        return aRunnable;
    }

    /**
     * Updates timer as position in audio track changes
     */
    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            long totalDuration = player.getAudioLength();
            long currentDuration = player.getCurrentProgress();

            timeElapsed.setText(TimerUtility.milliSecondsToTimer(currentDuration));
            timeTotal.setText(TimerUtility.milliSecondsToTimer(totalDuration-currentDuration));
            timerHandler.postDelayed(mUpdateTimeTask, 100);
        }
    };


    /**
     * Create and start all threads for annotations that must be displayed
     */
    private void playerAnnotationsInit() {
        for (Annotation annotation : annotations) {
            Runnable annotationUpdater = null;

            if (annotation instanceof Question) {
                annotationUpdater = createQuestionRunnable((Question) annotation);
                annotationHandler.post(annotationUpdater);
                annotationUpdaters.add(annotationUpdater);
            }
            else if (annotation instanceof Theme) {
                for (TimeStamp timeStamp : ((Theme) annotation).getTimeStamps()) {
                    annotationUpdater = createThemeRunnable(timeStamp, (Theme) annotation);
                    annotationHandler.post(annotationUpdater);
                    annotationUpdaters.add(annotationUpdater);
                }
            }
            else {
                annotationUpdater = createNoteRunnable(annotation);
                annotationHandler.post(annotationUpdater);
                annotationUpdaters.add(annotationUpdater);
            }

        }
    }

    /**
     * Restarts player when audio track is finished playing
     */
    public void onFinishPlaying() {
        Drawable progress = new BitmapDrawable(getResources(), visualizerView.updateProgress(0, player.getAudioLength()));
        seekbar.setProgressDrawable(progress);
        isPlaying = false;
        play.setImageResource(R.mipmap.ic_play_arrow_white_48dp);
        timeElapsed.setText("0:00");
        timeTotal.setText(TimerUtility.milliSecondsToTimer(player.getAudioLength()));
        enableSeekButtons(false);
        seekbar.setProgress(0);
        themeTextView.setText("");
        question.setText("");
        note.setText("");
        for (Runnable annotationUpdater: annotationUpdaters) {
            annotationHandler.removeCallbacks(annotationUpdater);
        }
    }

    /**
     * Enables buttons when playing
     * @param enable enables if true, disables if false
     */
    private void enableSeekButtons(boolean enable) {
        if (enable) {
            seekPrevious.setEnabled(true);
            seekNext.setEnabled(true);
            seekPrevious.setImageAlpha(255);
            seekNext.setImageAlpha(255);
            replay.setEnabled(true);
            forward.setEnabled(true);
            replay.setImageAlpha(255);
            forward.setImageAlpha(255);
        }
        else {
            seekPrevious.setEnabled(false);
            seekNext.setEnabled(false);
            seekPrevious.setImageAlpha(127);
            seekNext.setImageAlpha(127);
            replay.setEnabled(false);
            forward.setEnabled(false);
            replay.setImageAlpha(127);
            forward.setImageAlpha(127);
        }
    }

    /**
     * Takes user back to main activity when back button is pressed twice
     */
    @Override
    public void onBackPressed() {
        if (stop) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            title.setText("");
            interviewDate.setText("");
            interviewDetails.setText("");
            timerHandler.removeCallbacks(mUpdateTimeTask);
            visualizerViewHandler.removeCallbacks(updateVisualizer);
            for (Runnable annotationUpdater: annotationUpdaters) {
                annotationHandler.removeCallbacks(annotationUpdater);
            }
            finish();

        } else {
            Toast.makeText(this, "Press Back again to stop and return to Home Screen",
                    Toast.LENGTH_SHORT).show();
            stop = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    stop = false;
                    isPlaying = false;
                }
            }, 3 * 1000);
        }
    }
}
