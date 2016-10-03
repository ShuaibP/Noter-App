package com.uct.noter.noter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shuaib on 2016-09-02.
 * Custom adapter for for recorder activity. Themes in the list will have a switch that can be
 * activated and deactivated. Only one switch can be activated at a time, ehich means the rest
 * are deactivated.
 */
public class SwitchButtonListAdapter extends ArrayAdapter<String> {
    final RecorderActivity context;
    private final List<String> values;
    private final List<Boolean> type;
    private final List<Boolean> switchStatus ;
    private boolean active;

    /**
     * Cosntructor for SwitchButtonList Adapter
     * @param context
     * @param values question and themes
     * @param type boolean indicating whether question or theme
     */
    public SwitchButtonListAdapter(Context context, List<String> values, List<Boolean> type) {
        super(context, R.layout.activity_main, values);
        this.context = (RecorderActivity) context;
        this.values = values;
        this.type = type;
        this.switchStatus = new ArrayList<>();
        active = false;
        initialize();
    }

    /**
     * Initialize switch status to true
     */
    private void initialize(){
        for (int i = 0; i<type.size(); i++)
        {
            this.switchStatus.add(i,true);
        }
    }

    /**
     * Determines if the item is a question or theme, then adds a switch button with a listerner
     * if it is a theme.
     * @param position
     * @param convertView
     * @param parent
     * @return view
     */
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.row_layout, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.annotation_text);
        Switch switchButton = (Switch) rowView.findViewById(R.id.switch_button);
        if (this.type.get(position)){
            switchButton.setVisibility(View.VISIBLE);
            switchButton.setChecked(!this.switchStatus.get(position));
        }

        if (active && switchStatus.get(position)){
            switchButton.setEnabled(false);
        }

        else if (!active){
            switchButton.setEnabled(true);
        }
        //Listener for checking if switch button is checked
        switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    context.setThemeStart(values.get(position));
                    switchStatus.set(position,false);
                    active = true;
                    notifyDataSetChanged();
                }
                else {
                    context.setThemeEnd(values.get(position));
                    switchStatus.set(position,true);
                    active = false;
                    notifyDataSetChanged();
                }
            }
        });

        if (!values.get(position).equals(""))
            textView.setText(values.get(position));

        return rowView;
    }


}
