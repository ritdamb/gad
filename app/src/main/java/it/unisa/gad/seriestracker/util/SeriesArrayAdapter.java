package it.unisa.gad.seriestracker.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.androidquery.AQuery;

import java.util.ArrayList;

import it.unisa.gad.seriestracker.Domain.Series;
import it.unisa.gad.seriestracker.R;

/**
 * Created by ludimar on 30/11/15.
 */

public class SeriesArrayAdapter extends ArrayAdapter<Series> {

    public SeriesArrayAdapter (Context context, ArrayList<Series> items) {
        super(context, R.layout.series_list_item, items);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        Series item = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.series_list_item, parent, false);
        }
        AQuery aq = new AQuery(convertView);

        // Lookup view for data population
        TextView textview = (TextView) convertView.findViewById(R.id.textViewTitle);
        TextView textviewDate = (TextView) convertView.findViewById(R.id.textViewDate);
        TextView textviewRating = (TextView) convertView.findViewById(R.id.textViewSubTitle);
        // Populate the data into the template view using the data object

        textview.setText(item.getName());
        if(item.getStartH() == null ) {
            textviewDate.setText("");
        } else {
            textviewDate.setText(item.getStartH());
        }

        if(item.getEpisodeTitle() == null) {
            textviewRating.setText("");
        } else {
            textviewRating.setText(item.getEpisodeTitle());
        }

        //we are loading a huge image from the network, but we only need the image to be bigger than 200 pixels wide
        //passing in the target width of 200 will down sample the image to conserve memory
        //aquery will only down sample with power of 2 (2,4,8...) for good image quality and efficiency
        //the resulting image width will be between 200 and 399 pixels
        if(item.getImageURL() == null || item.getImageURL().length() < 1 ) {
            aq.id(R.id.imageNews).image(R.drawable.noimage);
        }else {
            aq.id(R.id.imageNews).image(item.getImageURL(), true, true, 200,0);
        }
        // Return the completed view to render on screen
        return convertView;
    }
}
