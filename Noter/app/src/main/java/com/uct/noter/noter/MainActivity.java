package com.uct.noter.noter;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by Shuaib on 2016-08-07.
 * Activity class, first page that the user will see, contains a list view of recordings and allows
 * the user to enter the recording page select a recording to play.
 */

public class MainActivity extends AppCompatActivity {

    private Boolean exit = false;
    RecordingRepo recordingRepo;
    ListView recordingsListView;
    Intent setupIntent, playerIntent;
    ProgressBar storageIndicator;
    StorageInformation storageInformation;
    TextView availableSpace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        storageIndicator = (ProgressBar) findViewById(R.id.toolbar_progress_bar);
        availableSpace = (TextView) findViewById(R.id.available_space);

        storageInformation = new StorageInformation();
        float percentageUsed  = ((float) storageInformation.busyMemory()/(float)storageInformation.totalMemory())*100;
        //Get the percentage of memory used
        availableSpace.setText(((int) percentageUsed )+"%");
        if (percentageUsed > 75){
            storageIndicator.getProgressDrawable().setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_IN);
        }
        if (storageInformation.freeMemory() < 10){
            storageIndicator.getProgressDrawable().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
        }
        storageIndicator.setProgress((int) percentageUsed);
        setSupportActionBar(toolbar);
        recordingRepo = RecordingRepo.getInstance();
        try {
            initializeRecordingRepo();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Button for moving to setup page
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setupIntent = new Intent(getApplicationContext(), SetupActivity.class);
                startActivity(setupIntent);

            }
        });

        MultiItemListAdapter recordingsAdapter = new MultiItemListAdapter(this,recordingRepo.getRecordings());
        recordingsListView = (ListView) findViewById(R.id.Recording_listView);
        recordingsListView.setAdapter(recordingsAdapter);

        //List view Onclick for selecting a recording opening it in PlayActivity
        recordingsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int recordingIndex = position;

                playerIntent = new Intent(getApplicationContext(), PlayActivity.class);
                playerIntent.putExtra("recordingIndex", recordingIndex);
                startActivity(playerIntent);
            }
        });
    }

    /**
     * Gets all json files and creates recording objects from that data then adds those recording objects
     * to recordingRepo
     * @throws IOException
     */
    private void initializeRecordingRepo() throws IOException{
        ObjectMapper mapper = new ObjectMapper();
        List<String> recordingNames = FileReader.getRecordings();
        for (int i = 0; i<recordingNames.size(); i++){
            recordingRepo.addRecording(mapper.readValue(new File(FileReader.path + recordingNames.get(i)+".txt"), Recording.class));
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * User interface feature that ensures a double back press within 3 seconds only will exit the
     * application
     */
    @Override
    public void onBackPressed() {
        if (exit) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//***Change Here***
            startActivity(intent);
            finish();
            System.exit(0);
        } else {
            Toast.makeText(this, "Press Back again to Exit.",
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
