package com.uct.noter.noter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.WindowManager;
import android.widget.EditText;

/**
 * Created by Khadeejah Omar on 2016/09/04.
 * Dialog that allows user to add a note
 */
public class AddNoteDialog extends DialogFragment {

    public AddNoteDialog() {
    }

    /**
     * Interface with a method to check when a note was saved
     */
    public interface SaveNoteListener {
        void onSaveNote(String inputText);
    }

    /**
     * Creates a new fragment with specified title
     * @param title heading to display on dialog
     * @return frag
     */
    public static AddNoteDialog newInstance(String title) {
        AddNoteDialog frag = new AddNoteDialog();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    /**
     * Creates and returns the dialog for users' to add notes while recording
     * @param savedInstanceState state of application so that no prior information is lost
     * @return dialog
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // create dialog
        String title = getArguments().getString("title");
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setView(getActivity().getLayoutInflater().inflate(R.layout.dialog_fragment_add_note, null));

        // create save button to save note and close dialog
        alertDialogBuilder.setPositiveButton("Save",  new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText note = (EditText) getDialog().findViewById(R.id.note_edit_text);
                SaveNoteListener listener = (SaveNoteListener) getActivity();
                listener.onSaveNote(note.getText().toString());
            }
        });

        // create cancel button discard changes made and close dialog
        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        // show dialog and soft keyboard
        AlertDialog dialog = alertDialogBuilder.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        return dialog;
    }

}
