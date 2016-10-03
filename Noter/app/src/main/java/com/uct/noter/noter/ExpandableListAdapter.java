package com.uct.noter.noter;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Shuaib on 2016-08-31.
 * List Adaptor used in setup Avtivity.
 * This adaptor consists of 2 nested lists, one for questins and one for themes
 * The first rows in both lists have text input fields so the user can enter text.
 * The est of the have text vies but when clicked an editText will appear so that the user may
 * edit or delete the entry.
 */
public class ExpandableListAdapter extends BaseExpandableListAdapter {
    private Context _context;
    private List<String> _listDataHeader;
    private HashMap<String, List<String>> _listDataChild;

    public ExpandableListAdapter(Context context, List<String> listDataHeader,
                                 HashMap<String, List<String>> listChildData) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
    }

    /**
     * Returns child item in Expandable list
     * @param groupPosition
     * @param childPosition
     * @return childItem
     */
    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .get(childPosition);
    }

    /**
     * Returns position of child item
     * @param groupPosition
     * @param childPosition
     * @return child positions
     */
    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    /**
     * Used to generate view for expandable list adaptor
     * @param groupPosition
     * @param childPosition
     * @param isLastChild
     * @param convertView
     * @param parent
     * @return view
     */
    @Override
    public View getChildView(final int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final String childText = (String) getChild(groupPosition, childPosition);

        if (childPosition == 0) { //Checks if its the first item in the list, to expand a different layout
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_item_add, null);
            final EditText edittext = (EditText) convertView.findViewById(R.id.editText1);

            edittext.setOnKeyListener(new View.OnKeyListener() {
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    // If the event is a key-down event on the "enter" button
                    if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                            (keyCode == KeyEvent.KEYCODE_ENTER) || (keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER)) {
                        // Perform action on key press
                        String text = edittext.getText().toString();
                        if (!text.matches(""))
                            if (!SetupActivity.containsData(groupPosition, childPosition, text))
                                SetupActivity.addData(groupPosition, childPosition, text);
                        return true;
                    }
                    return false;
                }
            });
            final ImageButton addButton = (ImageButton) convertView.findViewById(R.id.addButton);
            addButton.setVisibility(View.VISIBLE);
            addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String text = edittext.getText().toString();
                    if (!text.matches(""))
                        if(!SetupActivity.containsData(groupPosition, childPosition, text))
                            SetupActivity.addData(groupPosition, childPosition, text);
                }
            });
            return convertView;
        }

        else { //  If not first in the list, inflate a different view
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_item, null);

        }

        final ImageButton deleteButton = (ImageButton) convertView.findViewById(R.id.delete);

        final TextView txtListChild = (TextView) convertView
                .findViewById(R.id.lblListItem);
        final EditText editText = (EditText) convertView.findViewById(R.id.editText2);
        final ImageButton addButton = (ImageButton) convertView.findViewById(R.id.addButton1);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetupActivity.editData(groupPosition, childPosition, editText.getText().toString());
            }
        });

        final InputMethodManager imm = (InputMethodManager) this._context.getSystemService(Context.INPUT_METHOD_SERVICE); //To ensure keyboard is visible
        txtListChild.setText(childText);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetupActivity.removeData(groupPosition, childPosition ,editText.getText().toString());
            }
        });
        return convertView;
    }

    /**
     * Returns number of child items
     * @param groupPosition
     * @return children count
     */
    @Override
    public int getChildrenCount(int groupPosition) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .size();
    }

    /**
     * Returns the entire group
     * @param groupPosition
     * @return group
     */
    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    /**
     * Returns the number of groups
     * @return group count
     */
    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    /**
     * Returns the postion of the group
     * @param groupPosition
     * @return group position
     */
    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_group, null);
        }

        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.lblListHeader);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

}
