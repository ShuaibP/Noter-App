package com.uct.noter.noter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Shuaib & Khadeejah on 2016-08-07.
 * Set up page activity. This page is meant for the user to enter a recording name, his/her name and
 * the name of the person being interviewed. It also gives the user the option of creating questions
 * and themes before starting the interview.
 */

public class SetupActivity extends AppCompatActivity {

    private static ExpandableListAdapter listAdapter;
    private static ExpandableListView expListView;
    private static List<String> listDataHeader =  new ArrayList<String>();
    private static HashMap<String, List<String>> listDataChild  = new HashMap<String, List<String>>();
    private static List<String> Themes = new ArrayList<String>();
    private static List<String> Questions = new ArrayList<String>();
    private static List<String> recordings;
    private Boolean exit = false;
    private boolean error;
    private Intent recorderIntent;
    private Recording recording;
    final RecordingRepo recordingRepo = RecordingRepo.getInstance();
    private EditText interviewerName, intervieweeName;
    private TextInputLayout textInputLayoutRecording, textInputLayoutInterviewer, textInputLayoutInterviewee;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        recordings = FileReader.getRecordings();
        interviewerName = (EditText) findViewById(R.id.interviewer_edit_text);
        intervieweeName = (EditText) findViewById(R.id.interviewee_edit_text);
        final EditText recordingName = (EditText) findViewById(R.id.editText);
        textInputLayoutRecording = (TextInputLayout) findViewById(R.id.text_input_layout_recording_name);
        textInputLayoutInterviewer = (TextInputLayout) findViewById(R.id.text_input_layout_interviewer_name);
        textInputLayoutInterviewee = (TextInputLayout) findViewById(R.id.text_input_layout_interviewee_name);

        /**
         * Text changed listener to validate what the user enters
         */
        recordingName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                for (int i = 0; i <recordings.size(); i++){
                    if (recordingName.getText().toString().trim().equals(recordings.get(i))) {
                        textInputLayoutRecording.setError("Recording name exists. Please choose another name");
                        error = true;
                        break;
                    }
                    else{
                        textInputLayoutRecording.setError(null);
                        error = false;
                    }
                }
            }
        });

        /**
         * Text changed listener to validate what the user enters
         */
        interviewerName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count>0){
                    textInputLayoutInterviewer.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (interviewerName.getText().toString().equals("")){
                    textInputLayoutInterviewer.setError("Please enter a name");
                }
            }
        });

        /**
         * Text changed listener to validate what the user enters
         */
        intervieweeName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count>0){
                    textInputLayoutInterviewee.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (intervieweeName.getText().toString().equals("")){
                    textInputLayoutInterviewee.setError("Please enter a name");
                }
            }
        });

        /**
         * Button to proceed to recording page. Checks if all fields are filled in and done
         * so correctly.
         */
        Button fab = (Button) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (intervieweeName.getText().toString().equals("") ){
                    textInputLayoutInterviewee.setError("Please enter a name");
                }

                if(interviewerName.getText().toString().equals("")){
                    textInputLayoutInterviewer.setError("Please enter a name");
                }

                if (recordingName.getText().toString().equals("")){
                    textInputLayoutRecording.setError("Please enter a name for recording");
                }

                if (error){
                    textInputLayoutRecording.setError("Recording name exists. Please choose another name");
                }

                else if (!intervieweeName.getText().toString().equals("") && !interviewerName.getText().toString().equals("") &&
                        !recordingName.getText().toString().equals("")) {
                    recording = new Recording(recordingName.getText().toString(), Themes, Questions);
                    //Creates recording object
                    recording.setIntervieweeName(intervieweeName.getText().toString());
                    recording.setInterviewerName(interviewerName.getText().toString());
                    recording.setDate(new Date().toString());
                    //Adds other data to recording
                    recordingRepo.setCurrentSession(recording);
                    //Sets the recording as the current session
                    recorderIntent = new Intent(getApplicationContext(), RecorderActivity.class);
                    startActivity(recorderIntent);
                }
            }
        });

        listDataHeader =  new ArrayList<String>();
        listDataChild  = new HashMap<String, List<String>>();;
        Themes = new ArrayList<String>();
        Questions = new ArrayList<String>();

        // get the listview
        expListView = (ExpandableListView) findViewById(R.id.lvExp);

        // preparing list data
        prepareListData();

        listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);

        expListView.setAdapter(listAdapter);

        expListView.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);

        // Listview on child click listener
        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        final int groupPosition, final int childPosition, long id) {
                final TextView txtListChild = (TextView) v.findViewById(R.id.lblListItem);
                final EditText edittext = (EditText) v.findViewById(R.id.editText2);
                edittext.setVisibility(View.VISIBLE);
                edittext.setText(txtListChild.getText().toString());
                txtListChild.setVisibility(View.GONE);
                edittext.requestFocus();
                InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(edittext, InputMethodManager.SHOW_IMPLICIT);

                //Editor Action listener to detect if keyboard opens
                edittext.setOnEditorActionListener(new EditText.OnEditorActionListener() {

                    @Override
                    public boolean onEditorAction(TextView v, int actionId,
                                                  KeyEvent event) {
                        if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER))
                                || (actionId == EditorInfo.IME_ACTION_DONE)) {
                            String text = edittext.getText().toString();
                            if (!text.matches(""))
                                if (!SetupActivity.containsData(groupPosition, childPosition, text))
                                    SetupActivity.editData(groupPosition, childPosition, text);
                            return true;
                        }
                        return false;
                    }
                });

                //Editor Action listener to detect keyboard presses
                edittext.setOnKeyListener(new View.OnKeyListener() {
                    public boolean onKey(View v, int keyCode, KeyEvent event) {
                        // If the event is a key-down event on the "enter" button
                        if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                                (keyCode == KeyEvent.KEYCODE_ENTER) || (keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER)) {
                            // Perform action on key press
                            String text = edittext.getText().toString();
                            if (!text.matches(""))
                                if (!SetupActivity.containsData(groupPosition, childPosition, text))
                                    SetupActivity.editData(groupPosition, childPosition, text);
                            return true;
                        }
                        return false;
                    }
                });
                return false;
            }
        });

        //Long click listener for list view. Displays delete button.
        expListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (ExpandableListView.getPackedPositionType(id) == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
                    ImageButton deleteButton = (ImageButton) view.findViewById(R.id.delete);
                    deleteButton.setVisibility(View.VISIBLE);
                }
                return false;
            }
        });

        // Expand listener
        expListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {
                InputMethodManager inputManager = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (getWindow().getCurrentFocus() != null) {
                    inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    getCurrentFocus().clearFocus();
                }
            }
        });

        // Collspse listener
        expListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
                InputMethodManager inputManager = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (getWindow().getCurrentFocus() != null) {
                    inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    getCurrentFocus().clearFocus();
                }
            }
        });
    }

    /**
     * Preparing list data
     */
    private void prepareListData() {

        // Adding child data
        listDataHeader.add("Themes");
        listDataHeader.add("Questions");

        // Adding child data
        Themes.add("");
        Questions.add("");

        listDataChild.put(listDataHeader.get(0), Themes); // Header, Child data
        listDataChild.put(listDataHeader.get(1), Questions);
    }

    /**
     * Add data to the arrayList and then notify the list adapter of the changes
     * @param groupPosition
     * @param ItemPosition
     * @param text
     */
    public static void addData(int groupPosition, int ItemPosition, String text)
    {
        if (groupPosition == 0)
            Themes.add(text);
        else
            Questions.add(text);
        listAdapter.notifyDataSetChanged();
    }

    /**
     * Remove data to the arrayList and then notify the list adapter of the changes
     * @param groupPosition
     * @param ItemPosition
     * @param text
     */
    public static void removeData(int groupPosition, int ItemPosition, String text)
    {
        if (groupPosition == 0)
            Themes.remove(ItemPosition);
        else
            Questions.remove(ItemPosition);
        listAdapter.notifyDataSetChanged();
    }

    /**
     * Edit data in the arrayList and then notify the list adapter of the changes
     * @param groupPosition
     * @param ItemPosition
     * @param text
     */
    public static void editData(int groupPosition, int ItemPosition, String text){
        if (groupPosition == 0)
            Themes.set(ItemPosition,text);
        else
            Questions.set(ItemPosition,text);
        listAdapter.notifyDataSetChanged();
    }

    /**
     * Determines of the arrays contain an entry
     * @param groupPosition
     * @param ItemPosition
     * @param text
     * @return
     */
    public static boolean containsData(int groupPosition, int ItemPosition, String text)
    {
        if (groupPosition == 0){
            if (Themes.contains(text)){
                return true;
            }
        }
        else{
            if (Questions.contains(text)){
                return true;
            }
        }
        return false;
    }

    /**
     * User interface feature that ensures a double back press within 3 seconds only will exit the
     * application
     */
    @Override
    public void onBackPressed() {
        if (exit) {
            finish();
        } else {
            Toast.makeText(this, "Press Back again to Exit. All Changes will be lost",
                    Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 3 * 1000);

        }
    }
}
