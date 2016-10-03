package com.uct.noter.noter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Shuaib on 2016-09-18.
 * Custom list adaptor used to display recording details
 */
public class MultiItemListAdapter extends BaseAdapter {
    private Activity activity;
    private List<Recording> recordings;
    private static LayoutInflater inflater = null;

    /**
     * Constructor for Multi Item List Adapter
     * @param activity context
     * @param recordings to be displayed
     */
    public MultiItemListAdapter(Activity activity, List<Recording> recordings) {
        this.activity = activity;
        this.recordings = recordings;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    /**
     * Returns the number of recordings in list
     * @return count
     */
    @Override
    public int getCount() {
        return recordings.size();
    }

    /**
     * Returns the index of the position
     * @param position
     * @return position
     */
    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (convertView==null)
            v = inflater.inflate(R.layout.recording_row_layout,null);

        TextView recordingTitle = (TextView) v.findViewById(R.id.title);
        TextView interviewerName = (TextView) v.findViewById(R.id.Interviewer);
        TextView intervieweeName = (TextView) v.findViewById(R.id.Interviewee);
        TextView duration = (TextView) v.findViewById(R.id.duration);
        TextView date = (TextView) v.findViewById(R.id.date);

        //Sets the appropriate recording details to the relevant text views
        recordingTitle.setText(recordings.get(position).getTitle());
        interviewerName.setText(recordings.get(position).getInterviewerName());
        intervieweeName.setText(recordings.get(position).getIntervieweeName());
        duration.setText(TimerUtility.milliSecondsToTimer(recordings.get(position).getDuration()));
        date.setText(recordings.get(position).getDate());
        return v;
    }
}
