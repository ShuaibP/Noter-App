package com.uct.noter.noter;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Shuaib & Khadeejah on 2016-08-07.
 * Recording page activity, this page allows the user to start and stop recording. It allows the user
 * to add annotations. A timer is displayed when the recording starts.
 */

public class RecorderActivity extends AppCompatActivity implements AddNoteDialog.SaveNoteListener {

    private final Recorder recorder = Recorder.getRecorder();
    private final RecordingRepo recordingRepo = RecordingRepo.getInstance();
    private ImageButton shiftButton, recordButton, addNoteButton;
    private static TextView timerTextView;
    static Timer timer;
    private AudioVisualizerView visualizerView;
    private Handler amplitudeHandler, annotationHandler, handlerUi;
    private Intent intent;
    private List<String> annotations = new ArrayList<>();
    private List<Boolean> type = new ArrayList<>();
    private ListView annotationsListView;
    private String currentAnnotation, currentTheme;
    private long noteTimestamp;
    private static int notePosition;
    private boolean isRecording, themeActivated;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recorder);
        intent = new Intent(this, MainActivity.class);

        visualizerView = (AudioVisualizerView) findViewById(R.id.recorder_visualizer_view);
        amplitudeHandler = new Handler();
        annotationHandler = new Handler();
        handlerUi = new Handler();
        currentAnnotation = "";
        currentTheme = "";

        shiftButton = (ImageButton) findViewById(R.id.shift_annotation_button);
        shiftButton.setEnabled(false);
        shiftButton.setImageAlpha(127);
        recordButton = (ImageButton) findViewById(R.id.record_button);
        addNoteButton = (ImageButton) findViewById(R.id.add_note_button);
        addNoteButton.setEnabled(false);
        addNoteButton.setImageAlpha(127);
        annotationsListView = (ListView) findViewById(R.id.annotation_list);

        timerTextView = (TextView) findViewById(R.id.timer_text_view);
        timerTextView.setText(Timer.DEFAULT_TIME_DISPLAYED);
        timer = new Timer();

        recorder.setOutputFile(recordingRepo.getCurrentSession().getTitle());

        recordingRepo.getCurrentSession().getPreSetAnnotations(annotations, type);
        final SwitchButtonListAdapter switchButtonListAdapter = new SwitchButtonListAdapter(this, annotations, type);
        annotationsListView.setAdapter(switchButtonListAdapter);

        // set listener for predefined questions and themes in list
        annotationsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                long time = timer.getTimeInMilliseconds();

                if (!type.get(position)) {
                    String description = (annotationsListView.getItemAtPosition(position).toString());
                    recordingRepo.getCurrentSession().setQuestionTime(description, time, new Date());
                    currentAnnotation = annotations.get(position);
                    annotationHandler.post(updateAnnotations);
                    annotations.remove(position);
                    type.remove(position);
                    switchButtonListAdapter.notifyDataSetChanged();
                }
            }
        });

        // set listener for starting/stopping recording
        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isRecording) {
                    recorder.start();
                    timer.start();
                    addNoteButton.setEnabled(true);
                    addNoteButton.setImageAlpha(255);
                    handlerUi.postDelayed(mUpdateTask, 10000);
                    amplitudeHandler.post(updateVisualizer);
                    recordButton.setImageResource(R.mipmap.ic_stop_white_48dp);
                } else {
                    stopRecording();
                }
                isRecording = !isRecording;
            }
        });

        // set add note button listener
        addNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                noteTimestamp = timer.getTimeInMilliseconds();
                notePosition = visualizerView.getCurrentViewSize();
                showNoteDialog();
            }
        });

        // set shift button listener
        shiftButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                visualizerView.shiftAnnotation();
                recordingRepo.getCurrentSession().shiftAnnotation(currentAnnotation);
            }
        });
    }

    /**
     * Stop and save recording together with its recordings if interrupted
     */
    @Override
    protected void onStop() {
        super.onStop();
        if (isRecording)
            stopRecording();
    }

    /**
     * Stops recording and exports it with the used annotations
     */
    private void stopRecording() {
        recorder.stop();
        long time = timer.getTimeInMilliseconds();
        if (!currentTheme.equals("")){
            setThemeEnd(currentTheme);
        }
        recordingRepo.getCurrentSession().setDuration(time);
        recordingRepo.cleanCurrentSession(time);
        timer.stop();
        amplitudeHandler.removeCallbacks(updateVisualizer);
        visualizerView.clear();
        try {
            FileReader.JsonExport(recordingRepo.getCurrentSession().getTitle() + ".txt", recordingRepo.getCurrentSession());
        } catch (IOException ioexception) {
            Log.e("Export Error", ioexception.toString());
        }
        finish();
        startActivity(intent);
    }

    /**
     * Updates timer displayed while recording
     * @param timeElapsed current time elapsed
     */
    public static void setTimerDisplay(String timeElapsed) {
        timerTextView.setText(timeElapsed);
    }

    /**
     * Thread to update waveform amplitudes
     */
    private Runnable updateVisualizer = new Runnable() {
        @Override
        public void run() {
            visualizerView.addAmplitude(recorder.getAmplitude());
            if (themeActivated)
                visualizerView.addTheme(1);
            else
                visualizerView.addTheme(0);
            amplitudeHandler.postDelayed(this, 40);
        }
    };

    /**
     * Thread to update waveform with annotated question
     */
    private Runnable updateAnnotations = new Runnable() {
        @Override
        public void run() {
            visualizerView.addAnnotation();
        }
    };


    /**
     * Creates Thread to update waveform with annotated note
     * @param notePosition placement on waveform
     * @return thread
     */
    private Runnable createNoteRunnable(final int notePosition) {
        Runnable aRunnable = new Runnable() {
            public void run() {
                visualizerView.addAnnotation(notePosition);
            }
        };
        return aRunnable;
    }

    /**
     * Shows dialog to add note
     */
    private void showNoteDialog() {
        FragmentManager fm = getSupportFragmentManager();
        AddNoteDialog noteFragment = AddNoteDialog.newInstance("Add new note");
        noteFragment.show(fm, "dialog_fragment_add_note");
    }

    /**
     * Stores new note data
     * @param noteDescription description of note as entered by user
     */
    @Override
    public void onSaveNote(String noteDescription) {
        this.currentAnnotation = noteDescription;
        recordingRepo.getCurrentSession().addNote(noteDescription, noteTimestamp, new Date());
        annotationHandler.post(createNoteRunnable(notePosition));
        Toast.makeText(this, "Note saved", Toast.LENGTH_SHORT).show();
    }

    /**
     * Sets start time of theme when it is activated
     * @param themeDescription description of theme
     */
    public void setThemeStart(String themeDescription) {
        currentTheme = themeDescription;
        recordingRepo.getCurrentSession().setThemeStartTime(themeDescription, timer.getTimeInMilliseconds(), new Date());
        themeActivated = true;
    }

    /**
     * Sets end time of theme
     * @param themeDescription description of theme
     */
    public void setThemeEnd(String themeDescription) {
        currentTheme = "";
        recordingRepo.getCurrentSession().setThemeEndTime(themeDescription, timer.getTimeInMilliseconds(), new Date());
        themeActivated = false;
    }

    /**
     * Enables shift button
     */
    private Runnable mUpdateTask = new Runnable() {
        public void run() {
            if (currentAnnotation != "") {
                shiftButton.setEnabled(true);
                shiftButton.setImageAlpha(255);
                shiftButton.setImageAlpha(255);
            }
            else
                handlerUi.post(this);

        }
    };

    /**
     * update note position when waveform begins moving off screen
     */
    public static void updateNotePosition() {
        notePosition--;
    }
}
