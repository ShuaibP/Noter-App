package com.uct.noter.noter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Khadeejah Omar on 2016/08/30.
 * View that displays waveform
 */
public class AudioVisualizerView extends View {

    private static final int LINE_WIDTH = 1, LINE_SCALE = 75;
    private ColorFilter secondaryTint;
    private Paint linePaint, annotationPaint;
    private List<Float> amplitudes;
    private List<Integer> annotations, themes;
    private int width, height, currentAnnotation;
    private RecordingRepo recordingRepo;
    private boolean playing;
    private Bitmap playWaveform;
    private float progress;

    public AudioVisualizerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.linePaint = new Paint();
        this.linePaint.setColor(ContextCompat.getColor(getContext(), R.color.colorWaveformDefault));
        this.linePaint.setStrokeWidth(LINE_WIDTH);
        this.annotationPaint = new Paint();
        this.annotationPaint.setColor(Color.DKGRAY);
        this.annotationPaint.setShadowLayer(15, 0, 0, Color.DKGRAY);
        this.secondaryTint = new PorterDuffColorFilter(ContextCompat.getColor(getContext(), R.color.colorWaveformTint), PorterDuff.Mode.SRC_ATOP);
        setLayerType(LAYER_TYPE_SOFTWARE, annotationPaint);
        this.currentAnnotation = -1;
        this.recordingRepo = RecordingRepo.getInstance();
    }

    /**
     * Sets  width and height of view if window size changes
     * @param width current width of view
     * @param height current height of view
     * @param oldWidth width before change
     * @param oldHeight height before change
     */
    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        this.width = width;
        this.height = height;
        this.amplitudes = new ArrayList<Float>(width / LINE_WIDTH);
        this.themes = new ArrayList<Integer>(width / LINE_WIDTH);
        this.annotations = new ArrayList<Integer>(Collections.nCopies(width / LINE_WIDTH, 0));
    }

    /**
     * Clears view after recording and stores remaining data in recording
     */
    public void clear() {
        while(!amplitudes.isEmpty()){
            this.recordingRepo.getCurrentSession().addAmplitude(amplitudes.remove(0));
            this.recordingRepo.getCurrentSession().addVisualizerAnnotation(annotations.remove(0));
            this.recordingRepo.getCurrentSession().addVisualTheme(themes.remove(0));
        }
    }

    /**
     *  Updates waveform while recording
     * @param amplitude power value received from media player while recording
     */
    public void addAmplitude(float amplitude) {
        amplitudes.add(amplitude);

        // if the view is filled, remove oldest value
        if (amplitudes.size() * LINE_WIDTH >= width) {
            this.recordingRepo.getCurrentSession().addAmplitude(amplitudes.remove(0));
            this.recordingRepo.getCurrentSession().addVisualizerAnnotation(annotations.remove(0));
            this.recordingRepo.getCurrentSession().addVisualTheme(themes.remove(0));
            this.annotations.add(0);
            this.currentAnnotation--;
            RecorderActivity.updateNotePosition();
        }
        this.invalidate();
    }

    public int getCurrentViewSize() {
        return this.amplitudes.size() - 1;
    }

    /**
     * Adds theme to waveform
     * @param activated describes if theme is activated or not
     */
    public void addTheme(int activated) {
        this.themes.add(activated);
        this.invalidate();
    }

    /**
     * Adds notes to waveform
     * @param notePosition defines where on the waveform the note should be placed
     */
    public void addAnnotation(int notePosition) {
        this.currentAnnotation = notePosition;
        this.annotations.set(this.currentAnnotation, 1);
        this.invalidate();
    }

    /**
     * Adds questions to waveform
     */
    public void addAnnotation() {
        this.currentAnnotation = amplitudes.size()-1;
        this.annotations.set(this.currentAnnotation, 1);
        this.invalidate();
    }

    /**
     * Creates waveform displayed during playback
     * @param amplitudes amplitudes to display as waveform
     * @param annotations notes and questions to display on waveform
     * @param themes where themes are activated
     */
    public void initPlay(List<Float> amplitudes, List<Integer> annotations, List<Integer> themes) {
        if(!this.playing)
            this.annotations.clear();
        this.playing = true;
        int end = 0;
        while(end < amplitudes.size()) {
            this.amplitudes.add(amplitudes.get(end));
            this.annotations.add(annotations.get(end));
            this.themes.add(themes.get(end));
            end++;
        }
        this.invalidate();
    }

    /**
     * Draws waveform
     * @param canvas hosts drawing calls
     */
    @Override
    public void onDraw(Canvas canvas) {
        if (this.playing)
            drawCompressedWaveform(canvas);
        else
            drawUpdatedWaveform(canvas);
    }

    /**
     * Shifts note or question on waveform when user subtracts 10s from start time while recording
     */
    public void shiftAnnotation() {
        if (this.currentAnnotation >= 249) {
            this.annotations.set(this.currentAnnotation - 249, 1);
            this.annotations.set(this.currentAnnotation, 0);
            this.currentAnnotation -= 249;
        }
        else if (this.currentAnnotation < -1) {
            this.annotations.set(this.currentAnnotation, 0);
            this.currentAnnotation = -1;
        }
        this.invalidate();
    }

    /**
     * Drawing method used to display waveform on playback
     * @param canvas hosts drawing calls
     */
    public void drawCompressedWaveform(Canvas canvas) {
        this.linePaint.setColorFilter(null);
        this.annotationPaint.setColorFilter(null);

        float scalingFactor = (float) this.amplitudes.size() / (float)width;
        float curX = 0;
        int index = 0;
        int middle = this.height / 2;

        // draw waveform of amplitudes
        this.linePaint.setStrokeWidth(1/scalingFactor);
        while (curX <= this.width && index < this.amplitudes.size()) {
            curX += (float) 1/scalingFactor;
            if (curX >= this.progress) {
                this.linePaint.setColorFilter(this.secondaryTint);
                annotationPaint.setColorFilter(this.secondaryTint);
            }
            if (this.themes.get(index) == 1)
                this.linePaint.setColor(Color.MAGENTA);
            else
                this.linePaint.setColor(ContextCompat.getColor(getContext(), R.color.colorWaveformDefault));

            float scaledHeight = this.amplitudes.get(index) / LINE_SCALE;
            canvas.drawLine(curX, middle + scaledHeight / 2, curX, middle
                    - scaledHeight / 2, this.linePaint);
            index++;
        }

        // draw annotations
        curX=0;
        for (int i = 0; i < this.annotations.size(); i++) {
            curX += 1/scalingFactor;
            if (this.annotations.get(i) == 1) {
                this.annotationPaint.setColor(Color.DKGRAY);
                this.annotationPaint.setShadowLayer(15, 0, 0, Color.DKGRAY);
                canvas.drawCircle(curX, middle, 8, this.annotationPaint);
            }
        }
    }

    /**
     * Draw wavform while recording
     * @param canvas hosts drawing calls
     */
    public void drawUpdatedWaveform(Canvas canvas) {
        int middle = height / 2;
        float curX = 0;

        // draw waveform of amplitudes with themes where it is activated
        for (int i = 0; i < this.amplitudes.size(); i++) {
            float power = this.amplitudes.get(i);
            float scaledHeight = power / LINE_SCALE;
            curX += LINE_WIDTH;

            if (this.themes.get(i) == 1)
                this.linePaint.setColor(Color.MAGENTA);
            else
                this.linePaint.setColor(ContextCompat.getColor(getContext(), R.color.colorWaveformDefault));
            canvas.drawLine(curX, middle + scaledHeight / 2, curX, middle
                    - scaledHeight / 2, this.linePaint);
        }

        // draw annotations
        curX = 0;
        for (int i = 0; i < this.annotations.size(); i++) {
            if (this.annotations.get(i) == 1) {
                if (i == this.currentAnnotation) {
                    this.annotationPaint.setColor(Color.LTGRAY);
                    this.annotationPaint.setShadowLayer(15, 0, 0, Color.LTGRAY);
                }
                else {
                    this.annotationPaint.setColor(Color.DKGRAY);
                    this.annotationPaint.setShadowLayer(15, 0, 0, Color.DKGRAY);
                }
                canvas.drawCircle(curX, middle, 8, this.annotationPaint);
            }
            curX += LINE_WIDTH;
        }
        this.annotationPaint.setColor(Color.DKGRAY);
        this.annotationPaint.setShadowLayer(15, 0, 0, Color.DKGRAY);
    }

    /**
     * Convert drawn waveform to bitmap
     */
    private void canvasToBitmap() {
        this.playWaveform = Bitmap.createBitmap(this.getWidth(), this.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(this.playWaveform);
        this.draw(canvas);
    }

    /**
     * Returns waveform as bitmap to be used in play activity as seekbar
     * @return playWavform
     */
    public Bitmap getPlayWaveform() {
        canvasToBitmap();
        return this.playWaveform;

    }

    /**
     * Syncs waveform seekbar with audio
     * @param milliseconds current position in audio track
     * @param audioLength audio track length
     * @return synced waveform
     */
    public Bitmap updateProgress(float milliseconds, float audioLength) {
        float pixelsPerMs = width/audioLength;
        if (milliseconds == 0)
            this.progress = 0;
        else
            this.progress = pixelsPerMs*milliseconds;

        return getPlayWaveform();
    }
}

