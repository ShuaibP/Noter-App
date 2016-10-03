package com.uct.noter.noter;

import android.os.Environment;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shuaib on 2016-08-12.
 * Class encapsulates all I/O methods and logic for files, including managing of files and folders
 */
public class FileReader {

    static ArrayList<String> recordings;
    public static final String path = Environment.getExternalStorageDirectory().getAbsolutePath()
            + "/Noter/";

    public FileReader() {
    }

    /**
     * Return the lsit of all recording names within the folder
     * @return recording names
     */
    public static List<String> getRecordings() {
        getFiles();
        return recordings;
    }

    /**
     * Exports the recording to a json file
     * @param filename name of json file
     * @param recording to be saved
     * @throws IOException
     */
    public static void JsonExport(String filename, Recording recording) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());
        writer.writeValue(new File(path + filename), recording);
    }

    /**
     * Obtains all the names of the data files
     */
    private static void getFiles(){
        recordings = new ArrayList<String>();
        if (!dirExists(path))
        {
            File directory = new File(path);
            directory.mkdirs();
        }
        File location = new File(path);

        File[] files = location.listFiles();
        if (files.length != 0) {
            for (int i=0; i<files.length; i++)
                if (files[i].getName().substring(files[i].getName().length()-4).equals(".txt"))
                recordings.add(files[i].getName().substring(0,files[i].getName().length()-4));
        }
    }

    /**
     * Checks if the folder exists
     * @param path
     * @return
     */
    public static boolean dirExists(String path)
    {
        File dir = new File(path);
        if(dir.exists() && dir.isDirectory())
            return true;
        return false;
    }
}
