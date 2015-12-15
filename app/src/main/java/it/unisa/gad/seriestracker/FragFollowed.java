package it.unisa.gad.seriestracker;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;



import android.content.Intent;
import android.os.Environment;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import it.unisa.gad.seriestracker.Domain.Series;

/**
 * Created by Rita on 13/12/2015.
 */
public class FragFollowed extends Fragment {

    public FragFollowed() {
        // Empty constructor required for fragment subclasses
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_followed, container, false);
        ListView listView = (ListView) rootView
                .findViewById(R.id.listViewSeguiti);
        ArrayList<String> arr = new ArrayList<String>();

        File fileDir = getDocumentStorageDir("telefilm");
        if (isExternalStorageReadable()) {

            arr = loadFileInArray(new File(fileDir, "telefilm.txt"));

            if (arr.size() == 0)
                arr.add("");

            ArrayAdapter<String> ad = new ArrayAdapter<String>(
                    rootView.getContext(), R.layout.text_view, arr);
            listView.setAdapter(ad);
        } else {
            Toast.makeText(rootView.getContext(),
                    "Impossibile leggere la sdcard", Toast.LENGTH_LONG).show();
        }

       listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                String name = (String) parent.getItemAtPosition(position);
                Intent intent = new Intent(getView().getContext(), DetailsActivity.class);
                intent.putExtra(Series.NAME_TELEFILM, name);
                startActivity(intent);
            }
        });
        return rootView;
    }

    private ArrayList<String> loadFileInArray(File file) {

        ArrayList<String> arr = new ArrayList<String>();
        FileReader filereader = null;

        try {
            filereader = new FileReader(file);
        } catch (FileNotFoundException e1) {
            arr.add("");
            return arr;
        }

        BufferedReader buff = new BufferedReader(filereader);
        String line = "";

        try {
            while ((line = buff.readLine()) != null) {
                arr.add(line);
            }
            buff.close();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return arr;
    }

    public File getDocumentStorageDir(String documentName) {
        // Get the directory for the user's public pictures directory.
        File file = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                documentName);
        return file;
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)
                || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }
}
