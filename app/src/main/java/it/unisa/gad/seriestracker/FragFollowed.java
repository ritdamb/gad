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
import it.unisa.gad.seriestracker.util.ApplicationVariables;
import it.unisa.gad.seriestracker.util.FoundSeriesAdapter;

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
        ListView listView = (ListView) rootView.findViewById(R.id.listViewSeguiti);
        ApplicationVariables appvar= ApplicationVariables.getInstance();
        ArrayList<Series> resultSeries = appvar.getPreferiteSeries(rootView.getContext());

        //per ora estrapolo solo i title
        //va modificato

        FoundSeriesAdapter arrayAdapter = new FoundSeriesAdapter(getContext(),resultSeries);


        listView.setAdapter(arrayAdapter);


        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Series serie= (Series) parent.getItemAtPosition(position);
                Intent intent = new Intent(getView().getContext(), DetailsActivity.class);
                intent.putExtra(Series.NAME_TELEFILM, serie.getName());
                intent.putExtra(Series.ID_TELEFILM,serie.getId());
                startActivity(intent);
            }
        });
        return rootView;
    }


}
