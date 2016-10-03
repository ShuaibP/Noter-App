package com.uct.noter.noter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Khadeejah Omar on 2016/08/31.
 * Acts as a collection of recording objects with methods to access recordings, add recordings and
 * update annotations of a recording in progress
 */
public class RecordingRepo {

    private List<Recording> recordings;
    private Recording currentSession;

    private static RecordingRepo recordingRepo = new RecordingRepo();
    public static RecordingRepo getInstance() {
        return recordingRepo;
    }

    private RecordingRepo() {
        recordings = new ArrayList<Recording>();
    }

    /**
     * Obtain recordings in repo
     * @return list of recordings
     */
    public List<Recording> getRecordings() {
        return recordings;
    }

    public void setRecordings(List<Recording> recordings) {
        this.recordings = recordings;
    }

    /**
     * Returns recording object for recording in progress
     * @return currentSession
     */
    public Recording getCurrentSession() {
        return currentSession;
    }


    /**
     * Obtain recording in progress
     * @param currentSession current recording
     */
    public void setCurrentSession(Recording currentSession) {
        this.currentSession = currentSession;
    }

    /**
     * Adds recording to repo
     * @param recording recording with its annotations
     * @return position of recording in repo's list
     */
    public int addRecording(Recording recording) {
        int index = containsRecording(recording.getTitle());
        if (index == -1) {
            this.recordings.add(recording);
            index = recordings.size()-1;
        }
        return index;
    }

    /**
     * Returns all annotations of specified recording in a sorted list
     * @param recordingIndex position of recording in list
     * @return list of annotations
     */
    public ArrayList<Annotation> getAllAnnotations(int recordingIndex) {
        return accumulateAnnotations(this.recordings.get(recordingIndex));
    }

    /**
     * Returns amplitudes for specified recording
     * @param recordingIndex recording position in list
     * @return list of amplitudes
     */
    public List<Float> getAmplitudes(int recordingIndex) {
        return this.recordings.get(recordingIndex).getVisualAmplitudes();
    }

    /**
     * Returns questions and notes on waveform for specified recording
     * @param recordingIndex recording position in list
     * @return list of positions of notes and questions
     */
    public List<Integer> getVisualAnnotations(int recordingIndex) {
        return this.recordings.get(recordingIndex).getVisualAnnotations();
    }

    /**
     * Returns themes for waveform of specified recording
     * @param recordingIndex recording position in list
     * @return list of positions of active themes
     */
    public List<Integer> getVisualThemes(int recordingIndex) {
        return this.recordings.get(recordingIndex).getVisualThemes();
    }


    /**
     * Checks whether a recording with specified name exists
     * @param recordingName specified name
     * @return position in list if found, -1 if not found
     */
    private int containsRecording(String recordingName) {
        for (Recording recording : this.recordings) {
            if (recording.getTitle().equals(recordingName))
                return recordings.indexOf(recording);
        }
        return -1;
    }

    /**
     * Sorts all annotations given a recording
     * @param recording given recording
     * @return list of sorted annotations
     */
    private ArrayList<Annotation> accumulateAnnotations (Recording recording) {
        ArrayList<Annotation> allAnnotations = new ArrayList<Annotation>();
        allAnnotations.addAll(recording.getQuestions());
        allAnnotations.addAll(recording.getThemes());
        allAnnotations.addAll(recording.getNotes());
        Collections.sort(allAnnotations);
        return allAnnotations;

    }

    /**
     * Gets recording details including, title, interviewer, interviewee, date
     * @param index recording position
     * @return list of details
     */
    public String[] getRecordingDetails(int index) {
        String[] details = new String[3];
        Recording recording = recordings.get(index);
        details[0] = recording.getTitle();
        details[1] = recording.getIntervieweeName() + " interviewed by " + recording.getInterviewerName();
        details[2] = recording.getDate();
        return details;
    }

    /**
     * Removes unused annotations and updates theme that was active when user stopped recording
     * @param recordingLength length of audio track
     */
    public void cleanCurrentSession(long recordingLength) {
        // cleanup questions
        int index = 0;
         while (index < this.currentSession.getQuestions().size() && index >= 0) {
             if (this.currentSession.getQuestions().get(index).getStartTime() == -1) {
                 this.currentSession.getQuestions().remove(index);
                 index--;
                 continue;
             }
             index++;
        }

        // cleanup themes
        index = 0;
        while (index < this.currentSession.getThemes().size() && index >= 0) {
            if (this.currentSession.getThemes().get(index).getTimeStamps().size() == 0) {
                this.currentSession.getThemes().remove(index);
                index--;
                continue;
            }
            else {
                for (int segmentIndex = 0; segmentIndex < this.currentSession.getThemes().get(index).getTimeStamps().size(); segmentIndex++) {
                    if (this.currentSession.getThemes().get(index).getTimeStamps().get(segmentIndex).getEndTime() == -1) {
                        this.currentSession.getThemes().get(index).getTimeStamps().get(segmentIndex).setEndTime(recordingLength);
                    }

                }
                index++;
            }

        }
    }
}
